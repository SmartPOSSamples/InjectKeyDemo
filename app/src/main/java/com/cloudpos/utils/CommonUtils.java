package com.cloudpos.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by lizhou on 18-3-2.
 */

public class CommonUtils {
    private static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public static short byte2ToShort(byte[] src, int offset) {
        if (null == src || offset < 0 || offset > src.length) {
            throw new NullPointerException("Invalid byte array ");
        }
        if ((src.length - offset) < 2) {
            throw new IndexOutOfBoundsException("Invalid offset. Source byte length: " + src.length + ", offset: " + offset);
        }
        return (short) ((src[offset + 0] & 0xFF) << 8 | (src[offset + 1] & 0xFF));
    }

    public static String toHex(byte[] src) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            sb.append(hexDigits[src[i] >>> 4 & 0x0F]);
            sb.append(hexDigits[src[i] & 0x0F]);
        }
        return sb.toString();
    }

    public static byte[] toBytes(String hex) {
        int len = hex.length() >> 1;
        byte[] d = new byte[len];
        int byteLen = len * 2;
        for (int i = 0; i < byteLen; i++) {
            int shift = (i % 2 == 1) ? 0 : 4;
            d[i >> 1] |= Character.digit(hex.charAt(i), 16) << shift;
        }
        return d;
    }

    public static void closeQuietly(Closeable io) {
        if (io == null) {
            return;
        }
        try {
            io.close();
        } catch (IOException e) {
            // Do nothing
        }
    }

    public static byte[] subBytes(byte[] src, int srcPos, int length) {
        byte[] bs = new byte[length];
        System.arraycopy(src, srcPos, bs, 0, length);
        return bs;
    }

    public static void append(byte[] src, byte[] dest, int offset) {
        if (dest.length < offset + src.length) {

        }
        System.arraycopy(src, 0, dest, offset, src.length);
    }

    public static byte[] append(byte[]...srcs) {
        int len = 0;
        for (byte[] src : srcs) {
            len += src.length;
        }
        byte[] result = new byte[len];
        int pos = 0;
        for (byte[] src : srcs) {
            System.arraycopy(src, 0, result, pos, src.length);
            pos += src.length;
        }
        return result;
    }
}
