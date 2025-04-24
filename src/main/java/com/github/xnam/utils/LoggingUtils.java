package com.github.xnam.utils;

import com.github.xnam.token.Token;

public class LoggingUtils {
    public static String curAndPeekTokenPrint(Token curToken, Token peekToken) {
        return String.format(String.format("CurTok: %s, PeekTok: %s", curToken, peekToken));
    }
}
