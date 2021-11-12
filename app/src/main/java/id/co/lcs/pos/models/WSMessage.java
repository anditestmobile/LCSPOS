package id.co.lcs.pos.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class WSMessage implements Serializable {
    @SerializedName("DocNo")
    private String docNo;
    @SerializedName("Message")
    private String message;
    @SerializedName("Type")
    private String type;

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
