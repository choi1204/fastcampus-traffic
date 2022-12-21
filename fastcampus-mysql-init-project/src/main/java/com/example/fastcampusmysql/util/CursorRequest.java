package com.example.fastcampusmysql.util;

/*
    커서키의 가장 중요한점은 중복키가 없어야 한다. == 유니크
    디폴트키 설정
 */
public record CursorRequest(Long key, int size) {
    public static final Long NONE_KEY = -1L;

    public Boolean hasKey() {
        return key != null;
    }
    public CursorRequest next(Long key) {
        return new CursorRequest(key, size);
    }
}
