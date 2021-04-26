/**
 * Validates JPEG data to fulfill the requirements in RFC2435.
 *
 * @author Emanuel Günther (s76954)
 */
public class JpegValidator {
    private JpegValidator() {
    }

    public static JpegRtpMetadata validateRtp(byte[] data) {
        JpegRtpMetadata jrm = new JpegRtpMetadata();
        return jrm;
    }
}

