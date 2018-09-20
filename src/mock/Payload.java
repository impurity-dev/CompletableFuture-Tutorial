package mock;

import org.junit.runner.notification.RunListener.ThreadSafe;

/**
 * A thread-safe mock payload class that must be delivered to a destination
 *
 * @param <T>
 */
@ThreadSafe
public class Payload<T> {
    private Destination destination;
    private Destination origin;
    private Destination currentLocation;
    private T contents;
    private String name;
    private final Object lock = new Object();

    /**
     * Mock payload implementation
     * SHALL be used for demonstration of executing on a completableFuture and NOT full app
     *
     * @param name - Name of package
     * @param contents - Contents of the package to be delivered
     */
    public Payload(String name, T contents) {
        this.name = name;
        this.contents = contents;
    }

    /**
     * Full payload implementation
     * SHALL be used to mock payload movements
     *
     * @param name - Name of package
     * @param contents - Contents of the package to be delivered
     * @param origin - Start Position in the mock environment
     * @param destination - Final Position in the mock environment
     */
    public Payload(String name, T contents, Destination origin, Destination destination) {
        this.name = name;
        this.contents = contents;
        this.destination = destination;
        this.origin = origin;
    }

    /**
     * Get the name of this package
     *
     * @return name of the package
     */
    public String getName() {
        return name;
    }

    public T getContents() {
        return contents;
    }

    /**
     * Determines if the package has arrived at its final destination
     *
     * @return True if arrived at destination, false otherwise
     */
    public synchronized boolean isDelivered() {
        return currentLocation == destination;
    }

    /**
     * Thread-safe
     * @param newLocation
     */
    public synchronized void setCurrentLocation(Destination newLocation) {
        currentLocation = newLocation;
    }

    public Destination getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Get the payload's starting destination
     *
     * @return The starting destination
     */
    public Destination getOrigin() {
        return origin;
    }

    /**
     * Get this payload's final destination
     *
     * @return The final destination
     */
    public Destination getDestination() {
        return destination;
    }
}
