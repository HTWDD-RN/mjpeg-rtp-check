SRCDIR = src
BINDIR = bin
CLASS = $(BINDIR)/MJpegRtpCheck.class \
	    $(BINDIR)/JpegMetadataExtractor.class \
	    $(BINDIR)/JpegRtpMetadata.class \
	    $(BINDIR)/VideoFileBuffer.class


all: MJpegRtpCheck.jar

$(BINDIR)/%.class: $(SRCDIR)/%.java
	mkdir -p $(@D)
	javac -cp $(<D) -d $(@D) $^

MJpegRtpCheck.jar: $(CLASS)
	jar -c -f $(BINDIR)/$@  $^

clean:
	rm -rf $(CLASS) $(BINDIR)/MJpegRtpCheck.jar

.PHONY: clean MJpegRtpCheck.jar

