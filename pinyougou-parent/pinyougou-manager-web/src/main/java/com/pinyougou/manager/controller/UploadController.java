package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import util.FastDFSClient;
import vo.Result;

@RestController
public class UploadController {
	
	@Value("${FILE_SERVICE_URL}")
	private String file_service_url;
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {
		
		//获取文件全名
		String originalFilename = file.getOriginalFilename();
		//扩展名
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		
		String name = file.getName();
		try {
			FastDFSClient upload = new FastDFSClient("classpath:config/fdfs_client.conf");
		
			String fileId = upload.uploadFile(file.getBytes(), extName);
			
			fileId = file_service_url + fileId; 
			
			return new Result(true, fileId);
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(false, "上传失败");
		}
		
	}
}
