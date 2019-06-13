package com.kaidongyuan.app.kdyorder.bean;

public class ChartOrderSumItem {

    private String BUSINESS_IDX;
    private String PARTY_CODE;
    private String PARTY_NAME;
    private String PRODUCT_NO;
    private String PRODUCT_NAME;
    private String NUMBER;
    private String AMOUNT_MONEY;

    public String getBUSINESS_IDX() {
        return BUSINESS_IDX;
    }
    public void setBUSINESS_IDX(String BUSINESS_IDX) {
        this.BUSINESS_IDX = BUSINESS_IDX;
    }

    public String getPARTY_CODE() {
        return PARTY_CODE;
    }
    public void setPARTY_CODE(String PARTY_CODE) {
        this.PARTY_CODE = PARTY_CODE;
    }

    public String getPARTY_NAME() {
        return PARTY_NAME;
    }
    public void setPARTY_NAME(String PARTY_NAME) {
        this.PARTY_NAME = PARTY_NAME;
    }

    public String getPRODUCT_NO() {
        return PRODUCT_NO;
    }
    public void setPRODUCT_NO(String PRODUCT_NO) {
        this.PRODUCT_NO = PRODUCT_NO;
    }

    public String getPRODUCT_NAME() {
        return PRODUCT_NAME;
    }
    public void setPRODUCT_NAME(String PRODUCT_NAME) {
        this.PRODUCT_NAME = PRODUCT_NAME;
    }

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

    @Override
    public String toString() {
        return "ChartOrderSum{" +
                "BUSINESS_IDX='" + BUSINESS_IDX + '\'' +
                ", PARTY_CODE=" + PARTY_CODE +
                ", PARTY_NAME=" + PARTY_NAME +
                ", PRODUCT_NO=" + PRODUCT_NO +
                ", PRODUCT_NAME=" + PRODUCT_NAME +
                ", NUMBER=" + NUMBER +
                ", AMOUNT_MONEY=" + AMOUNT_MONEY +
                '}';
    }
}
