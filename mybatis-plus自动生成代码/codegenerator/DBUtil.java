package com.dfdk.common.codegenerator;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;

import java.sql.*;

public class DBUtil {

    private String user = "";
    private String password = "";
    private String url = "";

    public DBUtil(){
        DataSourceConfig dsc = getDataSourceConfig();
        this.url=dsc.getUrl();
        this.user=dsc.getUsername();
        this.password=dsc.getPassword();
    }

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.url, this.user, this.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public  void closeJDBC(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static DataSourceConfig getDataSourceConfig(){
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://192.168.100.203:3306/huizhou_electric_cable_system?useUnicode=true&useSSL=false&characterEncoding=utf8");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("df5520127");

        return dsc;
    }
}
