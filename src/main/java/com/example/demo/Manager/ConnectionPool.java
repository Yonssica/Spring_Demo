package com.example.demo.Manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/**
 * 数据库连接池的公共类
 **/
public class ConnectionPool {
    private Vector<Connection> pool;//声明集合，里面只能是放Connection
    /**
     * 声明要的东西
     */
    private String url = "jdbc:sqlserver://ils-deploy-resources.database.chinacloudapi.cn:1433; database=ils-deploy-database";
    private String username = "ilabservice";
    private String password = "U~~i^Vzvq(rxtUtPQ*gN";
    private String driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /**
     * 连接池的大小，也就是连接池中有多少个数据库连接
     */
    private int poolSize = 5;
    private static ConnectionPool instance = null;

    /**
     * 私有的构造方法，禁止外部创建本类的对象，要想获得本类的对象，通过<code>getInstance</code>方法
     * 使用了设计模式中的单子模式
     */
    private ConnectionPool() {
        init();
    }

    /**
     * 连接池初始化方法，读取属性文件的内容 建立连接池中的初始连
     */
    private void init() {
        pool = new Vector<Connection>(poolSize);
//readConfig();
        addConnection();
    }

    /**
     * 返回连接到连接池
     */
    public synchronized void release(ResultSet resultSet, Statement statement, Connection conn) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        pool.add(conn);
    }

    /**
     * 关闭连接池中的所有数据库连接
     */
    public synchronized void closePool() {
        for (int i = 0; i < pool.size(); i++) {
            try {
                ((Connection) pool.get(i)).close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            pool.remove(i);
        }
    }

    /**
     * 返回当前连接池的对象
     */
    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    /**
     * 返回连接池中的一个数据库连接
     */
    public synchronized Connection getConnection() {
        if (pool.size() > 0) {
            Connection conn = pool.get(0);
            pool.remove(conn);
            return conn;
        } else {
            return null;
        }
    }

    /**
     * 在连接池中创建初始设置的的数据库连接
     */
    private void addConnection() {
        Connection conn = null;
        for (int i = 0; i < poolSize; i++) {
            try {
                Class.forName(driverClassName);
                conn = java.sql.DriverManager.getConnection(url, username, password);
                pool.add(conn);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
