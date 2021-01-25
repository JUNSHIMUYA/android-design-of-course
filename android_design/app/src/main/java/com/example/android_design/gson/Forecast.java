package com.example.android_design.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature
    {
        public String max;
        public String min;
    }

    public class More
    {
        @SerializedName("txt_d")
        public  String info;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public void setMore(More more) {
        this.more = more;
    }
}
