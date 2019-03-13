package org.sunbird.akka.example1;

import java.util.ArrayList;
import java.util.List;

public class MyService {
    public static List<String> names = new ArrayList();

    public void add(String name) {
        System.out.println("Got you " + name);
        names.add(name);
    }

    public void print() {
        System.out.println("***********************");
        names.forEach(p -> System.out.println(p));
        System.out.println("***********************");
    }
}
