package com.sunseed.simtool.service;

import java.io.IOException;
import java.util.List;

public interface DatFileProcessorService {

	public String processFilesAndMultiply(List<String> fileNames,Integer weekIndex) throws IOException;
}
