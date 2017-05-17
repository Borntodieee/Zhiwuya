

package com.example.borntodieee.zhiwuya.util;

/**
 * 包含了主要的api接口
 * this class contain the main APIs
 */
public class Api {

    // 所有的知乎日报API的HTTP METHOD 均为GET
    // the method of all zhihu daily's api is GET

    // 知乎日报base url,将文章id拼接值base url之后即可
    // ZhihuDaily base url, used in browser, add the post id to it
    // public static final String ZHIHU_DAILY_BASE_URL = "http://news-at.zhihu.com/story/";

    // 获取界面启动图像
    // get the open screen page image url
    // start_image后面为图像分辨率
    // The end of start_image is the open screen image's resolution
    // public static final String START_IMAGE = "http://news-at.zhihu.com/api/4/start-image/1080*1776";

    // 最新消息
    // latest posts
    // ZHIHU_NEWS API替代，拼接当日日期后可以获取
    // public static final String LATEST = "http://news-at.zhihu.com/api/4/news/latest";

    // 消息内容获取与离线下载
    // content of post and download offline
    // 在最新消息中获取到的id，拼接到这个NEWS之后，可以获得对应的JSON格式的内容
    // add the id that you got from latest post to ZHIHU_NEWS and you will get the content as json format
    public static final String ZHIHU_NEWS = "http://news-at.zhihu.com/api/4/news/";

    // 过往消息
    // past posts
    // 若要查询的11月18日的消息，before后面的数字应该为20161118
    // if you want to select the posts of November 11th, the number after 'before' should be 20161118
    // 知乎日报的生日为2013 年 5 月 19 日，如果before后面的数字小于20130520，那么只能获取到空消息
    // the birthday of ZhiHuDaily is May 19th, 2013. So if the number is lower than 20130520, you will get a null value of post
    public static final String ZHIHU_HISTORY = "http://news.at.zhihu.com/api/4/news/before/";

}
