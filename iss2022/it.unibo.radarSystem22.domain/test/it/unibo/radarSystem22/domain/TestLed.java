package it.unibo.radarSystem22.domain;

import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.mock.LedMock;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TestLed {
    @Test
    public void testLedMock() {
        ILed led = new LedMock();
        assertTrue(!led.getState());

        led.turnOn();
        assertTrue(led.getState());

        led.turnOff();
        assertTrue(!led.getState());
    }
}
