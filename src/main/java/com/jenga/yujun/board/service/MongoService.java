package com.jenga.yujun.board.service;



import com.jenga.yujun.board.repository.MongoDAO;
import com.jenga.yujun.board.dto.MongoDTO;
import com.jenga.yujun.member.dto.MemberDTO;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoService {

    private MongoDAO dao;

    @Autowired
    public MongoService(MongoDAO dao) {
        this.dao = dao;
    }

    public void getAnyway(MemberDTO member, JSONObject json) { dao.getAnyway(member, json); }

    public MongoDTO modifyViewGET(String key, String bl_uid) {
        return dao.modifyViewGET(key, bl_uid);
    }

    public void writeViewBmks(String bl_uid, String bl_bookmarks) { dao.writeViewBmks(bl_uid, bl_bookmarks); }

    public String getView(String key, String bl_uid) { return dao.getView(key, bl_uid); }

    public String getObjId(String key, String bl_uid) { return dao.getObjId(key, bl_uid); }

    public void modifyViewPOST(String key, String bl_uid, String bl_bookmarks) { dao.modifyViewPOST(key, bl_uid, bl_bookmarks); }

    public void deleteBlock(String key, String bl_uid) { dao.deleteBlock(key, bl_uid); }
}
