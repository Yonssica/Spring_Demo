package com.example.demo.Controller;

import com.example.demo.DemoApplication;
import com.example.demo.Entity.RestResponse;
import com.example.demo.Manager.ConnectionPool;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class DataSourceController {

    private ConnectionPool connectionPool = null;
    public DataSourceController() {
        connectionPool = DemoApplication.connectionPool;
    }

    /**
     * 获取设备日均报警数
     * @param requestParam
     */
    @RequestMapping(value = "/api/report/daily/alert", method = RequestMethod.GET)
    public RestResponse getReportTotalDailyAlert(@RequestParam Map<String, String> requestParam) {
        Connection connection = connectionPool.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        Map<String, Object> responseMap = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = new Date(Long.valueOf(requestParam.get("startTime")));
        String start = dateFormat.format(startTime);
        Date endTime = new Date(Long.valueOf(requestParam.get("endTime")));
        String end = dateFormat.format(endTime);

        String sql = String.format("SELECT SUM(alert_count) sum, monitor_type_id FROM fact_alert_daily_sum" +
                        " WHERE device_id= %d and create_date >= '%s' AND create_date < '%s' GROUP BY monitor_type_id",
                Integer.valueOf(requestParam.get("deviceId")), start, end);
        try {
            statement = connection.createStatement();
            boolean ret = statement.execute(sql);

            if (ret) {
                resultSet = statement.getResultSet();
                List<Integer> sumList = new ArrayList<>();
                List<Integer> inspectTypeList = new ArrayList<>();
                while (resultSet.next()) {
                    sumList.add(resultSet.getInt("sum"));
                    inspectTypeList.add(resultSet.getInt("monitor_type_id"));
                    System.out.println(resultSet.getInt("sum") + ":"  + resultSet.getInt("monitor_type_id"));
                }
                responseMap.put("sumList", sumList);
                responseMap.put("inspectTypeList", inspectTypeList);
            }
            return new RestResponse(responseMap);
        } catch (SQLException e) {
            e.printStackTrace();
            return new RestResponse("ERROR", 500, e.toString());
        } finally {
            connectionPool.release(resultSet, statement, connection);
        }
    }

}
