import { useState, useEffect } from 'react';
import { getFile, storeFile } from './indexDbSetup';
import { useDispatch } from 'react-redux';
import { unzip } from "fflate";
function isZipUrl(url) {
  try {
    const decodedUrl = decodeURIComponent(url);
    return decodedUrl.toLowerCase().endsWith('.zip');
  } catch (e) {
    return false;
  }
}
const useDownloadAndSaveFile = (fileUrl, fileKey,
  fileRelatedInfo,
  setLoader) => {
  const [isDownloading, setIsDownloading] = useState(false);
  const [error, setError] = useState(null);
  const [file, setFile] = useState(null);

  // console.log("objFile1", fileUrl);



  // const [files, setFiles] = useState([]);
  // useEffect(() => {
  //   async function fetchFiles() {
  //     const allFiles = await getAllFiles();
  //     // console.log("allFiles", allFiles);
  //     setFiles(allFiles);
  //   }

  //   fetchFiles();
  // }, []);


  // console.log("files", files);
  useEffect(() => {
    const downloadAndSave = async () => {
      setIsDownloading(true);
      setError(null);

      try {
        // First, check if the file already exists in IndexedDB

        const existingFile = await getFile(fileKey);

        if (existingFile) {
          // If the file exists, use it instead of downloading
          setFile(existingFile);
          // console.log('File retrieved from IndexedDB', existingFile);
        } else {
          setLoader(true);
          // If the file does not exist, proceed to download it
          const response = await fetch(fileUrl);

          if (!response.ok) {
            throw new Error('Failed to download file');
          }

          if (isZipUrl(fileUrl)) {


            const zipBuffer = await response.arrayBuffer();

            // Convert ArrayBuffer to Uint8Array
            const zipData = new Uint8Array(zipBuffer);

            // Unzip the file
            unzip(zipData, {
              filter(file) {
                // optional filter to extract only specific files
                return true;
              },
              useWorkers: true // 👈 THIS IS THE MAGIC
            }, (err, unzipped) => {
              if (err) {
                console.error("Error extracting ZIP:", err);
                return;
              }

              const fileName = Object.keys(unzipped)[0];
              const extractedFile = new Blob([unzipped[fileName]], { type: "application/octet-stream" });

              storeFile(fileKey, extractedFile, fileRelatedInfo);
              setFile(extractedFile);
            });
          } else {
            // Convert the response into a Blob
            const blob = await response.blob();
            // Save the Blob in to IndexedDB with the provided key
            await storeFile(fileKey, blob, fileRelatedInfo);
            // Update the state with the downloaded file
            setFile(blob);

            // console.log('File successfully downloaded and saved to IndexedDB');
          }
        }
      } catch (err) {
        console.error('Error downloading or saving file:', err);
        setError(err.message);
      } finally {
        setIsDownloading(false);

      }
    };

    if (fileUrl && fileKey) {
      downloadAndSave();
    } else {
      // setFile(null);
      setError("No file URL or key provided");
    }
  }, [fileUrl, fileKey]);

  return { file, isDownloading, error };
};

export default useDownloadAndSaveFile;
