package com.campus.service;

import com.campus.service.storage.CloudResourceGateway;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习资源服务（预留 Google Drive 接入）
 */
@Service
public class LearningResourceService {

    private final CloudResourceGateway cloudResourceGateway;

    public LearningResourceService(CloudResourceGateway cloudResourceGateway) {
        this.cloudResourceGateway = cloudResourceGateway;
    }

    public Map<String, Object> listMaterials(String keyword, int page, int pageSize) {
        return buildListResponse("materials", keyword, page, pageSize);
    }

    public Map<String, Object> listSoftware(String keyword, int page, int pageSize) {
        return buildListResponse("software", keyword, page, pageSize);
    }

    public Map<String, Object> getMaterialDownload(String resourceId) {
        return cloudResourceGateway.buildDownloadInfo("materials", resourceId);
    }

    public Map<String, Object> getSoftwareDownload(String resourceId) {
        return cloudResourceGateway.buildDownloadInfo("software", resourceId);
    }

    public Map<String, Object> createMaterialUploadSession(String fileName, String mimeType) {
        return cloudResourceGateway.buildUploadSession("materials", fileName, mimeType);
    }

    private Map<String, Object> buildListResponse(String type, String keyword, int page, int pageSize) {
        List<Map<String, Object>> list = cloudResourceGateway.listResources(type, keyword, page, pageSize);

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", list.size());
        data.put("page", page);
        data.put("pageSize", pageSize);
        data.put("provider", cloudResourceGateway.providerName());
        data.put("reserved", true);
        data.put("message", "已预留 Google Drive 接口，后续可直接替换为真实文件列表");
        return data;
    }
}

