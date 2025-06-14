package com.SimpleMQ.Route;
import java.util.List;

public class RoundRobinRouter extends AbsRouter{
    private static int currentTargetIndex = 0;

    public RoundRobinRouter(String source, List<String> destinations) {
        super(source, destinations);
    }


    @Override
    public void RountAndSelectDetination() {
        if(currentTargetIndex+1 < this.getDestinationList().size())
        {
            currentTargetIndex += 1;
        }
        else
        {
            currentTargetIndex = 0;
        }
    }

    @Override
    public String GetSelectedDetination() {
        return this.getDestinationList().get(currentTargetIndex);
    }
}
