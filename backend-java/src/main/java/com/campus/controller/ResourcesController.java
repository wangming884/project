package com.campus.controller;

import com.campus.common.Result;
import com.campus.service.LearningResourceService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学习资源控制器（Google Drive 接入预留）
 */
@RestController
@RequestMapping("/resources")
public class ResourcesController {

    private final LearningResourceService learningResourceService;

    public ResourcesController(LearningResourceService learningResourceService) {
        this.learningResourceService = learningResourceService;
    }

    /**
     * 学习资料列表（预留 Google Drive）
     */
    @GetMapping("/materials/list")
    public Result<Map<String, Object>> listMaterials(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(learningResourceService.listMaterials(keyword, page, pageSize));
    }

    /**
     * 学习资料下载链接（预留 Google Drive）
     */
    @GetMapping("/materials/download")
    public Result<Map<String, Object>> downloadMaterial(@RequestParam String id) {
        return Result.success(learningResourceService.getMaterialDownload(id));
    }

    /**
     * 学习资料上传会话（预留 Google Drive）
     */
    @PostMapping("/materials/upload")
    public Result<Map<String, Object>> uploadMaterial(@RequestBody UploadRequest request) {
        return Result.success(learningResourceService.createMaterialUploadSession(
            request.getFileName(),
            request.getMimeType()
        ));
    }

    /**
     * 学习软件列表（预留 Google Drive）
     */
    @GetMapping("/software/list")
    public Result<Map<String, Object>> listSoftware(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return Result.success(learningResourceService.listSoftware(keyword, page, pageSize));
    }

    /**
     * 学习软件下载链接（预留 Google Drive）
     */
    @GetMapping("/software/download")
    public Result<Map<String, Object>> downloadSoftware(@RequestParam String id) {
        return Result.success(learningResourceService.getSoftwareDownload(id));
    }

    @Data
    static class UploadRequest {
        private String fileName;
        private String mimeType;
    }
}

