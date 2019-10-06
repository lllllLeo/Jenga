package com.jenga.yujun.board.controller;


import com.jenga.yujun.board.dto.BlockPathDTO;
import com.jenga.yujun.board.dto.BoardDTO;
import com.jenga.yujun.board.service.BoardService;
import com.jenga.yujun.board.util.BlockCompType;
import com.jenga.yujun.board.util.FileIOUtil;
import com.jenga.yujun.board.util.PageAccumluator;
import com.jenga.yujun.board.util.Paging;
import com.jenga.yujun.member.dto.MemberDTO;
import com.jenga.yujun.util.AuthUser;
import com.jenga.yujun.util.FileType;
import com.jenga.yujun.util.JsonParseManager;
import com.jenga.yujun.util.session.MemberSession;
import com.jenga.yujun.util.session.SessionValidate;
import com.jenga.yujun.util.status_code.BlockStatusCode;
import com.jenga.yujun.util.status_code.FileStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/board")
public class BoardController {
    public static final Logger logger = LoggerFactory.getLogger(BoardController.class);
    private BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    private FileIOUtil fileIOUtil;

    @Value("${image_resource.block_path}")
    private String imagePath;
    @Value("${image_resource.root_path}")
    private String imageRootPath;

    @Value("${bookmark.root_path}")
    private String bookmarkPath;
    @Value("${bookmark.absolute_path}")
    private String bookmarkAbsolutePath;


    @GetMapping(value = "/search")
    public String SearchGET() {
        return "stackBoard/boardSearch";
    }


    @GetMapping(value = "/searchAction")
    public @ResponseBody
    List<Map<String, Object>> Search(@RequestParam("search") String keyword, @RequestParam("search_check") String checkType,
                                     @RequestParam("pageNum") int page, @AuthUser MemberDTO authUser) {

       Paging pageInfo = PageAccumluator.getPageInfo(page, boardService.countSearch(keyword, checkType));
        System.out.println(pageInfo.toString());
       return boardService.search(keyword, checkType, authUser.getMem_iuid(), pageInfo.getHeadPageNum(), pageInfo.getTailPageNum());
    }

    @GetMapping(value = "/boardView")
    public String getBoardDetail(@RequestParam("bl_uid") String bl_uid, Model model) {
        model.addAttribute("bl_uid", bl_uid);
        model.addAttribute("map", boardService.getView(bl_uid));
        return "stackBoard/boardDetailView";
    }


    @GetMapping(value = "/stackBlock")
    public String getWriteView(Model model, @AuthUser MemberDTO authUser) {

          model.addAttribute("category", JsonParseManager.parseToString(boardService.getCategoryName()));
          String resultHTML = boardService.getBookMarkFromHTML(authUser.getMem_iuid());
          if (resultHTML != null) {  model.addAttribute("resultHTML", resultHTML);  }
          model.addAttribute("statusToken", "stack");
          return "stackBoard/stackBlock";
    }


    @GetMapping(value = "/modifyBlock")
    public String getBlockModifyView(Model model, @RequestParam(value = "bl_uid") String bl_uid, @AuthUser MemberDTO authUser) {

        String resultHTML = boardService.getBookMarkFromHTML(authUser.getMem_iuid());
        if (resultHTML!=null) {
            model.addAttribute("resultHTML", resultHTML);
        }
        model.addAttribute("map", boardService.getModifyBlock(bl_uid));
        model.addAttribute("category", JsonParseManager.parseToString(boardService.getCategoryName()));
        model.addAttribute("statusToken", "modify");
        model.addAttribute("bl_uid", bl_uid);
        return "stackBoard/stackBlock";


    }

    @PostMapping(value = "/uploadBlock", produces = "multipart/form-data; charset=utf-8")
    public @ResponseBody
    String WriteViewPOST(@ModelAttribute BoardDTO boardDTO, @RequestPart(value = "bti_url", required = false) MultipartFile uploadFile,
                         @RequestParam("bl_bookmarks") String bl_bookmarks, @AuthUser MemberDTO authUser) {

        boardDTO.setBl_uid(UUID.randomUUID().toString());
        boardDTO.setBl_writer(authUser.getMem_iuid());
        String uploadPath = null;

        if (uploadFile != null) {
            fileIOUtil = new FileIOUtil(FileType.IMAGE);
            System.out.println(imagePath);
            System.out.println(imageRootPath);

            uploadPath = fileIOUtil. getUploadedFilePath(uploadFile, imagePath, imageRootPath, FileType.IMAGE);

        }

        boardDTO.setBl_smCtg(boardService.transCtgUID(boardDTO.getBl_smCtg(), BlockCompType.CATEGORY_SAMLL));
        boardDTO.setBl_mainCtg(boardService.transCtgUID(boardDTO.getBl_mainCtg(), BlockCompType.CATEGORY_MAIN));
        boardService.writeViewBlock(boardDTO, uploadPath, bl_bookmarks);


        return boardDTO.getBl_uid();
    }


    @GetMapping(value = "/like/{bl_iuid}")
    public @ResponseBody
    int like(@PathVariable String bl_iuid, @AuthUser MemberDTO authUser) {
        boardService.likeCheck(bl_iuid, authUser.getMem_iuid());
        return boardService.likeCount(bl_iuid);

    }

    @GetMapping(value = "/isLikeExist/{bl_uid}")
    public @ResponseBody
    int isLikeExist(@PathVariable("bl_uid") Optional<String> blockUserUid, @AuthUser MemberDTO authUser) {

        if(blockUserUid.isPresent() && authUser != null) return boardService.isLikeExist(blockUserUid.get(), authUser.getMem_iuid()).getCode();
        return BlockStatusCode.LIKE_NOT_EXISTS.getCode();
    }


    @PostMapping(value = "/updateBlock")
    @ResponseBody
    public int modifyViewPOST(@ModelAttribute BoardDTO boardDTO, @RequestPart(value = "bti_url", required = false) MultipartFile uploadFile,
                              @RequestParam("bl_bookmarks") String bl_bookmarks, @AuthUser MemberDTO memberDTO) {
        String uploadName = null;

        if (uploadFile != null) {
            fileIOUtil = new FileIOUtil(FileType.BOOKMARK);
            uploadName = fileIOUtil.fileUpload(uploadFile, bookmarkAbsolutePath);
        }


        boardDTO.setBl_writer(memberDTO.getMem_iuid());

        return boardService.modifyViewPOST(boardDTO, uploadName, bl_bookmarks).getCode();

    }


    @GetMapping(value = "/delBlock")
    public @ResponseBody
    int deleteBlock(@RequestParam("bl_uid") String bl_uid) {
        return boardService.deleteBlock(bl_uid).getCode();
    }


    @PostMapping(value = "/fileUpload")
    public @ResponseBody
    int fileUpload(@RequestParam("bp_browstype") String bp_browstype, @RequestParam("bp_booktype") String bp_booktype,
                   @RequestParam("file") MultipartFile uploadFile, HttpSession session) {

        MemberDTO memberSession = (MemberDTO) SessionValidate.getValidSessionObj(session, MemberSession.MEMBER_SESSION_KEY);
        BlockPathDTO blockPathDTO = new BlockPathDTO();

        try {

            String uploadPath =  FileIOUtil.getUploadedFilePath(uploadFile, bookmarkAbsolutePath, bookmarkPath, FileType.BOOKMARK);
            blockPathDTO.setBp_booktype(bp_booktype);
            blockPathDTO.setBp_browstype(bp_browstype);
            blockPathDTO.setBp_path(uploadPath);

            boardService.addBookmarkPath(memberSession.getMem_iuid(), blockPathDTO);

            return FileStatusCode.FILE_UPLOAD_SUCCESS.getCode();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return FileStatusCode.FILE_UPLOAD_FAIL.getCode();

    }


    @GetMapping(value = "/follow")
    public @ResponseBody
    int follower(@RequestParam("bl_writer") String bl_writer, @AuthUser MemberDTO authUser) {

      return boardService.follow(bl_writer, authUser.getMem_iuid()).getCode();

    }

    @GetMapping(value = "/unFollow")
    public @ResponseBody
    int unFollow(@RequestParam("bl_writer") String bl_writer, @AuthUser MemberDTO authUser) {
       return boardService.unFollow(bl_writer, authUser.getMem_iuid()).getCode();
    }


    //팔로워한 사람 리스트
    @GetMapping(value = "/followlist")
    public List<MemberDTO> myFollower(@AuthUser MemberDTO authUser) {
        return boardService.getMyFollower(authUser.getMem_iuid());
    }


    @GetMapping(value = "follwerBlock")
    public @ResponseBody
    List<BoardDTO> followerBlock(String follow_iuid, @AuthUser MemberDTO authUser) {
        return boardService.getFollowerBoard(follow_iuid, authUser.getMem_iuid());
    }

    @GetMapping(value="getMyFavoriteBlock")
    public @ResponseBody
    List<Map<String, Object>> getMyFavoriteBlock(@AuthUser MemberDTO authUser){
        return boardService.getMyFavoriteBlocks(authUser.getMem_iuid());
    }

    @GetMapping(value="followCheck")
    public @ResponseBody
    int followCheck(@RequestParam("bl_writer") String writerUid, @AuthUser MemberDTO authUser){
        return boardService.followCheck(writerUid, authUser.getMem_iuid()).getCode();
    }

    @GetMapping(value = "/getMyBlockManage")
    public String getMyBlockManage(Model model, @AuthUser MemberDTO authUser) {
        model.addAttribute("boards", JsonParseManager.parseToString(boardService.getMyBlock(authUser.getMem_iuid())));
        return "myBoard/myBoardManage";
    }

    @GetMapping(value = "/getMyFavorBlock")
    public String getMyFavorBlock(Model model) {
        model.addAttribute("categories", JsonParseManager.parseToString(boardService.getCategoryName()));
        return "myBoard/myFavorBlock";
    }

    @GetMapping(value = "/getNoticeBoard")
    public String getNoticeBoard() {
        return "noticeBoard/noticeBoard";

    }

}