package com.SimpleMQ.Util;

import com.SimpleMQ.Message.RawMessage;
import com.SimpleMQ.Message.ResponseMessage;
import com.SimpleMQ.Route.AbsRouter;
import com.SimpleMQ.Route.RouterFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RouteOperation {
    public static String GetRoutedTargetOnResponseMessage(String routeRule, ResponseMessage msg,
                                                          Map<String,String[]> routetable) throws Exception {
        List<String> destinations = Arrays.asList(routetable.get(msg.getOriginalMsg().getTargetClientID()));
        if((long) destinations.size() ==0)
        {
            throw new Exception("No destination found with id {"+msg.getOriginalMsg().getTargetClientID()
                    +"} in response message");
        }

        if(destinations.size()==1)
        {
            return msg.getOriginalMsg().getTargetClientID();
        }

        AbsRouter router = RouterFactory.GetRouter(routeRule,msg.getClientID(),destinations);
        router.RountAndSelectDetination();
        return router.GetSelectedDetination();
    }

    public static String GetRoutedTargetOnRequestMessage(String routeRule, RawMessage msg,
                                                         Map<String,String[]> routetable)
    {
        List<String> destinations = Arrays.asList(routetable.get(msg.getTargetClientID()));
        if((long) destinations.size() ==0)
        {
            return "";
        }

        if(destinations.size()==1)
        {
            return msg.getTargetClientID();
        }

        AbsRouter router = RouterFactory.GetRouter(routeRule,msg.getClientID(),destinations);
        router.RountAndSelectDetination();
        return router.GetSelectedDetination();
    }
}
