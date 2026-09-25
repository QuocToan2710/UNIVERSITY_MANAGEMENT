package com.toan.university_management.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Executor;

/**
 * AOP Aspect chặn các lệnh gọi gửi email:
 * 1. Bất đồng bộ hóa (Async): Chuyển tác vụ gửi email sang ThreadPool mailTaskExecutor, giúp HTTP Controller trả về ngay lập tức (~20ms).
 * 2. An toàn CSDL (Transaction Safe): Nếu phát hiện Transaction DB đang mở (như khi tạo Sinh viên / Giảng viên),
 *    email sẽ được hoãn (deferred) và chỉ bắn đi SAU KHI Transaction COMMIT thành công (AFTER_COMMIT).
 *    Nếu Transaction bị Rollback, email sẽ bị hủy bỏ hoàn toàn, không bao giờ gửi email rác.
 */
@Aspect
@Component
@Order(10)
@Slf4j
public class EmailAsyncTransactionAspect {

    private final Executor mailTaskExecutor;

    public EmailAsyncTransactionAspect(@Qualifier("mailTaskExecutor") Executor mailTaskExecutor) {
        this.mailTaskExecutor = mailTaskExecutor;
    }

    /**
     * Intercept tất cả các method của EmailService hoặc bất kỳ method nào được gắn @AsyncTransactionalEmail
     */
    @Pointcut("execution(* com.toan.university_management.service.email.EmailService.*(..)) || " +
              "@annotation(com.toan.university_management.annotation.AsyncTransactionalEmail)")
    public void emailPointcut() {}

    @Around("emailPointcut()")
    public Object interceptEmail(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        Runnable emailTask = () -> {
            try {
                log.info("[AOP-EMAIL] Bat dau gui email bat dong bo: {} tren luong [{}]",
                        methodName, Thread.currentThread().getName());
                joinPoint.proceed();
                log.info("[AOP-EMAIL] Hoan tat xu ly email: {}", methodName);
            } catch (Throwable ex) {
                log.error("[AOP-EMAIL] Loi khi gui email bat dong bo cho {}: {}", methodName, ex.getMessage(), ex);
            }
        };

        // Kiem tra xem hien tai co Transaction CSDL dang hoat dong hay khong
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            log.info("[AOP-EMAIL] Phat hien Transaction CSDL dang mo cho {}. Dang ky hook gui sau khi COMMIT.", methodName);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    log.info("[AOP-EMAIL] DB Transaction da COMMIT thanh cong. Day task vao mailTaskExecutor cho {}.", methodName);
                    mailTaskExecutor.execute(emailTask);
                }
            });
            return null;
        } else {
            log.info("[AOP-EMAIL] Khong co DB Transaction. Day truc tiep vao mailTaskExecutor cho {}.", methodName);
            mailTaskExecutor.execute(emailTask);
            return null;
        }
    }
}
