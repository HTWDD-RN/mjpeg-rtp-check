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

    // The following parameters are set by jpeg_read_header().
    printf("width: %u, height: %u\n", cinfo.image_width, cinfo.image_height);
    printf("#components: %d\n", cinfo.num_components);
    printf("YCbCr?: %u\n", cinfo.jpeg_color_space == JCS_YCbCr);

    (void) jpeg_start_decompress(&cinfo);
    // now correct scaled output image dimensions available
    // also output colormap available

    row_stride = cinfo.output_width * cinfo.output_components;
    buffer = (*cinfo.mem->alloc_sarray)
        ((j_common_ptr) &cinfo, JPOOL_IMAGE, row_stride, 1);
    
    // read image
    while (cinfo.output_scanline < cinfo.output_height) {
        (void) jpeg_read_scanlines(&cinfo, buffer, 1);
        // process some scan or frame specific data
    }

    // state variables: indicate progress
    printf("SOSs: %d\n", cinfo.input_scan_number);

    // internal jpeg parameters, could change between scans
    size_t quant_tbls = 0;
    for (size_t i = 0; i < NUM_QUANT_TBLS; ++i) {
        if (cinfo.quant_tbl_ptrs[i]) {
            quant_tbls++;
        }
    }
    printf("quant_tbls: %zu\n", quant_tbls);

    size_t huff_tbls = 0;
    for (size_t i = 0; i < NUM_HUFF_TBLS; ++i) {
        if (cinfo.dc_huff_tbl_ptrs[i] && cinfo.ac_huff_tbl_ptrs[i]) {
            huff_tbls++;
        }
    }
    printf("huff_tbls: %zu\n", huff_tbls);
    // JHUFF_TBL* {d,a}c_huff_tbl_ptrs[NUM_HUFF_TBLS];

    // given by SOF/SOS markers, reset by SOI
    printf("prec: %d\n", cinfo.data_precision);
    printf("baseline?: %u\n", cinfo.is_baseline);
    /* If cinfo.progressive_mode == FALSE and cinfo.arith_code == FALSE
     * the encountered SOF marker is SOF0 or SOF1 (baseline or extended).
     * Together with some other information baseline mode can be verified.
     * The cinfo.is_baseline value just gets set to true in jpeg_write_header()
     * which is never called in the decoding process. So even if we can
     * access it (which means JPEG_LIB_VERSION >= 80), we cannot obtain information
     * from it.
     *
     * baseline:
     * prec: 8bit
     * sequential (!progressive_mode)
     * huffman coding: 2ac 2dc tables
     */
    printf("progressive?: %u\n", cinfo.progressive_mode);
    printf("huffman?: %u\n", !cinfo.arith_code);
    printf("DRIs: %u\n", cinfo.restart_interval);
    if (cinfo.comp_info) {
        for (size_t i = 0; i < cinfo.num_components; ++i) {
            printf("c%zu: h: %d, v: %d\n", i,
                    cinfo.comp_info[i].h_samp_factor,
                    cinfo.comp_info[i].v_samp_factor);
        }
    }

    /* valid during any one scan */
    /* when using num_components and comp_info, it could be counted to the prev section */
    /*
    if (cinfo.cur_comp_info) {
        for (size_t i = 0; i < cinfo.comps_in_scan; ++i) {
            printf("c%zu: h: %d, v: %d\n", i,
                    cinfo.cur_comp_info[i]->h_samp_factor,
                    cinfo.cur_comp_info[i]->v_samp_factor);
        }
    }
    */

    // optional markers
    if (cinfo.saw_JFIF_marker) {
        printf("square pixels?: %u\n", cinfo.X_density == cinfo.Y_density);
    }

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

