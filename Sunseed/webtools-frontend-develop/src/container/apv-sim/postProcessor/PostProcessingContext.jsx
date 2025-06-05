import React, { createContext, useContext, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import { getProjects } from "../../../api/userProfile";
import { getRunNames } from "../../../api/runManager";
import { setActiveTab, setRunOptionsAcross } from "../../../redux/action/postProcessorAction";

const PostProcessingContext = createContext();

export const PostProcessingProvider = ({ children, projectId }) => {
  const dispatch = useDispatch();
  const currentSelectedRun = useSelector((state) => state.postProcessor.currentSelectedRun);

  const [postProcessingRuns, setPostProcessingRuns] = useState([]);
  const [defaultRun, setDefaultRun] = useState(currentSelectedRun);
  const [runsOptions, setRunsOptions] = useState([]);
  const [apvAgriRuns, setApvAgriRuns] = useState([]);
  const [pvRuns, setPvRuns] = useState([]);

  useEffect(() => {
    return () => {
      // Cleanup logic (runs on unmount)
      dispatch(setActiveTab(0));
      console.log('Component unmounted');
    };
  }, []);

  useEffect(() => {
    const fetchProjectList = async () => {
      try {
        const response = await getProjects();
        const data = response.data.data;
        const projectDetails = data.find((data) => data.projectId == projectId);
        if (projectDetails) {
          const runs = projectDetails.runIds;
          setPostProcessingRuns(runs);
          localStorage.setItem("post-processing-runs", JSON.stringify(runs));
        }
        if(projectDetails.runIds.length > 0){
          fetchRuns(projectDetails.runIds);
        }
      } catch (error) {
        console.error("Error fetching project list:", error);
      }
    };

    fetchProjectList();
  }, [projectId]);

  const fetchRuns = (runIdList) => {
    getRunNames(projectId, {
      runIdList: runIdList,
    }).then((res) => {
      if (res?.data?.data?.runs?.length) {
        const tempRunOptions = res?.data?.data?.runs.map((run) => ({
          id: run.id,
          name: run.name,
          toggle: run.toggle
        }));

        setRunsOptions(tempRunOptions);
        localStorage.setItem("current-runs", JSON.stringify(tempRunOptions));
        const apvAndAgriRuns = tempRunOptions.filter(
          (run) =>
            run.toggle.toLowerCase() === "apv" ||
            run.toggle.toLowerCase() === "only agri"
        ).map((run) => ({ label: run.name, value: run.id }));
        const onlyPvRuns = tempRunOptions.filter(
          (run) => 
            run.toggle.toLowerCase() === "apv" ||
            run.toggle.toLowerCase() === "only pv"
        ).map((run) => ({ label: run.name, value: run.id }));
        setApvAgriRuns(apvAndAgriRuns);
        setPvRuns(onlyPvRuns);
        localStorage.setItem("apv_agri_runs", JSON.stringify(apvAndAgriRuns));
        localStorage.setItem("pv_runs", JSON.stringify(onlyPvRuns));

        // if (defaultRun == null || res?.data?.data?.runs.some((run) => run.id !== defaultRun)){
        //   setDefaultRun(res?.data?.data?.runs?.[0]?.id);
        // }
        if (
          defaultRun == null ||
          !res.data.data.runs.some((run) => run.id === defaultRun)
        ) {
          setDefaultRun(res.data.data.runs[0]?.id);
        }
        // For Across Runs
        dispatch(
          setRunOptionsAcross(
            tempRunOptions.map((run) => ({ label: run.name, value: run.id }))
          )
        );
      }
    });
  };

  return (
    <PostProcessingContext.Provider
      value={{
        postProcessingRuns,
        defaultRun,
        setDefaultRun,
        runsOptions,
        apvAgriRuns,
        pvRuns,
      }}
    >
      {children}
    </PostProcessingContext.Provider>
  );
};

export const usePostProcessing = () => useContext(PostProcessingContext);
