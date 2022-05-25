package id.co.lcs.pos.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Invoice implements Serializable {
    @SerializedName("Invoice")
    private String invoice;

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }
}
