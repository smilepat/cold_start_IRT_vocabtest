package com.marvrus.vocabularytest;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtilsTest.class);
    @Test
    public void test() {
        LOGGER.debug("{}", StringUtils.deleteWhitespace("신음소리를 내다"));
        LOGGER.debug("{}", StringUtils.deleteWhitespace("신음소리를내다"));
        assertTrue(StringUtils.equals(StringUtils.deleteWhitespace("신음소리를 내다"), StringUtils.deleteWhitespace("신음소리를내다")));
    }
}
