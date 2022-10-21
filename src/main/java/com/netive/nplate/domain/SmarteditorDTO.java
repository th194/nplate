package com.netive.nplate.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SmarteditorDTO {
    private MultipartFile filedata;
    private String callback;
    private String callback_func;
}
