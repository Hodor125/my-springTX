package org.springframework.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * 解析xml文件
 * @author ：hodor007
 * @date ：Created in 2021/2/7
 * @description ：
 * @version: 1.0
 */
public class SpringConfigPaser {

    //获取配置文件中的扫描包属性
    public static String getBasePackage(String springconfig) {
        String basePackage = "";
        InputStream is = null;
        try {
            SAXReader reader = new SAXReader();
            //使用当前线程的类加载器得到得到流对象
//            is = SpringConfigPaser.class.getClassLoader().getResourceAsStream(springconfig);
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(springconfig);
            Document document = reader.read(is);
            //得到根节点beans
            Element rootElement = document.getRootElement();
            Element element = rootElement.element("component-scan");
            Attribute attribute = element.attribute("base-package");
            basePackage = attribute.getText();
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
        return basePackage;
    }
}
