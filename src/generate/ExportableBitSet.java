package generate;

import java.util.Arrays;
import java.util.BitSet;

public class ExportableBitSet extends BitSet {

    private static final long serialVersionUID = 1L;

    public ExportableBitSet() {
        super();
    }

    public ExportableBitSet(int nbits) {
        super(nbits);
    }

    public ExportableBitSet(byte[] bytes) {
        this(bytes == null ? 0 : bytes.length * 8);
        for (int i = 0; i < size(); i++) {
            if (isBitOn(i, bytes)) {
                set(i);
            }
        }
    }

    public byte[] toByteArray() {

        if (size() == 0) {
            return new byte[0];
        }

        // Find highest bit
        int hiBit = -1;
        for (int i = 0; i < size(); i++) {
            if (get(i)) {
                hiBit = i;
            }
        }

        int n = (hiBit + 8) / 8;
        byte[] bytes = new byte[n];
        if (n == 0) {
            return bytes;
        }

        Arrays.fill(bytes, (byte) 0);
        for (int i = 0; i < n * 8; i++) {
            if (get(i)) {
                setBit(i, bytes);
            }
        }

        return bytes;
    }
    protected static int BIT_MASK[] = {0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01};

    protected static boolean isBitOn(int bit, byte[] bytes) {
        int size = bytes == null ? 0 : bytes.length * 8;

        if (bit >= size) {
            return false;
        }

        return (bytes[bit / 8] & BIT_MASK[bit % 8]) != 0;
    }

    protected static void setBit(int bit, byte[] bytes) {
        int size = bytes == null ? 0 : bytes.length * 8;

        if (bit >= size) {
            throw new ArrayIndexOutOfBoundsException("Byte array too small");
        }

        bytes[bit / 8] |= BIT_MASK[bit % 8];
    }
}