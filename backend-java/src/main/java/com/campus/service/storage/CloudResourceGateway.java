package com.campus.service.storage;

import java.util.List;
import java.util.Map;

/**
 * 学习资源云存储网关（预留接口）
 * 后续可接入 Google Drive、S3 等云存储服务。
 */
public interface CloudResourceGateway {

    List<Map<String, Object>> listResources(String resourceType, String keyword, int page, int pageSize);

    Map<String, Object> buildDownloadInfo(String resourceType, String resourceId);

    Map<String, Object> buildUploadSession(String resourceType, String fileName, String mimeType);

    String providerName();
}

