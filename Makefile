CFLAGS += -Wall -Wpedantic -ggdb -std=c11
INCLUDE += -I include
SRC = src/check.c \
	  src/jpeg.c \
	  src/mjrtp_check.c
OBJ = $(SRC:.c=.o)

EXE = mj_check

all: $(EXE)

$(EXE): $(OBJ)
	$(CC) $(LDFLAGS) -o $@ $^

%.o: %.c
	$(CC) $(CFLAGS) $(INCLUDE) -c $*.c -o $*.o

clean:
	rm -f mj_check $(OBJ)

.PHONY: mj_check clean

