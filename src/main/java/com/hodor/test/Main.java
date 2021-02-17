package com.hodor.test;

import org.springframework.container.ClassPathXmlApplicationContext;

/**
 * @author ：hodor007
 * @date ：Created in 2021/2/8
 * @description ：
 * @version: 1.0
 */
public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }
}
