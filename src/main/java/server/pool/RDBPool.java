package server.pool;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.netty.handler.EchoServerHandler;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class RDBPool{
    private static final Logger LOGGER= LoggerFactory.getLogger(EchoServerHandler.class);
    private static BasicDataSource ds=new BasicDataSource();;

    static {
      DataSourceConfig();
    }

    private static void DataSourceConfig()
    {
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUsername("root");
        ds.setPassword("root");
        ds.setUrl("jdbc:mysql://localhost:3306/package?useUnicode=true&characterEncoding=utf-8&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        ds.setInitialSize(500);
        ds.setMaxTotal(1024);
        ds.setMaxIdle(500);
        ds.setMaxWaitMillis(60000);
        ds.setMinIdle(500);
    }

    public synchronized static Connection getConnection(){
        try {
            if(ds==null){
                LOGGER.error("mysql连接池为空");
                return null;
            }else{
                return ds.getConnection();
            }
        } catch (Exception e) {
            LOGGER.error("连接获取失败");
            throw new RuntimeException(e);
        }
    }

    public static void close(ResultSet rs, Statement st, Connection conn){
        if(rs!=null){
            try {
                rs.close();
            } catch (Exception e) {
                LOGGER.error("连接关闭失败");
                e.printStackTrace();
            }
        }
        if(st!=null){
            try {
                st.close();
            } catch (Exception e) {
                LOGGER.error("连接关闭失败");
                e.printStackTrace();
            }
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (Exception e) {
                LOGGER.error("连接关闭失败");
                e.printStackTrace();
            }
        }
    }
}
