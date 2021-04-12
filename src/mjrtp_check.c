#include "mjrtp_check.h"

bool check_file_extension(char filename[]) {
    size_t len = strlen(filename);
    char* ext = filename + (len - 6);
    int comp = strncmp(ext, ".mjpeg", 6);
    if (comp == 0) {
        return true;
    }
    ext++;
    ext++;
    comp = strncmp(ext, ".avi", 4);
    if (comp == 0) {
        return true;
    }
    comp = strncmp(ext, ".mov", 4);
    if (comp == 0) {
        return true;
    }
    return false;
}

bool parse_file(const char filename[]) {
    int buffer_size = 65536;
    unsigned char* buffer = malloc(buffer_size);
    if (!buffer) {
        printf("error: cannot allocate memory for file\n");
        return false;
    }
    FILE* in = fopen(filename, "rb");
    if (!in) {
        printf("file could not be opened\n");
        return false;
    }

    size_t nbytes = fread(buffer, 1, buffer_size, in);
    if (nbytes < buffer_size) {
        printf("%zu bytes read\n", nbytes);
    }

    for (size_t i = 0; i < buffer_size; ++i) {
        // print_bytes_hex(2, &buffer[i]);
        if (buffer[i] == JPEG_MARKER) {
            while (i < buffer_size-1 && buffer[i+1] == JPEG_MARKER) {
                i++;
            }
            if (i < buffer_size -1) {
                switch (buffer[i+1]) {
                    case JPEG_SOI:
                        handle_soi();
                        break;
                    case JPEG_EOI:
                        handle_eoi();
                        break;
                    case JPEG_SOF0:
                        handle_sof0();
                        break;
                    case JPEG_SOF1:
                    case JPEG_SOF2:
                    case JPEG_SOF3:
                    case JPEG_SOF5:
                    case JPEG_SOF6:
                    case JPEG_SOF7:
                    case JPEG_SOF9:
                    case JPEG_SOFA:
                    case JPEG_SOFB:
                    case JPEG_SOFD:
                    case JPEG_SOFE:
                    case JPEG_SOFF:
                        printf("found ");
                        print_bytes_hex(2, &buffer[i]);
                        printf("\n");
                        break;
                    case JPEG_ZERO:
                        // data
                        break;
                    case JPEG_MARKER:
                        // "slider" condition, should not occur
                        break;
                    default:
                        printf("something not handled yet: ");
                        print_bytes_hex(2, &buffer[i]);
                        printf("\n");
                        break;
                }
            }
            i++; // jump over second byte
        }
    }


    fclose(in);
    free(buffer);
    return true;
}

void print_bytes_hex(const size_t size, const unsigned char bytes[size]) {
    printf("0x");
    for (size_t s = 0; s < size; ++s) {
        printf("%02hhx", bytes[s]);
    }
}

void print_usage() {
    printf("mjrtpc <file>\n");
}

