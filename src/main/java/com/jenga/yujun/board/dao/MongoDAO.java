package com.jenga.yujun.board.dao;


import com.jenga.yujun.board.dto.MongoDTO;
import com.jenga.yujun.member.dto.MemberDTO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class MongoDAO {


    private MongoTemplate mongoTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MongoDAO.class);

    @Autowired
    public MongoDAO(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void getAnyway(MemberDTO memberDTO, JSONObject json) {
        MongoDTO mongoDTO = new MongoDTO();

        mongoDTO.set_value(json);


        System.out.println(mongoTemplate);
//        mongoTemplate.insert(mongoDTO,"jenga");
        mongoTemplate.insert(mongoDTO,"c_block");


    }

    public String getView(String key, String bl_uid) {

        Criteria criteria = new Criteria(key);
        criteria.is(bl_uid);
        Query query = new Query(criteria);
        query.fields().include("_value");
        query.fields().exclude("_id");

        return mongoTemplate.findOne(query, String.class, "c_block");
    }

    public String getObjId(String key, String bl_uid) {

        Criteria criteria = new Criteria(key);
        criteria.is(bl_uid);

        Query query = new Query(criteria);

        MongoDTO mongoDTO = mongoTemplate.findOne(query, MongoDTO.class, "c_block");
        logger.info(mongoDTO.get_refBoardId());
        logger.info(mongoDTO.get_blockId());
        logger.info(mongoDTO.get_value().toString());
        return mongoDTO.get_blockId();

    }

    public void modifyViewPOST(String key, String bl_uid,  String bl_bookmarks) {

        MongoDTO mongoDTO = new MongoDTO();
        Criteria criteria = new Criteria(key);
        criteria.is(bl_uid);

        Query query = new Query(criteria);
        Update update = new Update();
        update.set("_value", bl_bookmarks);
        mongoTemplate.updateFirst(query, update, "c_block");

    }

    public void deleteBlock(String key, String bl_uid) {

        Criteria criteria = new Criteria(key);
        criteria.is(bl_uid);

        Query query = new Query(criteria);

        mongoTemplate.remove(query, "c_block");

    }

    public MongoDTO modifyViewGET(String key, String bl_uid) {

        Criteria criteria = new Criteria(key);
        criteria.is(bl_uid);

        Query query = new Query(criteria);

        return mongoTemplate.findOne(query, MongoDTO.class,"c_block");
    }

    public void writeViewBmks(String bl_uid,  String bl_bookmarks) {

        MongoDTO mongoDTO = new MongoDTO();

        mongoDTO.set_refBoardId(bl_uid);
        mongoDTO.set_value(bl_bookmarks);


        mongoTemplate.insert(mongoDTO,"c_block");
    }
}
