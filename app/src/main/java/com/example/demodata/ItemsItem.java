package com.example.demodata;

public class ItemsItem
{
    String name;
    String MRP;
    String QTY;
    String TvAmount;

    public ItemsItem(String name, String MRP, String QTY, String tvAmount) {
        this.name = name;
        this.MRP = MRP;
        this.QTY = QTY;
        TvAmount = tvAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getQTY() {
        return QTY;
    }

    public void setQTY(String QTY) {
        this.QTY = QTY;
    }

    public String getTvAmount() {
        return TvAmount;
    }

    public void setTvAmount(String tvAmount) {
        TvAmount = tvAmount;
    }
}