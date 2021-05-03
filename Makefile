SRCDIR = src
BINDIR = bin
CLASS = $(BINDIR)/MJpegRtpCheck.class \
	    $(BINDIR)/ArgumentParser.class \
	    $(BINDIR)/JpegMetadataExtractor.class \
	    $(BINDIR)/JpegRtpMetadata.class \
	    $(BINDIR)/VideoFileBuffer.class
JARFILES = $(CLASS:$(BINDIR)/%=%)


all: MJpegRtpCheck.jar

$(BINDIR)/%.class: $(SRCDIR)/%.java
	mkdir -p $(@D)
	javac -cp $(<D) -d $(@D) $^

MJpegRtpCheck.jar: $(CLASS)
	cd $(BINDIR); jar -c -f $@ -e MJpegRtpCheck $(JARFILES)

clean:
	rm -rf $(BINDIR)

.PHONY: clean MJpegRtpCheck.jar

