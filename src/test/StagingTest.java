package test;

import mock.entity.Payload;
import mock.entity.Transport;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.concurrent.*;

public class StagingTest {

    private static Payload<String> defaultStringPayload;
    private static Payload<Integer> defaultIntPayload;
    private static Transport transportSmall;
    private static Transport transportMedium;
    private static Transport transportLarge;
    private static Transport transportExtraLarge;

    @BeforeClass
    public static void setUp() {
        defaultStringPayload = new Payload<>("Default", "I am the default String payload");
        defaultIntPayload = new Payload<>("Default", 10);
        transportSmall = new Transport(true, 10);
        transportMedium = new Transport(false, 1_000);
        transportLarge = new Transport(false, 100_000);
        transportExtraLarge = new Transport(false, 1_000_000_000);
    }

    @AfterClass
    public static void log() {
        System.out.println("*************************************************");
    }

    @Test
    // TODO: Explanation
    public void thenAccept() throws Exception {
        // The payload we will be operating on currently has no bundle
        assertNull(defaultIntPayload.getBundle().peek());

        // Create our future
        CompletableFuture<Payload<Integer>> completableFuture = CompletableFuture.supplyAsync(() -> defaultIntPayload);

        // Once done, lets modify our payload by adding a bundle to it
        completableFuture.thenAccept(payload ->  payload.addBundle(defaultStringPayload));
        // Await its finish
        completableFuture.join();

        // The payload should have been modified to have a bundle
        assertEquals(completableFuture.get().getBundle().peek(), defaultStringPayload);
    }

    @Test
    // TODO: Explanation
    public void thenAcceptAsync() {
    }

}
