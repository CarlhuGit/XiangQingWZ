package com.junit.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class JuintTest {

    private ApplicationContext ioc = new ClassPathXmlApplicationContext("spring-tx.xml");

    @Test
    public void test01() throws SQLException {

        DataSource dataSource = ioc.getBean(DataSource.class);
        Connection connection = dataSource.getConnection();

        System.out.println(connection);

    }
}
