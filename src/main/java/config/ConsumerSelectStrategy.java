package config;

public enum ConsumerSelectStrategy {
    FIRST_AVAILABLE,  // 第一个可用
    ROUND_ROBIN,     // 轮询
    PRIORITY,        // 优先级
    LOAD_BALANCE     // 负载均衡
} 