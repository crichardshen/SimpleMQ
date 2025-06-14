package com.SimpleMQ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.SimpleMQ")
public class SimpleMQApplication {


    public static void main(String[] args) {
        try
        {
            SpringApplication.run(SimpleMQApplication.class, args);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
} 
