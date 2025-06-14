package com.SimpleMQ.Route;

import java.util.List;
import java.util.Random;

public class RandomRouter extends AbsRouter{
    private int currentTargetIndex = 0;

    public RandomRouter(String source, List<String> destinations) {
        super(source, destinations);
    }

    @Override
    public void RountAndSelectDetination() {
        if(this.getDestinationList().size() == 1)
        {
            this.currentTargetIndex = 0;
            return;
        }

        Random random = new Random();
        this.currentTargetIndex = random.nextInt(this.getDestinationList().size());
    }

    @Override
    public String GetSelectedDetination() {
        return this.getDestinationList().get(this.currentTargetIndex);
    }
}
