package com.liao.book.service.impl;

import cn.hutool.http.HttpUtil;
import com.liao.book.dao.ReadingProgressDao;
import com.liao.book.entity.Chapter;
import com.liao.book.common.ModuleConstants;
import com.liao.book.service.ChapterService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * <p>
 * 爬取数据章节列表
 * </p>
 *
 * @author LiAo
 * @since 2021/1/14
 */
public class ChapterServiceImpl implements ChapterService {

    // 重试次数
    public static int index = 2;

    // 阅读进度持久化
    static ReadingProgressDao instance = ReadingProgressDao.getInstance();

    /**
     * 爬取章节信息
     *
     * @param link 链接
     */
    @Override
    public void getBookChapterByType(String link) {
        switch (instance.searchType) {
            case ModuleConstants.XIANG_SHU:
                // 笔趣阁
                searchBookChapterData(link);
                break;
            case ModuleConstants.BI_QU_GE:
                // 笔趣阁2
                searchBookChapterData_bqg2(link);
                break;
        }
    }

    /**
     * 香书小说
     *
     * @param link 链接
     */
    @Override
    public void searchBookChapterData(String link) {
        instance.chapters.clear();
        String result1 = HttpUtil.get(link);
        try {
            getHTMLDocument(result1);
        } catch (Exception e) {
            // 判断次数
            if (index == 0) {
                return;
            }
            index--;
            searchBookChapterData(link);
        }
    }


    /**
     * 笔趣阁
     *
     * @param link 链接
     */
    @Override
    public void searchBookChapterData_bqg2(String link) {

        instance.chapters.clear();

        String html = HttpUtil.get(link);
        try {
            getHTMLDocument(html);
        } catch (Exception e) {
            if (index == 0) {
                return;
            }

            index--;
            searchBookChapterData_bqg2(link);
        }
    }

    /**
     * 解析页面书籍章节内容
     *
     * @param htmlStr 链接
     */
    private void getHTMLDocument(String htmlStr) {
        Document parse = Jsoup.parse(htmlStr);
        Elements grid = parse.getElementsByTag("dd");

        for (Element element : grid) {
            Chapter chapter = new Chapter();
            // 链接
            String attr = element.getElementsByTag("a").eq(0).attr("href");
            // 名称
            String name = element.getElementsByTag("a").eq(0).text();

            chapter.setName(name);

            chapter.setLink(attr);

            instance.chapters.add(chapter);
        }
    }
}
