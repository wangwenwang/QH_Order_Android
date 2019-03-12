package com.kaidongyuan.app.kdyorder.bean;

public class KpiTrack implements java.io.Serializable {

    private String AmountMoney;
    private String CompleteAmountMoney;
    private String CompleteSalesVolume;
    private String SalesVolume;

    public String getSalesVolume() {
        return SalesVolume;
    }

    public String getCompleteSalesVolume() {
        return CompleteSalesVolume;
    }

    public String getAmountMoney() {
        return AmountMoney;
    }

    public String getCompleteAmountMoney() {
        return CompleteAmountMoney;
    }

    public void setAmountMoney(String AmountMoney) {
        this.AmountMoney = AmountMoney;
    }

    public void setCompleteAmountMoney(String CompleteAmountMoney) {
        this.CompleteAmountMoney = CompleteAmountMoney;
    }

    public void setCompleteSalesVolume(String CompleteSalesVolume) {
        this.CompleteSalesVolume = CompleteSalesVolume;
    }

    public void setSalesVolume(String SalesVolume) {
        this.SalesVolume = SalesVolume;
    }

    @Override
    public String toString() {
        return "KpiTrack{" +
                "AmountMoney='" + AmountMoney + '\'' +
                ", CompleteAmountMoney='" + CompleteAmountMoney + '\'' +
                ", CompleteSalesVolume=" + CompleteSalesVolume +
                ", SalesVolume=" + SalesVolume +
                '}';
    }
}
