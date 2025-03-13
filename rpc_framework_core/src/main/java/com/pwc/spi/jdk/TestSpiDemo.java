package com.pwc.spi.jdk;

import com.pwc.router.Router;

import java.util.Iterator;
import java.util.ServiceLoader;

public class TestSpiDemo {
    public static void doTest(SpiTest spiTest){
        System.out.println("begin");
        spiTest.test();
        System.out.println("end");
        System.out.println(Router.class.getName());
    }

    public static void main(String[] args) {
        ServiceLoader<SpiTest> serviceLoader = ServiceLoader.load(SpiTest.class);
        Iterator<SpiTest> spiTestIterator = serviceLoader.iterator();
        while (spiTestIterator.hasNext()){
            SpiTest iSpiTest = spiTestIterator.next();
            TestSpiDemo.doTest(iSpiTest);
        }
    }
}
