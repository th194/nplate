package com.netive.nplate.domain;

import lombok.Getter;

@Getter
public enum Area {
    전국("00"),
    서울특별시("11"),
    부산광역시("21"),
    대구광역시("22"),
    인천광역시("23"),
    광주광역시("24"),
    대전광역시("25"),
    울산광역시("26"),
    세종특별자치시("29"),
    경기도("31"),
    강원도("32"),
    충청북도("33"),
    충청남도("34"),
    전라북도("35"),
    전라남도("36"),
    경상북도("37"),
    경상남도("38"),
    제주특별자치도("39");

    private String code;

    Area(String code) {
        this.code = code;
    }

}
