package com.sunseed.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.ImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

	private final UserProfileRepository userProfileRepo;

	/* path to the folder for storing images */
	@Override
	public String uploadImage(String path, MultipartFile image, String email) throws IOException {
		String name = image.getOriginalFilename();
		// creating path till the file
		String randomId = UUID.randomUUID().toString();

		int index = name.lastIndexOf(".");
		String ext = name.substring(index + 1);

		String filePath = null;

		if (ext.toLowerCase().equals("png") || ext.toLowerCase().equals("jpg")) {
			String file = randomId.concat(name.substring(index));

			filePath = path + File.separatorChar + file;

			// create folder for the images
			File f1 = new File(path);
			if (!f1.exists()) {
				// make a new directory
				f1.mkdir();
			}

			Files.copy(image.getInputStream(), Paths.get(filePath));
		} else {
			throw new UnprocessableException("fileformat.invalid");
		}

		String completePath = path.substring(path.lastIndexOf("/")) + "/" + randomId + "." + ext;
//		return filePath;
		return completePath; // returning the image/UUID.jpg (or.png)
	}
	
	@Override
	public String uploadImageWithFolderNameAndFileName(String path, MultipartFile image, String folderName,
			String fileName) throws IOException {
		String name = image.getOriginalFilename();
		// creating path till the file
		String randomId = UUID.randomUUID().toString();

		int index = name.lastIndexOf(".");
		String ext = name.substring(index + 1);

		String filePath = null;

		if (ext.toLowerCase().equals("png") || ext.toLowerCase().equals("jpg")) {
			String file = fileName + randomId.concat(name.substring(index));

			filePath = path + File.separatorChar + folderName + File.separatorChar + file;

			// create folder for the images
			File f1 = new File(path + File.separatorChar + folderName);
			if (!f1.exists()) {
				// make a new directory
				f1.mkdir();
			}

			Files.copy(image.getInputStream(), Paths.get(filePath));
		} else {
			throw new UnprocessableException("fileformat.invalid");
		}

		String completePath = path.substring(path.lastIndexOf("/")) + "/" + folderName + "/" + fileName + randomId + "."
				+ ext;
//		return filePath;
		return completePath; // returning the image/UUID.jpg (or.png)
	}

	@Override
	public InputStream getResource(String path, String imageUrl) throws FileNotFoundException {
		String completePath = path + File.separator + imageUrl; // make it email.jpg

		InputStream is = new FileInputStream(completePath);

		return is;
	}

}
