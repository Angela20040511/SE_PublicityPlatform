package com.se.publicityplatform.controller;

import com.se.publicityplatform.config.AppProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class UploadController {

    private final AppProperties appProperties;

    public UploadController(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @GetMapping("/upload/{fileName:.+}")
    public Object getUpload(@PathVariable String fileName) throws MalformedURLException {
        if (!StringUtils.hasText(fileName) || fileName.contains("..")) {
            return missing(fileName);
        }
        Path uploadRoot = Path.of(appProperties.getUploadDir()).toAbsolutePath().normalize();
        Path file = uploadRoot.resolve(fileName).normalize();
        if (!file.startsWith(uploadRoot) || !Files.isRegularFile(file)) {
            return missing(fileName);
        }
        Resource resource = new UrlResource(file.toUri());
        return ResponseEntity.ok()
                .contentType(contentType(file))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    private ModelAndView missing(String fileName) {
        ModelAndView modelAndView = new ModelAndView("error/upload-missing");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    private MediaType contentType(Path file) {
        try {
            String detected = Files.probeContentType(file);
            if (StringUtils.hasText(detected)) {
                return MediaType.parseMediaType(detected);
            }
        } catch (Exception ignored) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
