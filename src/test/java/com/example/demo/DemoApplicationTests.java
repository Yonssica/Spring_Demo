package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.Manager.ConnectionPool;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    public static List<String> collectLocalTimes(LocalTime start, LocalTime end) {
        // 用起始时间作为流的源头，按照每次加一天的方式创建一个无限流
        return Stream.iterate(start, localTime -> localTime.plusHours(1))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(ChronoUnit.HOURS.between(start, end) + 1)
                // 由于最后要的是字符串，所以map转换一下
                .map(LocalTime::toString)
                // 把流收集为List
                .collect(Collectors.toList());
    }

    @Test
    public void contextLoads() {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
    }

    @Test
    public void test() {
        String time = "18-04-23T14:58:36Z".replace("Z", "CST");
        Date date = new Date(1524542400000L);
        SimpleDateFormat d1 = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat d2 = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        d2.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            time = d2.format(d1.parse(time));
            System.out.println(time);
            String time2 = d2.format(date);
            System.out.println(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        long current = System.currentTimeMillis();//当前时间毫秒数
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getTimeZone("Asia/Shanghai").getRawOffset();//今天零点零分零秒的毫秒数
        System.out.println(zero);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse("2099-02-02 12:00:00");
            Boolean b = date.after(DateUtils.addHours(new Date(), 1));
            System.out.println(b);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日 HH时mm分ss秒");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String time = dateFormat.format(new Date().getTime());
        System.out.println(time);
        System.out.println(time.length());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse("1526140800000", dateTimeFormatter);
        System.out.println(localDate);
    }

    @Test
    public void testAPI() {
        Logger LOGGER = LogManager.getLogger(DemoApplicationTests.class);
        long st = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            GetMethod method = null;
            String response = null;
            String result = null;
            long endTime = 0;
            long excTime = 0;
            // 开始时间
            long startTime = System.currentTimeMillis();
            try {
                HttpClient client = new HttpClient();
                client.getHttpConnectionManager().getParams().setConnectionTimeout(2000);
                client.getHttpConnectionManager().getParams().setSoTimeout(5000);
                String url = "http://localhost:8999/api/rest/firm/device/daily/alert?startTime=1525449600000&endTime=1525795200000&deviceId=268";
                method = new GetMethod(url);

                client.executeMethod(method);
                //打印服务器返回的状态
                if (method.getStatusLine().getStatusCode() != 200) {
                    LOGGER.error("insert data api returned HTTP status: " + method.getStatusLine());
                }
                InputStream stream = method.getResponseBodyAsStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                StringBuffer buf = new StringBuffer();
                String line;
                while (null != (line = br.readLine())) {
                    buf.append(line).append("\n");
                }

                response = buf.toString();
                JSONObject jsonObject = JSON.parseObject(response);
                result = null;
                if (null != jsonObject.get("data"))
                    result = jsonObject.get("data").toString();

                // 结束时间
                endTime = System.currentTimeMillis();
                // 处理报文消耗时间
                excTime = endTime - startTime;
                LOGGER.info(String.format("Processing message takes %d milliseconds", excTime));
            } catch (SocketTimeoutException e) {
                endTime = System.currentTimeMillis();
                excTime = endTime - startTime;
                LOGGER.error(String.format("连接超时，花费%d毫秒，Error: %s", excTime, e.toString()));
            } catch (ConnectException e) {
                endTime = System.currentTimeMillis();
                excTime = endTime - startTime;
                LOGGER.error(String.format("连接失败，花费%d毫秒，Error: %s", excTime, e.toString()));
            } catch (Exception e) {
                endTime = System.currentTimeMillis();
                excTime = endTime - startTime;
                LOGGER.error(String.format("连接时出现异常，花费%d毫秒，Error: %s", excTime, e.toString()));
            } finally {
                System.out.println(i);
                if (method != null) {
                    try {
                        //释放连接
                        method.releaseConnection();
                        LOGGER.info("Release connection");
                        LOGGER.info(String.format("Insert data 返回JSON: %s, 数据 %s", response, result));
                    } catch (Exception e) {
                        endTime = System.currentTimeMillis();
                        excTime = endTime - startTime;
                        LOGGER.error(String.format("释放网络连接失败，花费%d毫秒，Error: %s", excTime, e.toString()));
                    }
                }
            }
        }
        long et = System.currentTimeMillis();
        System.out.println("花费" + (et - st) + "毫秒");
    }

    @Test
    public void test4() {
        Date sDate = new Date(1525104000000L);
        Calendar cBegin = Calendar.getInstance();
        cBegin.setTime(sDate);
        cBegin.set(Calendar.MINUTE, 0);
        cBegin.set(Calendar.SECOND, 0);

        Date eDate = new Date(1525269600000L);
        Calendar cEnd = Calendar.getInstance();
        cEnd.setTime(eDate);
        cEnd.set(Calendar.MINUTE, 0);
        cEnd.set(Calendar.SECOND, 0);

        List<Date> dateList = new ArrayList<>();
        while (eDate.after(cBegin.getTime())) {
            cBegin.add(Calendar.HOUR_OF_DAY, 1);
            dateList.add(cBegin.getTime());
        }

        System.out.println(dateList);
    }

    @Test
    public void testMap() {
//        Float a = 1f;
//        Float b = 2f;
//        System.out.println(a <= b);
//        int num = Integer.parseInt("0010", 16);
//        System.out.println(num);

//        Double d = 0.07750698179006577;
//        System.out.println(String.format("%.2f", d));

        final Set<String> LEASE_OPERATION_SET = new HashSet<String>() {{
            add("confirm");
        }};
        System.out.println(LEASE_OPERATION_SET.contains(null));
    }

    @Test
    public void testRegex() {
        String regex_email = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        String email = "hh.1003186308@qq.com";
        boolean b = Pattern.matches(regex_email, email);
        System.out.println(b);
    }

    @Test
    public void testEvenMinutes() {
        Date time = new Date(1536076757000L);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int min = calendar.get(Calendar.MINUTE);
        if (min % 2 != 0) {
            calendar.set(Calendar.MINUTE, min + 1);
        }
        calendar.set(Calendar.SECOND, 0);
        Date evenMin = calendar.getTime();
        System.out.println(evenMin);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new Date());
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        Date date = calendar1.getTime();
        System.out.println(date);

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.set(Calendar.HOUR_OF_DAY, 0);
        gregorianCalendar.set(Calendar.MINUTE, 0);
        gregorianCalendar.set(Calendar.SECOND, 0);
        Date date1 = gregorianCalendar.getTime();
        System.out.println(date1);
    }

    @Test
    public void testRandom() {
        String str = "设备警报";
        StringBuilder utfcode = new StringBuilder();
        for (byte bit : str.getBytes(StandardCharsets.UTF_8)) {
            char hex = (char) (bit & 0xFF);
            utfcode.append(Integer.toHexString(hex));
        }
        System.out.println(utfcode.toString().toUpperCase());
    }

}
