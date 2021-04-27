import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.w3c.dom.Node;

import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * Extracts Metadata from an JPEG image.
 *
 * @author Emanuel Günther (s76954)
 */
public class JpegMetadataExtractor {
    private JpegMetadataExtractor() {
    }

    public static JpegRtpMetadata extractMetadata(byte[] data) {
        JpegRtpMetadata jrm = new JpegRtpMetadata();

        try {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        MemoryCacheImageInputStream stream = new MemoryCacheImageInputStream(bais);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
        if (readers.hasNext()) {
            ImageReader ir = readers.next();
            ir.setInput(stream, true);
            IIOMetadata md = null;
            try {
                md = ir.getImageMetadata(0);
            } catch (IOException e) {
                System.out.println(e);
            }
            String[] names = md.getMetadataFormatNames();
            for (String name : names) {
                Node node = md.getAsTree(name);

                // todo: extract from xml
                transformer.transform(new DOMSource(node), new StreamResult(writer));
                String xml = writer.toString();
                System.out.println(xml);
            }

        }
        } catch (Exception e) {}

        return jrm;
    }
}

