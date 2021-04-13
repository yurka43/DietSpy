package com.example.dietspy;

public class Nutrient {

    private String name;
    private int target, flag;
    private Unit unit;

    public Nutrient(String name, int target, int flag, int units) {
        this.name = name;
        this.target = target;
        this.flag = flag;
        this.unit = Unit.values()[units];
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

    public String getUnits() {
        switch(unit) {
            case G:
                return "g";
            case MG:
                return "mg";
            case MCG:
                return "mcg";
            default:
                return "Error";
        }
    }
}
