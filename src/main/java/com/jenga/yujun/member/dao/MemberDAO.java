package com.jenga.yujun.member.dao;


import com.jenga.yujun.member.dto.EmailMemberDTO;
import com.jenga.yujun.member.dto.MemberDTO;
import com.jenga.yujun.member.dto.SocialMemberDTO;
import com.jenga.yujun.member.util.cipher.AES256Cipher;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MemberDAO{

    private static final Logger logger = LoggerFactory.getLogger(MemberDAO.class);
    private SqlSession sqlSession;
    private AES256Cipher aes256Cipher;

    @Autowired
    public MemberDAO(SqlSession sqlSession, AES256Cipher aes256Cipher) {
        this.sqlSession = sqlSession;
        this.aes256Cipher = aes256Cipher;
    }
    
    public int addEmailMemInfo(MemberDTO memberDTO) {
        return sqlSession.update("member.addEmailMemInfo", memberDTO);
    }
    
    public void addSocialMemInfo(MemberDTO memberDTO) {
        sqlSession.insert("member.addSocialMemInfo", memberDTO);
    }
    
    public String getBookmarkUploadDate(String memUid) {
        Date uploadedDate = sqlSession.selectOne("member.getBookmarkUploadDate", memUid);
        if(uploadedDate != null){
            return String.valueOf(uploadedDate.getTime());
        }
        return null;
    }
    
    @Transactional
    public int changePwd(String memUid, String encodedAesPwd) {
        Map<String, String> map = new HashMap();
        map.put("memUid", memUid);
        map.put("memPwd", encodedAesPwd);

        System.out.println(map);
       return sqlSession.update("member.changePwd", map);
    }
    
    public void addEMember(String encodedAesUid) { sqlSession.update("member.addEmailMember", encodedAesUid); }
    
    public void addSocialMember(SocialMemberDTO socialMemberDTO, String memberUid) {
        HashMap<String, Object> map = new HashMap();
        map.put("socialMemberDTO", socialMemberDTO);
        map.put("memberUid", memberUid);

        sqlSession.insert("member.addSocialMember", map);
    }
    
    public MemberDTO getExistMember(String memUid) {
        return sqlSession.selectOne("member.getExistMember", memUid);
    }
    
    public String isEmailMemberExists(String encodedAesUid) {
        return sqlSession.selectOne("member.isEMExist", encodedAesUid);
    }
    
    public void findEmailPwd(String encodedAesPwd, String shaKey) throws SQLException {
        Map <String, String> map = new HashMap<>();
        map.put("aes_find_pwd", encodedAesPwd);
        map.put("sha_key", shaKey);
        int updatedRow = sqlSession.update("member.findEPwd",map);
        if(updatedRow > 1){
            throw new SQLException();
        }
    }
    
    public void tempIns(String memUid) {

        sqlSession.insert("member.tempIns", memUid);
    }
    
    public int authCheck(EmailMemberDTO emailMemberDTO) {
        return sqlSession.update("member.authTokenUpdate", emailMemberDTO);

    }
    
    public String findMemUidByEmail(String email) {
        return sqlSession.selectOne("member.findMemUidByEmail", email);
    }
    
    public void delMemInfo(String memUid) {
        sqlSession.delete("member.delMemInfo", memUid);

    }
    
    public void addMemberFavor(String encodedAesUid, String favor) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("memUid", encodedAesUid);
        map.put("favor", favor);
        sqlSession.insert("member.addMemberFavor", map);
    }

    @Transactional
    public void sendKey(EmailMemberDTO emailMemberDTO) {

           sqlSession.insert("member.setTempMemInfo", emailMemberDTO);
           sqlSession.insert("member.sendKey", emailMemberDTO);

    }

    
    public int checkEmail(String userUid) {
        return sqlSession.selectOne("member.checkid", userUid);
    }
    
    public int checkPwd(EmailMemberDTO emailMemberDTO){
        return sqlSession.selectOne("member.checkpass",emailMemberDTO);
    }
    
    public String getAuthToken(EmailMemberDTO emailMemberDTO) {

        return sqlSession.selectOne("member.checkauth",emailMemberDTO);
    }
    
    public List<String> getMemFavor(String memberUid) { return sqlSession.selectList("member.getMemFavor", memberUid); }
    
    public MemberDTO modMemberInfoGET(String encodedAesUid) {  return sqlSession.selectOne("member.modMemberInfoGET", encodedAesUid); }

    @Transactional
    public MemberDTO modMemberInfoPOST(String memUid, MemberDTO memberDTO, String[] favor){
        Map<String, Object> map = new HashMap();
        memberDTO.setMem_iuid(memUid);
        map.put("memberDTO", memberDTO);
        map.put("memUid", memUid);

        sqlSession.update("member.modMemberInfoPOST_MemInfo", map);
        sqlSession.delete("member.delMemberFavor", memUid);

        for(String item : favor) {
            Map<String, Object> param = new HashMap<>();
            param.put("memUid", memUid);
            param.put("favor",item);
                sqlSession.insert("member.addMemberFavor", param);
        }

        return sqlSession.selectOne("member.getMemInfoSession", memUid);

    }

    
    public String getMemProfile(String memUid) {
        return sqlSession.selectOne("member.getMemProfile", memUid);
    }

    
    public List<Map<String,String>> getCategory() {
       return sqlSession.selectList("member.getCategory");
    }

    
    public MemberDTO getUserInfo(String userUid) {
        return sqlSession.selectOne("member.getUserInfo", userUid);
    }

    
    public void insertWhetherRegInfo(String memUid) {

        sqlSession.insert("member.insertWhetherRegInfo", memUid);

    }

    
    public int deleteWhetherRegInfo(String memUid) {
        return sqlSession.delete("member.deleteWhetherRegInfo", memUid);
    }
    
    public int selectWhetherRegInfo(String memUid){

        return sqlSession.selectOne("member.getWhetherRegInfo", memUid);
    }
}
