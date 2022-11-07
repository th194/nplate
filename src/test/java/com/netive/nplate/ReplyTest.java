package com.netive.nplate;

import com.netive.nplate.domain.ReplyDTO;
import com.netive.nplate.service.ReplyService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReplyTest {
    @Autowired
    private ReplyService replyService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void insertReplyTest() {
        int number = 10;

        for (int i = 0; i <= number; i++) {
            ReplyDTO reply = new ReplyDTO();
            reply.setBbscttNo((long) 551);
            reply.setAnswerCn(i + "댓글테스트 내용");
            reply.setAnswerWrter(i + "댓글테스트 작성자");
            replyService.registerReply(reply);
        }

        logger.debug("댓글 " + number + " 개 등록");
    }

    @Test
    public void deleteReplyTest() {
        replyService.deleteReply((long) 10);

        getReplyListTest();
    }

    @Test
    public void getReplyListTest() {
        ReplyDTO reply = new ReplyDTO();
        reply.setBbscttNo((long) 551);
        replyService.getReplyList(reply);

    }
}
