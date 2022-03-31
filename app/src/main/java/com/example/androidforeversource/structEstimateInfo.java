package com.example.androidforeversource;

public class structEstimateInfo {
    private String name;
    private String cost;
    private  String dateOfCreation;

    public structEstimateInfo(String name, String cost, String dateOfCreation){
        this.name = name;
        this.cost = cost;
        this.dateOfCreation = dateOfCreation;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getCost(){
        return cost;
    }
    public void setCost(String cost){
        this.cost = cost;
    }
    public String getDateOfCreation(){
        return dateOfCreation;
    }
    public void setDateOfCreation(String dateOfCreation){
        this.dateOfCreation = dateOfCreation;
    }
}
