import java.util.Arrays;

/**
 * Structure for JPEG metadata required to check conformance with RFC 2435.
 *
 * @author Emanuel Günther (s76954)
 */
class JpegRtpMetadata {
    public boolean baseline; // process = 0
    public boolean huffman;
    public int numSOS; // single scan: = 1
    public int width;
    public int height;
    public int samplePrecision;
    public double pixelAspectRatio;
    public int numFrameComponents;
    public int numDQT;
    public int numDHT;
    public byte[] huffmanLumDc;
    public byte[] huffmanLumAc;
    public byte[] huffmanChmDc;
    public byte[] huffmanChmAc;
    public byte[][] subsampling;

    private static final String ANSI_ESCAPE_RESET = "\u001B[0m";
    private static final String ANSI_ESCAPE_RED = "\u001B[31m";
    private static final String ANSI_ESCAPE_GREEN = "\u001B[32m";

    /* huffman codelens and symbols taken from RFC2435 Appendix B,
     * complient with ISO10918-1 Annex K.3 */
    private static byte[] LUM_DC_CODELENS = {
        (byte) 0x00, (byte) 0x01, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
        (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    private static byte[] LUM_DC_SYMBOLS = {
        (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07,
        (byte) 0x08, (byte) 0x09, (byte) 0x0a, (byte) 0x0b };

    private static byte[] LUM_AC_CODELENS = {
        (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x03, (byte) 0x03, (byte) 0x02, (byte) 0x04, (byte) 0x03,
        (byte) 0x05, (byte) 0x05, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x7d };

    private static byte[] LUM_AC_SYMBOLS = {
        (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x11, (byte) 0x05, (byte) 0x12,
        (byte) 0x21, (byte) 0x31, (byte) 0x41, (byte) 0x06, (byte) 0x13, (byte) 0x51, (byte) 0x61, (byte) 0x07,
        (byte) 0x22, (byte) 0x71, (byte) 0x14, (byte) 0x32, (byte) 0x81, (byte) 0x91, (byte) 0xa1, (byte) 0x08,
        (byte) 0x23, (byte) 0x42, (byte) 0xb1, (byte) 0xc1, (byte) 0x15, (byte) 0x52, (byte) 0xd1, (byte) 0xf0,
        (byte) 0x24, (byte) 0x33, (byte) 0x62, (byte) 0x72, (byte) 0x82, (byte) 0x09, (byte) 0x0a, (byte) 0x16,
        (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x1a, (byte) 0x25, (byte) 0x26, (byte) 0x27, (byte) 0x28,
        (byte) 0x29, (byte) 0x2a, (byte) 0x34, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x39,
        (byte) 0x3a, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48, (byte) 0x49,
        (byte) 0x4a, (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58, (byte) 0x59,
        (byte) 0x5a, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x67, (byte) 0x68, (byte) 0x69,
        (byte) 0x6a, (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77, (byte) 0x78, (byte) 0x79,
        (byte) 0x7a, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89,
        (byte) 0x8a, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96, (byte) 0x97, (byte) 0x98,
        (byte) 0x99, (byte) 0x9a, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4, (byte) 0xa5, (byte) 0xa6, (byte) 0xa7,
        (byte) 0xa8, (byte) 0xa9, (byte) 0xaa, (byte) 0xb2, (byte) 0xb3, (byte) 0xb4, (byte) 0xb5, (byte) 0xb6,
        (byte) 0xb7, (byte) 0xb8, (byte) 0xb9, (byte) 0xba, (byte) 0xc2, (byte) 0xc3, (byte) 0xc4, (byte) 0xc5,
        (byte) 0xc6, (byte) 0xc7, (byte) 0xc8, (byte) 0xc9, (byte) 0xca, (byte) 0xd2, (byte) 0xd3, (byte) 0xd4,
        (byte) 0xd5, (byte) 0xd6, (byte) 0xd7, (byte) 0xd8, (byte) 0xd9, (byte) 0xda, (byte) 0xe1, (byte) 0xe2,
        (byte) 0xe3, (byte) 0xe4, (byte) 0xe5, (byte) 0xe6, (byte) 0xe7, (byte) 0xe8, (byte) 0xe9, (byte) 0xea,
        (byte) 0xf1, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7, (byte) 0xf8,
        (byte) 0xf9, (byte) 0xfa };

    private static byte[] CHM_DC_CODELENS = {
        (byte) 0x00, (byte) 0x03, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
        (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    private static byte[] CHM_DC_SYMBOLS = {
        (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07,
        (byte) 0x08, (byte) 0x09, (byte) 0x0a, (byte) 0x0b };

    private static byte[] CHM_AC_CODELENS = {
        (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x04, (byte) 0x03, (byte) 0x04,
        (byte) 0x07, (byte) 0x05, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x77 };

    private static byte[] CHM_AC_SYMBOLS = {
        (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x11, (byte) 0x04, (byte) 0x05, (byte) 0x21,
        (byte) 0x31, (byte) 0x06, (byte) 0x12, (byte) 0x41, (byte) 0x51, (byte) 0x07, (byte) 0x61, (byte) 0x71,
        (byte) 0x13, (byte) 0x22, (byte) 0x32, (byte) 0x81, (byte) 0x08, (byte) 0x14, (byte) 0x42, (byte) 0x91,
        (byte) 0xa1, (byte) 0xb1, (byte) 0xc1, (byte) 0x09, (byte) 0x23, (byte) 0x33, (byte) 0x52, (byte) 0xf0,
        (byte) 0x15, (byte) 0x62, (byte) 0x72, (byte) 0xd1, (byte) 0x0a, (byte) 0x16, (byte) 0x24, (byte) 0x34,
        (byte) 0xe1, (byte) 0x25, (byte) 0xf1, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x1a, (byte) 0x26,
        (byte) 0x27, (byte) 0x28, (byte) 0x29, (byte) 0x2a, (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38,
        (byte) 0x39, (byte) 0x3a, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48,
        (byte) 0x49, (byte) 0x4a, (byte) 0x53, (byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58,
        (byte) 0x59, (byte) 0x5a, (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x67, (byte) 0x68,
        (byte) 0x69, (byte) 0x6a, (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77, (byte) 0x78,
        (byte) 0x79, (byte) 0x7a, (byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87,
        (byte) 0x88, (byte) 0x89, (byte) 0x8a, (byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96,
        (byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0x9a, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4, (byte) 0xa5,
        (byte) 0xa6, (byte) 0xa7, (byte) 0xa8, (byte) 0xa9, (byte) 0xaa, (byte) 0xb2, (byte) 0xb3, (byte) 0xb4,
        (byte) 0xb5, (byte) 0xb6, (byte) 0xb7, (byte) 0xb8, (byte) 0xb9, (byte) 0xba, (byte) 0xc2, (byte) 0xc3,
        (byte) 0xc4, (byte) 0xc5, (byte) 0xc6, (byte) 0xc7, (byte) 0xc8, (byte) 0xc9, (byte) 0xca, (byte) 0xd2,
        (byte) 0xd3, (byte) 0xd4, (byte) 0xd5, (byte) 0xd6, (byte) 0xd7, (byte) 0xd8, (byte) 0xd9, (byte) 0xda,
        (byte) 0xe2, (byte) 0xe3, (byte) 0xe4, (byte) 0xe5, (byte) 0xe6, (byte) 0xe7, (byte) 0xe8, (byte) 0xe9,
        (byte) 0xea, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6, (byte) 0xf7, (byte) 0xf8,
        (byte) 0xf9, (byte) 0xfa };


    public JpegRtpMetadata() {
        baseline = false;
        huffman = false;
        numSOS = 0;
        width = 0;
        height = 0;
        samplePrecision = 0;
        pixelAspectRatio = 0.0;
        numFrameComponents = 0;
        numDQT = 0;
        numDHT = 0;
        huffmanLumDc = null;
        huffmanLumAc = null;
        huffmanChmDc = null;
        huffmanChmAc = null;
        subsampling = null;
    }

    public boolean checkRtp2435Conformance() {
        JpegRtpMetadata req = rtp2435ConformantData();
        boolean conformant = true;

        printTestResult(baseline == req.baseline, "Baseline DCT sequential");
        if (baseline != req.baseline) {
            conformant = false;
        }

        printTestResult(huffman == req.huffman, "Huffman entropy coding");
        if (huffman != req.huffman) {
            conformant = false;
        }

        printTestResult(numSOS == req.numSOS, "single-scan, interleaved");
        if (numSOS != req.numSOS) {
            conformant = false;
        }

        printTestResult(width <= req.width, "width <= " + req.width);
        if (width > req.width) {
            conformant = false;
        }

        printTestResult(height <= req.height, "height <= " + req.height);
        if (height > req.height) {
            conformant = false;
        }

        printTestResult(samplePrecision == req.samplePrecision,
                "sample precision " + req.samplePrecision + " bit");
        if (samplePrecision != req.samplePrecision) {
            conformant = false;
        }

        printTestResult(pixelAspectRatio == req.pixelAspectRatio,
                "pixel aspect ratio = " + req.pixelAspectRatio);
        if (pixelAspectRatio != req.pixelAspectRatio) {
            conformant = false;
        }

        printTestResult(numFrameComponents == req.numFrameComponents,
                "number of components in frame = " + req.numFrameComponents);
        if (numFrameComponents != req.numFrameComponents) {
            conformant = false;
        }

        printTestResult(numDQT > 0 && numDQT <= req.numDQT,
                "number of quantization tables = [1, " + req.numDQT + "]");
        if (numDQT == 0 || numDQT > req.numDQT) {
            conformant = false;
        }

        printTestResult(numDHT > 0 && numDHT <= req.numDHT,
                "number of huffman tables = [1, " + req.numDHT + "]");
        if (numDHT == 0 || numDHT > req.numDHT) {
            conformant = false;
        }

        boolean huffmanTableCheck = true;
        if (!Arrays.equals(req.huffmanLumDc, huffmanLumDc)
                || !Arrays.equals(req.huffmanLumAc, huffmanLumAc)
                || !Arrays.equals(req.huffmanChmDc, huffmanChmDc)
                || !Arrays.equals(req.huffmanChmAc, huffmanChmAc)) {
            huffmanTableCheck = false;
        }
        printTestResult(huffmanTableCheck, "Huffman tables are conform with Annex K.3");

        boolean subsmplCheck = false;
        if (subsampling != null) {
            for (int i = 0; i < req.subsampling.length; i++) {
                if (subsampling[0][0] == req.subsampling[i][0]
                        && subsampling[0][1] == req.subsampling[i][1]
                        && subsampling[0][2] == req.subsampling[i][2]) {
                    subsmplCheck = true;
                    break;
                }
            }
        }
        printTestResult(subsmplCheck, "subsampling 4:2:2 or 4:2:0");
        if (!subsmplCheck) {
            conformant = false;
        }

        System.out.println("====================================");
        System.out.println("JPEG is " + (conformant ? "" : "not ") + "conformant to RFC 2435");

        return conformant;
    }

    public boolean isEqual(JpegRtpMetadata jrm) {
        boolean equal = true;

        if (baseline != jrm.baseline) {
            equal = false;
        }
        if (huffman != jrm.huffman) {
            equal = false;
        }
        if (numSOS != jrm.numSOS) {
            equal = false;
        }
        if (width != jrm.width) {
            equal = false;
        }
        if (height != jrm.height) {
            equal = false;
        }
        if (samplePrecision != jrm.samplePrecision) {
            equal = false;
        }
        if (pixelAspectRatio != jrm.pixelAspectRatio) {
            equal = false;
        }
        if (numFrameComponents != jrm.numFrameComponents) {
            equal = false;
        }
        if (numDQT != jrm.numDQT) {
            equal = false;
        }
        if (numDHT != jrm.numDHT) {
            equal = false;
        }

        if (!Arrays.equals(jrm.huffmanLumDc, huffmanLumDc)
                || !Arrays.equals(jrm.huffmanLumAc, huffmanLumAc)
                || !Arrays.equals(jrm.huffmanChmDc, huffmanChmDc)
                || !Arrays.equals(jrm.huffmanChmAc, huffmanChmAc)) {
            equal = false;
        }

        if (subsampling[0][0] != jrm.subsampling[0][0]
                || subsampling[0][1] != jrm.subsampling[0][1]
                || subsampling[0][2] != jrm.subsampling[0][2]) {
            equal = false;
        }

        return equal;
    }

    public static void printTestResult(boolean passed, String info) {
        String out = "";
        if (passed) {
            out += ANSI_ESCAPE_GREEN;
            out += "[  PASSED  ]  " + info;
        } else {
            out += ANSI_ESCAPE_RED;
            out += "[  FAILED  ]  " + info;
        }
        out += ANSI_ESCAPE_RESET;
        System.out.println(out);
    }

    private static JpegRtpMetadata rtp2435ConformantData() {
        JpegRtpMetadata jrm = new JpegRtpMetadata();
        jrm.baseline = true;
        jrm.huffman = true;
        jrm.numSOS = 1;
        jrm.width = 2040;
        jrm.height = 2040;
        jrm.samplePrecision = 8;
        jrm.pixelAspectRatio = 1.0;
        jrm.numFrameComponents = 3;
        jrm.numDQT = 2;
        jrm.numDHT = 4;
        jrm.huffmanLumDc = new byte[LUM_DC_CODELENS.length + LUM_DC_SYMBOLS.length];
        System.arraycopy(LUM_DC_CODELENS, 0, jrm.huffmanLumDc, 0, LUM_DC_CODELENS.length);
        System.arraycopy(LUM_DC_SYMBOLS, 0, jrm.huffmanLumDc, LUM_DC_CODELENS.length, LUM_DC_SYMBOLS.length);
        jrm.huffmanLumAc = new byte[LUM_AC_CODELENS.length + LUM_AC_SYMBOLS.length];
        System.arraycopy(LUM_AC_CODELENS, 0, jrm.huffmanLumAc, 0, LUM_AC_CODELENS.length);
        System.arraycopy(LUM_AC_SYMBOLS, 0, jrm.huffmanLumAc, LUM_AC_CODELENS.length, LUM_AC_SYMBOLS.length);
        jrm.huffmanChmDc = new byte[CHM_DC_CODELENS.length + CHM_DC_SYMBOLS.length];
        System.arraycopy(CHM_DC_CODELENS, 0, jrm.huffmanChmDc, 0, CHM_DC_CODELENS.length);
        System.arraycopy(CHM_DC_SYMBOLS, 0, jrm.huffmanChmDc, CHM_DC_CODELENS.length, CHM_DC_SYMBOLS.length);
        jrm.huffmanChmAc = new byte[CHM_AC_CODELENS.length + CHM_AC_SYMBOLS.length];
        System.arraycopy(CHM_AC_CODELENS, 0, jrm.huffmanChmAc, 0, CHM_AC_CODELENS.length);
        System.arraycopy(CHM_AC_SYMBOLS, 0, jrm.huffmanChmAc, CHM_AC_CODELENS.length, CHM_AC_SYMBOLS.length);
        jrm.subsampling = new byte[][]{
            {4, 2, 2},
            {4, 2, 0}
        };
        return jrm;
    }
}

