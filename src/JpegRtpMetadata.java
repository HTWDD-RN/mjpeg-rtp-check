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
    public byte[][] huffmanTables;
    public byte[][] subsampling;

    private static final String ANSI_ESCAPE_RESET = "\u001B[0m";
    private static final String ANSI_ESCAPE_RED = "\u001B[31m";
    private static final String ANSI_ESCAPE_GREEN = "\u001B[32m";

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
        huffmanTables = null;
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

        // todo: content of huffman tables

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

    private static void printTestResult(boolean passed, String info) {
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
        jrm.huffmanTables = null; // not implemented yet
        jrm.subsampling = new byte[][]{
            {4, 2, 2},
            {4, 2, 0}
        };
        return jrm;
    }
}

