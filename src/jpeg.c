#include "jpeg.h"

jpeg_stat jstatus = {0};

void handle_soi() {
    jstatus.soi_cnt++;
}

void handle_eoi() {
    if (jstatus.soi_cnt) {
        jstatus.soi_cnt--;
    } else {
        jstatus.error = JPEG_EOI;
    }
}

void handle_sof0() {
    jstatus.data++;
}

