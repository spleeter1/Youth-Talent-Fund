package com.graduation.youthtalentfund.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class CodeGenerator {
    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Phương thức nội bộ để tạo một chuỗi ngẫu nhiên với độ dài cho trước.
     * @param length Độ dài của chuỗi cần tạo.
     * @return Một chuỗi ngẫu nhiên gồm chữ hoa và số.
     */
    private static String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALPHANUMERIC_CHARACTERS.charAt(RANDOM.nextInt(ALPHANUMERIC_CHARACTERS.length())));
        }
        return builder.toString();
    }

    /**
     * Tạo mã Người dùng (User Code).
     * Quy tắc: USR-[8 ký tự ngẫu nhiên]
     * Ví dụ: USR-A5KDE8B2
     * @return Mã người dùng mới.
     */
    public static String generateUserCode() {
        return "USR-" + generateRandomString(8);
    }

    /**
     * Tạo mã Chiến dịch (Campaign Code).
     * Quy tắc: CMP-[YYMMDD]-[6 ký tự ngẫu nhiên]
     * Ví dụ: CMP-241028-X4F9L1
     * @return Mã chiến dịch mới.
     */
    public static String generateCampaignCode() {
        String datePart = new SimpleDateFormat("yyMMdd").format(new Date());
        return "CMP-" + datePart + "-" + generateRandomString(6);
    }

    /**
     * Tạo mã Quyên góp (Donation Code).
     * Quy tắc: DON-[YYYYMMDD]-[8 ký tự ngẫu nhiên]
     * Ví dụ: DON-20241028-P9W3R7V1
     * @return Mã quyên góp mới.
     */
    public static String generateDonationCode() {
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return "DON-" + datePart + "-" + generateRandomString(8);
    }

    /**
     * Tạo mã Báo cáo (Proof Report Code).
     * Quy tắc: REP-[8 ký tự đầu của UUID]
     * Ví dụ: REP-A1B2C3D4
     * @return Mã báo cáo mới.
     */
    public static String generateReportCode() {
        String uuidPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "REP-" + uuidPart;
    }
}