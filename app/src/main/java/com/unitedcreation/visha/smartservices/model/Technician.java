package com.unitedcreation.visha.smartservices.model;

public class Technician {

    private String name;
    private String number;
    private String type;
    private String category;

    Technician(){

    }

    public Technician(String name, String number, String type, String category) {

        this.name = name;
        this.number = number;
        this.type = type;
        this.category = category;

    }

    public String getName(){return name;}

    public String getNumber(){return number;}

    public String getType(){return type;}

    public String getCategory(){return category;}
}
