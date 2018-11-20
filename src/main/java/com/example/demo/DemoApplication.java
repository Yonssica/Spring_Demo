package com.example.demo;

import com.example.demo.Controller.DataSourceController;
import com.example.demo.Manager.ConnectionPool;
import org.omg.CORBA.portable.ValueOutputStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static ConnectionPool connectionPool = null;

    public static void main(String[] args) {
        startConnectionPool();
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * 开启连接池
     */
    public static void startConnectionPool() {
        connectionPool = ConnectionPool.getInstance();
    }
}
