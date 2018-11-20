package com.example.demo.Manager;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InfluxDBManager {
    String serverIp;
    Integer port = 8086;

    InfluxDB influxDB = null;
    protected static Logger logger = LogManager.getLogger(InfluxDBManager.class);

    public InfluxDBManager(String serverIp, Integer port){
        this.serverIp = serverIp;
        this.port = port;
        influxDB = InfluxDBFactory.connect(String.format("http://%s:%d", serverIp, port));

    }

    public InfluxDBManager(String serverIp){
        this.serverIp = serverIp;

        influxDB = InfluxDBFactory.connect(String.format("http://%s:%d", serverIp, port));

    }

    public InfluxDBManager(String serverIp, String username, String password){
        this.serverIp = serverIp;
        influxDB = InfluxDBFactory.connect(String.format("http://%s:%d", serverIp, port), username, password);
    }

    public InfluxDBManager(String serverIp, Integer port, String username, String password){
        this.serverIp = serverIp;
        this.port = port;

        influxDB = InfluxDBFactory.connect(String.format("http://%s:%d", serverIp, port), username, password);
    }

    /**
     * 查询指定设备指定measurement指定参数指定时间内的报警信息。 注意， 同一设备的可能对同种measurement有多个参数。
     * 例如， 一台大型设备可能不同的部分会有不同的温度
     * @param inspectType
     * @param deviceId
     * @param deviceInspectId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<List<Object>> readTelemetryInTimeRange(String inspectType, Integer deviceId, Integer deviceInspectId, Date startTime, Date endTime, int granularity){

        String dbName = "intelab";

        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return null;
        }

        String retentionPolicy;

        switch(granularity){
            case Calendar.MINUTE: retentionPolicy = "ten_min"; break;
            case Calendar.HOUR: retentionPolicy = "hourly"; break;
            case Calendar.DATE: retentionPolicy = "daily"; break;
            default: retentionPolicy="original_telemetry";
        }

        String queryString = null;

        if(retentionPolicy == "original_telemetry"){
            queryString = String.format("SELECT value FROM %s WHERE device_id='%d' AND inspect_id='%d' AND time >= %d AND time <= %d ORDER BY time",
                    inspectType, deviceId, deviceInspectId, startNano, endNano);
        }else{
            queryString = String.format("SELECT mean_value as value FROM %s.%s WHERE device_id='%d' AND inspect_id='%d' AND time >= %d AND time <= %d ORDER BY time",
                    retentionPolicy, inspectType, deviceId, deviceInspectId, startNano, endNano);
        }

        logger.info("readTelemetryInTimeRange queryString : " + queryString);

        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

            if(resultList != null && resultList.size() > 0){
                QueryResult.Result tsData = resultList.get(0);

                List<QueryResult.Series> series = tsData.getSeries();

                if(series != null && series.size() > 0){
                    String measurementName = series.get(0).getName();
                    List<String> columes = series.get(0).getColumns();

                    // columes should be ['time', 'value']

                    if(columes.size() != 2 || !columes.contains("value") || !columes.contains("time")){
                        logger.error("The series in query result is incorrect, no time or value");
                        return null;
                    }

                    return series.get(0).getValues();

                }

            }

            return null;


        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return null;
        }
    }
}
