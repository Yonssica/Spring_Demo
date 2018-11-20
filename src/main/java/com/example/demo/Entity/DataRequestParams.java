package com.example.demo.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude
public class DataRequestParams {
    private long start_time;
    private long end_time;
    private List<MonitoringData> monitoring_data;

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public List<MonitoringData> getMonitoring_data() {
        return monitoring_data;
    }

    public void setMonitoring_data(List<MonitoringData> monitoring_data) {
        this.monitoring_data = monitoring_data;
    }

    @Override
    public String toString() {
        return "DataRequestParams{" +
                "start_time=" + start_time +
                ", end_time=" + end_time +
                ", monitoring_data=" + monitoring_data +
                '}';
    }
}
