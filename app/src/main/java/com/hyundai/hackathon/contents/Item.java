package com.hyundai.hackathon.contents;

import java.util.Comparator;

/**
 * Created by Cho on 2016-08-20.
 */
public class Item implements Comparable<Item>{
    public String title;
    public String imageUrl;
    public String address;
    public String newAddress;
    public String zipcode;
    public String phone;
    public double longitude;
    public double latitude;
    public double distance;
    public String category;
    public String id;
    public String placeUrl;
    public String direction;
    public String addressBCode;
    public int angle;
    public float x;
    public float y;

    @Override
    public int compareTo(Item si) {

        if (this.distance >=  si.distance) return -1;
        else return 1;

    }
}
