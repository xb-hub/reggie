package com.xb.reggie.controller;

import lombok.extern.slf4j.Slf4j;
import com.xb.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.upload}")
    private String bastPath;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("上传文件");
        if(file != null) {

            String fileName = file.getOriginalFilename();

            // 构建完整的文件存储路径
            String uniqueFileName = UUID.randomUUID().toString() + "-" + fileName;
            String filePath = bastPath + uniqueFileName;
            File targetFile = new File(filePath);
            int count = 1;
            while (targetFile.exists()) {
                uniqueFileName = fileName + "_" + count++;
                targetFile = new File(bastPath + uniqueFileName);
            }
            try {
                file.transferTo(targetFile);
            } catch (IOException e) {
                // 处理文件转存异常
                e.printStackTrace();
            }
            return R.success(uniqueFileName);
        }
        return R.error("文件为空");
    }

    @GetMapping("/download")
    public void download(@RequestParam("name") String name, HttpServletResponse response) throws IOException {
        log.info("下载文件");
        FileInputStream fileInputStream = null;
        ServletOutputStream servletOutputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(bastPath + name));
            servletOutputStream =  response.getOutputStream();
            response.setContentType("image/jepg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                servletOutputStream.write(bytes, 0, len);
                servletOutputStream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fileInputStream.close();
            servletOutputStream.close();
        }
    }

}
