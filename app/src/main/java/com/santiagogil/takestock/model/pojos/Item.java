package com.santiagogil.takestock.model.pojos;

import com.google.firebase.database.PropertyName;
import com.santiagogil.takestock.model.daos.DatabaseHelper;

public class Item { //implements Comparable<Item>, Serializable {

    public static final Integer DEFAULT_CONSUMPTION_RATE = 90;

    @PropertyName(DatabaseHelper.ID)
    private String ID;
    @PropertyName(DatabaseHelper.NAME)
    private String name;
    @PropertyName(DatabaseHelper.STOCK)
    private Integer stock;
    @PropertyName(DatabaseHelper.MINIMUMPURCHACEQUANTITY)
    private Integer minimumPurchaceQuantity;
    @PropertyName(DatabaseHelper.IMAGE)
    private Integer image;
    @PropertyName(DatabaseHelper.CONSUMPTIONRATE)
    private Integer consumptionRate;
    @PropertyName (DatabaseHelper.ACTIVE)
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Item(){

    }


    public Integer getConsumptionRate() {
        return consumptionRate;
    }

    public void setConsumptionRate(Integer consumptionRate) {
        this.consumptionRate = consumptionRate;
    }

    public Item(String name) {
        this.name = name;
        this.stock = 0;
        minimumPurchaceQuantity = 1;
        image = 0;
        active = true;
        consumptionRate = DEFAULT_CONSUMPTION_RATE;

    }

    public String getName() {
        return name;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getMinimumPurchaceQuantity() {
        return minimumPurchaceQuantity;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getID() {
        return ID;
    }

    public Integer getImage() {
        return image;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMinimumPurchaceQuantity(Integer minimumPurchaceQuantity) {
        this.minimumPurchaceQuantity = minimumPurchaceQuantity;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public Integer getIndependence(){
        return Math.round(getConsumptionRate() * getStock());
    }
}
