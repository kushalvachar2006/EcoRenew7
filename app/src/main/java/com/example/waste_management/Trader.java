package com.example.waste_management;
public class Trader {
    private String traderId;
    private String address;

    public Trader() {
        // Default constructor required for calls to DataSnapshot.getValue(Trader.class)
    }

    public Trader(String traderId, String address) {
        this.traderId = traderId;
        this.address = address;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
