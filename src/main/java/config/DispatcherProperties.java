package config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

@Component
@ConfigurationProperties(prefix = "simplemq.dispatcher")
public class DispatcherProperties {
    private ConsumerSelectStrategy consumerSelectStrategy = ConsumerSelectStrategy.FIRST_AVAILABLE;
    private List<String> priorityHosts = new ArrayList<>();  // 按优先级排序的主机列表
    
    public ConsumerSelectStrategy getConsumerSelectStrategy() {
        return consumerSelectStrategy;
    }
    
    public void setConsumerSelectStrategy(ConsumerSelectStrategy strategy) {
        this.consumerSelectStrategy = strategy;
    }
    
    public List<String> getPriorityHosts() {
        return priorityHosts;
    }
    
    public void setPriorityHosts(List<String> hosts) {
        this.priorityHosts = hosts;
    }
    
    // 根据hostname获取优先级
    public int getPriorityForHost(String hostname) {
        int index = priorityHosts.indexOf(hostname);
        return index >= 0 ? priorityHosts.size() - index : 0;  // 列表靠前的优先级更高
    }
} 