package com.example.travel_mobile_app.models;

public class Location {
    private long closetime;
    private String address;
    private String imglink;
    private String introduce;
    private String name;
    private String number;
    private long opentime;
    private String price;
    private String id;

    public Location(String id, String address,  String imglink, String introduce, String name, String number, long opentime, long closetine, String price) {
        this.id = id;
        this.address = address;
        this.imglink = imglink;
        this.introduce = introduce;
        this.name = name;
        this.number = number;
        this.opentime = opentime;
        this.closetime = closetine;
        this.price = price;
    }

    public Location() {}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public long getClosetime() {
        return closetime;
    }

    public void setClosetime(long closetime) {
        this.closetime = closetime;
    }

    public String getImglink() {
        return imglink;
    }

    public void setImglink(String imglink) {
        this.imglink = imglink;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getOpentime() {
        return opentime;
    }

    public void setOpentime(long opentime) {
        this.opentime = opentime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", price='" + price + '\'' +
                ", intro='" + introduce + '\'' +
                ", openTime='" + opentime + '\'' +
                ", closeTime='" + closetime + '\'' +
                ", imgLink='" + imglink + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
