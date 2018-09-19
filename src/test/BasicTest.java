package test;

import mock.Payload;
import mock.Transport;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BasicTest {

    private static Payload<String> defaultStringPayload;
    private static Payload<Integer> defaultIntPayload;
    private static Transport transportSmall;
    private static Transport getTransportMedium;
    private static Transport transportLarge;

    @BeforeClass
    public static void setUp() {
        defaultStringPayload = new Payload<>("Default", "I am the default String payload", null);
        defaultIntPayload = new Payload<>("Default", 10, null);
        transportSmall = new Transport(true, 10);
        getTransportMedium = new Transport(false, 1000);
        transportLarge = new Transport(false, 100000);
    }

    @AfterClass
    public static void log() {
        System.out.println("*************************************************");
    }

    @Test
    // TODO: Add Explanation
    public void instantiation() {
        CompletableFuture<Payload> completableFuture = new CompletableFuture<>();

        // At this point, our future has not completed and is not considered done, cancelled, or completed exceptionally
        assertFalse(completableFuture.isDone());
        assertFalse(completableFuture.isCancelled());
        assertFalse(completableFuture.isCompletedExceptionally());
    }

    @Test
    // TODO: Add Explanation
    public void supplyAsync() throws Exception {
        // Begin lengthy computation with a result to be returned
        CompletableFuture<Payload> completableFuture_1 = CompletableFuture.supplyAsync(() -> transportSmall.deliveryPayload(defaultStringPayload));

        // Grab that result when it has finished completing
        Payload result_1 = completableFuture_1.get(); // Block until completion
        System.out.println(String.format("%s has finished with this payload: \"%s\"", result_1.getName(), result_1.getContents()));
    }

    @Test
    // TODO: Add Explanation
    public void runAsync() throws Exception {
        // Begin lengthy computation with no result anticipated
        CompletableFuture completableFuture_1 = CompletableFuture.runAsync(() -> transportSmall.transportPayload(defaultStringPayload));
        completableFuture_1.get(); // Block until completion
        System.out.println("Future has completed");
    }

    @Test
    // TODO: Add Explanation
    public void complete() throws Exception {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportLarge.deliveryPayload(defaultStringPayload));

        // End instantly with custom completion payload
        completableFuture.complete(defaultIntPayload);
        // The future is indeed finished
        assertTrue(completableFuture.isDone());
        // But it was not cancelled
        assertFalse(completableFuture.isCancelled());
        // And its result is the payload_2 provided in the complete function
        assertEquals(completableFuture.get(), defaultIntPayload);
    }

    @Test
    // TODO: Add Explanation
    public void cancel() throws Exception  {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportLarge.deliveryPayload(defaultStringPayload));

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
    // TODO: Add Explanation
    public void completeExceptionally() {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportLarge.deliveryPayload(defaultStringPayload));

        // End instantly - Bool determines if it should can be interrupted
        completableFuture.completeExceptionally(new Throwable("Complete Exceptionally"));

        // The future is indeed finished
        assertTrue(completableFuture.isDone());
        // And it was not cancelled
        assertFalse(completableFuture.isCancelled());
        // And its result will throw the provided exception in the completeExceptionally
        assertThrows(ExecutionException.class, completableFuture::get);
    }

    @Test
    // TODO: Add Explanation
    public void get(){
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportLarge.deliveryPayload(defaultStringPayload));

        try {
            // Blocks and waits to grab the result
            Payload result = completableFuture.get();
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    // TODO: Add Explanation
    public void join() {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportLarge.deliveryPayload(defaultStringPayload));

        // Blocks and waits for result
        Payload result = completableFuture.join();
    }
}
