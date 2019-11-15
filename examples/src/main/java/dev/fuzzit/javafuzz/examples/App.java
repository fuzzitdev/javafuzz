package dev.fuzzit.javafuzz.examples;

/**
 * Hello world!
 *
 */
public class App 
{
    public static String parseComplex (byte[] data) {
        if (data.length >= 3) {
            if (Byte.compare(data[0],(byte)0x46) == 0 && Byte.compare(data[1], (byte)0x55) == 0 && Byte.compare(data[2], (byte)0x5a) == 0 &&
                    data[3] == (byte) 0x5a) {
                return "this will throw out of bound exception";
            }
        }
        return  "ok";
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
