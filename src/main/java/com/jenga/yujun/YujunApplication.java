package com.jenga.yujun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication(exclude={DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class}) 이거 ㅅㅂ 지우니까 MybatisConfiguration에있는 DataSource 빨간줄 없어지네 굿.. 시간 개날렸네
@SpringBootApplication
public class YujunApplication {

    public static void main(String[] args) {
        SpringApplication.run(YujunApplication.class, args);
    }
}
