#include "mjrtp_check.h"

int main(int argc, char* argv[]) {
    if (argc != 2) {
        print_usage();
        return EXIT_FAILURE;
    }

    bool err = check_file_extension(argv[1]);
    if (!err) {
        printf("file invalid\n");
        return EXIT_FAILURE;
    }

    err = parse_file(argv[1]);
    if (!err) {
        printf("jfif invalid\n");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

