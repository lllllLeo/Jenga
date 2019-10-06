package com.jenga.yujun.board.repository;


import com.jenga.yujun.board.dto.BlockPathDTO;
import com.jenga.yujun.board.dto.BoardDTO;
import com.jenga.yujun.board.util.BlockCompType;
import com.jenga.yujun.member.dto.MemberDTO;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BoardDAO {

    private static final Logger logger = LoggerFactory.getLogger(BoardDAO.class);

    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public BoardDAO(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }


    public void writeViewBlock(BoardDTO boardDTO) {
        sqlSessionTemplate.insert("board.writeViewBlock", boardDTO);
    }

    public void writeViewReadCount(String bl_uid) {
        sqlSessionTemplate.insert("board.writeViewReadCount", bl_uid);
    }

    public void writeViewThumbImg(String bl_uid, String uploadName) {
        Map<String, String> map = new HashMap();

        map.put("uploadName", uploadName);
        map.put("bl_uid", bl_uid);

        sqlSessionTemplate.insert("board.writeViewThumbImg", map);
    }

    public void writeViewTag(String bl_uid, String[] bt_name) {
        Map<String, String> map = new HashMap();
        map.put("bl_uid", bl_uid);

        for (String tag : bt_name) {
            map.put("tag", tag);
            sqlSessionTemplate.insert("board.writeViewTag", map);
        }
    }


    public int likeCheck(String bl_iuid, String session_mem_iuid) {
        Map<String, String> map = new HashMap();
        map.put("bl_iuid", bl_iuid);
        map.put("session_mem_iuid", session_mem_iuid);
        return sqlSessionTemplate.selectOne("board.likeCheck", map);

    }

    public void addLike(String bl_iuid, String session_mem_iuid) {
        Map<String, String> map = new HashMap();
        map.put("bl_iuid", bl_iuid);
        map.put("session_mem_iuid", session_mem_iuid);
        sqlSessionTemplate.insert("board.addLike", map);
    }

    public void cancelLike(String bl_iuid, String session_mem_iuid) {
        Map<String, String> map = new HashMap();
        map.put("bl_iuid", bl_iuid);
        map.put("session_mem_iuid", session_mem_iuid);
        sqlSessionTemplate.delete("board.cancelLike", map);
    }

    public List<MemberDTO> getMyFollower(String my_iuid) {
        return sqlSessionTemplate.selectList("board.getFollowerList", my_iuid);
    }

    public int likeCount(String bl_iuid) {
        return sqlSessionTemplate.selectOne("board.likeCount", bl_iuid);
    }

    public Map<String, List<String>> getCategoryName() {
        Map<String, List<String>> category = new HashMap();

        List<String> uids = sqlSessionTemplate.selectList("board.mCtgAllUids");
        for (String uid : uids) {
            List<String> list = sqlSessionTemplate.selectList("board.sCtgAllNames", uid);
            String name = sqlSessionTemplate.selectOne("board.mCtgAllNames", uid);

            category.put(name, list);
        }
        return category;
    }


    public Map<String, Object> getModifyBlock(String bl_uid) {
        return sqlSessionTemplate.selectOne("board.getModifyBlock", bl_uid);
    }


    public void modifyViewBoard(BoardDTO boardDTO) {
        sqlSessionTemplate.update("board.modifyViewBoard", boardDTO);
    }

    public void modifyViewThumbImg(BoardDTO boardDTO, String uploadName) {
        Map<String, String> map = new HashMap();
        map.put("bl_uid", boardDTO.getBl_uid());
        map.put("uploadName", uploadName);
        sqlSessionTemplate.update("board.modifyViewThumbImg", map);
    }

    public void modifyViewTag(BoardDTO boardDTO) {
        Map<String, String> map = new HashMap();
        String[] bt_name = boardDTO.getBt_name();

        for (String tag : bt_name) {
            map.put("tag", tag);
            sqlSessionTemplate.update("board.modifyViewTag", map);
        }

    }

    public String transCtgUID(String bl_smCtg, BlockCompType flag) {

        if (BlockCompType.CATEGORY_SAMLL.equals(flag)) {

            return sqlSessionTemplate.selectOne("board.sctgUID", bl_smCtg);

        }
        return sqlSessionTemplate.selectOne("board.mctgUID", bl_smCtg);
    }

    public List<Map<String, Object>> searchName(String search, double startRow, double endRow) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("search", search);
        map.put("startrow", startRow);
        map.put("endrow", endRow);
        return sqlSessionTemplate.selectList("board.searchName", map);
    }

    public List<Map<String, Object>> searchTag(String search, double startrow, double endrow) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("search", search);
        map.put("startrow",startrow);
        map.put("endrow", endrow);
        return sqlSessionTemplate.selectList("board.searchTag", map);
    }

    public List<Map<String, Object>> searchContents(List<String> search, double startRow, double endRow) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("search", search);
        map.put("startrow", startRow);
        map.put("endrow", endRow);
        System.out.println(map);
        return sqlSessionTemplate.selectList("board.searchTitle", map);
    }

    public void setSearchKeyword(String search, String session_iuid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("search", search);
        map.put("session_iuid", session_iuid);
        sqlSessionTemplate.insert("board.setSearchKeyword", map);
    }

    public int follow(String bl_writer, String session_iuid) {

        Map<String, String> map = new HashMap<String, String>();
        map.put("bl_writer", bl_writer);
        map.put("session_iuid", session_iuid);
        return sqlSessionTemplate.insert("board.follow", map);
    }

    public int followCheck(String bl_writer, String session_iuid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("bl_writer", bl_writer);
        map.put("session_iuid", session_iuid);
        return sqlSessionTemplate.selectOne("board.followCheck", map);
    }

    public int unFollow(String bl_writer, String session_iuid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("bl_writer", bl_writer);
        map.put("session_iuid",session_iuid);
        return sqlSessionTemplate.delete("board.unFollow",map);
    }

    public List<BoardDTO> getFollowerBoard(String follow_iuid, String my_iuid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("my_iuid", my_iuid);
        map.put("follow_iuid", follow_iuid);
        return sqlSessionTemplate.selectList("board.getFollowerBoard", map);
    }

    public List<Map<String, Object>> getMyBlock(String myUid) {
        return sqlSessionTemplate.selectList("board.getMyBlock", myUid);
    }

    public List<String> searchImgName(String search, int startrow, int endrow) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("search", search);
        map.put("startrow",startrow);
        map.put("endrow", endrow);
        return sqlSessionTemplate.selectList("board.searchImgName", map);
    }

    public List<String> searchImgTag(String search, int startrow, int endrow) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("search", search);
        map.put("startrow",startrow);
        map.put("endrow", endrow);
        return sqlSessionTemplate.selectList("board.searchImgTag", map);
    }

    public List<String> searchImgContents(List<String> search, int startrow, int endrow) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("search", search);
        map.put("startrow",startrow);
        map.put("endrow", endrow);

        return sqlSessionTemplate.selectList("board.searchImgTitle", map);
    }

    public int countSearchName(String search) {
        return sqlSessionTemplate.selectOne("board.countSearchName",search);
    }

    public int countSearchTag(String search) {
        return sqlSessionTemplate.selectOne("board.countSearchTag",search);
    }

    public int countSearchContents(List<String> search) {
        Map<String, Object> map = new HashMap<>();
        map.put("search", search);
        return sqlSessionTemplate.selectOne("board.countSearchTitle",map);
    }

    public List<Map<String, Object>> getMyFavoriteBlocks(String memUid){
        return sqlSessionTemplate.selectList("board.getMyFavoriteBlocks", memUid);
    }



    public String getUploadName(String bl_uid) {
        return sqlSessionTemplate.selectOne("board.getUploadName", bl_uid);
    }


    public int checkBmksPath(String session_iuid) {
        return sqlSessionTemplate.selectOne("board.checkBmksPath", session_iuid);
    }

    public void insertBmksPath(String session_iuid, BlockPathDTO blockPathDTO) {
        Map<String, Object> map = new HashMap();
        map.put("session_iuid", session_iuid);
        map.put("blockPathDTO", blockPathDTO);
        sqlSessionTemplate.insert("board.insertBmksPath", map);
    }

    public void updateBmksPath(String session_iuid, BlockPathDTO blockPathDTO) {
        Map<String, Object> map = new HashMap();
        map.put("session_iuid", session_iuid);
        map.put("blockPathDTO", blockPathDTO);
        sqlSessionTemplate.update("board.updateBmksPath", map);
    }
    public String getBookMarkFromHTML(String session_iuid) {
        return sqlSessionTemplate.selectOne("board.getBookMarkFromHTML", session_iuid);
    }

    public int deleteBlock(String bl_uid) {
        return sqlSessionTemplate.delete("board.deleteBlock", bl_uid);
    }

    public void getAddReadCount(String bl_uid) {
        sqlSessionTemplate.update("board.addReadCount", bl_uid);    // 조회수 + 1
    }

    public Map<String, Object> getBoardDetailBlock(String bl_uid) {
        return sqlSessionTemplate.selectOne("board.getBoardDetailBlock", bl_uid);
    }

    public List<String> getBoardDetailTags(String bl_uid) {
        return sqlSessionTemplate.selectList("board.getBoardDetailTags", bl_uid);   // 태그 뽑음
    }

    public List<Map<String, String>> getMyFavoirteBlockWithParam(String memUid, String searchOps, String category, List<String> keyword){
        Map<String ,Object> param = new HashMap<>();
        param.put("memUid", memUid);
        param.put("searchOps", searchOps);
        param.put("category", category);
        param.put("keyword", keyword);

        return sqlSessionTemplate.selectList("board.getMyFavoirteBlockWithParam", param);
    }
}



