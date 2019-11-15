package org.fuzzitdev.javafuzz.examples;

import org.fuzzitdev.javafuzz.core.AbstractFuzzTarget;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.ByteArrayInputStream;

public class FuzzYaml extends AbstractFuzzTarget {
    public void fuzz(byte[] data) {
        Yaml yaml = new Yaml();
        try {
            yaml.load(new ByteArrayInputStream(data));
        } catch (YAMLException | IllegalArgumentException e) {
            // ignore
        }
    }
}
