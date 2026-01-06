package com.marvrus.vocabularytest;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtilsTest.class);

    @Test
    public void test() {
        String str1 = "신음소리를 내다".replace(" ", "");
        String str2 = "신음소리를내다".replace(" ", "");
        LOGGER.debug("{}", str1);
        LOGGER.debug("{}", str2);
        assertTrue(str1.equals(str2));
    }
}
