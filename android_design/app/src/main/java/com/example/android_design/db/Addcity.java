package com.example.android_design.db;

public class Addcity {
    public int id;
    public String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString()
    {
        String result="";
        result += "id：" + this.id + "，";
        result+="名字："+this.name+",";
        return result;
    }
}
