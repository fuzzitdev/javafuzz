package org.fuzzitdev.javafuzz.examples;

import org.fuzzitdev.javafuzz.core.AbstractFuzzTarget;


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
