package com.liao.book.parse;

import com.liao.book.common.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * txt 文件解析
 * </p>
 *
 * @author LiAo
 * @since 2023-04-13
 */
public class TxtContentParser {

    // Default Pattern
    private static final Pattern CHAPTER_PATTERN = Pattern.compile(Constants.DEFAULT_CHAPTER_REGULAR);

    /**
     * 解析本地 txt 文件为 Map 格式，K 为章节名称，Value 为章节内容
     *
     * @param file txt 文件
     * @return <章节，章节内容>
     * @throws IOException ex
     */
    public static Map<String, String> parseTxt(File file) throws IOException {
        Map<String, String> chapterMap = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), "GBK"))) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            String title = null;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = CHAPTER_PATTERN.matcher(line);
                if (matcher.find()) {
                    if (title != null) {
                        chapterMap.put(title, contentBuilder.toString());
                        contentBuilder.setLength(0);
                    }
                    title = line;
                } else {
                    contentBuilder.append(line);
                    contentBuilder.append(System.lineSeparator());
                }
            }

            if (title != null) {
                chapterMap.put(title, contentBuilder.toString());
            }
        }

        return chapterMap;
    }
}
