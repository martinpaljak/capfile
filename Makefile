SHELL = bash
MVNOPTS = -Dmaven.javadoc.skip=true -Dmaven.test.skip=true -Dspotbugs.skip=true

SOURCES = $(shell find . -name '*.java' -o -name 'pom.xml')


default: target/capfile.jar

target/capfile.jar: $(SOURCES)
	./mvnw $(MVNOPTS) package

dep: $(SOURCES)
	./mvnw $(MVNOPTS) install

test: $(SOURCES)
	./mvnw verify

clean:
	./mvnw clean
