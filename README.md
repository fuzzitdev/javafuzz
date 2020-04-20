# Javafuzz: coverage-guided fuzz testing for Java

Javafuzz is a coverage-guided [fuzzer](https://developer.mozilla.org/en-US/docs/Glossary/Fuzzing) 
for testing Java packages.

Fuzzing is a powerful strategy for finding bugs like unhandled exceptions, logic bugs,
security bugs that arise from both logic bugs and Denial-of-Service caused by hangs and excessive memory usage.

Fuzzing can be seen as a powerful and efficient test technique in addition to classic unit tests.


## Usage

### Fuzz Target

The first step is to implement the following function (also called a fuzz target):

```java
public class FuzzExample extends AbstractFuzzTarget {
    public void fuzz(byte[] data) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException e) {
            // ignore as we expect this exception
        }
    }
}
```

Features of the fuzz target:

* Javafuzz will call the fuzz target in an infinite loop with random data (according to the coverage guided algorithm) passed to `data`.
* The function must catch and ignore only expected exceptions that arise when passing invalid input to the tested package (i.e. don't catch `Exception`).
* The fuzz target must call the test function/library with the passed buffer or a transformation on the test buffer 
if the structure or type is different.
* Fuzz functions can also implement application level checks to catch application/logical bugs
  * For example: decode the buffer with the testable library, encode it again, and check that both results are equal.
  To communicate unexpected results the function should throw an exception.
* Javafuzz will report any unhandled exceptions as crashes, as well as inputs that hit the specified memory limit. It will also report hangs i.e. runs that take longer than the specified timeout per testcase.

### Installing

Add this to your `pom.xml`

```xml
<dependencies>
	<dependency>
		<groupId>dev.fuzzit.javafuzz</groupId>
		<artifactId>core</artifactId>
		<version>1.23-SNAPSHOT</version>
		<scope>test</scope>
	</dependency>
</dependencies>

<plugins>
<plugin>
	<groupId>dev.fuzzit.javafuzz</groupId>
	<artifactId>javafuzz-maven-plugin</artifactId>
	<version>1.22</version>
</plugin>
</plugins>
```

### Running

The next step is to use javafuzz with your fuzz target function.

```bash
docker run -it maven:3.6.3-jdk-11 /bin/bash
git clone https://github.com/fuzzitdev/javafuzz.git
cd javafuzz
mvn install
cd examples
wget -O jacocoagent.jar https://github.com/fuzzitdev/javafuzz/raw/master/javafuzz-maven-plugin/src/main/resources/jacocoagent-exp.jar
MAVEN_OPTS="-javaagent:jacocoagent.jar" mvn javafuzz:fuzz -DclassName=dev.fuzzit.javafuzz.examples.FuzzYaml
```

```bash
# Output:
#0 READ units: 0
#1 NEW     cov: 61 corp: 0 exec/s: 1 rss: 23.37 MB
#23320 PULSE     cov: 61 corp: 1 exec/s: 10614 rss: 35.3 MB
#96022 NEW     cov: 70 corp: 1 exec/s: 11320 rss: 129.95 MB
#96971 NEW     cov: 78 corp: 2 exec/s: 10784 rss: 129.95 MB
#97046 NEW     cov: 79 corp: 3 exec/s: 9375 rss: 129.95 MB
#97081 NEW     cov: 81 corp: 4 exec/s: 11666 rss: 129.95 MB
#97195 NEW     cov: 93 corp: 5 exec/s: 9500 rss: 129.95 MB
#97216 NEW     cov: 97 corp: 6 exec/s: 10500 rss: 129.95 MB
#97238 NEW     cov: 102 corp: 7 exec/s: 11000 rss: 129.95 MB
#97303 NEW     cov: 108 corp: 8 exec/s: 10833 rss: 129.96 MB
#97857 PULSE     cov: 108 corp: 9 exec/s: 225 rss: 129.96 MB
#97857 PULSE     cov: 108 corp: 9 exec/s: 0 rss: 940.97 MB
#97857 PULSE     cov: 108 corp: 9 exec/s: 0 rss: 1566.01 MB
```

### Corpus

Javafuzz will generate and test various inputs in an infinite loop.
`corpus` is optional directory and will be used to save the generated testcases so
later runs can be started from the same point and provided as seed corpus.

Javafuzz can also start with an empty directory (i.e. no seed corpus) though some
valid test-cases in the seed corpus will likely substantially speed up the fuzzing.  

Javafuzz tries to mimic some of the arguments and output style from [libFuzzer](https://llvm.org/docs/LibFuzzer.html).

More fuzz targets examples (for real and popular libraries) are located under the examples directory and
bugs that were found using those targets are listed in the trophies section.

### Coverage

For coverage instrumentation we use [JaCoCo library](https://github.com/jacoco/jacoco)


## Other languages

Currently this library also exists for the following languages:
* Javascript [jsfuzz](https://github.com/fuzzitdev/jsfuzz)
* Python via [pythonfuzz](https://github.com/fuzzitdev/pythonfuzz)

## Credits & Acknowledgments

Javafuzz is a port of [fuzzitdev/jsfuzz](https://github.com/fuzzitdev/jsfuzz).

Which in turn based based on [go-fuzz](https://github.com/dvyukov/go-fuzz) originally developed by [Dmitry Vyukov's](https://twitter.com/dvyukov).
Which is in turn heavily based on [Michal Zalewski](https://twitter.com/lcamtuf) [AFL](http://lcamtuf.coredump.cx/afl/).

Another solid fuzzing with coverage library for Java is [JQF](https://github.com/rohanpadhye/jqf) but is more
focused on semantic fuzzing (i.e structure aware) and thus depends on quickcheck. JavaFuzz does not 
depend on any framework and focuses on mutations producing an array and using coverage to find more bugs.

## Contributions

Contributions are welcome! :)
There are still a lot of things to improve, and tests and features to add.
We will slowly post those in the issues section.
Before doing any major contribution please open an issue so we can discuss
and help guide the process before any unnecessary work is done.
