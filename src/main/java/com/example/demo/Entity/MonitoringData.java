package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitoringData {
    private String DeviceCode;
    private String DeviceName;
    private List<Telemetries> Telemetries;

    public String getDeviceCode() {
        return DeviceCode;
    }

    public void setDeviceCode(String DeviceCode) {
        this.DeviceCode = DeviceCode;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String DeviceName) {
        this.DeviceName = DeviceName;
    }

    public List<Telemetries> getTelemetries() {
        return Telemetries;
    }

    public void setTelemetries(List<Telemetries> Telemetries) {
        this.Telemetries = Telemetries;
    }

    @Override
    public String toString() {
        return "MonitoringData{" +
                "DeviceCode='" + DeviceCode + '\'' +
                ", DeviceName='" + DeviceName + '\'' +
                ", Telemetries=" + Telemetries +
                '}';
    }
}
