package edu.nccu.cs.simmeter.util;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ByteUtilsTest {

    @Test
    public void testLongToByteArray() throws IOException {
        long a = 0L;
        byte[] arr = ByteUtils.getBytesFromLong(a);
        assertThat(arr).hasSize(8);
    }

    @Test
    public void testAddALl() {
        byte[] a = new byte[]{0, 0, 0, 0};
        byte[] b = new byte[]{1, 1, 1, 1};
        byte[] c = ByteUtils.mergeByteArrays(a, b);
        assertThat(c).hasSize(8);
        assertThat(c).containsExactly(new byte[]{0, 0, 0, 0, 1, 1, 1, 1});
    }
}
