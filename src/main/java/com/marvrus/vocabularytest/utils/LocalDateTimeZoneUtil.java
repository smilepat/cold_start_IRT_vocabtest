package com.marvrus.vocabularytest.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * zone id 매번 넣을때 보기가 안좋아서 만듬
 */
public class LocalDateTimeZoneUtil {
    public static LocalDateTime getNow() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public static LocalDate getToday() {
        return LocalDate.now(ZoneId.of("Asia/Seoul"));
    }

    public static Date getNowAsDate() {
        return Date.from(LocalDateTime.now(ZoneId.of("Asia/Seoul")).atZone(ZoneId.systemDefault()).toInstant());
    }
}

