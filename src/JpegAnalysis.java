import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;

import java.util.Iterator;

import org.w3c.dom.Node;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


class JpegAnalysis {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            return;
        }
        File file = new File(args[0]);
        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        if (readers.hasNext()) {
            ImageReader ir = readers.next();
            ir.setInput(iis, true);
            JPEGImageReadParam param = (JPEGImageReadParam)ir.getDefaultReadParam();
            IIOMetadata md = ir.getImageMetadata(0);

            System.out.println(param.getDCHuffmanTables());

            String[] names = md.getMetadataFormatNames();
            for (String name : names) {
                System.out.println(name);
                Node node = md.getAsTree(name);
                transformer.transform(new DOMSource(node), new StreamResult(writer));
                String xml = writer.toString();
                System.out.println(xml);
            }
        }

        return;
    }
}

