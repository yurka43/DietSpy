package com.example.dietspy;

public class Nutrient {

    private String name;
    private int target, flag, units;

    public Nutrient(String name, int target, int flag, int units) {
        this.name = name;
        this.target = target;
        this.flag = flag;
        this.units = units;
    }

    public String getName() {
        return name;
    }

    public int getTarget() {
        return target;
    }

    public int getFlag() {
        return flag;
    }

    public int getUnits() {
        return units;
    }
}
