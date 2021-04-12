#ifndef _JPEG_H
#define _JPEG_H

#include <stdlib.h>

#define JPEG_ZERO 0x00

// huffman coding
#define JPEG_SOF0 0xc0 // baseline dct
#define JPEG_SOF1 0xc1 // extended sequential dct
#define JPEG_SOF2 0xc2 // progressive dct
#define JPEG_SOF3 0xc3 // lossless (sequential)

#define JPEG_SOF5 0xc5 // differential sequential dct
#define JPEG_SOF6 0xc6 // differential pregressive dct
#define JPEG_SOF7 0xc7 // differential lossless (sequential)

// arithmetic coding
#define JPEG_JPG 0xc8 // reserved for JPEG extensions
#define JPEG_SOF9 0xc9 // extended sequential dct
#define JPEG_SOFA 0xca // progressive dct
#define JPEG_SOFB 0xcb // lossless (sequential)

#define JPEG_SOFD 0xcd // differential sequential dct
#define JPEG_SOFE 0xce // differential progressive dct
#define JPEG_SOFF 0xcf // differential lossless (sequential)

// huffman table
#define JPEG_DHT 0xc4 // define huffman table(s)

// arithmetic coding conditioning spec
#define JPEG_DAC 0xcc // define arithmetic coding condition(s)

// restart interval termination
#define JPEG_RST0 0xd0 // restart with mod 8 count 0
#define JPEG_RST1 0xd1 // restart with mod 8 count 1
#define JPEG_RST2 0xd2 // restart with mod 8 count 2
#define JPEG_RST3 0xd3 // restart with mod 8 count 3
#define JPEG_RST4 0xd4 // restart with mod 8 count 4
#define JPEG_RST5 0xd5 // restart with mod 8 count 5
#define JPEG_RST6 0xd6 // restart with mod 8 count 6
#define JPEG_RST7 0xd7 // restart with mod 8 count 7

// other
#define JPEG_SOI 0xd8 // start of image
#define JPEG_EOI 0xd9 // end of image
#define JPEG_SOS 0xda // start of scan
#define JPEG_DQT 0xdb // define quantization table(s)
#define JPEG_DNL 0xdc // define number of lines
#define JPEG_DRI 0xdd // define restart interval
#define JPEG_DHP 0xde // define hierarchical progression
#define JPEG_EXP 0xdf // expand reference component(s)

// application segments
// e0 to ef

// jpeg extensions
// f0 to fd

#define JPEG_COM 0xfe // comment
#define JPEG_MARKER 0xff

// reserved
// 01
// 02 to bf


typedef struct jpeg_stat jpeg_stat;
struct jpeg_stat {
    size_t soi_cnt;
    size_t prec;
    size_t dqt;
    size_t dht;
    size_t dri;
    size_t data;

    size_t error;
};


void handle_soi();
void handle_eoi();
void handle_sof0();

#endif // _JPEG_H
