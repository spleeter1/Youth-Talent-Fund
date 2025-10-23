package com.graduation.youthtalentfund.constants;

public final class MessageConstants {

    // Constructor private để ngăn việc tạo instance của lớp tiện ích này
    private MessageConstants() {}

    // --- Thông báo Lỗi (Error Messages) ---

    // Xác thực
    public static final String EMAIL_IN_USE = "Lỗi: Email '%s' đã được sử dụng.";
    public static final String INVALID_CREDENTIALS = "Thông tin đăng nhập không chính xác.";

    // Truy cập Dữ liệu
    public static final String USER_NOT_FOUND = "Không tìm thấy người dùng với email: %s";
    public static final String ROLE_NOT_FOUND_SYSTEM = "Lỗi hệ thống: Vai trò '%s' không được tìm thấy.";
    public static final String RESOURCE_NOT_FOUND = "%s không được tìm thấy với %s: '%s'";

    // Hệ thống
    public static final String UNEXPECTED_ERROR = "Hệ thống đã gặp phải một lỗi không mong muốn. Vui lòng thử lại sau.";

    // --- Thông báo Thành công (Success Messages) ---
    public static final String USER_REGISTERED_SUCCESS = "Đăng ký người dùng thành công!";

}
