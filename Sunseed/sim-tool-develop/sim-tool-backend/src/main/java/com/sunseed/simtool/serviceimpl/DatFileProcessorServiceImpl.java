package com.sunseed.simtool.serviceimpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sunseed.simtool.service.DatFileProcessorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatFileProcessorServiceImpl implements DatFileProcessorService {

	@Value("${file.out-upload-dir}")
	private String fileOutputUploadDir;

	@Override
	public String processFilesAndMultiply(List<String> datFilePathsFromDb,Integer weekIndex) throws IOException {
		List<List<String>> datFileLinesList = new ArrayList<>();

		for (String datFilePath : datFilePathsFromDb) {
			// Replace .dat with .zip to get actual file on disk
			String zipFilePath = datFilePath.replaceAll("\\.dat$", ".zip");
			File zipFile = new File(zipFilePath);

			if (!zipFile.exists() || !zipFile.isFile()) {
				log.error("Zip file not found: {}", zipFilePath);
				throw new IOException("Zip file not found: " + zipFilePath);
			}

			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					if (!entry.isDirectory() && entry.getName().endsWith(".dat")) {
						List<String> lines = new ArrayList<>();
						BufferedReader reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));

						String line;
						while ((line = reader.readLine()) != null) {
							lines.add(line);
						}

						datFileLinesList.add(lines);
						break; // Only one .dat file expected inside the zip
					}
					zis.closeEntry();
				}
			}
		}

		// Compute results
		int maxLines = datFileLinesList.stream().mapToInt(List::size).max().orElse(0);
		List<Double> results = new ArrayList<>();
		double min = Double.MAX_VALUE, max = Double.MIN_VALUE;

		for (int i = 0; i < maxLines; i++) {
			double sum = 0.0;
			boolean hasValidLine = false;

			for (List<String> datLines : datFileLinesList) {
				if (i < datLines.size()) {
					String line = datLines.get(i).trim();
					if (!line.isEmpty()) {
						try {
							double val = Double.parseDouble(line);
							sum += val;
							hasValidLine = true;
						} catch (NumberFormatException e) {
							log.warn("Invalid number format at line {}: '{}'", i + 1, line);
						}
					}
				}
			}

			if (hasValidLine) {
				double multiplied = (sum * 3600) / 4.553 / 1000000;
				results.add(multiplied);
				min = Math.min(min, multiplied);
				max = Math.max(max, multiplied);
			}
		}

		if (results.isEmpty()) {
			throw new IOException("No valid numeric data found in .dat files.");
		}

		File outputDir = new File(fileOutputUploadDir);
		if (!outputDir.exists() && !outputDir.mkdirs()) {
			throw new IOException("Failed to create directory: " + fileOutputUploadDir);
		}

		String outputFileName = "dli_out_" + weekIndex.intValue() + "_" + System.currentTimeMillis() + ".dat";
		File outputFile = new File(outputDir, outputFileName);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			for (Double result : results) {
				writer.write(String.valueOf(result));
				writer.newLine();
			}
		}

		return String.format("%s, min: %.2f, max: %.2f", outputFile.getAbsolutePath(), min, max);
	}
}