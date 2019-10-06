package com.jenga.yujun.member.repository;


import com.jenga.yujun.member.dto.EmailMemberDTO;
import com.jenga.yujun.member.dto.MemberDTO;
import com.jenga.yujun.member.dto.SocialMemberDTO;
import com.jenga.yujun.member.util.cipher.AES256Cipher;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
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
    private SqlSessionTemplate sqlSessionTemplate;
    private AES256Cipher aes256Cipher;

    @Autowired
    public MemberDAO(SqlSessionTemplate sqlSessionTemplate, AES256Cipher aes256Cipher) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.aes256Cipher = aes256Cipher;
    }
    
    public int addEmailMemInfo(MemberDTO memberDTO) {
        return sqlSessionTemplate.update("member.addEmailMemInfo", memberDTO);
    }
    
    public void addSocialMemInfo(MemberDTO memberDTO) {
        sqlSessionTemplate.insert("member.addSocialMemInfo", memberDTO);
    }
    
    public String getBookmarkUploadDate(String memUid) {
        Date uploadedDate = sqlSessionTemplate.selectOne("member.getBookmarkUploadDate", memUid);
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
       return sqlSessionTemplate.update("member.changePwd", map);
    }
    
    public void addEMember(String encodedAesUid) { sqlSessionTemplate.update("member.addEmailMember", encodedAesUid); }
    
    public void addSocialMember(SocialMemberDTO socialMemberDTO, String memberUid) {
        HashMap<String, Object> map = new HashMap();
        map.put("socialMemberDTO", socialMemberDTO);
        map.put("memberUid", memberUid);

        sqlSessionTemplate.insert("member.addSocialMember", map);
    }
    
    public MemberDTO getExistMember(String memUid) {
        return sqlSessionTemplate.selectOne("member.getExistMember", memUid);
    }
    
    public String isEmailMemberExists(String encodedAesUid) {
        return sqlSessionTemplate.selectOne("member.isEMExist", encodedAesUid);
    }
    
    public void findEmailPwd(String encodedAesPwd, String shaKey) throws SQLException {
        Map <String, String> map = new HashMap<>();
        map.put("aes_find_pwd", encodedAesPwd);
        map.put("sha_key", shaKey);
        int updatedRow = sqlSessionTemplate.update("member.findEPwd",map);
        if(updatedRow > 1){
            throw new SQLException();
        }
    }
    
    public void tempIns(String memUid) {

        sqlSessionTemplate.insert("member.tempIns", memUid);
    }
    
    public int authCheck(EmailMemberDTO emailMemberDTO) {
        return sqlSessionTemplate.update("member.authTokenUpdate", emailMemberDTO);

    }
    
    public String findMemUidByEmail(String email) {
        return sqlSessionTemplate.selectOne("member.findMemUidByEmail", email);
    }
    
    public void delMemInfo(String memUid) {
        sqlSessionTemplate.delete("member.delMemInfo", memUid);

    }
    
    public void addMemberFavor(String encodedAesUid, String favor) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("memUid", encodedAesUid);
        map.put("favor", favor);
        sqlSessionTemplate.insert("member.addMemberFavor", map);
    }

    @Transactional
    public void sendKey(EmailMemberDTO emailMemberDTO) {

           sqlSessionTemplate.insert("member.setTempMemInfo", emailMemberDTO);
           sqlSessionTemplate.insert("member.sendKey", emailMemberDTO);

    }

    
    public int checkEmail(String userUid) {
        return sqlSessionTemplate.selectOne("member.checkid", userUid);
    }
    
    public int checkPwd(EmailMemberDTO emailMemberDTO){
        return sqlSessionTemplate.selectOne("member.checkpass",emailMemberDTO);
    }
    
    public String getAuthToken(EmailMemberDTO emailMemberDTO) {

        return sqlSessionTemplate.selectOne("member.checkauth",emailMemberDTO);
    }
    
    public List<String> getMemFavor(String memberUid) { return sqlSessionTemplate.selectList("member.getMemFavor", memberUid); }
    
    public MemberDTO modMemberInfoGET(String encodedAesUid) {  return sqlSessionTemplate.selectOne("member.modMemberInfoGET", encodedAesUid); }

    @Transactional
    public MemberDTO modMemberInfoPOST(String memUid, MemberDTO memberDTO, String[] favor){
        Map<String, Object> map = new HashMap();
        memberDTO.setMem_iuid(memUid);
        map.put("memberDTO", memberDTO);
        map.put("memUid", memUid);

        sqlSessionTemplate.update("member.modMemberInfoPOST_MemInfo", map);
        sqlSessionTemplate.delete("member.delMemberFavor", memUid);

        for(String item : favor) {
            Map<String, Object> param = new HashMap<>();
            param.put("memUid", memUid);
            param.put("favor",item);
                sqlSessionTemplate.insert("member.addMemberFavor", param);
        }

        return sqlSessionTemplate.selectOne("member.getMemInfoSession", memUid);

    }

    
    public String getMemProfile(String memUid) {
        return sqlSessionTemplate.selectOne("member.getMemProfile", memUid);
    }

    
    public List<Map<String,String>> getCategory() {
       return sqlSessionTemplate.selectList("member.getCategory");
    }

    
    public MemberDTO getUserInfo(String userUid) {
        return sqlSessionTemplate.selectOne("member.getUserInfo", userUid);
    }

    
    public void insertWhetherRegInfo(String memUid) {

        sqlSessionTemplate.insert("member.insertWhetherRegInfo", memUid);

    }

    
    public int deleteWhetherRegInfo(String memUid) {
        return sqlSessionTemplate.delete("member.deleteWhetherRegInfo", memUid);
    }
    
    public int selectWhetherRegInfo(String memUid){

        return sqlSessionTemplate.selectOne("member.getWhetherRegInfo", memUid);
    }
}
