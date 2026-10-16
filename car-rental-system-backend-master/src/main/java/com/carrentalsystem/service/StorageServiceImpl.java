package com.carrentalsystem.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.carrentalsystem.exception.InvalidFileUploadException;

@Component
public class StorageServiceImpl implements StorageService {

	@Value("${com.carrentalsystem.image.variant.folder.path}")
	private String VARIANT_IMAGE_BASEPATH;

	@Value("${com.carrentalsystem.image.license.folder.path}")
	private String LICENSE_IMAGE_BASEPATH;

	@Override
	public List<String> loadAllVariantImage() {
		File dirPath = new File(VARIANT_IMAGE_BASEPATH);
		String[] files = dirPath.list();
		return files == null ? Collections.emptyList() : Arrays.asList(files);
	}

	@Override
	public String storeVariantImage(MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		System.out.println(originalFilename);
		String fileName = buildStorageFileName(originalFilename);
		File filePath = new File(VARIANT_IMAGE_BASEPATH, fileName);
		try (FileOutputStream out = new FileOutputStream(filePath)) {
			FileCopyUtils.copy(file.getInputStream(), out);
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Resource loadVariantImage(String fileName) {
		File filePath = resolveWithinBase(VARIANT_IMAGE_BASEPATH, fileName);
		if (filePath != null && filePath.exists())
			return new FileSystemResource(filePath);
		return null;
	}

	@Override
	public void deleteVariantImage(String fileName) {
		File filePath = resolveWithinBase(VARIANT_IMAGE_BASEPATH, fileName);
		if (filePath != null && filePath.exists())
			filePath.delete();
	}

	@Override
	public List<String> loadAllLicenseImage() {
		File dirPath = new File(LICENSE_IMAGE_BASEPATH);
		String[] files = dirPath.list();
		return files == null ? Collections.emptyList() : Arrays.asList(files);
	}

	@Override
	public String storeLicenseImage(MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		System.out.println(originalFilename);
		String fileName = buildStorageFileName(originalFilename);
		File filePath = new File(LICENSE_IMAGE_BASEPATH, fileName);
		try (FileOutputStream out = new FileOutputStream(filePath)) {
			FileCopyUtils.copy(file.getInputStream(), out);
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Resource loadLicenseImage(String fileName) {
		File filePath = resolveWithinBase(LICENSE_IMAGE_BASEPATH, fileName);
		if (filePath != null && filePath.exists())
			return new FileSystemResource(filePath);
		return null;
	}

	@Override
	public void deleteLicenseImage(String fileName) {
		File filePath = resolveWithinBase(LICENSE_IMAGE_BASEPATH, fileName);
		if (filePath != null && filePath.exists())
			filePath.delete();
	}

	/**
	 * Builds a safe random storage name, keeping the uploaded file's extension.
	 * Rejects null or extensionless filenames instead of crashing.
	 */
	private String buildStorageFileName(String originalFilename) {
		String ext = StringUtils.getFilenameExtension(originalFilename);
		if (originalFilename == null || ext == null || ext.isEmpty()) {
			throw new InvalidFileUploadException("Uploaded file must have a valid file extension");
		}
		System.out.println("." + ext);
		return UUID.randomUUID().toString().replaceAll("-", "") + "." + ext;
	}

	/**
	 * Resolves a stored file name against its base directory and rejects any name
	 * that would escape it (path traversal). Returns null when invalid.
	 */
	private File resolveWithinBase(String basePath, String fileName) {
		if (fileName == null || fileName.isEmpty())
			return null;
		try {
			File base = new File(basePath).getCanonicalFile();
			File filePath = new File(base, fileName).getCanonicalFile();
			if (filePath.getPath().startsWith(base.getPath() + File.separator)) {
				return filePath;
			}
		} catch (IOException e) {
			// fall through to null
		}
		return null;
	}

}
