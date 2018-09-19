package mock;

public class Transport {
    private int iterations;

    public Transport(int iterations) {
        this.iterations = iterations;
    }

    public Payload TransportPayload(Payload payload) {
        for(int i = 0; i < iterations; i++) {
            System.out.println(String.format("Payload: %s has moved to its %2d position", payload.getName(), i));
        }
        return payload;
    }
}
