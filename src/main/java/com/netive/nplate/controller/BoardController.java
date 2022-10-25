package com.netive.nplate.controller;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import com.netive.nplate.domain.BoardFileDTO;
import com.netive.nplate.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.service.BoardService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BoardController {
	
	@Autowired
	private BoardService boardSerive;

	@Autowired
	private FileUtils fileUtils;

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
			List<BoardFileDTO> fileList = boardSerive.getFileList(idx);
			model.addAttribute("fileList", fileList);
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
	public String openBoardRegister(BoardDTO boardDTO, Model model, MultipartFile[] files, HttpServletRequest request) {

		System.out.println("boardDTO >>>>>>>>>> " + boardDTO);
		System.out.println("files >>>>>>>>>> " + files);

		try {

			String path = request.getSession().getServletContext().getRealPath("/img");

			System.out.println("컨트롤러 path = >>>>>>>>>>>>>>>>>>>>> " + path);
//			List<File> fileList = fileUtils.getImgFileList(file, path);

			boolean isRegistered = boardSerive.registerBoard(boardDTO, files, path);

			if (isRegistered == false) {
				System.out.println("등록 실패");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

//		boardSerive.registerBoard(boardDTO);
		List<BoardDTO> boardList = boardSerive.getBordList();
		model.addAttribute("boardList", boardList);

		return "board/list";
	}

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

		System.out.println("view idx = " + idx);
		if(idx == null) {
			// 올바르지 않은 접근
			return "redirect:/board/list.do";
		}
		
		BoardDTO boardDTO = boardSerive.getBoardDetail(idx);
		model.addAttribute("board", boardDTO);

		List<BoardFileDTO> fileList = boardSerive.getFileList(idx);
		model.addAttribute("fileList", fileList);

		System.out.println("view fileList >>>>>>>>>>>>>>>>>>>>" + fileList);

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
	 * 스마트 에디터 싱글 파일 업로드
	 * @param request
	 * @param smarteditorDTO
	 * @return
	 * @throws UnsupportedEncodingException
	 */
//	@PostMapping("/singleImageUploade.do")
//	public String smarteditorSingleImageUpload(HttpServletRequest request, SmarteditorDTO smarteditorDTO) throws UnsupportedEncodingException {
//
//
//		System.out.println("request >>>>>>>>>>>>>>>>>>>>>>>>>>> " + request);
//		System.out.println("smarteditorDTO >>>>>>>>>>>>>>>>>>>>>>>>> " + smarteditorDTO);
//
//		String callback = smarteditorDTO.getCallback();
//		String callback_func = smarteditorDTO.getCallback_func();
//		String file_result = "";
//		String result = "";
//		MultipartFile multiFile = smarteditorDTO.getFiledata();
//
//		try {
//			if(multiFile != null && multiFile.getSize() > 0 && StringUtils.isNotBlank(multiFile.getName())) {
//				if(multiFile.getContentType().toLowerCase().startsWith("image/")) {
//					String oriName = multiFile.getName();
//					String uploadPath = request.getServletContext().getRealPath("/img");
//					String path = uploadPath + "/smarteditor/";
//					File file = new File(path);
//
//					if (!file.exists()) {
//						file.mkdir();
//					}
//					String fileName = UUID.randomUUID().toString();
//					smarteditorDTO.getFiledata().transferTo(new File(path + fileName));
//					file_result += "&&bNewLine=true&sFileName=" + oriName + "&sFileURL=/img/smarteditor/" + fileName;
//				} else {
//					file_result += "&errstr=error";
//				}
//			} else {
//				file_result += "&errstr=error";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		result = "redirect:" + callback + "?callback_func=" + URLEncoder.encode(callback_func, "UTF-8") + file_result;
//
//		System.out.println("result>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + result);
//		return result;
//	}

	/**
	 * 스마트 에디터 멀티 파일 업로드
	 * @param request
	 * @param response
	 */
	@PostMapping("/smarteditorMultiImageUpload.do")
	public void smarteditorMultiImageUpload(HttpServletRequest request, HttpServletResponse response) {
		try {
			String sFileInfo = "";																		// 파일정보
			String sFileName = request.getHeader("file-name");									// 파일명 - 일반 원본 파일명
			String sFileNameExt = sFileName.substring(sFileName.lastIndexOf(".") + 1);		// 파일 확장자
			sFileNameExt = sFileNameExt.toLowerCase();													// 확장자를 소문자로 변경

			String[] allowFileArr = {"jpg", "png", "bmp", "gif"};										// 이미지 검증 배열 변수

			int nCnt = 0;
			for (int i = 0; i < allowFileArr.length; i ++) {
				if(sFileNameExt.equals(allowFileArr[i])) {
					nCnt ++;
				}
			}

			if(nCnt == 0) {
				PrintWriter print = response.getWriter();
				print.print("NOTALLOW_"+sFileName);
				print.flush();
				print.close();
			} else {
				// 디렉토리 설정 및 업로드
				// 파일경로
				String defaultPath = request.getSession().getServletContext().getRealPath("/");
				String path = defaultPath + "img" + File.separator;

				File file = new File(path);

				if(!file.exists()) {
					file.mkdir();
				}
				System.out.println("path >>>>>>>>>>>>>>> " + path);

				String sRealFileName = "";
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//				String today = formatter.format(new Date());
				sRealFileName = UUID.randomUUID().toString() + sFileName.substring(sFileName.lastIndexOf("."));
				String realFileName = path + sRealFileName;

				// 서버에 파일 쓰기
				InputStream inputStream = request.getInputStream();
				OutputStream outputStream = new FileOutputStream(realFileName);
				int numRead;
				byte bytes[] = new byte[Integer.parseInt(request.getHeader("file-size"))];
				while((numRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
					outputStream.write(bytes, 0, numRead);
				}
				if(inputStream != null) {
					inputStream.close();
				}
				outputStream.flush();
				outputStream.close();

				///////////////////// 이미지 /////////////////////////
				// 정보 출력
				sFileInfo += "&bNewLine=true";
				// img 태그의 title 속성을 원본파일명으로 적용시켜주기 위함
				sFileInfo += "&sFileName=" + sFileName;
				sFileInfo += "&sFileURL=/img/" + sRealFileName;

				System.out.println("sFileInfo = " + sFileInfo);

				PrintWriter printWriter = response.getWriter();
				printWriter.print(sFileInfo);
				printWriter.flush();
				printWriter.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}




	}

	/**
	 * 지도 팝업 추가
	 */
	@GetMapping("/board/mapPopup")
	public String openmapPopup() {
		return "board/mapPopup";
	}
}
