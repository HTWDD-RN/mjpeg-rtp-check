CFLAGS += -Wall -pedantic -ggdb -std=c11
INCLUDE += -I include
SRCDIR = src
BINDIR = bin
SRC = $(SRCDIR)/check.c \
	  $(SRCDIR)/jpeg.c \
	  $(SRCDIR)/mjrtp_check.c
OBJ = $(SRC:.c=.o)

EXE = mj_check

all: $(EXE)

$(EXE): $(OBJ)
	$(CC) $(LDFLAGS) -o $@ $^

%.o: %.c
	$(CC) $(CFLAGS) $(INCLUDE) -c $*.c -o $*.o

libjpeg_cap: $(SRCDIR)/libjpeg_cap.c
	$(CC) $(CFLAGS) -o $@ $^ `pkg-config --libs libjpeg`

$(BINDIR)/JpegAnalysis.class: $(SRCDIR)/JpegAnalysis.java
	mkdir -p $(@D)
	javac -cp $(SRCDIR)/ -d $(@D) $^

JpegAnalysis: $(BINDIR)/JpegAnalysis.class
	java -cp $(<D) $@ ../data/videos/htw_cut.mjpeg

$(BINDIR)/MJpegRtpCheck.class: $(SRCDIR)/MJpegRtpCheck.java
	mkdir -p $(@D)
	javac -cp $(SRCDIR)/ -d $(@D) $^

# JpegAnalysis: $(BINDIR)/MJpegRtpCheck.class
# 	java -cp $(<D) $@ ../data/videos/htw_cut.mjpeg

clean:
	rm -rf mj_check $(OBJ) libjpeg_cap bin

.PHONY: mj_check clean

