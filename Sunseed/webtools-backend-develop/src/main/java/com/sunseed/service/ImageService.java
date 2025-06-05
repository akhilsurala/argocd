package com.sunseed.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

	// added email of user as parameter
	String uploadImage(String path, MultipartFile file, String email) throws IOException;

	// returns input stream to read data
	InputStream getResource(String path, String fileName) throws FileNotFoundException;

	// byte[] getResourceAsByteArray(Long userId) throws IOException;

	String uploadImageWithFolderNameAndFileName(String path, MultipartFile image, String folderName, String fileName)
			throws IOException;
}
