package com.jenga.yujun.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.jenga.yujun.board.repository.BoardDAO;
import com.jenga.yujun.board.dto.BlockPathDTO;
import com.jenga.yujun.board.dto.BoardDTO;
import com.jenga.yujun.board.util.BlockCompType;
import com.jenga.yujun.board.util.FileIOUtil;
import com.jenga.yujun.member.dto.MemberDTO;
import com.jenga.yujun.member.util.cipher.AES256Cipher;
import com.jenga.yujun.util.EncryptManager;
import com.jenga.yujun.util.FileType;
import com.jenga.yujun.util.JsonParseManager;
import com.jenga.yujun.util.status_code.BlockStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BoardService {


    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);

    private BoardDAO dao;
    private MongoService mongoService;
    private AES256Cipher aes256Cipher;


    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${bookmark.root_path}")
    private String bookmark_absolute_path;

    @Autowired
    public BoardService(BoardDAO dao, MongoService mongoService, AES256Cipher aes256Cipher) {
        this.dao = dao;
        this.mongoService = mongoService;
        this.aes256Cipher = aes256Cipher;
    }


    @Transactional
    public void writeViewBlock(BoardDTO boardDTO, String uploadPath, String bookmark) {

        mongoService.writeViewBmks(boardDTO.getBl_uid(), bookmark);
        boardDTO.setBl_objId(mongoService.getObjId("_refBoardId", boardDTO.getBl_uid()));

        dao.writeViewBlock(boardDTO);
        dao.writeViewReadCount(boardDTO.getBl_uid());
        if (uploadPath != null) {
            dao.writeViewThumbImg(boardDTO.getBl_uid(), uploadPath);
        }
        dao.writeViewTag(boardDTO.getBl_uid(), boardDTO.getBt_name());


    }

    public Map<String, Object> getModifyBlock(String blockUid) {

        Map<String, Object> map = dao.getModifyBlock(blockUid);
        map.put("tag", JsonParseManager.parseToString(dao.getBoardDetailTags(blockUid)));
        map.put("bookmarks", mongoService.getView("_refBoardId", blockUid));
        map.put("bti_url", dao.getUploadName(blockUid));
        return map;
    }

    @Transactional
    public BlockStatusCode modifyViewPOST(BoardDTO boardDTO, String uploadFileName, String bookmark) {

        try {
            dao.modifyViewBoard(boardDTO);

            if (uploadFileName == null) {
                uploadFileName = dao.getUploadName(boardDTO.getBl_uid());
            }

            dao.modifyViewThumbImg(boardDTO, uploadFileName);
            dao.modifyViewTag(boardDTO);
            mongoService.modifyViewPOST("_refBoardId", boardDTO.getBl_uid(), bookmark);
            return BlockStatusCode.BLOCK_MOD_SUCCESS;

        }catch (Exception e){
            e.printStackTrace();
        }

        return BlockStatusCode.BLOCK_MOD_FAIL;

    }

    public void addBookmarkPath(String memUid, BlockPathDTO blockPathDTO) {

        if (dao.checkBmksPath(memUid) < 1) {
            dao.insertBmksPath(memUid, blockPathDTO);
        } else {
            dao.updateBmksPath(memUid, blockPathDTO);
        }
    }

    @Transactional
    public BlockStatusCode deleteBlock(String blockUid) {
        if (dao.deleteBlock(blockUid) > 0){
            mongoService.deleteBlock("_refBoardId", blockUid);
            return BlockStatusCode.BLOCK_DEL_SUCCESS;
        }
        return BlockStatusCode.BLOCK_DEL_FAIL;
    }


    @Transactional
    public Map<String, Object> getView(String boardUid) {

        dao.getAddReadCount(boardUid);
        Map<String, Object> map = dao.getBoardDetailBlock(boardUid);

        System.out.println(map);
        if(map == null) return null;
        String nick = String.valueOf(map.get("mem_nick"));
        String introduce = String.valueOf(map.get("mem_introduce"));
        String profile = String.valueOf(map.get("mem_profile"));

        map.put("mem_nick", EncryptManager.aesDecode(aes256Cipher, nick));
        map.put("mem_introduce", EncryptManager.aesDecode(aes256Cipher, introduce));
        map.put("mem_profile", EncryptManager.aesDecode(aes256Cipher, profile));


        map.put("tag", JsonParseManager.parseToString(dao.getBoardDetailTags(boardUid)));
        map.put("likes", dao.likeCount(boardUid));
        map.put("bookmarks", mongoService.getView("_refBoardId", boardUid));


        return map;
    }


    public BlockStatusCode isLikeExist(String blockUid, String memUid) {
        if (dao.likeCheck(blockUid, memUid) > 0) {
            return BlockStatusCode.LIKE_EXISTS;
        }
        return BlockStatusCode.LIKE_NOT_EXISTS;
    }

    public List<MemberDTO> getMyFollower(String memUid) {
        List<MemberDTO> list = dao.getMyFollower(memUid);
        for (MemberDTO member : list) member.setMem_nick(EncryptManager.aesDecode(aes256Cipher, member.getMem_nick()));
        return list;
    }

    public BlockStatusCode likeCheck(String blockUid, String memUid) {

        if (dao.likeCheck(blockUid, memUid) < 1) {
            dao.addLike(blockUid, memUid);
            return BlockStatusCode.LIKE_EXISTS;
        }
        dao.cancelLike(blockUid, memUid);
        return BlockStatusCode.LIKE_NOT_EXISTS;
    }

    public int likeCount(String blUid) {
        return dao.likeCount(blUid);
    }

    public String getBookMarkFromHTML(String memUid) {
        String fileFullName = dao.getBookMarkFromHTML(memUid);
        String result = null;
        if (fileFullName != null) {
            FileIOUtil fileIOUtil = new FileIOUtil(FileType.BOOKMARK);
            result = fileIOUtil.getFileToChar(fileFullName, bookmark_absolute_path);
        }

        return result;

    }

    public Map<String, List<String>> getCategoryName() {
        return dao.getCategoryName();
    }

    public String transCtgUID(String smallCategory, BlockCompType compType) {
        return dao.transCtgUID(smallCategory, compType);
    }

    public List<Map<String, Object>> search(String keyword, String searchOption, String memUid, double startRow, double endRow) {
        if (memUid != null) {
            dao.setSearchKeyword(keyword, memUid);
        }

        if (checkComponentType(BlockCompType.NAME, searchOption)) {
            keyword = EncryptManager.aesEnocde(aes256Cipher, keyword);
            return dao.searchName(keyword, startRow, endRow);

        } else if (checkComponentType(BlockCompType.TAG, searchOption)) {
            return dao.searchTag(keyword, startRow, endRow);
        }

        return dao.searchContents(stringTokenizer(keyword), startRow, endRow);

    }

    public BlockStatusCode follow(String writerUid, String memUid) {

        if (writerUid.equals(memUid))  return BlockStatusCode.FOLLOW_SAME_AUTH;
        if(dao.follow(writerUid, memUid) > 0) return BlockStatusCode.FOLLOW_SUCCESS;
        return BlockStatusCode.FOLLOW_FAIL;

    }

    public BlockStatusCode unFollow(String writerUid, String memUid) {
        if(!writerUid.equals(memUid)) {
            if (writerUid.equals(memUid))  return BlockStatusCode.FOLLOW_SAME_AUTH;
            if (dao.unFollow(writerUid, memUid) > 0) return BlockStatusCode.UNFOLLOW_SUCCESS;
        }
        return BlockStatusCode.UNFOLLOW_FAIL;
    }

    public BlockStatusCode followCheck(String writerUid, String memUid) {
        if (dao.followCheck(writerUid, memUid) > 0) return BlockStatusCode.FOLLOW;
        return BlockStatusCode.NOT_FOLLOW;
    }


    public List<BoardDTO> getFollowerBoard(String followUid, String memUid) {
        return dao.getFollowerBoard(followUid, memUid);
    }


    public List<Map<String, Object>> getMyBlock(String myUid) {
        return dao.getMyBlock(myUid);
    }

    public List<String> searchImg(String keyword, String searchOption, int startRow, int endRow) {
        if (checkComponentType(BlockCompType.NAME, searchOption)) {
            keyword = EncryptManager.aesEnocde(aes256Cipher, keyword);
            return dao.searchImgName(keyword, startRow, endRow);
        } else if (checkComponentType(BlockCompType.TAG, searchOption)) {
            return dao.searchImgTag(keyword, startRow, endRow);
        }
        return dao.searchImgContents(stringTokenizer(keyword), startRow, endRow);
    }

    public int countSearch(String keyword, String searchOption) {
        if (checkComponentType(BlockCompType.NAME, searchOption)) {
            keyword = EncryptManager.aesEnocde(aes256Cipher, keyword);
            return dao.countSearchName(keyword);

        } else if (checkComponentType(BlockCompType.TAG, searchOption)) {
            return dao.countSearchTag(keyword);
        }

        return dao.countSearchContents(stringTokenizer(keyword));

    }

    public List<Map<String, Object>> getMyFavoriteBlocks(String memUid){
        return dao.getMyFavoriteBlocks(memUid);
    }


    public List<Map<String, String>> getMyFavoirteBlockWithParam(String memUid, String searchOps, String category, String keyword){

        String[] sliceKeyword = keyword.split(" ");
        List<String> keywordTokens = new ArrayList<>();

        for(int i = 0 ; i<sliceKeyword.length; ++i){
            if(sliceKeyword[i].trim().length() == 0) continue;
            keywordTokens.add(sliceKeyword[i]);
        }

        System.out.println(Arrays.toString(keywordTokens.toArray()));

        return dao.getMyFavoirteBlockWithParam(memUid, searchOps, category, keywordTokens);
    }

    private boolean checkComponentType(BlockCompType compType, String keyword) {

        if (compType.name().equals(keyword.toUpperCase())) return true;
        return false;


    }


    @SuppressWarnings("unchecked")
    private List<String> stringTokenizer(String str) {

        String[] strArr = str.split(" ");
        return new ArrayList(Arrays.asList(strArr));

    }


}
