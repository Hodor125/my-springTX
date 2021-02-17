package com.hodor.bean;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/7
 * @description ：
 * @version: 1.0
 */
public class Order {

    private int oid;
    private String title;
    private String createTime;
    private double money;

    public Order() {
    }

    public Order(int oid, String title, String createTime, double money) {
        this.oid = oid;
        this.title = title;
        this.createTime = createTime;
        this.money = money;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
