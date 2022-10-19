package com.netive.nplate;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.netive.nplate.domain.BoardDTO;
import com.netive.nplate.mapper.BoardMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
class BoardApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private SqlSessionFactory sessionFactory;

	@Autowired
	private BoardMapper boardMapper;

	@Test
	void contextLoads() {
	}

	@Test
	public void testApplicationContext() {
		try {
			System.out.println("=====================================");
			System.out.println(context.getBean("sqlSessionFactory"));
			System.out.println("=====================================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSqlSessioFactory() {
		try {
			System.out.println("=====================================");
			System.out.println(sessionFactory.toString());
			System.out.println("=====================================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInsert() {
		BoardDTO boardDTO = new BoardDTO();
		boardDTO.setBbscttSj("2번 제목");
		boardDTO.setBbscttCn("2번 내용");
		boardDTO.setBbscttWrter("테스터");
		int result = boardMapper.insertBoard(boardDTO);
		System.out.println("result =======================>" + result);
	}

	@Test
	public void testSelectDetail() {
		BoardDTO boardDTO = boardMapper.selectBoardDetail((long) 1);

		try {
			String boardJSON = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(boardDTO);

			System.out.println("================================");
			System.out.println(boardJSON);
			System.out.println("================================");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdate() {
		BoardDTO boardDTO = new BoardDTO();

		boardDTO.setBbscttSj("2번 제목");
		boardDTO.setBbscttCn("2번 내용");
		boardDTO.setBbscttWrter("테스터");
		boardDTO.setBbscttNo((long) 2);

		int result = boardMapper.updateBoard(boardDTO);

		System.out.println("result ===============>" + result);
	}

	@Test
	public void testDelete() {

		int result = boardMapper.deleteBoard((long) 2);

		System.out.println("result ===================>" + result);
	}

	@Test
	public void testAdd() {
		BoardDTO boardDTO = new BoardDTO();
		for(int i = 0; i < 10; i++) {
			boardDTO.setBbscttSj("테스트제목 " + i);
			boardDTO.setBbscttCn("테스트 내용 " + i);
			boardDTO.setBbscttWrter("테스터 " + i);
			boardMapper.insertBoard(boardDTO);
		}
	}

	@Test
	public void testSelect() {
		int boardCount = boardMapper.selectBoardTotalCount();
		if (boardCount != 0) {
			List<BoardDTO> boardList = boardMapper.selectBoardList();
			if(boardList != null) {
				for(BoardDTO board : boardList) {
					System.out.println("================================");
					System.out.println(board.getBbscttSj());
					System.out.println(board.getBbscttCn());
					System.out.println(board.getBbscttWrter());
					System.out.println("================================");
				}
			}
		}
	}
}
