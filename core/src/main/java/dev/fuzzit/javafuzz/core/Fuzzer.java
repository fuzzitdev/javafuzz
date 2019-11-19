package dev.fuzzit.javafuzz.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Fuzzer {
    private final AbstractFuzzTarget target;
    private final Corpus corpus;
    private Object agent;
    private Method m;
    private long executionsInSample;
    private long lastSampleTime;
    private long totalExecutions;
    private long totalCoverage;

    public Fuzzer(AbstractFuzzTarget target, String dirs) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        this.target = target;
        this.corpus = new Corpus(dirs);
        Class c = Class.forName("org.jacoco.agent.rt.RT");
        Method m = c.getMethod("getAgent");
        this.agent = m.invoke(null);
        this.m = agent.getClass().getMethod("getHitCount", boolean.class);
    }

    void writeCrash(byte[] buf) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(buf);
        byte[] digest = md.digest();
        String hex = String.format("%064x", new BigInteger(1, digest));
        String filepath = "crash-" + hex;
        try (FileOutputStream fos = new FileOutputStream(filepath)) {
            fos.write(buf);
            System.out.printf("crash was written to %s\n", filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void logStats(String type) {
        long rss = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        long endTime = System.currentTimeMillis();
        long execs_per_second = -1;
        if ((endTime - this.lastSampleTime) != 0) {
            execs_per_second = (this.executionsInSample / (endTime - this.lastSampleTime)) * 1000;
        }
        this.lastSampleTime = endTime;
        this.executionsInSample = 0;

        System.out.printf("#%d %s     cov: %d corp: %d exec/s: %d rss: %d MB\n",
                this.totalExecutions, type, this.totalCoverage, this.corpus.getLength(), execs_per_second, rss);
    }

    public void start() throws InvocationTargetException, IllegalAccessException, NoSuchAlgorithmException {
        System.out.printf("#0 READ units: %d\n", this.corpus.getLength());
        this.totalCoverage = 0;
        this.totalExecutions = 0;
        this.executionsInSample = 0;
        this.lastSampleTime = System.currentTimeMillis();

        while (true) {
            byte[] buf = this.corpus.generateInput();
            // Next version will run this in a different thread.
            try {
                this.target.fuzz(buf);
            } catch (Exception e) {
                e.printStackTrace();
                this.writeCrash(buf);
                break;
            }

            this.totalExecutions++;
            this.executionsInSample++;

            int newCoverage = (int)this.m.invoke(this.agent, false);
            if (newCoverage > this.totalCoverage) {
                this.totalCoverage = newCoverage;
                this.corpus.putBuffer(buf);
                this.logStats("NEW");
            } else if ((System.currentTimeMillis() - this.lastSampleTime) > 3000) {
                this.logStats("PULSE");
            }

        }
    }
}
