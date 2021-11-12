package id.co.lcs.pos.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class PaymentItem implements Serializable {
    @SerializedName("ItemCode")
    private String itemCode;
    @SerializedName("ItemName")
    private String itemName;
    @SerializedName("UnitPrice")
    private double unitPrice;
    @SerializedName("Quantity")
    private double qty;
    @SerializedName("DiscPer")
    private double discPer;
    @SerializedName("DiscSum")
    private double discSum;
    @SerializedName("LineTotal")
    private double lineTotal;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = lineTotal;
    }

    public double getDiscPer() {
        return discPer;
    }

    public void setDiscPer(double discPer) {
        this.discPer = discPer;
    }

    public double getDiscSum() {
        return discSum;
    }

    public void setDiscSum(double discSum) {
        this.discSum = discSum;
    }
}
