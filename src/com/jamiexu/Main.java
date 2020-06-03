package com.jamiexu;

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
//        kgMusic.loadSong("4");
//        System.out.println(kgMusic.getCurrent().getUrl());

//        GoogleTranslation googleTranslation = new GoogleTranslation();
//        System.out.println(googleTranslation.translateTo("i love you",googleTranslation.getAllLanguage().get("中文")));

//        QQMusic qqMusic = new QQMusic();
//        qqMusic.loadSong("123");

//        CloudMusic cloudMusic = new CloudMusic();
//        cloudMusic.loadSong("123");

//        XiMaLaYaFm xiMaLaYaFm = new XiMaLaYaFm();
//        xiMaLaYaFm.loadAlbum("相声");
//        for (int i = 0; i < xiMaLaYaFm.size(); i++) {
//            XiMaLaYaFm.AlbumBean albumBean = xiMaLaYaFm.getAlbum(i);
//            for (int j = 0; j < albumBean.size(); j++) {
//                XiMaLaYaFm.AudioBean audioBean = albumBean.getAudio(j);
//                System.out.println(audioBean.getTitle());
//                System.out.println(audioBean.getUrl());
//            }
//        }
//        System.out.println(xiMaLaYaFm.getAlbum(0).getAudio(0).getTitle());
//        System.out.println(xiMaLaYaFm.getAlbum(0).getAudio(0).getUrl());


        KWMusic kwMusic = new KWMusic();
        kwMusic.loadSong("123");
        System.out.println(kwMusic.getCurrent().getUrl());
        System.out.println(kwMusic.nextSong().getUrl());
        System.out.println(kwMusic.nextSong().getUrl());
        System.out.println(kwMusic.nextSong().getUrl());
    }
}
