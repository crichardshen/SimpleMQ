package com.SimpleMQ.Route;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class AbsRouter {
    private String source;
    private List<String> destinationList;
    //use round-robin mode in defaul
    private RouteRule routeRule = RouteRule.ROUNDROBIN;

    public AbsRouter(String source, List<String> destinations)
    {
        this.source = source;
        this.destinationList = destinations;
    }

    public abstract void RountAndSelectDetination();
    public abstract String GetSelectedDetination();
}

