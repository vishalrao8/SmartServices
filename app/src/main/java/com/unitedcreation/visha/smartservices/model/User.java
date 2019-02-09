package com.unitedcreation.visha.smartservices.model;

public class User {

    private String name;
    private String number;
    private String type;
    //private String requestActive;

    User(){

    }

    public User(String name, String number, String type) {

        this.name = name;
        this.number = number;
        this.type = type;
        //this.requestActive=requestActive;

    }

    public String getName(){return name;}

    public String getNumber(){return number;}

    public String getType(){return type;}

    //public String getRequestActive(){return requestActive;}

}
