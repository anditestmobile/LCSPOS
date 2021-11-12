package id.co.lcs.pos.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Payment {
    @SerializedName("QuotationNo")
    private String quoNo;
    @SerializedName("CardCode")
    private String cardCode;
    @SerializedName("CardName")
    private String cardName;
    @SerializedName("DocDate")
    private String docDate;
    @SerializedName("DocTotal")
    private double docTotal;
    @SerializedName("DiscPer")
    private double discPer;
    @SerializedName("DiscSum")
    private double discSum;
    @SerializedName("DownPayment")
    private double downPayment;
    @SerializedName("CardNumber")
    private String cardNumber;
    @SerializedName("CardAmount")
    private double cardAmount;
    @SerializedName("Cash")
    private double cash;
    @SerializedName("TransactionNo")
    private String transNo;
    @SerializedName("CardValidMonth")
    private String cValidMonth;
    @SerializedName("CardValidYear")
    private String cValidYear;
    @SerializedName("NEFTAmount")
    private double netsAmount;
    @SerializedName("TransferReference")
    private String transNetsNo;
    @SerializedName("ProductInfo")
    private List<PaymentItem> productInfo;

    public String getQuoNo() {
        return quoNo;
    }

    public void setQuoNo(String quoNo) {
        this.quoNo = quoNo;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }

    public double getDocTotal() {
        return docTotal;
    }

    public void setDocTotal(double docTotal) {
        this.docTotal = docTotal;
    }

    public List<PaymentItem> getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(List<PaymentItem> productInfo) {
        this.productInfo = productInfo;
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

    public double getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(double downPayment) {
        this.downPayment = downPayment;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public double getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(double cardAmount) {
        this.cardAmount = cardAmount;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getcValidMonth() {
        return cValidMonth;
    }

    public void setcValidMonth(String cValidMonth) {
        this.cValidMonth = cValidMonth;
    }

    public String getcValidYear() {
        return cValidYear;
    }

    public void setcValidYear(String cValidYear) {
        this.cValidYear = cValidYear;
    }

    public double getNetsAmount() {
        return netsAmount;
    }

    public void setNetsAmount(double netsAmount) {
        this.netsAmount = netsAmount;
    }

    public String getTransNetsNo() {
        return transNetsNo;
    }

    public void setTransNetsNo(String transNetsNo) {
        this.transNetsNo = transNetsNo;
    }
}
