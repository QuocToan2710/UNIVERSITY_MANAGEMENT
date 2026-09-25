package com.toan.university_management.annotation;

import java.lang.annotation.*;

/**
 * Annotation đánh dấu các phương thức gửi email/thông báo cần được xử lý qua AOP:
 * 1. Bất đồng bộ (Async) trên ThreadPool riêng, không block HTTP Controller thread.
 * 2. Tự động đồng bộ hóa với Database Transaction (chỉ gửi thư đi sau khi Transaction COMMIT thành công).
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncTransactionalEmail {
}
