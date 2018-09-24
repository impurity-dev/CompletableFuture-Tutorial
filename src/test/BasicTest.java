package test;

import mock.entity.Payload;
import mock.entity.Transport;
import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.concurrent.*;

public class BasicTest {

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

        // Will be ignored since it has already completed
        completableFuture.complete(defaultStringPayload);
        // Contains the first complete() call's param
        assertEquals(defaultIntPayload, completableFuture.get());
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
    public void completeAsync() throws Exception {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportLarge.sleep(defaultStringPayload));

        // Complete this future with the result from the supplier
        completableFuture.completeAsync(() -> transportSmall.deliveryPayload(defaultIntPayload));

        // The get returns the result from the completeAsync call
        assertEquals(completableFuture.get(), defaultIntPayload);
        // The future is indeed finished
        assertTrue(completableFuture.isDone());
        // And it was not cancelled
        assertFalse(completableFuture.isCancelled());
    }

    @Test
    // TODO: Add Explanation
    public void completeOnTimeout() throws Exception {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportLarge.sleep(defaultStringPayload));

        // Complete this future with the result from the supplier after the allotted time expires
        completableFuture.completeOnTimeout(defaultIntPayload, 1, TimeUnit.MICROSECONDS);

        // The get returns the result from the completeAsync call
        assertEquals(completableFuture.get(), defaultIntPayload);
        // The future is indeed finished
        assertTrue(completableFuture.isDone());
        // And it was not cancelled
        assertFalse(completableFuture.isCancelled());
    }

    @Test
    // TODO: Add Explanation
    public void get() throws Exception {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture_1 = CompletableFuture.supplyAsync(() -> transportLarge.deliveryPayload(defaultStringPayload));
        // Blocks and waits to grab the result and throws Checked and Unchecked exceptions
        Payload result_1 = completableFuture_1.get();
        // Finished properly
        assertTrue(completableFuture_1.isDone());
        // Payload was the desired payload
        assertEquals(result_1, defaultStringPayload);


        CompletableFuture<Payload> completableFuture_2 = CompletableFuture.supplyAsync(() -> transportLarge.sleep(defaultStringPayload));
        // Blocks and waits to grab the result for 1 second then continues
        // Timeout exception to break out of the blocking
        assertThrows(TimeoutException.class, () -> completableFuture_2.get(1, TimeUnit.NANOSECONDS));
        // Never got to finish
        assertFalse(completableFuture_2.isDone());

        CompletableFuture<Payload> completableFuture_3 = CompletableFuture.supplyAsync(() -> transportLarge.sleep(defaultStringPayload));
        // Blocks and waits to grab the result and throws Checked and Unchecked exceptions
        Payload result_3 = completableFuture_3.getNow(defaultIntPayload);
        // Finished with given result
        assertEquals(result_3, defaultIntPayload);
        // Never got to finish
        assertFalse(completableFuture_2.isDone());
    }

    @Test
    // TODO: Add Explanation
    public void join() {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportLarge.deliveryPayload(defaultStringPayload));

        // Blocks and waits for result and throws Unchecked CompletionException
        Payload result = completableFuture.join();
    }

    @Test
    // TODO: Add Explanation
    public void obtrudeException() throws Exception {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture_1 = CompletableFuture.supplyAsync(() -> transportLarge.deliveryPayload(defaultStringPayload));

        // Forcibly complete with an exception
        completableFuture_1.obtrudeException(new IllegalArgumentException("Fake Exception"));
        assertTrue(completableFuture_1.isDone());
        assertFalse(completableFuture_1.isCancelled());
        assertTrue(completableFuture_1.isCompletedExceptionally());


        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture_2 = CompletableFuture.supplyAsync(() -> transportLarge.deliveryPayload(defaultStringPayload));
        assertDoesNotThrow((Executable) completableFuture_2::get);
        assertEquals(defaultStringPayload, completableFuture_2.get());

        // Forcibly complete and overrides the value if already completed
        completableFuture_2.obtrudeException(new IllegalArgumentException("Fake Exception"));
        assertTrue(completableFuture_2.isDone());
        assertFalse(completableFuture_2.isCancelled());
        assertTrue(completableFuture_2.isCompletedExceptionally());
        // The get, although prior to the obtrude was completed and did not throw an exception, now throws one
        assertThrows(ExecutionException.class, completableFuture_2::get);

    }

    @Test
    // TODO: Add Explanation
    public void obtrudeValue() throws Exception {
        // Begin lengthy computations
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportSmall.deliveryPayload(defaultStringPayload));
        // And its result is from the supplier
        assertEquals(completableFuture.get(), defaultStringPayload);

        // Will override the existing value from the completion
        completableFuture.obtrudeValue(defaultIntPayload);
        // Contains the obtrudeValue() call's param
        assertEquals(defaultIntPayload, completableFuture.get());
    }

    @Test
    // TODO: Add Explanation
    public void completedFuture() throws Exception {
        // Created a completed future
        CompletableFuture<Payload> completableFuture = CompletableFuture.completedFuture(defaultStringPayload);
        // And its result is a completed future that holds the result given above
        assertEquals(completableFuture.get(), defaultStringPayload);
        assertTrue(completableFuture.isDone());
        assertFalse(completableFuture.isCancelled());
    }

    @Test
    // TODO: Add Explanation
    public void completedStage() {
        // Create a completed completion stage
        CompletionStage<Payload> completionStage = CompletableFuture.completedStage(defaultStringPayload);

        // The stage can be operated on further
        completionStage.thenRun(() -> transportSmall.deliveryPayload(defaultIntPayload));
    }

    @Test
    // TODO: Add Explanation
    public void failedFuture() {
        // Create a completed completion stage
        CompletableFuture<Payload> completableFuture = CompletableFuture.failedFuture(new Exception("Failed Future"));
        // And its result is a completed future that holds the result given above
        assertThrows(Exception.class, completableFuture::get);
        assertTrue(completableFuture.isDone());
        assertFalse(completableFuture.isCancelled());
        assertTrue(completableFuture.isCompletedExceptionally());
    }

    @Test
    // TODO: Add Explanation
    public void failedStage() {
        // Create a completed failed stage
        CompletionStage<Payload> completionStage = CompletableFuture.failedStage(new Exception("Failed Stage"));

        // The stage can still be operated on further, even if it failed
        completionStage.thenRun(() -> transportSmall.deliveryPayload(defaultIntPayload));
    }
}
