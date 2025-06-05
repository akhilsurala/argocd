import { openDB } from 'idb';

// Initialize IndexedDB
/*
Our DB structure will be like this:

{
    "projects": [
      {
        "projectId": dbVersion,
        "name": "Project dbVersion",
        "runs": [
          {
            "runId": dbVersion,
            "name": "Run dbVersion",
            "weeklyFiles": [
              { "week": "2024-09-01", "obj": "file1.obj", "mtl": "file1.mtl", "dat": "file1.dat" },
              { "week": "2024-09-08", "obj": "file2.obj", "mtl": "file2.mtl", "dat": "file2.dat" }
            ]
          }
        ]
      }
    ]
  }
  
  */
 async function initDB() {
  const db = await openDB('FileDB', 1, {
    upgrade(db) {
      if (!db.objectStoreNames.contains('projects')) {
        const projectStore = db.createObjectStore('projects', { keyPath: 'projectId' });
      }
      if (!db.objectStoreNames.contains('runs')) {
        const runStore = db.createObjectStore('runs', { keyPath: 'runId' });
        runStore.createIndex('projectId', 'projectId', { unique: false });
      }
      if (!db.objectStoreNames.contains('files')) {
        const fileStore = db.createObjectStore('files', { keyPath: ['runId', 'week'] });
        fileStore.createIndex('runId', 'runId', { unique: false });
      }
    }
  });
  return db;
}

// Add or update a file entry
export async function addOrUpdateFile({ projectId, projectName, runId, runName, week, fileType, fileName }) {
  const db = await initDB();

  const tx = db.transaction(['projects', 'runs', 'files'], 'readwrite');
  const projectStore = tx.objectStore('projects');
  const runStore = tx.objectStore('runs');
  const fileStore = tx.objectStore('files');

  // Ensure the project exists
  let project = await projectStore.get(projectId);
  if (!project) {
    project = {
      projectId: projectId,
      name: projectName || `Project ${projectId}`
    };
    await projectStore.add(project);
  }

  // Ensure the run exists
  let run = await runStore.get(runId);
  if (!run) {
    run = {
      runId: runId,
      name: runName || `Run ${runId}`,
      projectId: projectId
    };
    await runStore.add(run);
  }

  // Retrieve existing file entry
  const fileKey = [runId, week];
  let fileEntry = await fileStore.get(fileKey);

  if (!fileEntry) {
    // Create a new file entry with only the current file type
    fileEntry = {
      runId: runId,
      week: week,
      obj: null,
      mtl: null,
      dat: null
    };
  }

  // Update the specific file type
  fileEntry[fileType] = fileName;

  // Put the updated entry back into the store
  await fileStore.put(fileEntry);

  // Commit the transaction
  await tx.done;

  // console.log(`Successfully added/updated ${fileType} file for Run ID ${runId}, Week ${week}`);
}



// Retrieve functions
// async function getAllProjects() {
//   const db = await initDB();
//   const tx = db.transaction('projects', 'readonly');
//   const store = tx.objectStore('projects');
//   const projects = await store.getAll();
//   await tx.done;
//   return projects;
// }

// async function getRunsByProjectId(projectId) {
//   const db = await initDB();
//   const tx = db.transaction('runs', 'readonly');
//   const store = tx.objectStore('runs');
//   const index = store.index('projectId');
//   const runs = await index.getAll(projectId);
//   await tx.done;
//   return runs;
// }

// async function getFilesByRunId(runId) {
//   const db = await initDB();
//   const tx = db.transaction('files', 'readonly');
//   const store = tx.objectStore('files');
//   const index = store.index('runId');
//   const files = await index.getAll(IDBKeyRange.only(runId));
//   await tx.done;
//   return files;
// }

// async function getFile(runId, week) {
//   const db = await initDB();
//   const tx = db.transaction('files', 'readonly');
//   const store = tx.objectStore('files');
//   const file = await store.get([runId, week]);
//   await tx.done;
//   return file;
// }

// async function getAllRunsSortedByWeek() {
//     const db = await initDB();
    
//     const txRuns = db.transaction('runs', 'readonly');
//     const runStore = txRuns.objectStore('runs');
//     const runs = await runStore.getAll();
    
//     const txFiles = db.transaction('files', 'readonly');
//     const fileStore = txFiles.objectStore('files');
//     const fileIndex = fileStore.index('runId');
  
//     // Map to store the earliest week date for each runId
//     const runWeekMap = new Map();
  
//     for (const run of runs) {
//       const runFiles = await fileIndex.getAll(run.runId);
  
//       // Find the earliest 'week' for this run
//       if (runFiles.length > 0) {
//         const earliestWeek = runFiles.reduce((earliest, file) => {
//           return (!earliest || new Date(file.week) < new Date(earliest)) ? file.week : earliest;
//         }, null);
  
//         // Store the earliest week date in the map
//         runWeekMap.set(run.runId, earliestWeek);
//       }
//     }
  
//     // Sort runs by the earliest week date
//     runs.sort((a, b) => {
//       const weekA = runWeekMap.get(a.runId);
//       const weekB = runWeekMap.get(b.runId);
//       return new Date(weekA) - new Date(weekB);
//     });
  
//     await txRuns.done;
//     await txFiles.done;
  
//     return runs;
//   }

//   // Get all files sorted by 'week' date
// async function getAllFilesSortedByWeek() {
//     const db = await initDB();
    
//     const txFiles = db.transaction('files', 'readonly');
//     const fileStore = txFiles.objectStore('files');
  
//     // Get all files
//     const files = await fileStore.getAll();
  
//     // Sort the files by 'week' date
//     files.sort((a, b) => new Date(a.week) - new Date(b.week));
  
//     await txFiles.done;
  
//     return files;
//   }

// // Example Usage
// (async () => {
//   try {


//     // Retrieve and log the file entry
//      const sortedRuns = await getAllFilesSortedByWeek();
//     console.log('Sorted Runs by Week Date:', sortedRuns);

//     // // Retrieve all projects
//     // const projects = await getAllProjects();
//     // console.log('All Projects:', projects);

//     // // Retrieve all runs for Project 1
//     // const runs = await getRunsByProjectId(1);
//     // console.log('Runs for Project 1:', runs);

//     // // Retrieve all files for Run 101
//     // const files = await getFilesByRunId(101);
//     // console.log('Files for Run 101:', files);
//   } catch (error) {
//     console.error('Error:', error);
//   }
// })();


export function indexedDBStuff () {
    // Check for IndexedDB support:
    if (!('indexedDB' in window)) {
      // Can't use IndexedDB
      console.error("This browser doesn't support IndexedDB");
      return;
    } else {
      // Do IndexedDB stuff here:
      // ...
      console.log("This browser support IndexedDB");


    
    }
  }
  
