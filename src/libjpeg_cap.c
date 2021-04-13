#include <stdio.h>
#include <stdlib.h>
#include "jpeglib.h"

void process_jpeg(FILE** in) {
    struct jpeg_decompress_struct cinfo;
    struct jpeg_error_mgr jerr;

    JSAMPARRAY buffer;
    int row_stride;

    cinfo.err = jpeg_std_error(&jerr);
    jpeg_create_decompress(&cinfo);

    jpeg_stdio_src(&cinfo, *in);
    (void) jpeg_read_header(&cinfo, TRUE);

    // set parameters for decompression

    (void) jpeg_start_decompress(&cinfo);
    // now correct scaled output image dimensions available
    // also output colormap available

    printf("width: %u, height: %u\n", cinfo.image_width, cinfo.image_height);
    printf("#components: %d\n", cinfo.num_components);
    printf("YCbCr?: %u\n", cinfo.jpeg_color_space == JCS_YCbCr);
    if (cinfo.saw_JFIF_marker) {
        printf("square pixels?: %u\n", cinfo.X_density == cinfo.Y_density);
    }

    row_stride = cinfo.output_width * cinfo.output_components;
    buffer = (*cinfo.mem->alloc_sarray)
        ((j_common_ptr) &cinfo, JPOOL_IMAGE, row_stride, 1);
    
    // read image
    while (cinfo.output_scanline < cinfo.output_height) {
        (void) jpeg_read_scanlines(&cinfo, buffer, 1);
    }

    (void) jpeg_finish_decompress(&cinfo);

    printf("SOSs: %d, prec: %d, baseline?: %u, huffman?: %u\n",
            cinfo.input_scan_number, cinfo.data_precision, cinfo.is_baseline,
            !cinfo.arith_code);
    printf("progressive?: %u\n", cinfo.progressive_mode);
    printf("DRIs: %u\n", cinfo.restart_interval);

    jpeg_destroy_decompress(&cinfo);
}

int main(int argc, char* argv[argc]) {
    FILE* in;
    if ((in = fopen(argv[1], "rb")) == NULL) {
        fprintf(stderr, "can't open %s\n", argv[1]);
        exit(EXIT_FAILURE);
    }
    long start = ftell(in);

    process_jpeg(&in);
    printf("bytes: 0x%lx\n", ftell(in) - start);
    if (!feof(in)) {
        printf("not at eof\n");
        process_jpeg(&in);
    }

    fclose(in);

    return EXIT_SUCCESS;
}

