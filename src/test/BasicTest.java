package test;

import mock.Payload;
import mock.Transport;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class BasicTest {

    @AfterClass
    public static void log() {
        System.out.println("*************************************************");
    }

    @Test
    public void instantiation() {
        CompletableFuture<Payload> completableFuture = new CompletableFuture<>();

        // At this point, our future has not completed and is not considered done, cancelled, or completed exceptionally
        assertFalse(completableFuture.isDone());
        assertFalse(completableFuture.isCancelled());
        assertFalse(completableFuture.isCompletedExceptionally());
    }

    @Test
    public void supplyAsync() throws Exception {
        Payload<String> payload_1 = new Payload<>("Default", "I am the default payload", null);
        Transport transport = new Transport(10);

        // Begin lengthy computation with a result to be returned
        CompletableFuture<Payload> completableFuture_1 = CompletableFuture.supplyAsync(() -> transport.deliveryPayload(payload_1));

        // Grab that result when it has finished completing
        Payload result_1 = completableFuture_1.get(); // Block until completion
        System.out.println(String.format("%s has finished with this payload: \"%s\"", result_1.getName(), result_1.getContents()));
    }

    @Test
    public void runAsync() throws Exception {
        Payload<String> payload_1 = new Payload<>("Default", "I am the default payload", null);
        Transport transport = new Transport(10);

        // Begin lengthy computation with no result anticipated
        CompletableFuture completableFuture_1 = CompletableFuture.runAsync(() -> transport.transportPayload(payload_1));
        completableFuture_1.get(); // Block until completion
        System.out.println("Future has completed");
    }

    @Test
    public void complete() throws Exception {
        Payload<String> payload_1 = new Payload<>("Default", "I am the default payload", null);
        Payload<String> payload_2 = new Payload<String>("New", "I am the new payload", null);
        Transport transport = new Transport(10000);

        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transport.deliveryPayload(payload_1));

        // End instantly with custom completion payload
        completableFuture.complete(payload_2);
        // The future is indeed finished
        assertTrue(completableFuture.isDone());
        // But it was not cancelled
        assertFalse(completableFuture.isCancelled());
        // And its result is the payload_2 provided in the complete function
        assertEquals(completableFuture.get(), payload_2);
    }

    @Test
    public void cancel() throws Exception  {
        Payload<String> payload = new Payload<>("Default", "I am the default payload", null);
        Transport transport = new Transport(10000);

        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transport.deliveryPayload(payload));

        // Sleep briefly so that the future can receive CPU time
        Thread.sleep(1);

        // End instantly - Bool determines if it should can be interrupted
        completableFuture.cancel(true);

        // The future is indeed finished
        assertTrue(completableFuture.isDone());
        // And it was canceled
        assertTrue(completableFuture.isCancelled());
        // And its result will throw a cancellation exception
        assertThrows(CancellationException.class, completableFuture::get);
    }

    @Test
    public void completeExceptionally() {
        Payload<String> payload = new Payload<>("Default", "I am the default payload", null);
        Transport transport = new Transport(10000);

        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transport.deliveryPayload(payload));

        // End instantly - Bool determines if it should can be interrupted
        completableFuture.completeExceptionally(new Exception("Complete Exceptionally"));

        // The future is indeed finished
        assertTrue(completableFuture.isDone());
        // And it was not cancelled
        assertFalse(completableFuture.isCancelled());
        // And its result will throw the provided exception in the completeExceptionally
        assertThrows(Exception.class, completableFuture::get);
    }
}
