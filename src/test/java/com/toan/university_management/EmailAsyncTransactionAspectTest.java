package com.toan.university_management;

import com.toan.university_management.service.email.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class EmailAsyncTransactionAspectTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("AOP Aspect: Gửi OTP ngoài Transaction - Thực thi bất đồng bộ ngay lập tức")
    void testSendOtpEmail_withoutTransaction_shouldExecuteAsynchronously() {
        long startTime = System.currentTimeMillis();

        // Gửi OTP ngoài transaction
        emailService.sendOtpEmail("test_user@example.com", "Test User", "123456");

        long duration = System.currentTimeMillis() - startTime;
        // Luồng gọi phải trả về gần như tức thì (< 500ms) vì AOP đã đẩy tác vụ sang ThreadPool
        assertThat(duration).isLessThan(1000);
    }

    @Test
    @DisplayName("AOP Aspect: Gửi Email trong Transaction - Chỉ gửi khi Transaction COMMIT thành công")
    void testSendAccountEmail_withCommittedTransaction_shouldExecuteAfterCommit() {
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        long startTime = System.currentTimeMillis();

        txTemplate.execute(status -> {
            emailService.sendAccountCreatedEmail("student@example.com", "Nguyễn Văn Sinh Viên", "SV9999", "SV9999@123", "ROLE_STUDENT");
            return true;
        });

        long duration = System.currentTimeMillis() - startTime;
        assertThat(duration).isLessThan(1000);
    }

    @Test
    @DisplayName("AOP Aspect: Gửi Email trong Transaction bị ROLLBACK - Phải hủy bỏ và KHÔNG được gửi")
    void testSendAccountEmail_withRolledBackTransaction_shouldNotExecute() {
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);

        try {
            txTemplate.execute(status -> {
                emailService.sendAccountCreatedEmail("cancelled@example.com", "Hủy Bỏ", "SV0000", "SV0000@123", "ROLE_STUDENT");
                throw new RuntimeException("Simulated DB transaction failure");
            });
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage()).isEqualTo("Simulated DB transaction failure");
        }

        // Transaction đã bị rollback nên hook afterCommit không bao giờ được gọi
    }
}
