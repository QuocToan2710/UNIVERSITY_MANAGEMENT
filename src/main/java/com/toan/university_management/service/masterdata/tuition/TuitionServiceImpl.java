package com.toan.university_management.service.masterdata.tuition;

import com.toan.university_management.dto.request.masterdata.RecordPaymentRequest;
import com.toan.university_management.dto.response.masterdata.StudentTuitionSummaryResponse;
import com.toan.university_management.dto.response.masterdata.TuitionDashboardSummaryResponse;
import com.toan.university_management.dto.response.masterdata.TuitionItemResponse;
import com.toan.university_management.entity.masterdata.ClassGroup;
import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.Major;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.entity.masterdata.SubjectClass;
import com.toan.university_management.entity.masterdata.TuitionFee;
import com.toan.university_management.enums.NotificationPriority;
import com.toan.university_management.enums.NotificationTargetType;
import com.toan.university_management.enums.NotificationType;
import com.toan.university_management.enums.TuitionStatus;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.masterdata.ClassGroupRepository;
import com.toan.university_management.repository.masterdata.EnrollmentRepository;
import com.toan.university_management.repository.masterdata.MajorRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.SubjectClassRepository;
import com.toan.university_management.repository.masterdata.SubjectRepository;
import com.toan.university_management.repository.masterdata.TuitionFeeRepository;
import com.toan.university_management.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TuitionServiceImpl implements TuitionService {

    public static final long DEFAULT_PRICE_PER_CREDIT = 450000L;

    TuitionFeeRepository tuitionFeeRepository;
    EnrollmentRepository enrollmentRepository;
    StudentRepository studentRepository;
    SubjectClassRepository subjectClassRepository;
    SubjectRepository subjectRepository;
    ClassGroupRepository classGroupRepository;
    MajorRepository majorRepository;
    UserRepository userRepository;
    NotificationService notificationService;

    @Override
    @Transactional
    public StudentTuitionSummaryResponse getMyTuitionSummary(String semester, String academicYear) {
        Student student = resolveCurrentStudent();
        return buildStudentTuitionSummary(student, semester, academicYear);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentTuitionSummaryResponse> getMyTuitionHistory() {
        Student student = resolveCurrentStudent();
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentIdAndDeletedFalse(student.getId());

        Set<Long> scIds = enrollments.stream().map(Enrollment::getSubjectClassId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, SubjectClass> scMap = subjectClassRepository.findAllByIdInAndDeletedFalse(scIds).stream()
                .collect(Collectors.toMap(SubjectClass::getId, Function.identity()));

        // Gather distinct (semester, academicYear) pairs
        Set<String> distinctTerms = new HashSet<>();
        for (Enrollment e : enrollments) {
            SubjectClass sc = scMap.get(e.getSubjectClassId());
            if (sc != null && sc.getSemester() != null && sc.getAcademicYear() != null) {
                distinctTerms.add(sc.getSemester() + ":::" + sc.getAcademicYear());
            }
        }

        List<TuitionFee> feeRecords = tuitionFeeRepository.findAllByStudentIdAndDeletedFalse(student.getId());
        for (TuitionFee tf : feeRecords) {
            if (tf.getSemester() != null && tf.getAcademicYear() != null) {
                distinctTerms.add(tf.getSemester() + ":::" + tf.getAcademicYear());
            }
        }

        if (distinctTerms.isEmpty()) {
            return Collections.singletonList(buildStudentTuitionSummary(student, "1", "2024-2025"));
        }

        return distinctTerms.stream().map(term -> {
            String[] parts = term.split(":::");
            return buildStudentTuitionSummary(student, parts[0], parts[1]);
        }).sorted(Comparator.comparing(StudentTuitionSummaryResponse::getAcademicYear).reversed()
                .thenComparing(StudentTuitionSummaryResponse::getSemester).reversed())
        .toList();
    }

    @Override
    @Transactional
    public StudentTuitionSummaryResponse getStudentTuitionSummary(Long studentId, String semester, String academicYear) {
        Student student = studentRepository.findByIdAndDeletedFalse(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        return buildStudentTuitionSummary(student, semester, academicYear);
    }

    @Override
    @Transactional
    public Page<StudentTuitionSummaryResponse> getAllStudentsTuition(
            String semester,
            String academicYear,
            Long classGroupId,
            TuitionStatus status,
            String search,
            Pageable pageable
    ) {
        List<Student> allStudents = studentRepository.findAllByDeletedFalse();

        if (classGroupId != null) {
            allStudents = allStudents.stream()
                    .filter(s -> Objects.equals(s.getClassGroupId(), classGroupId))
                    .toList();
        }

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            allStudents = allStudents.stream()
                    .filter(s -> (s.getStudentCode() != null && s.getStudentCode().toLowerCase().contains(q)) ||
                                 (s.getFullName() != null && s.getFullName().toLowerCase().contains(q)) ||
                                 (s.getEmail() != null && s.getEmail().toLowerCase().contains(q)))
                    .toList();
        }

        List<StudentTuitionSummaryResponse> summaries = allStudents.stream()
                .map(s -> buildStudentTuitionSummary(s, semester, academicYear))
                .filter(sum -> status == null || sum.getStatus() == status)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), summaries.size());
        List<StudentTuitionSummaryResponse> pageContent = (start <= end) ? summaries.subList(start, end) : Collections.emptyList();

        return new PageImpl<>(pageContent, pageable, summaries.size());
    }

    @Override
    @Transactional
    public TuitionDashboardSummaryResponse getDashboardSummary(String semester, String academicYear) {
        List<Student> allStudents = studentRepository.findAllByDeletedFalse();
        List<StudentTuitionSummaryResponse> summaries = allStudents.stream()
                .map(s -> buildStudentTuitionSummary(s, semester, academicYear))
                .toList();

        long totalStudents = summaries.size();
        long totalCreditsEnrolled = summaries.stream().mapToLong(s -> s.getTotalCredits() != null ? s.getTotalCredits() : 0).sum();
        long totalTuitionExpected = summaries.stream().mapToLong(s -> s.getTotalAmount() != null ? s.getTotalAmount() : 0).sum();
        long totalTuitionDiscount = summaries.stream().mapToLong(s -> s.getDiscountAmount() != null ? s.getDiscountAmount() : 0).sum();
        long totalTuitionCollected = summaries.stream().mapToLong(s -> s.getPaidAmount() != null ? s.getPaidAmount() : 0).sum();
        long totalTuitionDebt = summaries.stream().mapToLong(s -> s.getBalanceAmount() != null ? s.getBalanceAmount() : 0).sum();

        long netExpected = Math.max(0, totalTuitionExpected - totalTuitionDiscount);
        double collectionRate = netExpected > 0 ? ((double) totalTuitionCollected / netExpected) * 100.0 : 100.0;

        long paidCount = summaries.stream().filter(s -> s.getStatus() == TuitionStatus.PAID).count();
        long partiallyPaidCount = summaries.stream().filter(s -> s.getStatus() == TuitionStatus.PARTIALLY_PAID).count();
        long unpaidCount = summaries.stream().filter(s -> s.getStatus() == TuitionStatus.UNPAID).count();
        long overdueCount = summaries.stream().filter(s -> s.getStatus() == TuitionStatus.OVERDUE).count();

        return TuitionDashboardSummaryResponse.builder()
                .semester(semester)
                .academicYear(academicYear)
                .totalStudents(totalStudents)
                .totalCreditsEnrolled(totalCreditsEnrolled)
                .totalTuitionExpected(totalTuitionExpected)
                .totalTuitionDiscount(totalTuitionDiscount)
                .totalTuitionCollected(totalTuitionCollected)
                .totalTuitionDebt(totalTuitionDebt)
                .collectionRatePercent(Math.round(collectionRate * 10.0) / 10.0)
                .paidCount(paidCount)
                .partiallyPaidCount(partiallyPaidCount)
                .unpaidCount(unpaidCount)
                .overdueCount(overdueCount)
                .build();
    }

    @Override
    @Transactional
    public StudentTuitionSummaryResponse recordPayment(RecordPaymentRequest request) {
        Student student = studentRepository.findByIdAndDeletedFalse(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        String sem = (request.getSemester() != null && !request.getSemester().isBlank()) ? request.getSemester().trim() : "1";
        String year = (request.getAcademicYear() != null && !request.getAcademicYear().isBlank()) ? request.getAcademicYear().trim() : "2024-2025";

        TuitionFee fee = tuitionFeeRepository.findByStudentIdAndSemesterAndAcademicYearAndDeletedFalse(student.getId(), sem, year)
                .orElseGet(() -> {
                    TuitionFee newFee = TuitionFee.builder()
                            .studentId(student.getId())
                            .semester(sem)
                            .academicYear(year)
                            .pricePerCredit(DEFAULT_PRICE_PER_CREDIT)
                            .dueDate(LocalDate.now().plusMonths(1))
                            .paidAmount(0L)
                            .discountAmount(0L)
                            .build();
                    return tuitionFeeRepository.save(newFee);
                });

        long currentPaid = fee.getPaidAmount() != null ? fee.getPaidAmount() : 0L;
        fee.setPaidAmount(currentPaid + request.getPaymentAmount());

        if (request.getDiscountAmount() != null && request.getDiscountAmount() >= 0) {
            fee.setDiscountAmount(request.getDiscountAmount());
        }
        if (request.getDueDate() != null) {
            fee.setDueDate(request.getDueDate());
        }

        String noteAppend = String.format("[Giao dịch: +%s VNĐ qua %s - %s]",
                NumberFormat.getInstance(Locale.GERMANY).format(request.getPaymentAmount()),
                request.getPaymentMethod() != null ? request.getPaymentMethod() : "Trực tiếp",
                request.getTransactionReference() != null ? request.getTransactionReference() : LocalDate.now().toString());

        fee.setNotes((fee.getNotes() != null ? fee.getNotes() + " " : "") + noteAppend);
        fee.calculateAmounts();
        tuitionFeeRepository.save(fee);

        // Send notification to student
        try {
            if (student.getUserId() != null) {
                notificationService.sendSystemNotification(
                        "Xác nhận đóng học phí",
                        String.format("Hệ thống đã ghi nhận thanh toán %s VNĐ học phí Học kỳ %s (%s). Số tiền còn nợ: %s VNĐ.",
                                NumberFormat.getInstance(Locale.GERMANY).format(request.getPaymentAmount()),
                                sem, year,
                                NumberFormat.getInstance(Locale.GERMANY).format(fee.getBalanceAmount())),
                        NotificationType.SYSTEM,
                        NotificationPriority.NORMAL,
                        NotificationTargetType.USER,
                        String.valueOf(student.getUserId()),
                        "/finance/tuition"
                );
            }
        } catch (Exception ex) {
            log.warn("Could not send payment confirmation notification: {}", ex.getMessage());
        }

        return buildStudentTuitionSummary(student, sem, year);
    }

    private StudentTuitionSummaryResponse buildStudentTuitionSummary(Student student, String semester, String academicYear) {
        String effectiveSem = (semester != null && !semester.isBlank() && !"ALL".equalsIgnoreCase(semester.trim())) ? semester.trim() : "1";
        String effectiveYear = (academicYear != null && !academicYear.isBlank() && !"ALL".equalsIgnoreCase(academicYear.trim())) ? academicYear.trim() : "2024-2025";

        // Fetch enrollments of this student
        List<Enrollment> allEnrollments = enrollmentRepository.findAllByStudentIdAndDeletedFalse(student.getId());
        Set<Long> scIds = allEnrollments.stream().map(Enrollment::getSubjectClassId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, SubjectClass> scMap = subjectClassRepository.findAllByIdInAndDeletedFalse(scIds).stream()
                .collect(Collectors.toMap(SubjectClass::getId, Function.identity()));

        Set<Long> subjectIds = scMap.values().stream().map(SubjectClass::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Subject> subjectMap = subjectRepository.findAllByIdInAndDeletedFalse(subjectIds).stream()
                .collect(Collectors.toMap(Subject::getId, Function.identity()));

        // Filter matching enrollments for this semester & academicYear
        List<Enrollment> matchingEnrollments = allEnrollments.stream().filter(e -> {
            SubjectClass sc = scMap.get(e.getSubjectClassId());
            if (sc == null) return false;
            return matchSemester(sc.getSemester(), effectiveSem) && matchAcademicYear(sc.getAcademicYear(), effectiveYear);
        }).toList();

        List<TuitionItemResponse> items = new ArrayList<>();
        int totalCredits = 0;

        for (Enrollment e : matchingEnrollments) {
            SubjectClass sc = scMap.get(e.getSubjectClassId());
            Subject sub = (sc != null && sc.getSubjectId() != null) ? subjectMap.get(sc.getSubjectId()) : null;

            int credit = (sub != null && sub.getCredit() > 0) ? sub.getCredit() : 3;
            totalCredits += credit;
            long itemAmount = credit * DEFAULT_PRICE_PER_CREDIT;

            items.add(TuitionItemResponse.builder()
                    .subjectClassId(sc != null ? sc.getId() : null)
                    .subjectClassCode(sc != null ? sc.getSubjectClassCode() : "")
                    .subjectClassName(sc != null ? sc.getName() : "")
                    .subjectCode(sub != null ? sub.getSubjectCode() : "")
                    .subjectName(sub != null ? sub.getName() : "")
                    .credit(credit)
                    .pricePerCredit(DEFAULT_PRICE_PER_CREDIT)
                    .totalAmount(itemAmount)
                    .enrolledAt(e.getEnrolledAt() != null ? e.getEnrolledAt().toString() : null)
                    .status("Đã ghi danh")
                    .build());
        }

        long calculatedTotalAmount = totalCredits * DEFAULT_PRICE_PER_CREDIT;

        // Fetch or create TuitionFee record in DB
        TuitionFee tuitionFee = tuitionFeeRepository.findByStudentIdAndSemesterAndAcademicYearAndDeletedFalse(
                student.getId(), effectiveSem, effectiveYear).orElse(null);

        if (tuitionFee == null) {
            tuitionFee = TuitionFee.builder()
                    .studentId(student.getId())
                    .semester(effectiveSem)
                    .academicYear(effectiveYear)
                    .totalCredits(totalCredits)
                    .pricePerCredit(DEFAULT_PRICE_PER_CREDIT)
                    .totalAmount(calculatedTotalAmount)
                    .discountAmount(0L)
                    .paidAmount(0L)
                    .balanceAmount(calculatedTotalAmount)
                    .dueDate(LocalDate.now().plusMonths(1))
                    .status(calculatedTotalAmount > 0 ? TuitionStatus.UNPAID : TuitionStatus.PAID)
                    .build();
            tuitionFee = tuitionFeeRepository.save(tuitionFee);
        } else {
            // Update total credits & amounts if student registered more or canceled courses
            tuitionFee.setTotalCredits(totalCredits);
            tuitionFee.setTotalAmount(calculatedTotalAmount);
            tuitionFee.calculateAmounts();
            tuitionFee = tuitionFeeRepository.save(tuitionFee);
        }

        // Fetch class group and major names
        String classGroupCode = "";
        String classGroupName = "";
        String majorName = "";

        if (student.getClassGroupId() != null) {
            ClassGroup cg = classGroupRepository.findByIdAndDeletedFalse(student.getClassGroupId()).orElse(null);
            if (cg != null) {
                classGroupCode = cg.getClassCode() != null ? cg.getClassCode() : "";
                classGroupName = cg.getClassName() != null ? cg.getClassName() : "";
                if (cg.getMajorId() != null) {
                    Major m = majorRepository.findByIdAndDeletedFalse(cg.getMajorId()).orElse(null);
                    if (m != null) majorName = m.getName();
                }
            }
        }

        return StudentTuitionSummaryResponse.builder()
                .tuitionFeeId(tuitionFee.getId())
                .studentId(student.getId())
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .phone(student.getPhoneNumber())
                .classGroupId(student.getClassGroupId())
                .classGroupCode(classGroupCode)
                .classGroupName(classGroupName)
                .majorName(majorName)
                .semester(effectiveSem)
                .academicYear(effectiveYear)
                .totalCredits(totalCredits)
                .pricePerCredit(DEFAULT_PRICE_PER_CREDIT)
                .totalAmount(tuitionFee.getTotalAmount())
                .discountAmount(tuitionFee.getDiscountAmount())
                .paidAmount(tuitionFee.getPaidAmount())
                .balanceAmount(tuitionFee.getBalanceAmount())
                .dueDate(tuitionFee.getDueDate())
                .status(tuitionFee.getStatus())
                .statusDescription(tuitionFee.getStatus() != null ? tuitionFee.getStatus().getDescription() : "")
                .notes(tuitionFee.getNotes())
                .items(items)
                .build();
    }

    private Student resolveCurrentStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = auth.getName();
        Optional<Student> studentOpt = userRepository.findByUsername(username)
                .or(() -> userRepository.findByUsernameIgnoreCase(username))
                .flatMap(u -> studentRepository.findByUserIdAndDeletedFalse(u.getId()))
                .or(() -> studentRepository.findByStudentCodeAndDeletedFalse(username))
                .or(() -> studentRepository.findByEmailAndDeletedFalse(username));

        if (studentOpt.isPresent()) {
            return studentOpt.get();
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
        if (isAdmin) {
            return studentRepository.findAllByDeletedFalse().stream().findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        }

        throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
    }

    private boolean matchSemester(String actual, String expected) {
        if (expected == null || expected.isBlank() || "ALL".equalsIgnoreCase(expected.trim())) return true;
        if (actual == null || actual.isBlank()) return true;
        String a = actual.trim().toLowerCase().replaceAll("[^0-9a-z]", "");
        String e = expected.trim().toLowerCase().replaceAll("[^0-9a-z]", "");
        if (a.equals(e)) return true;
        if (a.endsWith(e) || e.endsWith(a)) return true;
        return actual.trim().equalsIgnoreCase(expected.trim());
    }

    private boolean matchAcademicYear(String actual, String expected) {
        if (expected == null || expected.isBlank() || "ALL".equalsIgnoreCase(expected.trim())) return true;
        if (actual == null || actual.isBlank()) return true;
        return actual.trim().equalsIgnoreCase(expected.trim());
    }
}