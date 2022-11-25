package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.mapper.AdminMapper;
import com.netive.nplate.mapper.LoginMapper;
import com.netive.nplate.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    LoginMapper loginMapper;

    @Autowired
    MemberMapper memberMapper;
    
    @Autowired
    AdminMapper adminMapper;

    // 스프링 시큐리티 회원 가입
    public void registerMember(MemberDTO memberDTO) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        memberDTO.setPwd(passwordEncoder.encode(memberDTO.getPwd()));
        memberMapper.registerMember(memberDTO);
    }

    // 관리자용 회원 여러명 추가
    public void addMembers(MemberDTO memberDTO) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        memberDTO.setPwd(passwordEncoder.encode(memberDTO.getPwd()));
        adminMapper.addMembers(memberDTO);
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        MemberDTO memberDTO = loginMapper.loginBySpringSecurity(id);

        if (memberDTO == null) {
            throw new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.");
        }

        return memberDTO;
    }
}
