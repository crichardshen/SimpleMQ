package com.SimpleMQ.Route;


import java.util.List;
import java.util.Objects;

public class SingleRouter extends AbsRouter {
    private String specifiedTarget;
    private int specifiedTargetIndex;

    public SingleRouter(String source, List<String> destinations) {
        super(source, destinations);
    }


    public void SetTarget(String targetDestination)
    {
        if(!this.getDestinationList().contains(targetDestination))
        {
            this.specifiedTargetIndex = this.getDestinationList().indexOf(targetDestination);
        }
        else
        {
            this.specifiedTargetIndex = 0;
        }
    }

    @Override
    public void RountAndSelectDetination() {
        //to check whether the specified target is in destinations or not
        //if not set specified target to null
        if(this.getDestinationList().size() > specifiedTargetIndex)
        {
            this.specifiedTarget = this.getDestinationList().get(specifiedTargetIndex);
        }
        else
        {
            this.specifiedTarget = this.getDestinationList().get(0);
        }
    }

    @Override
    public String GetSelectedDetination() {
        return this.specifiedTarget;
    }
}
