package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public List<MemberDTO> getMemberList() {
        List<MemberDTO> memberList = Collections.emptyList();
        // todo count 수정하기
        int count = 1;
        if (count > 0) {
            memberList = memberMapper.listMembers();
        }
        return memberList;
    }
}
