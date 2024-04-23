package com.example.travel_mobile_app.models;

public class Location {
    private String address;
    private String event;
    private String imglink;
    private String introduce;
    private String name;
    private String number;
    private String opentime;
    private String price;

    public Location(String address, String event, String imglink, String introduce, String name, String number, String opentime, String price) {
        this.address = address;
        this.event = event;
        this.imglink = imglink;
        this.introduce = introduce;
        this.name = name;
        this.number = number;
        this.opentime = opentime;
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
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

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
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
                ", event='" + event + '\'' +
                ", intro='" + introduce + '\'' +
                ", openTime='" + opentime + '\'' +
                ", imgLink='" + imglink + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
