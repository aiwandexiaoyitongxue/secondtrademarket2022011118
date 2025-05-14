package com.secondtrade.common.utils;

import com.secondtrade.common.constants.Constants;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * 通用工具类
 * 包含常用的工具方法
 */
public class CommonUtils {

    /**
     * 生成随机字符串
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成订单号
     * 格式：年月日时分秒 + 6位随机字符串
     * @return 订单号
     */
    public static String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + 
               generateRandomString(6);
    }

    /**
     * 生成UUID
     * @return UUID字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成验证码
     * @param length 验证码长度
     * @return 验证码
     */
    public static String generateVerifyCode(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 计算积分
     * @param amount 金额
     * @return 积分
     */
    public static int calculatePoints(BigDecimal amount) {
        return amount.multiply(Constants.POINTS_RATE).intValue();
    }

    /**
     * 计算积分抵扣金额
     * @param points 积分
     * @return 抵扣金额
     */
    public static BigDecimal calculatePointsDiscount(int points) {
        return new BigDecimal(points).divide(Constants.POINTS_DISCOUNT_RATE, 2, BigDecimal.ROUND_DOWN);
    }

    /**
     * 计算商家手续费
     * @param amount 订单金额
     * @param level 商家等级
     * @return 手续费
     */
    public static BigDecimal calculateCommission(BigDecimal amount, int level) {
        BigDecimal rate;
        switch (level) {
            case 1:
                rate = Constants.MERCHANT_LEVEL_1_RATE;
                break;
            case 2:
                rate = Constants.MERCHANT_LEVEL_2_RATE;
                break;
            case 3:
                rate = Constants.MERCHANT_LEVEL_3_RATE;
                break;
            case 4:
                rate = Constants.MERCHANT_LEVEL_4_RATE;
                break;
            case 5:
                rate = Constants.MERCHANT_LEVEL_5_RATE;
                break;
            default:
                rate = Constants.MERCHANT_LEVEL_1_RATE;
        }
        return amount.multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
    }

    /**
     * 检查手机号格式
     * @param phone 手机号
     * @return 是否合法
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 检查邮箱格式
     * @param email 邮箱
     * @return 是否合法
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

    /**
     * 检查密码强度
     * @param password 密码
     * @return 是否合法
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
    }
}