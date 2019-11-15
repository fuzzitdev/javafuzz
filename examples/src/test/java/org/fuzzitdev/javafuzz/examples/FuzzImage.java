package org.fuzzitdev.javafuzz.examples;

import org.fuzzitdev.javafuzz.core.AbstractFuzzTarget;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class FuzzImage extends AbstractFuzzTarget {
    public void fuzz(byte[] data) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException e) {
            // ignore
        }
    }
}
