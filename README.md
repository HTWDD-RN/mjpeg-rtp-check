# Motion JPEG RTP Check

A program which analyses a MJPEG file for suiting the requirements of the RTP stream defined in [RFC 2435](https://tools.ietf.org/html/rfc2435).

## Constraints of RFC 2435

| RFC section | constraint                                      | location in JFIF data                    |
|------------:|:------------------------------------------------|:-----------------------------------------|
|          2. | Baseline DCT sequential,  Huffman coding        | SOF0 (0xffc0), no other SOF-Marker       |
|          2. | single-scan, interleaved                        | just one SOS marker                      |
|      3.1.2. | max. 2^24 bytes data                            | ?                                        |
|  3.1.{5/6}. | max. width and height = 2040                    | {Y,X} in SOF-Segment                     |
|      3.1.8. | 8bit or 16bit precision for quantization values | Pq in DQT-Segment                        |
|      3.1.9. | 0xff in data must be followed by 0x00           | required by JPEG/JFIF                    |
|        4.1. | 8bit samples                                    | P in SOF-Segment                         |
|        4.1. | square pixels                                   | compare {H,V}density in APP0-Segment     |
|    1., 4.1. | three components in YUV/YCbCr color space       | Nf in SOF-Segment, also required by JFIF |
|        4.1. | sampling 4:2:2 or 4:2:0                         | {H,V}i in SOF-Segment                    |

The maximum amount of data in the RTP packets cannot be exactly checked because it depends on how and how often the quantization and huffman tables are transferred.

A lot of the other content of RFC 2435 deals with the format which is used to store the JPEG data in the RTP packet.
These restrictions cannot be checked by this program.

