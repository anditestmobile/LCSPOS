package id.co.lcs.pos.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class SerialNumber implements Serializable {
    @SerializedName("SerialNumber")
    private String serialNumber;
    @SerializedName("SysSerial")
    private String sysSerial;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSysSerial() {
        return sysSerial;
    }

    public void setSysSerial(String sysSerial) {
        this.sysSerial = sysSerial;
    }
}
