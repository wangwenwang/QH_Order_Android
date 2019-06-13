package com.kaidongyuan.app.kdyorder.bean;

public class ChartOrderSum {

    private String NUMBER;
    private String AMOUNT_MONEY;
    private String BUSINESS_IDX;
    private String PARTY_NAME;
    private String ORDER_NUMBER;
    private String PARTY_CODE;

    public String getNUMBER() {
        return NUMBER;
    }
    public void setNUMBER(String NUMBER) {
        this.NUMBER = NUMBER;
    }

    public String getAMOUNT_MONEY() {
        return AMOUNT_MONEY;
    }
    public void setAMOUNT_MONEY(String AMOUNT_MONEY) {
        this.AMOUNT_MONEY = AMOUNT_MONEY;
    }

    public String getBUSINESS_IDX() {
        return BUSINESS_IDX;
    }
    public void setBUSINESS_IDX(String BUSINESS_IDX) {
        this.BUSINESS_IDX = BUSINESS_IDX;
    }

    public String getPARTY_NAME() {
        return PARTY_NAME;
    }
    public void setPARTY_NAME(String PARTY_NAME) {
        this.PARTY_NAME = PARTY_NAME;
    }

    public String getORDER_NUMBER() {
        return ORDER_NUMBER;
    }
    public void setORDER_NUMBER(String ORDER_NUMBER) {
        this.ORDER_NUMBER = ORDER_NUMBER;
    }

    public String getPARTY_CODE() {
        return PARTY_CODE;
    }
    public void setPARTY_CODE(String PARTY_CODE) {
        this.PARTY_CODE = PARTY_CODE;
    }

    @Override
    public String toString() {
        return "ChartOrderSum{" +
                "NUMBER='" + NUMBER + '\'' +
                ", AMOUNT_MONEY=" + AMOUNT_MONEY +
                ", BUSINESS_IDX=" + BUSINESS_IDX +
                ", PARTY_NAME=" + PARTY_NAME +
                ", ORDER_NUMBER=" + ORDER_NUMBER +
                ", PARTY_CODE=" + PARTY_CODE +
                '}';
    }
}
