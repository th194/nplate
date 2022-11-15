package com.netive.nplate.controller;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.netive.nplate.common.MessageDTO;
import com.netive.nplate.domain.*;
import com.netive.nplate.service.*;
import com.netive.nplate.util.BoardUtils;
import com.netive.nplate.util.MemberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {
	
	@Autowired
	private BoardService boardService;

	@Autowired
	private FileService fileService;

	@Autowired
	private LoginService loginService;

	@Autowired
	private LikesService likesService;

	@Autowired
	private FollowingService followingService;

	@Autowired
	private BoardUtils boardUtils;

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
			BoardDTO board = boardService.getBoardDetail(idx);
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

		if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
			boardService.registerBoard(board);
			String content = board.getBbscttCn();
			Long idx = board.getBbscttNo(); // 게시글 저장 후의 게시글 번호
			if(jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i ++) {

					JsonObject _jsonData = (JsonObject) jsonArray.get(i);
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

		}
		jsonArray = new JsonArray();
		MessageDTO message = new MessageDTO("게시글 등록이 완료되었습니다.", "/member/board/list", RequestMethod.GET, null);
		return showMessageAndRedirect(message, model);
	}



	/**
	 * 스크롤 페이징 ajax
	 * @param params
	 * @return
	 */
	@PostMapping("/board/scrollList.do")
	@ResponseBody
	public Map<String, Object> openBoardScrollList(@ModelAttribute("params") final SearchDTO params, HttpServletRequest request) {
		System.out.println("스크롤 페이징====================================");

		System.out.println("params===========================");
		System.out.println(params.toString());

		Map<String , Object> resMap = new HashMap<>();
		HttpSession session = request.getSession();

		if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
			try {
				List<BoardDTO> boardList;
				String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
				
				switch (params.getSearchType()) {
					case "search":
					case "hashtag":
						System.out.println("키워드로 조회 목록");
						boardList = boardService.getBordListByKeyword(params);
						break;
					case "following":
						System.out.println("내가 팔로잉하는 사람들 조회(나포함)");
						List<String> followingIds;
						if (session.getAttribute(SessionConstants.FOLLOWING_IDS) != null) {
							followingIds = (List<String>) session.getAttribute(SessionConstants.FOLLOWING_IDS);
						} else {
							followingIds = memberUtils.getFollowingMember(memberId);
						}
						followingIds.add(memberId); // 내 아이디도 넣기

						Map <String, Object> followingMap = new HashMap<>();
						followingMap.put("followingIds", followingIds);
						followingMap.put("limitStart", params.getLimitStart());
						followingMap.put("recordSize", params.getRecordSize());

						if (followingIds.size() != 0) {
							boardList = boardService.getBordListByIds(followingMap);
						} else {
							boardList = null;
						}
						System.out.println("글목록 크기:" + boardList.size());
						break;
					case "like":
						System.out.println("좋아하는 글목록");
						Map <String, Object> likeMap = new HashMap<>();
						List<Long> likeNumbers;

						if (session.getAttribute(SessionConstants.LIKE_NUMBERS) != null) {
							likeNumbers = (List<Long>) session.getAttribute(SessionConstants.LIKE_NUMBERS);
						} else {
							likeNumbers = boardUtils.getLikeNumbers(memberId);
						}

						likeMap.put("likeNumbers", likeNumbers);
						likeMap.put("limitStart", params.getLimitStart());
						likeMap.put("recordSize", params.getRecordSize());

						if (likeNumbers.size() != 0) {
							boardList = loginService.getLikes(likeMap);
						} else {
							boardList = null;
						}
						System.out.println("글목록 크기:" + boardList.size());
						// todo likePosts 좋아요 누른 순서대로 정렬되게 처리 추가
						break;
					default:
						System.out.println("특정 아이디로 조회 글목록");
						boardList = boardService.getBordListById(params); // 특정 아이디로 조회 글목록
				}

				resMap.put("response", boardList);
				resMap.put("search", params); // 분기처리 위해 넣음

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
		MemberDTO memberDTO = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
		model.addAttribute("memberInfo", memberDTO);

		BoardDTO board = boardService.getBoardDetail(idx);
		model.addAttribute("board", board);

		String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);

		// 팔로잉 여부 확인
		List<String> followingIds;
		if (session.getAttribute(SessionConstants.FOLLOWING_IDS) != null) {
			followingIds = (List<String>) session.getAttribute(SessionConstants.FOLLOWING_IDS);
		} else {
			followingIds = memberUtils.getFollowingMember(memberId);
		}
		boolean isFollowing = followingIds.contains(board.getBbscttWrter());
		System.out.println("팔로잉하고있는지 isFollowing================= " + isFollowing);
		model.addAttribute("isFollowing", isFollowing);

		// 메뉴 팔로잉 처리
		List<MemberDTO> followingMembers;
		if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
			followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
		} else {
			followingMembers = memberUtils.getFollowingsInfo(followingIds);
		}
		model.addAttribute("followingMembers", followingMembers);

		boardService.cntPlus(idx); // 조회수 증가 추가

		return "bootstrap-template/view";
	}


	/**
	 * 게시글 수정
	 * @param board
	 * @return
	 */
	@PostMapping("/board/update.do")
	public String updateBoard(final BoardDTO board, Model model) {

		Long idx = board.getBbscttNo();
		String fileNmTemp = ""; // 새로 추가된 사진 임시 저장명 담을 변수
		String fileNm ="";		// 새로 추가된 사진 원본 파일명
		String fileCours = "";	// 경로

		// 기존 저장되어있는 파일명 담을 변수
		String oldFileNmTemp = "";

		// 게시글 번호로 기존에 저장돼 있던 파일 목록 가져오기
		List<BoardFileDTO> oldFile = fileService.selectBoardFile(idx);

		String content = board.getBbscttCn(); // update 된 게시글 내용

		// 기존 게시글의 저장 파일명과 신규로 업데이트 된 게시글 내용의 저장파일명 비교
		for(int i = 0; i < oldFile.size(); i ++) {
			oldFileNmTemp = oldFile.get(i).getFileNmTemp();

			// 업데이트 된 게시글 내용에 예전에 저장되어있던 파일명이 없으면
			if(!content.contains(oldFileNmTemp)) {
				// 서버에서 파일 삭제
				File removeFile = new File(filePath + oldFileNmTemp);

				if(removeFile.exists()) {
					removeFile.delete();
				}
				// 업데이트 된 게시글 내용에서 지워진 파일명만 삭제
				fileService.removeBoardFileByNm(oldFileNmTemp);
			}
		}


		// 새로 추가된 사진이 있다면
		if(jsonArray.size() > 0) {
			for (int i = 0; i < jsonArray.size(); i ++) {

				JsonObject _jsonData = (JsonObject) jsonArray.get(i);

				fileNm = _jsonData.get("FILE_NM").getAsString();					// 원본 파일명
				fileNmTemp = _jsonData.get("FILE_NM_TEMP").getAsString();		// 임시 파일명
				fileCours = _jsonData.get("FILE_COURS").getAsString();			// 경로

				// 등록(register.do)과 동일
				if(content.contains(fileNmTemp)) {
					fileService.saveBoardFile(fileNm, fileNmTemp, fileCours, idx);
				} else {
					File file = new File(filePath + fileNmTemp);

					if(file.exists()) {
						file.delete();
					}
				}
			}
		}
		jsonArray = new JsonArray();

		// 게시글 업데이트
		boardService.updateBoard(board);

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

		List<BoardFileDTO> boardFileList = fileService.selectBoardFile(idx);	// 현재 게시글 번호로 저장돼 있는 파일 정보 가져오기
		String fileNmTemp = "";		// 저장된 파일명 담을 변수

		for (int i = 0; i < boardFileList.size(); i++ ) {
			fileNmTemp = boardFileList.get(i).getFileNmTemp();

			// 서버에서 파일 삭제
			File removeFile = new File(filePath + fileNmTemp);

			if (removeFile.exists()) {
				removeFile.delete();
			}
		}
		boardService.deleteBoard(idx);
		MessageDTO message = new MessageDTO("게시글 삭제가 완료되었습니다.", "/member/board/list", RequestMethod.GET, null);
		return showMessageAndRedirect(message, model);
	}


	/**
	 * 스마트 에디터 멀티 파일 업로드
	 * @param request
	 * @param response
	 */
	@PostMapping("/smarteditorMultiImageUpload.do")
	public void smarteditorMultiImageUpload(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		JsonObject jsonObject = new JsonObject();
		if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
			MemberDTO member = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
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
	public @ResponseBody String likePost(HttpServletRequest request, Long idx) {
		System.out.println("좋아요 추가 컨트롤러 시작=========");
		HttpSession session = request.getSession();
		String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
		System.out.println("아이디" + memberId);
		System.out.println("글번호" + idx);

		LikesDTO likesDTO = new LikesDTO(memberId, idx);
		int result = likesService.addLike(likesDTO);
		if (result > 0) {
			System.out.println("좋아요 추가 성공=========");
			session.setAttribute(SessionConstants.LIKE_NUMBERS, null);
			return "success";
		} else {
			System.out.println("좋아요 추가 실패=========");
			return "fail";
		}
	}


	// 게시물 좋아요 취소
	@GetMapping("/board/deleteLike")
	public @ResponseBody String deleteLike(HttpServletRequest request, Long idx) {
		System.out.println("좋아요 취소 컨트롤러 시작=========");
		HttpSession session = request.getSession();
		String memberId = (String) session.getAttribute(SessionConstants.MEMBER_ID);
		System.out.println("아이디" + memberId);
		System.out.println("글번호" + idx);

		LikesDTO likesDTO = new LikesDTO(memberId, idx);
		int result = likesService.deleteLike(likesDTO);
		if (result > 0) {
			System.out.println("좋아요 취소 성공=========");
			session.setAttribute(SessionConstants.LIKE_NUMBERS, null);
			return "success";
		} else {
			System.out.println("좋아요 취소 실패=========");
			return "fail";
		}
	}


	// 검색 결과 페이지
	@GetMapping("/board/search")
	public String getSearchResult(@ModelAttribute("params") final SearchDTO params, Model model, HttpServletRequest request) {
		System.out.println("검색 결과 페이지========");
		HttpSession session = request.getSession();

		System.out.println("서치디티오=====================");
		System.out.println(params.toString());

		if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
			try {
				// todo 똑같은 처리들 하나로 통합하기
				MemberDTO dto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
				String memberId = dto.getId();

				model.addAttribute("memberInfo", dto);
				model.addAttribute("search", params);

				// 좋아요
				List<Long> likeNumbers;
				if (session.getAttribute(SessionConstants.LIKE_NUMBERS) != null) {
					likeNumbers = (List<Long>) session.getAttribute(SessionConstants.LIKE_NUMBERS);
				} else {
					likeNumbers = boardUtils.getLikeNumbers(memberId);
				}
				model.addAttribute("likeNumbers", likeNumbers);

				// 팔로잉 처리
				List<MemberDTO> followingMembers;
				if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
					followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
				} else {
					List<String> followingIds = memberUtils.getFollowingMember(memberId);
					followingMembers = memberUtils.getFollowingsInfo(followingIds);
				}
				model.addAttribute("followingMembers", followingMembers);

				return "bootstrap-template/list";

			} catch (Exception e) {
				// todo 에러 발생시 처리 추가
				System.out.println("에러=======");
				e.printStackTrace();
				return "member/index";
			}
		}
		return "member/index";
	}


	// 팔로잉 피드 페이지
	@GetMapping("/board/following")
	public String getFeed(@ModelAttribute("params") final SearchDTO params, Model model, HttpServletRequest request) {
		System.out.println("팔로잉 피드 페이지========");
		HttpSession session = request.getSession();

		System.out.println("서치디티오=====================");
		System.out.println(params.toString());

		if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
			try {
				// todo 똑같은 처리들 하나로 통합하기(좋아요 등)
				MemberDTO dto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
				String memberId = dto.getId();

				// 리스트 타입(피드: 팔로잉)
				params.setSearchType("following");
				model.addAttribute("memberInfo", dto);
				model.addAttribute("search", params);

				// 좋아요
				List<Long> likeNumbers;
				if (session.getAttribute(SessionConstants.LIKE_NUMBERS) != null) {
					likeNumbers = (List<Long>) session.getAttribute(SessionConstants.LIKE_NUMBERS);
				} else {
					likeNumbers = boardUtils.getLikeNumbers(memberId);
				}
				model.addAttribute("likeNumbers", likeNumbers);


				// 팔로잉 처리
				List<MemberDTO> followingMembers;
				if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
					followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
				} else {
					List<String> followingIds = memberUtils.getFollowingMember(memberId);
					followingMembers = memberUtils.getFollowingsInfo(followingIds);
				}
				model.addAttribute("followingMembers", followingMembers);

				return "bootstrap-template/list";

			} catch (Exception e) {
				// todo 에러 발생시 처리 추가
				System.out.println("에러=======");
				e.printStackTrace();
				return "member/index";
			}
		}
		return "member/index";
	}


	// 좋아한 게시글 목록(북마크)
	@GetMapping("/member/board/likePosts")
	public String lisePosts(Model model, HttpServletRequest request, String keyword) {
		System.out.println("좋아한 게시글 목록========");
		HttpSession session = request.getSession();

		if ((boolean) session.getAttribute(SessionConstants.IS_LOGIN) && session.getAttribute(SessionConstants.MEMBER_DTO) != null) {
			try {
				MemberDTO dto = (MemberDTO) session.getAttribute(SessionConstants.MEMBER_DTO);
				model.addAttribute("memberInfo", dto);
				String memberId = dto.getId();

				// 리스트 타입(좋아요: 좋아한 글 목록)
				SearchDTO searchDTO = new SearchDTO("");
				searchDTO.setSearchType("like");
				model.addAttribute("search", searchDTO);

				// 좋아요
				List<Long> likeNumbers;
				if (session.getAttribute(SessionConstants.LIKE_NUMBERS) != null) {
					likeNumbers = (List<Long>) session.getAttribute(SessionConstants.LIKE_NUMBERS);
				} else {
					likeNumbers = boardUtils.getLikeNumbers(memberId);
				}
				model.addAttribute("likeNumbers", likeNumbers);

				// 팔로잉 처리
				List<MemberDTO> followingMembers;
				if (session.getAttribute(SessionConstants.FOLLOWING_MEMBERS) != null) {
					followingMembers = (List<MemberDTO>) session.getAttribute(SessionConstants.FOLLOWING_MEMBERS);
				} else {
					List<String> followingIds = memberUtils.getFollowingMember(memberId);
					followingMembers = memberUtils.getFollowingsInfo(followingIds);
				}
				model.addAttribute("followingMembers", followingMembers);

				return "bootstrap-template/list";

			} catch (Exception e) {
				// todo 에러 발생시 처리 추가
				System.out.println("에러=======");
				e.printStackTrace();
				return "member/index";
			}
		} else {
			return "member/index";
		}
	}
}
