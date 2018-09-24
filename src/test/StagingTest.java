package test;

import mock.entity.Payload;
import mock.entity.Transport;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    // Returns a new CompletionStage that, when this stage completes normally,
    // is executed with this stage's result as the argument to the supplied function.
    public void thenApply() throws Exception {
        // The payload we will be operating on currently has no bundle
        assertNull(defaultIntPayload.getBundle().peek());

        // Create our future
        // Once done, lets modify our payload by adding a bundle to it in a BLOCKING manner
        CompletableFuture<Payload> completableFuture = CompletableFuture
                .supplyAsync(() -> transportSmall.deliveryPayload(defaultIntPayload))
                .thenApplyAsync(payload ->  {
                    payload.addBundle(defaultStringPayload);
                    return payload;
                });
        // Await its finish
        completableFuture.join();

        // Should be done
        assertTrue(completableFuture.isDone());
        // The payload should have been modified to have a bundle
        assertEquals(completableFuture.get().getBundle().peek(), defaultStringPayload);
    }

    @Test
    // Returns a new CompletionStage that, when this stage completes normally,
    // executes the given action.
    public void thenRun() {
        // Create our future
        // Once done, lets execute some logic AFTER the first stage finishes
        CompletableFuture<Void> completableFuture = CompletableFuture
                .supplyAsync(() -> transportSmall.deliveryPayload(defaultIntPayload))
                .thenRunAsync(() -> System.out.println("DONE!"));
        // Await its finish
        completableFuture.join();
        // Should be done
        assertTrue(completableFuture.isDone());
    }

    @Test
    // Returns a new CompletionStage that, when this stage completes normally,
    // is executed with this stage's result as the argument to the supplied action.
    public void thenAccept() {
        // The payload we will be operating on currently has no bundle
        assertNull(defaultIntPayload.getBundle().peek());

        // Create our future
        // Once done, lets modify our payload by adding a bundle to it in a BLOCKING manner
        CompletableFuture<Void> completableFuture = CompletableFuture
                .supplyAsync(() -> transportSmall.deliveryPayload(defaultIntPayload))
                .thenAcceptAsync(payload ->  payload.addBundle(defaultStringPayload));
        // Await its finish
        completableFuture.join();

        // Should be done
        assertTrue(completableFuture.isDone());
        // The payload should have been modified to have a bundle
        assertEquals(defaultIntPayload.getBundle().peek(), defaultStringPayload);
    }

    @Test
    // Returns a new CompletionStage that is completed with the same value as
    // the CompletionStage returned by the given function.
    public void thenCombine() throws Exception {
        // Create our future
        CompletableFuture<Payload> completableFuture_1 = CompletableFuture.supplyAsync(() -> transportSmall.deliveryPayload(defaultIntPayload));
        CompletableFuture<Payload> completableFuture_2 = CompletableFuture.supplyAsync(() -> transportSmall.deliveryPayload(defaultStringPayload));
        CompletableFuture<Payload> completableFuture_3 = completableFuture_1.thenCombine(completableFuture_2, (s1, s2) -> {
            s1.getBundle().add(s2);
            return s1;
        });
        // Wait for it to finish
        completableFuture_3.join();

        // Ensure that the combination was proper
        assertNotNull(completableFuture_3.get().getBundle().peek());
        assertEquals(completableFuture_3.get().getBundle().peek(), defaultStringPayload);
    }

    @Test
    // Returns a new CompletionStage that, when this stage completes normally,
    // is executed with this stage as the argument to the supplied function.
    public void thenCompose() {
        // Create our future
        CompletableFuture<Payload> completableFuture = CompletableFuture.supplyAsync(() -> transportSmall.deliveryPayload(defaultIntPayload))
                .thenComposeAsync((result) -> CompletableFuture.supplyAsync(() -> {
                    result.getBundle().add(defaultStringPayload);
                    return result;
                }));

        // Wait for it to finish
        completableFuture.join();

        // Ensure that the combination was proper
        assertNotNull(defaultIntPayload.getBundle().peek());
        assertEquals(defaultIntPayload.getBundle().peek(), defaultStringPayload);
    }

    @Test
    // Returns a new CompletionStage that, when this stage completes either normally or exceptionally,
    // is executed with this stage's result and exception as arguments to the supplied function.
    public void handle() throws Exception {
        // Create a future that has a handle that will execute no matter how this completes
        CompletableFuture<Payload> completableFuture_1 = CompletableFuture
                .supplyAsync(() -> transportLarge.deliveryPayload(defaultIntPayload))
                .handle((result, throwable) -> {
                    if(result != null) {
                        defaultIntPayload.getBundle().add(defaultStringPayload);
                        return result;
                    }
                    System.err.println("Error arose: " + throwable.getMessage());
                    return null;
                });

        // Grab the result
        Payload resultPayload = completableFuture_1.get();

        // It properly executed
        assertEquals(resultPayload.getBundle().peek(), defaultStringPayload);
    }

    @Test
    // Returns a new CompletionStage that, when this stage completes exceptionally,
    // is executed with this stage's exception as the argument to the supplied function.
    public void exceptionally() {
        // Create a future that has a handle that will execute no matter how this completes
        CompletableFuture completableFuture = CompletableFuture
                .supplyAsync(() -> transportExtraLarge.sleep(defaultIntPayload))
                .exceptionally(throwable -> {
                    System.out.println(throwable.getMessage());
                    return null;
                });

        completableFuture.completeExceptionally(new Exception("Complete Exceptionally"));

        // It properly executed
        assertTrue(completableFuture.isCompletedExceptionally());
        assertThrows(Exception.class, completableFuture::get);
    }
}
