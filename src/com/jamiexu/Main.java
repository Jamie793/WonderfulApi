package com.jamiexu;

import com.jamiexu.KGMusic;

/**
 * @author Jamiexu
 * time:2020-5-5 19:14
 * blog:blog.jamiexu.cn
 * This code is for learning only！！！
 * This code is for learning only！！！
 * This code is for learning only！！！
 */

public class Main {
    public static void main(String[] args) {
//        KGMusic kgMusic = new KGMusic();
//        kgMusic.loadSong("123");
//        System.out.println(kgMusic.getCurrent().getUrl());

//        GoogleTranslation googleTranslation = new GoogleTranslation();
//        System.out.println(googleTranslation.translateTo("i love you",googleTranslation.getAllLanguage().get("中文")));

//        QQMusic qqMusic = new QQMusic();
//        qqMusic.loadSong("123");

        CloudMusic cloudMusic = new CloudMusic();
        cloudMusic.loadSong("123");
    }
}
