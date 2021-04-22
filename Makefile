CFLAGS += -Wall -pedantic -ggdb -std=c11
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

libjpeg_cap: src/libjpeg_cap.c
	$(CC) $(CFLAGS) -o $@ $^ `pkg-config --libs libjpeg`

bin/JpegAnalysis.class: src/JpegAnalysis.java
	mkdir -p $(@D)
	javac -cp src/ -d $(@D) $^

JpegAnalysis: bin/JpegAnalysis.class
	java -cp $(<D) $@ ../data/videos/htw_cut.mjpeg

clean:
	rm -rf mj_check $(OBJ) libjpeg_cap bin

.PHONY: mj_check clean

