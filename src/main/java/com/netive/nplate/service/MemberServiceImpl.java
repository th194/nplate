package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public MemberDTO getMemberInfo(String id) {
        return memberMapper.selectMemberById(id);
    }

    @Override
    public MemberDTO login(String id, String pwd) {
        return memberMapper.login(id, pwd);
    }

    @Override
    public int deleteMember(String id) {
        return memberMapper.deleteMember(id);
    }

    @Override
    public int updateInfo(MemberDTO memberDTO) {
        return memberMapper.updateInfo(memberDTO);
    }

    @Override
    public int checkOverlappedId(String id) {
        return memberMapper.checkOverlappedId(id);
    }

    @Override
    public int updatePwd(MemberDTO memberDTO) {
        return memberMapper.updatePwd(memberDTO);
    }

    @Override
    public List<MemberDTO> getFollowingInfo(Map map) {
        return memberMapper.getFollowingInfo(map);
    }

    @Override
    public MemberDTO getUserInfo(String id) {
        return memberMapper.getUserInfo(id);
    }
}
