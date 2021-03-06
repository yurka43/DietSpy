package com.example.dietspy;

public class Nutrient {

    private String name;
    private int flag, unitInt;
    private double target;
    private Unit unit;

    public Nutrient(String name, double target, int flag, int units) {
        this.name = name;
        this.target = target;
        this.flag = flag;
        this.unitInt = units;
        this.unit = Unit.values()[units];
    }

    public String getName() {
        return name;
    }

    public double getTarget() {
        return target;
    }

    public int getFlag() {
        return flag;
    }

    public int getUnitInt() { return unitInt; }

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
