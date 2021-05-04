import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.Integer;
import java.util.Arrays;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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
                if (name != "javax_imageio_jpeg_image_1.0") {
                    // just use one instance and do not process multiple copies
                    continue;
                }
                Node node = md.getAsTree(name);
                boolean success = JpegMetadataExtractor.parseMetadataTree(node, jrm);

            }
        }

        extractHuffmanTables(jrm, data);

        return jrm;
    }

    private static void extractHuffmanTables(JpegRtpMetadata jrm, byte[] data) {
        int idx = 1;
        while (idx < data.length) {
            if (data[idx-1] == VideoFileBuffer.JPEG_MARKER && data[idx] == VideoFileBuffer.JPEG_DHT) {
                idx++; // point to length

                /*
                 * The bytes need to be masked because of the implicit promotion to type (signed) int.
                 * The promotion does sign-extension rather than zero-extension and that is why the
                 * masking is needed.
                 */
                int length = (data[idx] & 0xFF) << 8 | (data[idx+1] & 0xFF);
                idx += 2;
                length -= 2; // exclude length parameter itself

                int tmpIdx = idx;
                do {
                    int tc = (data[tmpIdx] & 0xF0) >> 4; // table class
                    int th = (data[tmpIdx] & 0x0F); // huffman destination id
                    tmpIdx++;

                    int numEntries = 0;
                    for (int i = 0; i < 16; i++) { // there are 16 entries
                        numEntries += data[tmpIdx+i];
                    }

                    byte[] tbl = Arrays.copyOfRange(data, tmpIdx, tmpIdx + 16 + numEntries);
                    if (tc == 0) { // DC
                        if (th == 0) { // lum according to RFC 2435
                            jrm.huffmanLumDc = tbl;
                        } else if (th == 1) { // chr according to RFC 2435
                            jrm.huffmanChmDc = tbl;
                        }
                        // else is not supported
                    } else if (tc == 1) { // AC
                        if (th == 0) { // lum according to RFC 2435
                            jrm.huffmanLumAc = tbl;
                        } else if (th == 1) { // chr according to RFC 2435
                            jrm.huffmanChmAc = tbl;
                        }
                        // else is not supported
                    }
                    // else is not supported

                    tmpIdx += 16 + numEntries;
                } while (tmpIdx - idx < length);
                idx = tmpIdx;
            }
            idx++; // ??
        }
    }

    private static void parseDht(Node node, JpegRtpMetadata jrm) {
        NodeList list = node.getChildNodes();
        jrm.numDHT = list.getLength();
        jrm.huffman = true;
    }

    private static void parseDqt(Node node, JpegRtpMetadata jrm) {
        NodeList list = node.getChildNodes();
        jrm.numDQT = list.getLength();
    }

    private static void parseJpegVariety(Node node, JpegRtpMetadata jrm) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            if (n.getNodeName() == "app0JFIF") {
                NamedNodeMap map = n.getAttributes();
                int xdens = Integer.parseInt(map.getNamedItem("Xdensity").getNodeValue());
                int ydens = Integer.parseInt(map.getNamedItem("Ydensity").getNodeValue());
                jrm.pixelAspectRatio = (double)xdens/ydens;
            }
        }
    }

    private static void parseMarkerSequence(Node node, JpegRtpMetadata jrm) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            switch (n.getNodeName()) {
                case "dqt":
                    JpegMetadataExtractor.parseDqt(n, jrm);
                    break;
                case "dht":
                    JpegMetadataExtractor.parseDht(n, jrm);
                    break;
                case "sof":
                    JpegMetadataExtractor.parseSof(n, jrm);
                    break;
                case "sos":
                    JpegMetadataExtractor.parseSos(n, jrm);
                    break;
            }
        }
    }

    private static boolean parseMetadataTree(Node node, JpegRtpMetadata jrm) {
        String name = node.getNodeName();

        switch (name) {
            case "javax_imageio_jpeg_image_1.0":
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    JpegMetadataExtractor.parseMetadataTree(list.item(i), jrm);
                }
                break;
            case "JPEGvariety":
                JpegMetadataExtractor.parseJpegVariety(node, jrm);
                break;
            case "markerSequence":
                JpegMetadataExtractor.parseMarkerSequence(node, jrm);
                break;
        }

        return true;
    }

    private static void parseSof(Node node, JpegRtpMetadata jrm) {
        NamedNodeMap attrs = node.getAttributes();
        int process = Integer.parseInt(attrs.getNamedItem("process").getNodeValue());
        jrm.baseline = process == 0 ? true : false;
        jrm.samplePrecision = Integer.parseInt(attrs.getNamedItem("samplePrecision").getNodeValue());
        jrm.width = Integer.parseInt(attrs.getNamedItem("samplesPerLine").getNodeValue());
        jrm.height = Integer.parseInt(attrs.getNamedItem("numLines").getNodeValue());
        jrm.numFrameComponents = Integer.parseInt(attrs.getNamedItem("numFrameComponents").getNodeValue());

        byte[][] samplFact = new byte[3][2]; // three components, horizontal and vertical
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            NamedNodeMap compSpecs = list.item(i).getAttributes();
            int id = Integer.parseInt(compSpecs.getNamedItem("componentId").getNodeValue());
            id--;
            samplFact[id][0] = (byte)Integer.parseInt(compSpecs.getNamedItem("HsamplingFactor").getNodeValue());
            samplFact[id][1] = (byte)Integer.parseInt(compSpecs.getNamedItem("VsamplingFactor").getNodeValue());
        }

        jrm.subsampling = new byte[1][3];
        jrm.subsampling[0][0] = 4;
        for (int j = 1; j < 3; j++) {
            double hfract = (double) samplFact[j][0] / samplFact[0][0];
            double vfract = (double) samplFact[j][1] / samplFact[0][1];
            if (hfract == 0.5 && vfract == 0.5) {
                jrm.subsampling[0][j] = 0; // indicator for 4:2:0
            } else {
                jrm.subsampling[0][j] = (byte)(hfract * jrm.subsampling[0][0]);
            }
        }
        if (jrm.subsampling[0][1] == 0 && jrm.subsampling[0][2] == 0) {
            // 4:2:0 recognized
            jrm.subsampling[0][1] = 2;
        }
    }

    private static void parseSos(Node node, JpegRtpMetadata jrm) {
        jrm.numSOS++;
    }
}

