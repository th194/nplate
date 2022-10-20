package com.netive.nplate.controller;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.netive.nplate.service.MemberService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.service.BoardService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BoardController {
	
	@Autowired
	private BoardService boardSerive;

	/**
	 * 게시글 작성
	 * @param idx
	 * @param model
	 * @return
	 */
	@GetMapping("/board/write.do")
	public String openBoardWrite(@RequestParam(value = "idx", required = false) Long idx, Model model) {
		if (idx == null) {
			model.addAttribute("board", new BoardDTO());
			
		} else {
			BoardDTO boardDTO = boardSerive.getBoardDetail(idx);
			if(boardDTO == null) {
				return "redirect:board/list.do";
			}
			model.addAttribute("board", boardDTO);
		}
		
		return "board/write";
	}

	/**
	 * 게시글 등록/수정
	 * @param boardDTO
	 * @param model
	 * @return
	 */
	@PostMapping("/board/register.do")
	public String openBoardRegister(BoardDTO boardDTO, Model model) {
		System.out.println(boardDTO);
		boardSerive.registerBoard(boardDTO);
		List<BoardDTO> boardList = boardSerive.getBordList();
		model.addAttribute("boardList", boardList);
		
		return "board/list";
	}

//	@PostMapping("/board/register.do")
//	public Map<String, Object> openBoardRegisterAll(MultipartHttpServletRequest multipartRequest,
//													HttpServletResponse response,
//													@RequestParam Map<String, String> boardDTO,
//													@RequestParam(value = "file[]") List<String> summerfile) throws Exception{
//
//		System.out.println(boardDTO);
////		boardSerive.registerBoard(boardDTO);
//		List<BoardDTO> boardList = boardSerive.getBordList();
////		model.addAttribute("boardList", boardList);
//
////		return "board/list";
//	}

	/**
	 * 게시글 목록
	 * @param model
	 * @return
	 */
	@GetMapping("/board/list.do")
	public String openBoardList(Model model) {
		List<BoardDTO> boardList = boardSerive.getBordList();
		model.addAttribute("boardList", boardList);
		return "board/list";
	}
	
	/**
	 * 게시글 상세보기
	 * @param idx
	 * @param model
	 * @return
	 */
	@GetMapping("/board/view.do")
	public String openBoardDetail(@RequestParam(value="idx", required=false) Long idx, Model model) {
		if(idx == null) {
			// 올바르지 않은 접근
			return "redirect:/board/list.do";
		}
		
		BoardDTO boardDTO = boardSerive.getBoardDetail(idx);
//		if(boardDTO == null || "Y".equals(boardDTO.getDeleteYn())) {
//			// 없는 게시글 or 이미 삭제된 게시글
//			return "redirect:/board/list.do";
//		}
		model.addAttribute("board", boardDTO);
		boardSerive.cntPlus(idx); // 게시글 조회수 증가
		return "board/view";
	}
	
	/**
	 * 게시글 삭제
	 * @param idx
	 * @return
	 */
	@PostMapping("/board/delete.do")
	public String deleteBoard(@RequestParam(value="idx", required=false) Long idx) {
		if(idx == null) {
			return "redirect:/board/list.do";
		}
		
		try {
			boolean isDelete = boardSerive.deleteBoard(idx);
			if(isDelete == false) {
				
			}
		} catch (DataAccessException daex) { // DB 처리과정 문제 발생
			daex.printStackTrace();
		} catch (Exception e) { // 시스템 에러
			e.printStackTrace();
		}
		
		return "redirect:/board/list.do";
	}


	/**
	 * 파일업로드
	 * @param multipartFile
	 * @return
	 */
	@RequestMapping(value="/board/uploadSummernoteImageFile", produces = "application/json; charset=utf8")
	@ResponseBody
	public JsonObject uploadSummernoteImageFile(@RequestParam( value = "file") MultipartFile multipartFile) throws IOException{

		System.out.println("=>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>파일 업로드 시작");
		JsonObject jsonObject = new JsonObject();

		String fileRoot = "C:/kth/summernote_img_temp/";											// 외부파일 경로
		String originalFileName = multipartFile.getOriginalFilename();								// 원본 파일명
		String extension = originalFileName.substring(originalFileName.lastIndexOf("."));		// 파일 확장자

		String savedFileName = UUID.randomUUID() + extension;

		File targetFile = new File(fileRoot + savedFileName);

		try {
			InputStream fileStream = multipartFile.getInputStream();
			FileUtils.copyInputStreamToFile(fileStream, targetFile);	// 파일 저장
			jsonObject.addProperty("url", "summernoteImage/savedFileName="+savedFileName);
			jsonObject.addProperty("responseCode", "success");
		} catch (IOException e) {
			FileUtils.deleteQuietly(targetFile);		// 저장된 파일 삭제
			jsonObject.addProperty("responseCode", "error");
			e.printStackTrace();
		}
		System.out.println("===========>jsonObj = " + jsonObject);
		return jsonObject;
	}
//	@RequestMapping(value="resources/summerimages", method=RequestMethod.POST)
//	public ResponseEntity<?> summerimage(@RequestParam("file") MultipartFile img, HttpServletRequest request) throws IOException {
//		String path = request.getServletContext().getRealPath("localhost:8080/");
//		Random random = new Random();
//
//		long currentTime = System.currentTimeMillis();
//		int	randomValue = random.nextInt(100);
//		String fileName = Long.toString(currentTime) + "_"+randomValue+"_a_"+img.getOriginalFilename();
//
//		File file = new File(path , fileName);
//		img.transferTo(file);
//		return ResponseEntity.ok().body("localhost:8080/"+fileName);
//
//	}

//	@RequestMapping(value="/summernoteImage/getImg.do", method = RequestMethod.GET)
//	public Image getImg(@RequestParam(value="savedFileName") String savedFileName, HttpServletResponse response) throws Exception {
//		String filePath;
//		String DIR = "C:/kth/summernote_img_temp/";
//		filePath = DIR + savedFileName;
//		Image image = getImg(filePath, response);
//		System.out.println("=>>>>>>>>>>>>>>>> getImg = " + image);
//		return image;
//	}
//
//	@RequestMapping(value="/summernoteImage/getImgCopy.do", method=RequestMethod.GET)
//	public Image getImgCopy(@RequestParam(value="savedFileName") String savedFileName, HttpServletResponse response) throws Exception {
//		String filePath;
//		String DIR = "C:/kth/summernote_img/";
//		filePath = DIR + savedFileName;
//		Image image = getImg(filePath, response);
//		System.out.println("=>>>>>>>>>>>>>>>> getImgCopy = " + image);
//		return image;
//	}
}
