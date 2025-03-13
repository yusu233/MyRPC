package com.pwc.spi.jdk;

public class DefaultSpiTest implements SpiTest{
    @Override
    public void test() {
        System.out.println("hello world!");
    }
}
