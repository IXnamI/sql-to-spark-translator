package com.github.xnam.utils;

public class StringUtils {
    public static boolean isLetter(Character chr) {
        return ('a' <= chr && chr <= 'z') || ('A' <= chr && 'Z' >= chr) || chr == '_';
    }

    public static boolean isStringEnd(Character chr) {
        return (chr == '\'') || (chr == '\"');
    }
}
