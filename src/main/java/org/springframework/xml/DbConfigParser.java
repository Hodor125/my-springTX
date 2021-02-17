package org.springframework.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/17
 * @description ：
 * @version: 1.0
 */
public class DbConfigParser {

    public static String driver;
    public static String url;
    public static String username;
    public static String password;
    private static String applicationContext;

    public DbConfigParser() {
    }

    //获取配置文件数据库配置
    public static String getProperties(String springconfig) {
        String properties = "";
        InputStream is = null;
        try {
            SAXReader reader = new SAXReader();
            //使用当前线程的类加载器得到得到流对象
//            is = SpringConfigPaser.class.getClassLoader().getResourceAsStream(springconfig);
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(springconfig);
            Document document = reader.read(is);
            //得到根节点beans
            Element rootElement = document.getRootElement();
            Element element = rootElement.element("resources");
            Attribute attribute = element.attribute("locatioin");
            properties = attribute.getText();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    static{
        try {
            Properties properties = new Properties();
            properties.load(DbConfigParser.class.getClassLoader().getResourceAsStream(getProperties("applicationContext.xml")));
            driver = (String) properties.get("jdbc.driver");
            url = (String) properties.get("jdbc.url");
            username = (String) properties.get("jdbc.username");
            password = (String) properties.get("jdbc.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
