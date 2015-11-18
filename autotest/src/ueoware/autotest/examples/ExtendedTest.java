package ueoware.autotest.examples;

import jp.horarium.DefaultHorarium;
import jp.horarium.framework.ExtendedTestCase;

import org.junit.Test;


public class ExtendedTest extends ExtendedTestCase {

    DefaultHorarium selenium = null;

    @Override
    public void setUp() {
        System.out.println("ExtendedTest up!");
        selenium = new DefaultHorarium(serverHost, serverPort, browserStartCommand, browserURL);
        selenium.start();
    }

    @Test
    public void extendedTest() {
        System.out.println("extendedTest!");

    }

    @Override
    public void tearDown() {
        System.out.println("ExtendedTest down!");
        selenium.stop();
    }
}
