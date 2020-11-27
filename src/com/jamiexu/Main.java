package com.jamiexu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
        KGMusic kgMusic = new KGMusic();
        kgMusic.loadSong("559",1);
        ArrayList<KGMusic.KGBean> kgBeans = kgMusic.getDataLists();
        for (KGMusic.KGBean kgBean : kgBeans){
            System.out.println(kgBean.getName());
            System.out.println(kgBean.getSingerName());
            System.out.println(kgBean.getUrl());
            System.out.println(kgBean.getSize());
            System.out.println();
        }


        GoogleTranslation googleTranslation = new GoogleTranslation();
        System.out.println(googleTranslation.translateTo("我爱你", googleTranslation.getAllLanguage().get("英文")));

//        QQMusic qqMusic = new QQMusic();
//        qqMusic.loadSong("我爱你",1);
//        ArrayList<QQMusic.QQMusicBean> arrayList = qqMusic.getDataLists();
//        for (QQMusic.QQMusicBean qqMusicBean : arrayList){
//            System.out.println(qqMusicBean.getName());
//            System.out.println(qqMusicBean.getSingerName());
//            System.out.println(qqMusicBean.getUrl());
//            System.out.println(qqMusicBean.getSize());
//            System.out.println();
//        }

//        CloudMusic cloudMusic = new CloudMusic();
//        cloudMusic.loadSong("123",1);
//        ArrayList<CloudMusic.CloudMusicBean> cloudMusicBeans = cloudMusic.getDataLists();
//        for (CloudMusic.CloudMusicBean cloudMusicBean : cloudMusicBeans){
//            System.out.println(cloudMusicBean.getName());
//            System.out.println(cloudMusicBean.getSingerName());
//            System.out.println(cloudMusicBean.getUrl());
//            System.out.println(cloudMusicBean.getSize());
//            System.out.println();
//        }

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

//
//        KWMusic kwMusic = new KWMusic();
//        kwMusic.loadSong("123");
//        ArrayList<KWMusic.KWBean> kgBeans = kwMusic.getDataLists();
//        for (KWMusic.KWBean kwBean : kgBeans) {
//            System.out.println(kwBean.getName());
//            System.out.println(kwBean.getSingerName());
//            System.out.println(kwBean.getUrl());
//            System.out.println(kwBean.getSize());
//            System.out.println();
//        }

//        XmMusic xmMusic = new XmMusic();
//        xmMusic.loadSong("13");

    }
}
