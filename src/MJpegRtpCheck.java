import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

// import java.util.Arrays;

/**
 * Checks if the MJPEG data in a given file satisfies the requirements of
 * RFC 2435 for JPEG transmission over RTP.
 *
 * @author Emanuel Günther (s76954)
 */
public class MJpegRtpCheck {
    private String filename;
    private String fileExtension;
    private int frameCount;
    private byte[] dataBuffer;


    public MJpegRtpCheck(String filename) {
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

        // read in data
        RandomAccessFile in = null;
        try {
            in = new RandomAccessFile(filename, "r");
        } catch (FileNotFoundException e) {
            System.out.println(e);
            return false;
        }
        JpegRtpMetadata jrm_result = new JpegRtpMetadata();
        byte[] data = null;
        while (seekToSoi(in, data)) {
            JpegRtpMetadata jrm = JpegValidator.validateRtp(data);
            // compare with jrm_result (previous result(s))
            frameCount++;
        }

        try {
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return false;
    }

    private void printError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }

    private boolean seekToSoi(RandomAccessFile in, byte[] data) {
        // read file into dataBuffer
        // search for soi and eoi
        // data = new byte[required_size];
        // write jpeg to data
        // write remaining read data to dataBuffer (without offset)
        // return true, if successful

        // long currentOffset = in.getFilePointer();
        // void in.seek(offset); // starting from file start
        return false;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: MJpegRtpCheck <file>");
            return;
        }

        MJpegRtpCheck mjrc = new MJpegRtpCheck(args[0]);
        mjrc.run();
    }
}

