package org.springframework.tx;

import org.springframework.xml.DbConfigParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/17
 * @description ：事务管理器
 * @version: 1.0
 */
public class TransactionManager {
    /**
     * 本地线程局部变量
     * 假设程序是多线程的，有很多个线程访问，A线程放的变量只有它自己能使用，其他的线程无法获取
     * 同一个线程存取的数据是同一个
     */
    static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

    static {
        Connection connection = getConnection();
        threadLocal.set(connection);
    }

    public static Connection getThreadLocalConnection() {
        return threadLocal.get();
    }

    private static Connection getConnection() {
        DbConfigParser dbConfigParser = new DbConfigParser();
        try {
            Connection conn = null;
            Class.forName(dbConfigParser.driver);
            conn = DriverManager.getConnection(dbConfigParser.url, dbConfigParser.username, dbConfigParser.password);
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
