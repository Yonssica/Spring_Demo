package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeAndValue {
    private String Time;
    private float Value;

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public float getValue() {
        return Value;
    }

    public void setValue(float Value) {
        this.Value = Value;
    }

    @Override
    public String toString() {
        return "TimeAndValue{" +
                "Time='" + Time +
                "', Value=" + Value +
                '}';
    }
}
