package org.skyme.util;

import java.util.Random;

/**
 * @author:Skyme
 * @create: 2023-09-05 23:25
 * @Description:
 */
public class CodeGenerator {
    private static final String[] NUMBER_FORMATS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final int RANDOM_BOUND = 100000; // 最大随机数为100000
    private static final Random RANDOM = new Random();

    /**
     * 随机生成指定长度的字符串，由数字组成。
     * @param length 字符串长度，必须大于等于1。
     * @return 由数字组成的指定长度的字符串。
     * @throws IllegalArgumentException 如果length小于1，则抛出此异常。
     */
    public static String generateNumber(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Length must be greater than or equal to 1");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(NUMBER_FORMATS[RANDOM.nextInt(NUMBER_FORMATS.length)]);
        }
        return builder.toString();
    }
}
