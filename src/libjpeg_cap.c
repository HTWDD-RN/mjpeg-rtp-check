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
    /*
    printf("max_h_samp_factor: %d\n", cinfo.max_h_samp_factor);
    printf("max_v_samp_factor: %d\n", cinfo.max_v_samp_factor);
    printf("min_DCT_h_scaled_size: %d\n", cinfo.min_DCT_h_scaled_size);
    printf("min_DCT_v_scaled_size: %d\n", cinfo.min_DCT_v_scaled_size);
    */

    row_stride = cinfo.output_width * cinfo.output_components;
    buffer = (*cinfo.mem->alloc_sarray)
        ((j_common_ptr) &cinfo, JPOOL_IMAGE, row_stride, 1);
    
    // read image
    while (cinfo.output_scanline < cinfo.output_height) {
        (void) jpeg_read_scanlines(&cinfo, buffer, 1);
        // process some scan or frame specific data
    }

    if (cinfo.quant_tbl_ptrs) {
        printf("quant_tbl = 8bit?: %u\n", cinfo.quant_tbl_ptrs[0]->quantval[0] < 0xff);
    }
    printf("prec: %d\n", cinfo.data_precision);
    printf("baseline?: %u, huffman?: %u\n", cinfo.is_baseline, !cinfo.arith_code);
    printf("progressive?: %u\n", cinfo.progressive_mode);
    if (cinfo.cur_comp_info) {
        for (size_t i = 0; i < cinfo.comps_in_scan; ++i) {
            printf("c%zu: h: %d, v: %d\n", i,
                    cinfo.cur_comp_info[i]->h_samp_factor,
                    cinfo.cur_comp_info[i]->v_samp_factor);
        }
    }
    printf("SOSs: %d\n", cinfo.input_scan_number);
    printf("DRIs: %u\n", cinfo.restart_interval);

    (void) jpeg_finish_decompress(&cinfo);

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

