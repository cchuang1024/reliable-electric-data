package edu.nccu.cs.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtils {
    public static byte[] getBytesFromLong(final long i) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(i);
        return buffer.array();
    }

    public static byte[] mergeByteArrays(final byte[] array1, byte[] array2) {
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static long getLongFromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }
}
