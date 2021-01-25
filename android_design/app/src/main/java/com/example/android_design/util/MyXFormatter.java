package com.example.android_design.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

public class MyXFormatter implements IAxisValueFormatter {
    private List<String> mValues;

    public MyXFormatter(List<String> values)
    {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis)
    {
        if(((int)value >=0 && (int)value < mValues.size()))
            return mValues.get((int) value);
        else
            return "";
    }
}