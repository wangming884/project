package com.campus.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Google Drive 接入占位实现（预留）
 * 当前仅返回结构化数据，后续可在此实现真实 Drive API 调用。
 */
@Component
public class GoogleDriveGatewayStub implements CloudResourceGateway {

    @Value("${google-drive.enabled:false}")
    private boolean enabled;

    @Value("${google-drive.materials-folder-id:}")
    private String materialsFolderId;

    @Value("${google-drive.software-folder-id:}")
    private String softwareFolderId;

    @Override
    public List<Map<String, Object>> listResources(String resourceType, String keyword, int page, int pageSize) {
        List<Map<String, Object>> mock = "materials".equals(resourceType)
            ? buildMaterialsMock()
            : buildSoftwareMock();

        String q = keyword == null ? "" : keyword.trim().toLowerCase();
        if (!q.isBlank()) {
            mock = mock.stream()
                .filter(item -> String.valueOf(item.get("title")).toLowerCase().contains(q))
                .toList();
        }

        int from = Math.max(0, (page - 1) * pageSize);
        if (from >= mock.size()) {
            return new ArrayList<>();
        }
        int to = Math.min(mock.size(), from + pageSize);
        return new ArrayList<>(mock.subList(from, to));
    }

    @Override
    public Map<String, Object> buildDownloadInfo(String resourceType, String resourceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("resourceId", resourceId);
        data.put("resourceType", resourceType);
        data.put("provider", providerName());
        data.put("integrationEnabled", enabled);

        if (enabled) {
            data.put("downloadUrl", "https://drive.google.com/file/d/" + resourceId + "/view");
            data.put("message", "已生成 Google Drive 下载链接");
        } else {
            data.put("downloadUrl", "");
            data.put("message", "Google Drive 未启用，当前为预留模式");
        }
        return data;
    }

    @Override
    public Map<String, Object> buildUploadSession(String resourceType, String fileName, String mimeType) {
        Map<String, Object> data = new HashMap<>();
        data.put("resourceType", resourceType);
        data.put("fileName", fileName);
        data.put("mimeType", mimeType);
        data.put("provider", providerName());
        data.put("integrationEnabled", enabled);
        data.put("uploadEnabled", false);
        data.put("uploadUrl", "");
        data.put("message", "Google Drive 上传接口已预留，后续接入 Drive API 后可启用");
        return data;
    }

    @Override
    public String providerName() {
        return "google-drive";
    }

    private List<Map<String, Object>> buildMaterialsMock() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(buildItem("mat-001", "2024 高等数学A 期末复习提纲", "PDF · 2.4 MB", 1200));
        list.add(buildItem("mat-002", "C语言程序设计课后习题答案全集", "DOCX · 1.1 MB", 850));
        list.add(buildItem("mat-003", "计算机网络全套复习PPT归档", "ZIP · 45 MB", 500));
        return list;
    }

    private List<Map<String, Object>> buildSoftwareMock() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(buildItem("soft-001", "MATLAB R2023a 校园版", "ISO · 18.5 GB", 300));
        list.add(buildItem("soft-002", "AutoCAD 2024 学生版", "EXE · 2.3 GB", 220));
        list.add(buildItem("soft-003", "PyCharm Professional 2023", "EXE · 600 MB", 980));
        return list;
    }

    private Map<String, Object> buildItem(String id, String title, String meta, int downloadCount) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", id);
        item.put("title", title);
        item.put("meta", meta);
        item.put("downloadCount", downloadCount);
        item.put("provider", providerName());
        item.put("driveFileId", id);
        item.put("updatedAt", LocalDateTime.now().toString());
        item.put("materialsFolderId", materialsFolderId);
        item.put("softwareFolderId", softwareFolderId);
        return item;
    }
}

