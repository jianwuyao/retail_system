package com.jwyao.system.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件下载
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @GetMapping("/download/{type}")
    public void download(HttpServletResponse response, @PathVariable String type, String name) throws IOException {
        // 输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(uploadPath + File.separator + type + File.separator + name));
        // 输出流将文件写回浏览器，以便在浏览器展示图片
        ServletOutputStream outputStream = response.getOutputStream();
        // 设置响应回去的文件类型
        response.setContentType("image/jpeg");
        // 边读边写
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, len);
            outputStream.flush();
        }
        // 关闭资源
        fileInputStream.close();
        outputStream.close();
    }

}
