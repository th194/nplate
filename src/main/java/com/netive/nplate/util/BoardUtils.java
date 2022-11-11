package com.netive.nplate.util;

import com.netive.nplate.domain.LikesDTO;
import com.netive.nplate.service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BoardUtils {

    @Autowired
    private LikesService likesService;

    public List<Long> getLikeNumbers(String id) {
        List<LikesDTO> likes = likesService.getLikes(id);
        List<Long> likeNumbers = new ArrayList<Long>();
        for (LikesDTO likesDTO : likes) {
            System.out.println("넘버===" + likesDTO.getBbscttNo());
            likeNumbers.add(likesDTO.getBbscttNo());
        }
        return likeNumbers;
    }
}
