import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

// import java.util.Arrays;

/**
 * Checks if the MJPEG data in a given file satisfies the requirements of
 * RFC 2435 for JPEG transmission over RTP.
 *
 * @author Emanuel Günther (s76954)
 */
public class MJpegRtpCheck {
    public static final String VERSION = "0.1.0";

    private String filename;
    private String fileExtension;
    private boolean fullParse;
    private int frameCount;
    private byte[] dataBuffer;


    public MJpegRtpCheck(String filename, boolean fullParse) {
        this.filename = filename;
        if (filename.endsWith(".mjpeg") || filename.endsWith(".mjpg")) {
            this.fileExtension = "mjpeg";
        } else if (filename.endsWith(".avi")) {
            this.fileExtension = "avi";
        } else if (filename.endsWith(".mov")) {
            this.fileExtension = "mov";
        } else {
            this.fileExtension = "invalid";
        }
        this.fullParse = fullParse;
        frameCount = 0;
    }

    public boolean run() {
        if (fileExtension == "invalid") {
            printError("File extension not recognized.");
            return false;
        }
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            printError("File does not exist or is a directory: " + filename);
            return false;
        }

        // examine format specific file data (e.g. mov)

        VideoFileBuffer in = null;
        try {
            in = new VideoFileBuffer(filename);
        } catch (FileNotFoundException e) {
            System.out.println(e);
            return false;
        }

        // JpegRtpMetadata jrm_result = new JpegRtpMetadata();
        byte[] data = null;
        // peek
        // while (in.seekToSoi()) {
        data = in.nextJpeg();
        JpegRtpMetadata jrm = JpegMetadataExtractor.extractMetadata(data);
        // compare with jrm_result (previous result(s))
        jrm.checkRtp2435Conformance();
        frameCount++;
        // }

        in.close();
        return false;
    }

    private void printError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }

    public static void main(String[] args) {
        ArgumentParser argparse = new ArgumentParser("MJpegRtpCheck", MJpegRtpCheck.VERSION);
        argparse.registerArgument("file");
        argparse.registerOption("f", "full-parse", "parse all images of the file, not just one");
        argparse.parse(args);

        String file = argparse.getString("file");
        boolean fullParse = argparse.getBoolean("full-parse");
        MJpegRtpCheck mjrc = new MJpegRtpCheck(file, fullParse);
        mjrc.run();
    }
}

