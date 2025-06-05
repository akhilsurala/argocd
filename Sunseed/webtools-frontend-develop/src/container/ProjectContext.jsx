import React, { createContext, useContext, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import { getProjectDetails } from "../api/userProfile";

const ProjectContext = createContext();

export const ProjectProvider = ({ children, projectId }) => {
  const dispatch = useDispatch();

  const [project, setProject] = useState([]);

  useEffect(() => {
    const fetchProject = async () => {
      try {
        const response = await getProjectDetails(projectId);
        const data = response.data.data;
        setProject(data);
        localStorage.setItem(
          "currentProjectName",
          data.projectName
        );
      } catch (error) {
        console.error("Error fetching project details:", error);
      }
    };
    
    if(projectId){
      fetchProject();
    }
  }, [projectId]);

  return (
    <ProjectContext.Provider
      value={{
        project,
      }}
    >
      {children}
    </ProjectContext.Provider>
  );
};

export const useProject = () => useContext(ProjectContext);
