package id.co.lcs.pos.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Customer implements Serializable {
    @SerializedName("CardCode")
    private String custCode;
    @SerializedName("CardName")
    private String custName;

    public Customer() {
    }

    public Customer(String custCode, String custName) {
        this.custCode = custCode;
        this.custName = custName;
    }

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }
}
