package com.deceiver.controller;

import com.deceiver.utils.FastDFSClient;
import com.deceiver.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Deceiver
 * Date: 2018-06-29
 * Time: 15:23
 */
@Controller
@RequestMapping("/pic")
public class PictureController {

    @Value("${IMAGE_SERVER_URL}")
    private String IMAGE_SERVER_URL;

    @RequestMapping(value = "/upload", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8")
    @ResponseBody
    public String fileUpload(MultipartFile uploadFile){
        //创建结果集
        Map result = new HashMap<>();
        try {
            //获取文件扩展名
            String originalFileName = uploadFile.getOriginalFilename();
            String extName = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            //创建FastDFS对象
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");
            //上传文件
            String path = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
            //拼接上传地址
            String url = IMAGE_SERVER_URL + path;
            //返回上传结果
            result.put("error", 0);
            result.put("url", url);
            return JsonUtils.objectToJson(result);
        }catch (Exception e){
            e.printStackTrace();
            result.put("error", 1);
            result.put("message", "图片上传失败");
            return JsonUtils.objectToJson(result);
        }
    }

}
