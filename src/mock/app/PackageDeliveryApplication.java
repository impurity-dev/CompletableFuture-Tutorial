package mock.app;

import mock.entity.Destination;
import mock.entity.Payload;
import mock.entity.Transport;

import java.util.List;

public class PackageDeliveryApplication {
    private List<Destination> headquarters;
    private List<Payload> packages;
    private List<Transport> trucks;

    public PackageDeliveryApplication() {
    }

    public void setUpHeadquarters(int headquartersAmount) {
    }

    public void start() {

    }

    public static void main(String[] args) {
        PackageDeliveryApplication app = new PackageDeliveryApplication();
        app.start();
    }
}
