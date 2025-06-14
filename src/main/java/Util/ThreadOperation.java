package com.SimpleMQ.Util;

public class ThreadOperation {
    public static String GetCurrentThreadName()
    {
        return Thread.currentThread().getName();
    }

    public static void SetCurrentThreadName(String name)
    {
        Thread.currentThread().setName(name);
    }

    public static String ToString()
    {
        return "[Thread: "+ThreadOperation.GetCurrentThreadName()+"]";
    }
}
