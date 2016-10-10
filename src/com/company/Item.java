package com.company;

public class Item implements Cloneable{
    String name;
    int power;
    String Garbgeb1;

    String garbageC4;

    String GarbageB2;
    Item (String name,int power) {
        this.name = name;
        this.power = power;
    }

    public Object clone () throws CloneNotSupportedException{
        Item it = new Item(name,power);
        return it;
    }
}
