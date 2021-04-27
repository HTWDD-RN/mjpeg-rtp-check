import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;


/**
 * Buffer for reading multiple JPEGs or metadata from a file.
 * 
 * The buffer reads from a file and stores the remaining data
 * in a temporary buffer.
 */
public class VideoFileBuffer {
    private RandomAccessFile in = null;
    private byte[] buffer = null;
    private int bufferSize = 0; // size of whole buffer
    private int bufferOffset = 0; // for reading new data
    private boolean eof = false;

    // public static final byte JPEG_ZERO = 0x00;
    public static final byte JPEG_MARKER = (byte)0xFF;
    public static final byte JPEG_SOI = (byte)0xD8;
    public static final byte JPEG_EOI = (byte)0xD9;

    /*
    public static final byte JPEG_SOS = 0xDA;
    public static final byte JPEG_DQT = 0x;
    public static final byte JPEG_DHT = 0x;
    */

    /**
     * Create a VideoFileBuffer for reading image data and video meta data
     * from a file.
     *
     * @param filename name of the video file
     * @throws FileNotFoundException if file does not exist
     */
    public VideoFileBuffer(String filename) throws FileNotFoundException {
        in = new RandomAccessFile(filename, "r");
        bufferSize = 65536;
        buffer = new byte[bufferSize];
        fillBuffer();
    }

    /**
     * Close all streams.
     */
    public void close() {
        in = null;
        buffer = null;
        bufferSize = 0;
        bufferOffset = 0;
        eof = true;
    }

    /**
     * Get the next JPEG image from the file.
     *
     * @return JPEG image as byte[]
     */
    public byte[] nextJpeg() {
        if (!seekToSoi()) {
            return null;
        }

        byte[] data = null;
        int eoiPos = -1;

        do {
            for (int i = 0; i < bufferOffset-1; i++) {
                if (buffer[i] == JPEG_MARKER) {
                    if (buffer[i+1] == JPEG_EOI) {
                        eoiPos = i;
                        break;
                    }
                }
            }
            if (eof) {
                break;
            }
            if (eoiPos == -1) {
                byte[] newData = new byte[data.length + bufferOffset];
                System.arraycopy(data, 0, newData, 0, data.length);
                System.arraycopy(buffer, 0, newData, data.length, bufferOffset);
                data = newData;
                bufferOffset = 0;
                fillBuffer();
            }
        } while (eoiPos == -1);

        if (eof) {
            return null;
        }

        if (eoiPos == -1) {
            return null;
        }

        eoiPos += 2; // include EOI marker in data
        byte[] newData = new byte[data.length + eoiPos];
        System.arraycopy(data, 0, newData, 0, data.length);
        System.arraycopy(buffer, 0, newData, data.length, eoiPos);
        data = newData;

        buffer = Arrays.copyOfRange(buffer, eoiPos, eoiPos + bufferSize);
        bufferOffset = bufferSize - eoiPos;
        fillBuffer();

        return data;
    }

    /**
     * Search for JPEG SOI maker and place it at the begin of the buffer.
     *
     * @return true if SOI marker found, false otherwise
     */
    public boolean seekToSoi() {
        int soiPos = -1;
        
        do {
            for (int i = 0; i < bufferOffset-1; i++) {
                if (buffer[i] == JPEG_MARKER) {
                    if (buffer[i+1] == JPEG_SOI) {
                        soiPos = i;
                        break;
                    }
                }
            }
            if (eof) {
                break;
            }
            if (soiPos == -1) {
                bufferOffset = 0;
                fillBuffer();
            }
        } while (soiPos == -1);

        if (soiPos == -1) {
            return false;
        }

        if (soiPos != 0) {
            buffer = Arrays.copyOfRange(buffer, soiPos, soiPos + bufferSize);
            bufferOffset = bufferSize - soiPos;
            fillBuffer();
        }

        return true;
    }

    private boolean fillBuffer() {
        if (buffer == null) {
            return false;
        }
        if (bufferOffset == bufferSize) {
            return false;
        }
        if (eof) {
            return false;
        }

        while (bufferOffset < bufferSize) {
            int readBytes = 0;
            try {
                readBytes = in.read(buffer, bufferOffset, bufferSize-bufferOffset);
            } catch (IOException e) {
                System.out.println(e);
                return false;
            }
            if (readBytes == -1) {
                eof = true;
                break;
            }
            bufferOffset += readBytes;
        }

        return true;
    }
}

