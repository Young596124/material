package com.material.modules.system.controller;

import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.utils.DateUtils;
import com.material.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用控制器（文件上传）
 */
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    /** 允许的文件扩展名 */
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "png", "jpg", "jpeg", "gif", "pdf", "doc", "docx",
            "xls", "xlsx", "ppt", "pptx", "txt", "htm", "html"
    );

    @Value("${material.profile}")
    private String profile;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file)
    {
        if (file == null || file.isEmpty())
        {
            return AjaxResult.error("上传文件不能为空");
        }
        // 获取原始文件名
        String originalFileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(originalFileName))
        {
            return AjaxResult.error("上传文件名不能为空");
        }
        // 校验扩展名，防止路径穿越
        String extension = getExtension(originalFileName);
        if (!isAllowedExtension(extension))
        {
            return AjaxResult.error("上传文件格式不支持：" + extension);
        }
        // 校验文件名中是否包含路径分隔符（防止路径穿越）
        if (originalFileName.contains("..") || originalFileName.contains("/") || originalFileName.contains("\\"))
        {
            return AjaxResult.error("上传文件名包含非法字符");
        }
        // 按日期分目录
        String datePath = DateUtils.dateTimeNow("yyyy/MM/dd");
        // 生成新文件名：时间戳 + 随机数 + 扩展名
        String newFileName = DateUtils.dateTimeNow("yyyyMMddHHmmss") + "_" + DateUtils.getRandomNum(3) + "." + extension;
        // 目标目录
        String dirPath = profile + File.separator + datePath;
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs())
        {
            return AjaxResult.error("上传目录创建失败");
        }
        // 写入文件
        File destFile = new File(dir.getAbsoluteFile() + File.separator + newFileName);
        try
        {
            file.transferTo(destFile);
        }
        catch (IOException e)
        {
            log.error("上传文件失败", e);
            return AjaxResult.error("上传文件失败：" + e.getMessage());
        }
        // 构造返回结果
        String url = "/profile/" + datePath + "/" + newFileName;
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        data.put("fileName", originalFileName);
        data.put("newFileName", newFileName);
        return AjaxResult.success(data);
    }

    /**
     * 获取文件扩展名
     */
    private String getExtension(String fileName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            return "";
        }
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0 || dotIndex == fileName.length() - 1)
        {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    /**
     * 判断扩展名是否允许
     */
    private boolean isAllowedExtension(String extension)
    {
        if (StringUtils.isEmpty(extension))
        {
            return false;
        }
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase());
    }
}
