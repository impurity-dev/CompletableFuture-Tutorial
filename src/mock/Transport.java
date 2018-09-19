package mock;

public class Transport {
    private int iterations;
    private boolean shouldLog;

    public Transport(boolean shouldLog, int iterations) {
        this.shouldLog = shouldLog;
        this.iterations = iterations;
    }

    /**
     * Deliver the payload by transporting it then returning it
     *
     * @param payload payload to be delivered
     * @return the payload that has been acted upon
     */
    public Payload deliveryPayload(Payload payload) {
        transportPayload(payload);
        return payload;
    }

    /**
     * Deliver the payload by transporting it then returning it
     * but encounter a mock issue and throw an exception
     *
     * @param payload payload to be moved
     */
    public void deliveryPayload(Payload payload, boolean shouldFail) throws Exception {
        deliveryPayload(payload);
        throw new Exception("Delivery Failure");
    }

    /**
     * Transport the payload by moving it over iterations
     *
     * @param payload payload to be moved
     */
    public void transportPayload(Payload payload) {
        for(int i = 0; i < iterations; i++) {
            if(shouldLog)
                System.out.println(String.format("Payload: %s has moved to its %2d position", payload.getName(), i + 1));
        }

        System.out.println(String.format("Payload: %s has finished!", payload.getName()));
    }

    /**
     * Transport the payload by moving it over iterations
     * but encounter a mock issue and throw an exception
     *
     * @param payload payload to be moved
     */
    public void transportPayload(Payload payload, boolean shouldFail) throws Exception {
       transportPayload(payload);
       throw new Exception("Transport Failure");
    }

    public Payload sleep(Payload payload) {
        try {
            Thread.sleep(1_000_000_000);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return payload;
    }

}
