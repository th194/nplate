package com.netive.nplate.service;

import com.netive.nplate.domain.MemberDTO;
import com.netive.nplate.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminMapper adminMapper;

    @Override
    public List<MemberDTO> getMemberList(String type) {

        List<MemberDTO> members = new ArrayList<>();

        // type: user, manager, admin, expired
        switch (type) {
            case "user":
                members = adminMapper.listMembersTypeRole("ROLE_USER");
                break;
            case "manager":
                members = adminMapper.listMembersTypeRole("ROLE_MANAGER");
                break;
            case "admin":
                members = adminMapper.listMembersTypeRole("ROLE_ADMIN");
                break;
            case "expired":
                members = adminMapper.listExpiredMembers();
                break;
        }

        return members;
    }
}
