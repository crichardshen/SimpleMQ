@ConfigurationProperties(prefix = "simplemq")
@Component
public class SimpleMQProperties {
    private DispatcherType dispatcherType;
    private StorageType storageType;
    private DeliveryType deliveryType;
    
    // getters and setters
} 