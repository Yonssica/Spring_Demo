package com.example.demo.Entity;

import com.example.demo.Entity.TimeAndValue;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Telemetries {
    private String MonitorType;
    private String Unit;
    private List<TimeAndValue> Datas;

    public String getMonitorType() {
        return MonitorType;
    }

    public void setMonitorType(String MonitorType) {
        this.MonitorType = MonitorType;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public List<TimeAndValue> getTimeAndValues() {
        return Datas;
    }

    public void setTimeAndValues(List<TimeAndValue> Datas) {
        this.Datas = Datas;
    }

    @Override
    public String toString() {
        return "Telemetries{" +
                "MonitorType='" + MonitorType + '\'' +
                ", Unit='" + Unit + '\'' +
                ", timeAndValues=" + Datas +
                '}';
    }
}
