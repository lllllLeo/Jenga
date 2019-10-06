package com.jenga.yujun;

import org.apache.ibatis.session.SqlSessionFactory;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@MapperScan(basePackages = "mybatis")
@EnableTransactionManagement
public class MyBatisConfig {

    /*
    다른 클래스들이 사용할 수 있게 sqlSessionTemplate 생성자에 Mybatis 매퍼 설정 정보가 담긴 sqlSessionFactory 빈을 전달해서 빈을 생성함
    Mybatis-config.xml 파일에 대한 설정은 없어도 동작하지만 Mybatis 관련 설정 파일이 없다고 메시지가 출력되고, Mybatis에 대한 보다 세밀한 설정 제어가 필요한 경우에 사용하면 됨.
    */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        final SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sqlSessionTemplate;
    }

    /*
    sqlSessionFactory에서는 sql 매퍼들이 위치하는 경로와 Mybatis 설정 파일 경로를 지정
    */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        //        sqlSessionFactoryBean.setConfigLocation((new PathMatchingResourcePatternResolver().getResource("classpath:/mybatis-config.xml")));

        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

}
