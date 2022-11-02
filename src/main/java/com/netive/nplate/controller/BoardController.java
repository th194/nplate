package com.netive.nplate.controller;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.netive.nplate.common.MessageDTO;
import com.netive.nplate.domain.*;
import com.netive.nplate.paging.PagingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.netive.nplate.service.BoardService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
@Slf4j
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
	public String openBoardWrite(@RequestParam(value = "idx", required = false) final Long idx, Model model) {
		if (idx != null) {
			BoardDTO board = boardSerive.getBoardDetail(idx);
			model.addAttribute("board", board);
		}
		
		return "board/write";
	}

	/**
	 * 게시글 등록
	 * @param board
	 * @return
	 */
	@PostMapping("/board/register.do")
	public String openBoardRegister(final BoardDTO board, Model model) {
		boardSerive.registerBoard(board);
		MessageDTO message = new MessageDTO("게시글 등록이 완료되었습니다.", "/board/list.do", RequestMethod.GET, null);
		return showMessageAndRedirect(message, model);
	}

	/**
	 * 게시글 목록
	 * @param model
	 * @return
	 */
	@GetMapping("/board/list.do")
	public String openBoardList(@ModelAttribute("params") final SearchDTO params, Model model) {

		PagingResponse<BoardDTO> response = boardSerive.getBoardList(params);
		model.addAttribute("response", response);

		return "board/list";
	}

	/**
	 * 게시글 상세보기
	 * @param idx
	 * @param model
	 * @return
	 */
	@GetMapping("/board/view.do")
	public String openBoardDetail(@RequestParam final Long idx, Model model) {

		BoardDTO board = boardSerive.getBoardDetail(idx);
		model.addAttribute("board", board);
		return "board/view";
	}


	/**
	 * 게시글 수정
	 * @param board
	 * @return
	 */
	@PostMapping("/board/update.do")
	public String updateBoard(final BoardDTO board, Model model) {
		boardSerive.updateBoard(board);
		MessageDTO message = new MessageDTO("게시글 수정이 완료되었습니다.", "/board/list.do", RequestMethod.GET, null);
		return showMessageAndRedirect(message, model);
	}

	/**
	 * 게시글 삭제
	 * @param idx
	 * @return
	 */
	@PostMapping("/board/delete.do")
	public String deleteBoard(@RequestParam final Long idx, @RequestParam final Map<String, Object> queryParams, Model model) {

		boardSerive.deleteBoard(idx);
		MessageDTO message = new MessageDTO("게시글 삭제가 완료되었습니다.", "/board/list.do", RequestMethod.GET, queryParams);
		return showMessageAndRedirect(message, model);
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

//				String defaultPath = request.getSession().getServletContext().getRealPath("/");
//				String path = defaultPath + "img" + File.separator;
				String path = "/nplateImage/post/";

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
//				sFileInfo += "&sFileURL=D:/images/" + sRealFileName;
				sFileInfo += "&sFileURL=/nplateImage/post/" + sRealFileName;

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

	private String showMessageAndRedirect(final MessageDTO params, Model model) {
		model.addAttribute("params", params);
		return "common/messageRedirect";
	}

	/**
	 * 지도 팝업 추가
	 */
	@GetMapping("/board/mapPopup")
	public String openmapPopup() {
		return "board/mapPopup";
	}
}
