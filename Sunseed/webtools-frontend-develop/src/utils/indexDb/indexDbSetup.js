import { openDB } from 'idb';
import { addOrUpdateFile } from './indexDbQuerySetup';

async function  initDBForFileStore() {
  const db = await openDB('file-store', 1, {
    upgrade(db) {
      db.createObjectStore('files');
    },
  });
  return db
}

export const storeFile = async (key, file, fileRelatedInfo) => {
  const db = await initDBForFileStore();
  
  // Store both the file content and the metadata
  await db.put('files', { content: file, name: key }, key);
  await addOrUpdateFile(fileRelatedInfo);
};

export const getFile = async (key) => {
  const db = await initDBForFileStore();
  const fileData = await db.get('files', key);

  if (fileData) {
    return fileData.content;    
  }

  return null;
};

export const getFileAlongWithName = async (key) => {
  const db = await initDBForFileStore();
  const fileData = await db.get('files', key);

  if (fileData) {
    return {
      content: fileData.content,
      name: fileData.name,
    };
  }

  return null;
};
export const getAllFiles = async () => {
  const db = await initDBForFileStore();
  const tx = db.transaction('files', 'readonly');
  const store = tx.objectStore('files');

  // Get all records, which will include both content and name
  const allFiles = await store.getAll();

  await tx.done;

  // Map the results to return content and name for each file
  return allFiles.map((file) => file.name);
};

export async function deleteFile(fileId) {
  // Open the IndexedDB database
   const db = await initDBForFileStore();

  // Start a transaction to delete the file
  const tx = db.transaction('files', 'readwrite');
  const store = tx.objectStore('files');

  // Delete the file with the specified key (fileId)
  await store.delete(fileId);

  // Commit the transaction
  await tx.done;

  console.log(`File with ID ${fileId} deleted.`);
}


export const deleteAllDatabases = async () => {
  // Get a list of all database names
  indexedDB.databases().then((dbList) => {
    dbList.forEach((dbInfo) => {
      const { name } = dbInfo;
      // Delete each database
      const deleteRequest = indexedDB.deleteDatabase(name);

      deleteRequest.onsuccess = () => {
        console.log(`Database ${name} deleted successfully.`);
      };

      deleteRequest.onerror = (event) => {
        console.error(`Error deleting database ${name}:`, event);
      };

      deleteRequest.onblocked = () => {
        console.warn(`Database deletion for ${name} is blocked.`);
      };
    });
  }).catch((error) => {
    console.error('Error getting database list:', error);
  });
};

export const checkStorage = () => {
  if ('storage' in navigator && 'estimate' in navigator.storage) {
    navigator.storage.estimate().then(({ usage, quota }) => {
      console.log(`You've used ${usage} out of ${quota} bytes.`);
    });
  }
}; 

