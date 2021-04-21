package com.neoniou.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.ZipUtil;
import com.neoniou.tool.utils.ConvertUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Neo.Zzj
 * @date 2021/4/21
 */
public class Cht2ChsApplication {

    private static final String EPUB_SUFFIX = ".epub";
    private static final String HTML_SUFFIX = ".html";

    public static void main(String[] args) {
        startApplication(System.getProperty("user.dir"));
    }

    private static void startApplication(String basePath) {
        List<File> epubFiles = getEpubFiles(basePath);
        for (File epub : epubFiles) {
            System.out.println("处理：" + epub.getName());
            String docName = executeFile(epub, basePath);
            packDoc(basePath, docName);
        }
    }

    private static void packDoc(String basePath, String docName) {
        String path = basePath + "/" + docName;
        File zip = ZipUtil.zip(path, basePath + "/result/" + docName);
        FileUtil.rename(zip, docName + ".epub", false);
        FileUtil.del(path);
    }

    /**
     * 获取工作目录下的epub文件
     *
     * @param basePath 工作目录
     * @return epub文件列表
     */
    private static List<File> getEpubFiles(String basePath) {
        File[] files = FileUtil.ls(basePath);
        List<File> epubFiles = new ArrayList<>();

        for (File file : files) {
            if (file.getName().endsWith(EPUB_SUFFIX)) {
                epubFiles.add(file);
            }
        }
        return epubFiles;
    }

    /**
     * 处理单个epub文件
     *
     * @param epub epub文件
     * @return 文件夹的名字
     */
    public static String executeFile(File epub, String basePath) {
        String fileName = epub.getName();
        String docName = fileName.substring(0, fileName.lastIndexOf(EPUB_SUFFIX));
        ZipUtil.unzip(epub);

        List<File> htmlFiles = new ArrayList<>();
        getHtmlFiles(htmlFiles, new File(basePath + "/" + docName));
        for (File html : htmlFiles) {
            convert(html);
        }
        return docName;
    }

    private static void getHtmlFiles(List<File> htmlFiles, File thisFile) {
        if (thisFile.isDirectory()) {
            File[] files = FileUtil.ls(thisFile.getPath());
            for (File file : files) {
                getHtmlFiles(htmlFiles, file);
            }
        } else if (thisFile.isFile() && thisFile.getName().endsWith(HTML_SUFFIX)) {
            htmlFiles.add(thisFile);
        }
    }

    public static void convert(File html) {
        if (!html.getName().endsWith(HTML_SUFFIX)) {
            return;
        }
        String content = FileUtil.readUtf8String(html);
        String result = ConvertUtil.convert(content);
        FileWriter fileWriter = new FileWriter(html.getPath());
        fileWriter.write(result);
    }
}
