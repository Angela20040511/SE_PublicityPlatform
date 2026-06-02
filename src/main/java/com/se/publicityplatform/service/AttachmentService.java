package com.se.publicityplatform.service;

import com.se.publicityplatform.config.AppProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;

@Component("attachment")
public class AttachmentService {

    private final AppProperties appProperties;

    public AttachmentService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public boolean hasValue(String url) {
        return StringUtils.hasText(url);
    }

    public boolean external(String url) {
        return StringUtils.hasText(url)
                && (url.startsWith("http://") || url.startsWith("https://"));
    }

    public boolean localUpload(String url) {
        return StringUtils.hasText(url) && url.startsWith("/upload/");
    }

    public boolean available(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        if (external(url)) {
            return true;
        }
        if (!localUpload(url)) {
            return false;
        }
        String fileName = url.substring("/upload/".length());
        if (!StringUtils.hasText(fileName) || fileName.contains("..")) {
            return false;
        }
        Path path = Path.of(appProperties.getUploadDir()).toAbsolutePath().normalize().resolve(fileName).normalize();
        Path uploadRoot = Path.of(appProperties.getUploadDir()).toAbsolutePath().normalize();
        return path.startsWith(uploadRoot) && Files.isRegularFile(path);
    }

    public String display(String url) {
        if (!StringUtils.hasText(url)) {
            return "无附件";
        }
        if (available(url)) {
            return external(url) ? "打开外部链接" : "下载附件";
        }
        return localUpload(url) ? "文件未上传" : "无效链接";
    }
}
