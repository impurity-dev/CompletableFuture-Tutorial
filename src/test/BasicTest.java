package test;

import mock.Payload;
import mock.Transport;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class BasicTest {

    @Test
    public void instantiation() {
        CompletableFuture<Payload> completableFuture = new CompletableFuture<>();

        // At this point, our future has not completed and is not considered done, cancelled, or completed exceptionally
        assertFalse(completableFuture.isDone());
        assertFalse(completableFuture.isCancelled());
        assertFalse(completableFuture.isCompletedExceptionally());
    }

    @Test
    public void supplyAndRun() {

    }

    @Test
    public void forced_completions() {
        Payload<String> payload_1 = new Payload<>("Default", "I am the default payload", null);
        Payload<String> payload_2 = new Payload<String>("New", "I am the new payload", null);
        Transport transport = new Transport(10000);

        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transport.TransportPayload(payload_1));

        // End instantly with custom completion payload
        completableFuture.complete(payload_2);
        try {
            // The future is indeed finished
            assertTrue(completableFuture.isDone());
            // But it was not cancelled
            assertFalse(completableFuture.isCancelled());
            // And its result is the payload_2 provided in the complete function
            assertEquals(completableFuture.get(), payload_2);
        } catch(Exception e) {
            System.err.println("Error getting payload in forced_completions");
        }

        // Begin lengthy computations
        completableFuture = CompletableFuture.supplyAsync(() -> transport.TransportPayload(payload_1));

    }
}
