package com.netive.nplate.controller;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.netive.nplate.common.MessageDTO;
import com.netive.nplate.domain.*;
import com.netive.nplate.paging.Pagination;
import com.netive.nplate.paging.PagingResponse;
import com.netive.nplate.service.FileService;
import com.netive.nplate.service.LikesService;
import com.netive.nplate.service.LoginService;
import com.netive.nplate.util.MemberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.netive.nplate.service.BoardService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {
	
	@Autowired
	private BoardService boardSerive;

	@Autowired
	private FileService fileService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private LikesService likesService;

	@Autowired
	private MemberUtils memberUtils;

	@Value("${nplate.upload.path}")
	private String filePath;

	private JsonArray jsonArray = new JsonArray();

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
		
		return "bootstrap-template/write";
	}

	/**
	 * 게시글 등록
	 * @param board
	 * @return
	 */
	@PostMapping("/board/register.do")
	public String openBoardRegister(final BoardDTO board, Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();

		if ((boolean) session.getAttribute("isLogOn")) {
			boardSerive.registerBoard(board);
			String content = board.getBbscttCn();
			Long idx = board.getBbscttNo(); // 게시글 저장 후의 게시글 번호
			System.out.println("=====================board 정보 = " + board.toString());
			System.out.println("jsonArray 길이 =================> "+jsonArray.size());
			if(jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i ++) {

					JsonObject _jsonData = (JsonObject) jsonArray.get(i);
					System.out.println(_jsonData);
					String fileNm = _jsonData.get("FILE_NM").getAsString();					// 원본 파일명
					String fileNmTemp = _jsonData.get("FILE_NM_TEMP").getAsString();		// 임시 파일명
					String fileCours = _jsonData.get("FILE_COURS").getAsString();			// 경로

					// 게시글 등록 시에 스마트 에디터에서 사진 업로드 하면
					// 업로드 된 사진 정보가 jsonArray 담겨있다.
					// 문제 되는 상황을 예로 들면
					// 처음 이미지 업로드는 2개 파일 선택 하고
					// 후에 하나의 이미지 파일을 에디터에서 지웠을 경우에
					// jsonArray에는 처음에 두 개 올린 파일의 정보가 담겨 있다.
					// 따라서 해당 게시글 내용에서 fileNm이 포함된 파일만 업로드한다.
					if(content.contains(fileNm)) {
						System.out.println("=============================파일 업로드 시작");
						fileService.saveBoardFile(fileNm, fileNmTemp, fileCours, idx);
					} else {
						// smarteditorMultiUpload.do에 의해 에디터에 이미지 올릴경우
						// 서버에 바로 저장 되므로
						// 게시글 저장 될 시 없는 파일은 서버에서 삭제해준다.
						File file = new File(filePath + fileNmTemp);

						if(file.exists()) {
							file.delete();
						}
					}
				}
			}
			jsonArray = new JsonArray();
		}

		MessageDTO message = new MessageDTO("게시글 등록이 완료되었습니다.", "/member/board/list", RequestMethod.GET, null);
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
	 * 스크롤 페이징 ajax
	 * @param params
	 * @return
	 */
	@PostMapping("/board/scrollList.do")
	@ResponseBody
	public Map<String, Object> openBoardScrollList(@ModelAttribute("params") final SearchDTO params, HttpServletRequest request) {

		Map<String , Object> resMap = new HashMap<>();
		HttpSession session = request.getSession();

		if ((boolean) session.getAttribute("isLogOn")) {
			try {

				int count = loginService.countPostsById(params.getMemberId()); // 글 개수

				Pagination pagination = new Pagination(count, params);
				params.setPagination(pagination);

				System.out.println("params ========================" + params.toString());

				List<BoardDTO> boardList = loginService.getBordListById(params); // 특정 아이디로 조회 글목록
				PagingResponse<BoardDTO> response = new PagingResponse<>(boardList, pagination);
				resMap.put("response", response);
//				return resMap;
			} catch (Exception e) {
				System.out.println("에러=======");
				e.printStackTrace();
				resMap.put("error", e);

				return resMap;
			}
		}

		return resMap;
	}

	/**
	 * 게시글 상세보기
	 * @param idx
	 * @param model
	 * @return
	 */
	@GetMapping("/board/view.do")
	public String openBoardDetail(@RequestParam final Long idx, Model model, HttpServletRequest request) {

		HttpSession session = request.getSession();
		MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		model.addAttribute("memberInfo", memberDTO);

		BoardDTO board = boardSerive.getBoardDetail(idx);
		model.addAttribute("board", board);

		// todo 매번 글 불러올때마다 하지말고 한번만 하는걸로 처리 수정
		String memberId = String.valueOf( session.getAttribute("memberID") ) ;
		List<String> following = memberUtils.getFollowingMember(memberId);
		boolean isFollowing = following.contains(board.getBbscttWrter());
		System.out.println("팔로잉하고있는지 isFollowing================= " + isFollowing);
		model.addAttribute("isFollowing", isFollowing);
		
		return "bootstrap-template/view";
	}


	/**
	 * 게시글 수정
	 * @param board
	 * @return
	 */
	@PostMapping("/board/update.do")
	public String updateBoard(final BoardDTO board, Model model) {

		// 기존 게시글에 저장되어있는 파일 정보 담을 변수
		String fileNm = "";
		String fileNmTemp = "";
		String fileCours = "";

		// 새로운 게시글에 저장될 파일 정보
		String newFileNm = "";
		String newFileNmTemp = "";
		String newFileCours = "";

		Long idx = board.getBbscttNo();

		// 새로운 이미지 정보 담을 빈 배열 선언
		List<String> newFileList = new ArrayList<>();



		// 아직 저장 전
		// 게시글 번호로 원래 게시글 번호에 저장되어 있던 파일 리스트 가져오고
//		List<BoardFileDTO> boardFileList = fileService.selectBoardFile(board.getBbscttNo());
		System.out.println("파일 저장 목록 불러오기 ===============================");
		List<BoardFileDTO> fileList = fileService.selectBoardFile(idx);
		System.out.println("fileList==================================");
		System.out.println(fileList);

		for ( int i = 0; i < jsonArray.size(); i++) {
			newFileList.add(jsonArray.get(i).toString());

			System.out.println(newFileList);
		}

		System.out.println("합친 jsonArray==================================");
		System.out.println(jsonArray);


		// 이미 저장되어있던 게시글 내용을 가져오기 위함
		// boardContent에 게시글 테이블 bbscttCn(내용) 컬럼 데이터를 담아서
		// 해당 데이터의 img태그의 src랑
		// jsonArray에 담겨있는 newFileNm이랑 비교해서
		// 신규면 추가하자..
		// 근데 기존에 있던 이미지를 지우면..
		// 좀 생각해보기..
		BoardDTO boardDetail = boardSerive.getBoardDetail(idx);
		String boardContent = boardDetail.getBbscttCn();

		// 새로 사진 올렸으면 JsonArray에 담긴다
		// 따라서 jsonArray 사이즈가 0보다 크면 새로 추가한 사진이 있다는 뜻
		if(jsonArray.size() > 0) {

			for (int i = 0; i < jsonArray.size(); i ++) {

				JsonObject _jsonData = (JsonObject) jsonArray.get(i);
				System.out.println(_jsonData);
				newFileNm = _jsonData.get("FILE_NM").getAsString();					// 원본 파일명
				newFileNmTemp = _jsonData.get("FILE_NM_TEMP").getAsString();		// 임시 파일명
				newFileCours = _jsonData.get("FILE_COURS").getAsString();			// 경로

				// 수정한 게시글 내용 가져와서
				// 새로 등록한 사진 이미지 정보가 boardContent에 포함되어 있으면
				// 디비에 저장하자
				if(boardContent.contains(newFileNmTemp)) {
					fileService.saveBoardFile(newFileNm, newFileNmTemp, newFileCours, idx);
				} else {

					// 에디터에서 사진 업로드해서 올린 파일을
					// 글내용 수정해서 지우면
					// 서버에서도 파일 삭제
					File rmFile = new File(filePath + newFileNmTemp);
					if(rmFile.exists()) {
						rmFile.delete();
					}
				}

				System.out.println("=============================파일 업로드 시작");
			}
		}

		// 기존 파일 내용이랑 다시 비교


		System.out.println("================= boardFileList =====================");
		System.out.println(fileList);
		System.out.println("================== boardFile Size =======================");
		System.out.println(fileList.size());

		// 반복문 돌면서 각각 하나씩 빼서 변수에 담고
//		for (int i = 0; i < fileList.size(); i++) {
//			fileNm = fileList.get(i).getAsString();
//			fileNmTemp = fileList.get(i).getAsString();
//			fileCours = fileList.get(i).getAsString();
//
//
//
//			// 저장 할때 기존 파일명이랑 신규 파일명 다르면 파일 업데이트
//			if(fileNmTemp != fileList.get(i).getAsString()) {
//				fileService.updateBoardFile(fileNm, fileNmTemp, fileCours, idx);
//			}
//
//		}


		boardSerive.updateBoard(board);
		MessageDTO message = new MessageDTO("게시글 수정이 완료되었습니다.", "/member/board/list", RequestMethod.GET, null);
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
		MessageDTO message = new MessageDTO("게시글 삭제가 완료되었습니다.", "/member/board/list", RequestMethod.GET, queryParams);
		return showMessageAndRedirect(message, model);
	}



	/**
	 * 스마트 에디터 싱글 파일 업로드
	 * @param request
	 * @param smarteditorDTO
	 * @return
	 * @throws UnsupportedEncodingException
	 */
//	@PostMapping("/singleImageUpload.do")
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
		HttpSession session = request.getSession();
		JsonObject jsonObject = new JsonObject();
		if ((boolean) session.getAttribute("isLogOn")) {
			MemberDTO member = (MemberDTO) session.getAttribute("member");
			String id = member.getId();

			try {
				String sFileInfo = "";                                                                        // 파일정보
				String sFileName = request.getHeader("file-name");                                    // 파일명 - 일반 원본 파일명
				String sFileNameExt = sFileName.substring(sFileName.lastIndexOf(".") + 1);        // 파일 확장자
				sFileNameExt = sFileNameExt.toLowerCase();                                                    // 확장자를 소문자로 변경

				String[] allowFileArr = {"jpg", "png", "bmp", "gif"};                                        // 이미지 검증 배열 변수

				int nCnt = 0;
				for (int i = 0; i < allowFileArr.length; i++) {
					if (sFileNameExt.equals(allowFileArr[i])) {
						nCnt++;
					}
				}

				if (nCnt == 0) {
					PrintWriter print = response.getWriter();
					print.print("NOTALLOW_" + sFileName);
					print.flush();
					print.close();
				} else {
					// 디렉토리 설정 및 업로드
					// 파일경로
					String path = filePath;
					File file = new File(path);

					if (!file.exists()) {
						file.mkdir();
					}


					String sRealFileName = "";
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	//				String today = formatter.format(new Date());
					sRealFileName = UUID.randomUUID().toString() + sFileName.substring(sFileName.lastIndexOf("."));
					String realFileName = filePath + sRealFileName;

					// 서버에 파일 쓰기
					InputStream inputStream = request.getInputStream();
					OutputStream outputStream = new FileOutputStream(realFileName);
					int numRead;
					byte bytes[] = new byte[Integer.parseInt(request.getHeader("file-size"))];
					while ((numRead = inputStream.read(bytes, 0, bytes.length)) != -1) {
						outputStream.write(bytes, 0, numRead);


						// 업로드된 파일을 테이블에 저장하기 위한 json 오브젝트
						jsonObject.addProperty("FILE_NM", sFileName);
						jsonObject.addProperty("FILE_NM_TEMP", sRealFileName);
						jsonObject.addProperty("FILE_COURS", path);

						jsonArray.add(jsonObject);

					}
					if (inputStream != null) {
						inputStream.close();
					}
					outputStream.flush();
					outputStream.close();

					///////////////////// 이미지 /////////////////////////
					// 정보 출력
					sFileInfo += "&bNewLine=true";
					// img 태그의 title 속성을 원본파일명으로 적용시켜주기 위함
					sFileInfo += "&sFileName=" + sFileName;
	//				sFileInfo += "&sFileURL=/nplateImage/" + sRealFileName;
					sFileInfo += "&sFileURL=/nplateImage/" + sRealFileName;
					System.out.println("sFileInfo = " + sFileInfo);
					System.out.println("==========================" + jsonArray);



					PrintWriter printWriter = response.getWriter();
					printWriter.print(sFileInfo);
					printWriter.flush();
					printWriter.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
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


	// 게시물 좋아요 추가
	@GetMapping("/board/addLike")
	public @ResponseBody String likePost(String id, Long idx) {
		System.out.println("좋아요 추가 컨트롤러 시작=========");
		System.out.println("아이디" + id);
		System.out.println("글번호" + idx);

		LikesDTO likesDTO = new LikesDTO(id, idx);
		int result = likesService.addLike(likesDTO);
		if (result > 0) {
			System.out.println("좋아요 추가 성공=========");
			return "success";
		} else {
			System.out.println("좋아요 추가 실패=========");
			return "fail";
		}
	}


	// 게시물 좋아요 취소
	@GetMapping("/board/deleteLike")
	public @ResponseBody String deleteLike(String id, Long idx) {
		System.out.println("좋아요 취소 컨트롤러 시작=========");
		System.out.println("아이디" + id);
		System.out.println("글번호" + idx);

		LikesDTO likesDTO = new LikesDTO(id, idx);
		int result = likesService.deleteLike(likesDTO);
		if (result > 0) {
			System.out.println("좋아요 취소 성공=========");
			return "success";
		} else {
			System.out.println("좋아요 취소 실패=========");
			return "fail";
		}
	}

}
