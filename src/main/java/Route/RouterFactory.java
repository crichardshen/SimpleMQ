package com.SimpleMQ.Route;

import java.security.Signature;
import java.util.List;

public class RouterFactory {
    public static AbsRouter GetRouter(String rule, String source, List<String> destinations)
    {
        switch (rule)
        {
            case "single":
                return new SingleRouter(source,destinations);
            case "round-robin":
                return new RoundRobinRouter(source,destinations);
            case "random":
                return new RandomRouter(source,destinations);
            default:
                throw new IllegalArgumentException("Unsupported rule: " + rule);
        }
    }
}
