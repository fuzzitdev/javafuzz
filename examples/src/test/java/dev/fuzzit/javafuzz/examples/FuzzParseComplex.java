package dev.fuzzit.javafuzz.examples;

import dev.fuzzit.javafuzz.core.AbstractFuzzTarget;


/**
 * Unit test for simple App.
 */
public class FuzzParseComplex extends AbstractFuzzTarget
{
    public void fuzz(byte[] data)
    {
        App.parseComplex(data);
    }
}
