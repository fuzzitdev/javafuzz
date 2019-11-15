package dev.fuzzit.javafuzz.examples;

import dev.fuzzit.javafuzz.core.AbstractFuzzTarget;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class FuzzDecompress extends AbstractFuzzTarget {
    public byte[] decompressByteArray(byte[] bytes) {

        ByteArrayOutputStream baos = null;
        Inflater iflr = new Inflater();
        iflr.setInput(bytes);
        baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4*1024];
        try{
            int size = iflr.inflate(tmp);
            baos.write(tmp, 0, size);
        }  catch (DataFormatException e) {

        } finally {
            try{
                if(baos != null) baos.close();
            } catch(IOException ex){}
        }

        return baos.toByteArray();
    }

    public void fuzz(byte[] data) {
        decompressByteArray(data);
    }
}
