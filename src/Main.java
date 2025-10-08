
import java.util.HashMap;
import java.util.Map;

public class Main {
    public Main() {
        // Get the broker.
        PubSubBroker broker = PubSubBroker.getInstance();

        // Some subscribers.
        Subscriber s01 = (publisher, topic, params)
                -> System.out.printf("s01: %s message received.\n", topic);
        Subscriber s02 = (publisher, topic, params)
                -> System.out.printf("s02: Hello %s message received. Extra = %d\n",
                params.get("name"), params.get("extra"));

        // Subscribe for some topics.
        System.out.println("Subscribing...");
        broker.subscribe("hello", s01);
        broker.subscribe("hello", s02);
        broker.subscribe("goodbye", s01);

        // Publishing
        System.out.println("Publishing...");
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Bob");
        params.put("extra", 10);
        broker.publish(this, "hello", params);

        broker.publish(this, "goodbye", null);

        System.out.println("Unsubscribing s01...");
        broker.unsubscribe(s01);

        System.out.println("Republishing...");
        broker.publish(this, "hello", params);
        broker.publish(this, "goodbye", null);
    }

    public static void main(String[] args) {
        new Main();
    }
}
