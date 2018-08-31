package com.pinyougou;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utis {
    public static void main(String[] args) {
        String s = DigestUtils.md5Hex("123456");
        System.out.println(s);
    }
}
