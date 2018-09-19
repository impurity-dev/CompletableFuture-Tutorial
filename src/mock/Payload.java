package mock;

/**
 * A thread-safe mock payload class that must be delivered to a destination
 *
 * @param <T>
 */
public class Payload<T> {
    private Destination destination;
    private T contents;
    private String name;
    private final Object lock = new Object();

    public Payload(String name, T contents, Destination destination) {
        this.name = name;
        this.contents = contents;
        this.destination = destination;
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
}
