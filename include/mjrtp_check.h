#ifndef _MJRTP_CHECK_H
#define _MJRTP_CHECK_H

#include <stdbool.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "jpeg.h"

bool check_file_extension(char filename[]);
bool parse_file(const char filename[]);
void print_bytes_hex(const size_t size, const unsigned char bytes[size]);
void print_usage();

#endif // _MJRTP_CHECK_H
