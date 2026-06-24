import java.util.List;

public class AutomaticOrderThread extends Thread {

    private ServiceController serviceController;
    private long intervalMillis;
    private boolean running;

    public AutomaticOrderThread(ServiceController serviceController, long intervalMillis) {
        this.serviceController = serviceController;
        this.intervalMillis = intervalMillis;
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("Automatic order thread started.");
        while (running) {
            try {
                List<Order> periodicOrders = serviceController.runAutomaticPeriodicOrders();
                if (!periodicOrders.isEmpty()) {
                    System.out.println("Automatic periodic orders created: " + periodicOrders.size());}
                List<Order> shortageOrders = serviceController.runAutomaticShortageOrders();
                if (!shortageOrders.isEmpty()) {
                    System.out.println("Automatic shortage orders created: " + shortageOrders.size());}
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                running = false;
            } catch (Exception e) {
                System.out.println("Automatic order thread error: " + e.getMessage());
                try {
                    Thread.sleep(intervalMillis);
                } catch (InterruptedException interruptedException) {
                    running = false;
                }
            }
        }
        System.out.println("Automatic order thread stopped.");
    }
    public void stopRunning() {
        running = false;
        interrupt();
    }
}