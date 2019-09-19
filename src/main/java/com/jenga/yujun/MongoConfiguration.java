package com.jenga.yujun;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

/*
credential을 사용하는 경우 저런식으로 credential 객체를 만들어줘야 합니다.
그렇지 않으면 Command aggregate failed: not authorized on DB to execute command 에러가 발생하며 Mongo DB 기능을 이용할 수 없습니다.
*/
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.url}")
    private String url;
    @Value("{spring.data.mongodb.username}")
    private String username;
    @Value("{spring.data.mongodb.password}")
    private String password;

    @Override
    public MongoClient mongoClient() {
        MongoClientURI mongoClientURI = new MongoClientURI(this.url);
        MongoCredential mongoCredential = MongoCredential.createCredential(this.username, mongoClientURI.getDatabase(), this.password.toCharArray());
        ServerAddress serverAddress = new ServerAddress(mongoClientURI.getHosts().get(0));
        MongoClientOptions options = MongoClientOptions.builder().build();
        return new MongoClient(serverAddress, mongoCredential, options);
    }

    @Override
    protected String getDatabaseName() {
        return new MongoClientURI(this.url).getDatabase();
    }
}
