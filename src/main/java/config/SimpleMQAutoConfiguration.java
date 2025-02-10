@Configuration
@EnableConfigurationProperties(SimpleMQProperties.class)
public class SimpleMQAutoConfiguration {
    
    @Bean
    public Dispatcher messageDispatcher(SimpleMQProperties properties) {
        switch (properties.getDispatcherType()) {
            case ORDERED:
                return new OrderedDispatcher();
            case ROUND_ROBIN:
                return new RoundRobinDispatcher();
            case RANDOM:
                return new RandomDispatcher();
            default:
                throw new IllegalArgumentException("Unknown dispatcher type");
        }
    }
} 