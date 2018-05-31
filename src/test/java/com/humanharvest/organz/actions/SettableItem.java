package com.humanharvest.organz.actions;

public class SettableItem {

    private String string = "";
    private int anInt;
    private Integer integer;

    public void setString(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }
}
