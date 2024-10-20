package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

	@Override
	public String uploadFile(String path, MultipartFile file) throws IOException {

		String originalFilename = file.getOriginalFilename();
		String uniqueId = UUID.randomUUID().toString();
		String fileName = null;

		if (originalFilename != null) {
			fileName = uniqueId.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));
		}

		String filePath = path + File.separator + fileName;

		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		Files.copy(file.getInputStream(), Paths.get(filePath));

		return fileName;

	}

}
