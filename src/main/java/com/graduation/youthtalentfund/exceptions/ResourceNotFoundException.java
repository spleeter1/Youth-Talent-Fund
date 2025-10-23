package com.graduation.youthtalentfund.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor tiện lợi để tạo thông báo lỗi chuẩn hóa.
     * @param resourceName Tên của loại tài nguyên (ví dụ: "User", "Campaign").
     * @param fieldName Tên của trường dùng để tìm kiếm (ví dụ: "id", "email").
     * @param fieldValue Giá trị của trường tìm kiếm.
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s không được tìm thấy với %s: '%s'", resourceName, fieldName, fieldValue));
    }
}