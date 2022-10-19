package com.netive.nplate.controller;

import java.util.List;

import com.netive.nplate.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.service.BoardService;

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
		boardSerive.registerBoard(boardDTO);
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
	
}
