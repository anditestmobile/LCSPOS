package id.co.lcs.pos.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuotationItemResponse implements Serializable {
    @SerializedName("Description")
    private String description;
    @SerializedName("ItemCode")
    private String itemCode;
    @SerializedName("ListPrice")
    private double price;
    @SerializedName("Price")
    private double discPrice;
    @SerializedName("Quantity")
    private String qty;
    @SerializedName("Discount")
    private double disc;

    private transient int discMethod;
    private transient double totPrice;
    private transient double nettPrice;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public double getDisc() {
        return disc;
    }

    public void setDisc(double disc) {
        this.disc = disc;
    }

    public double getTotPrice() {
        return totPrice;
    }

    public void setTotPrice(double totPrice) {
        this.totPrice = totPrice;
    }

    public double getNettPrice() {
        return nettPrice;
    }

    public void setNettPrice(double nettPrice) {
        this.nettPrice = nettPrice;
    }

    public int getDiscMethod() {
        return discMethod;
    }

    public void setDiscMethod(int discMethod) {
        this.discMethod = discMethod;
    }

    public double getDiscPrice() {
        return discPrice;
    }

    public void setDiscPrice(double discPrice) {
        this.discPrice = discPrice;
    }
}
