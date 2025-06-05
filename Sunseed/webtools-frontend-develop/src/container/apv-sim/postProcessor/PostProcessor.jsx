import CustomCard2 from "../../../components/CustomCard2";
import CustomDropDown from "../../../components/CustomDropDown";
import CustomMiniCard from "../../../components/CustomMiniCard";
import CustomTabs from "../../../components/CustomTabs";
import { Box, Grid } from "@mui/material";
import {
  GRAPH_DOWNLOAD_STATUS,
  heatMapRoute,
  keyDeltaGraphRoute,
  designExplorerGraphRoute,
  agriSideHourlyRoute,
  agriSideWeeklyRoute,
  pvSideHourlyRoute,
  GRAPH_TYPE,
  agriSideWeeklyAcrossRoute,
  pvSideWeeklyAcrossRoute,
  cumulativeAgriPVAcrossRoute,
  hourlyHeatRoute,
  AppRoutesPath,
  threeDviewRoute,
  pvSideWeeklyRoute,
} from "../../../utils/constant";
import { useTheme } from "styled-components";
import { Outbound, Replay } from "@mui/icons-material";
import { useNavigate, useParams } from "react-router-dom";
import CustomSelect from "../../../components/graphs/CustomSelect";
import { useEffect, useState } from "react";
import { getAllRunsData, getRunNames } from "../../../api/runManager";
import GraphListCrad from "../../../components/GraphListCard";
import GraphListCradContainer from "../../../components/GraphListCardContainer";
import { useDispatch, useSelector } from "react-redux";
import {
  setActiveTab,
  setCurrentSelectedRun,
  setRunOptionsAcross,
} from "../../../redux/action/postProcessorAction";
import { getLocalStorageData } from "../../../utils/localStorage";
import { deleteFile, getAllFiles } from "../../../utils/indexDb/indexDbSetup";
import { getProjects } from "../../../api/userProfile";
import { usePostProcessing } from "./PostProcessingContext";

const PostProcessor = () => {
  const theme = useTheme();

  const { projectId } = useParams();
  // Post Processing Context
  const { 
    postProcessingRuns,
    defaultRun,
    setDefaultRun,
    runsOptions,
    apvAgriRuns,
    pvRuns,
  } = usePostProcessing();
  
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [selectedRun, setSelectedRun] = useState(defaultRun);
  const [currentSelectedToggle, setCurrentSelectedToggle] = useState(null)

  const { activeTab } = useSelector((state) => state.postProcessor);

  const handleTabChange = (newActiveTab) => {
    dispatch(setActiveTab(newActiveTab));
  };

  function match(id, tempRunOptions) {
    return tempRunOptions.find(item => item.id === id) || null;
  }

  // Synchronize selectedRun with defaultRun
  useEffect(() => {
    if (defaultRun !== null) {
      setSelectedRun(defaultRun);
    }
  }, [defaultRun])
  
  useEffect(() => {
    fetchProjectList();
  }, []);

  const handleRunChange = (value)=>{
    setSelectedRun(value);
    setDefaultRun(value);
  }

  const fetchProjectList = ()=>{
    getProjects()
    .then((response)=>{
      const data = response.data.data;
      const projectDetails = data.find((data)=>  data.projectId == projectId);
      localStorage.setItem(
        "post-processing-runs",
        JSON.stringify(projectDetails.runIds)
      );

    //   const runIds = localStorage.getItem("post-processing-runs");
    // if (JSON.parse(runIds)?.length) {
    // }
      // fetchRuns(projectDetails.runIds);
    })
    .catch((error)=>{
      console.log(error);
    })
  }

  // const fetchRuns = (runIdList) => {
  //   getRunNames(projectId, {
  //     runIdList: runIdList,
  //   }).then((res) => {
  //     if (res?.data?.data?.runs?.length) {
  //       const tempRunOptions = res?.data?.data?.runs.map((run) => ({
  //         id: run.id,
  //         name: run.name,
  //         toggle: run.toggle
  //       }));

  //       setRunsOptions(tempRunOptions);
  //       localStorage.setItem("current-runs", JSON.stringify(tempRunOptions));
  //       const apvAndAgriRuns = tempRunOptions.filter(
  //         (run) =>
  //           run.toggle.toLowerCase() === "apv" ||
  //           run.toggle.toLowerCase() === "only agri"
  //       ).map((run) => ({ label: run.name, value: run.id }));
  //       const onlyPvRuns = tempRunOptions.filter(
  //         (run) => 
  //           run.toggle.toLowerCase() === "apv" ||
  //           run.toggle.toLowerCase() === "only pv"
  //       ).map((run) => ({ label: run.name, value: run.id }));
  //       setApvAgriRuns(apvAndAgriRuns);
  //       setPvRuns(onlyPvRuns);
  //       localStorage.setItem("apv_agri_runs", JSON.stringify(apvAndAgriRuns));
  //       localStorage.setItem("pv_runs", JSON.stringify(onlyPvRuns));

  //       if (selectedRun == null)
  //         setSelectedRun(res?.data?.data?.runs?.[0]?.id);

  //       // For Across Runs
  //       dispatch(
  //         setRunOptionsAcross(
  //           tempRunOptions.map((run) => ({ label: run.name, value: run.id }))
  //         )
  //       );
  //     }
  //   });
  // };

  useEffect(() => {

    // console.log("debug", selectedRun, runsOptions)
    const obj = match(selectedRun, runsOptions)
    setCurrentSelectedToggle(obj?.toggle?.toUpperCase());

    dispatch(setCurrentSelectedRun(selectedRun));
    // if(selectedRun){
    //   localStorage.setItem("current-runs", selectedRun);
    // }
  }, [selectedRun, runsOptions])

  const open3DviewScene = () => {
    // console.log("hey", runsOptions)


    const obj = match(selectedRun, runsOptions)
    // console.log("obj", obj)
    const dataToSend = {
      projectId: projectId,
      runId: selectedRun,
      runName: obj.name,
      toggle: obj?.toggle || 'APV',
      quantityAvalilable: 3
    };
    // console.log("obj1", dataToSend)

    navigate(threeDviewRoute(projectId, selectedRun), { state: dataToSend });

  }
  const openTemperatureControl = () => {
    const obj = match(selectedRun, runsOptions)
    const dataToSend = {
      projectId: projectId,
      runId: selectedRun,
      runName: obj.name,
      toggle: obj?.toggle || 'APV',

      quantityAvalilable: 2
    };
    navigate(threeDviewRoute(projectId, selectedRun), { state: dataToSend });
  }

  const openCarbonAssimilation = () => {
    const obj = match(selectedRun, runsOptions)
    const dataToSend = {
      projectId: projectId,
      runId: selectedRun,
      runName: obj.name,
      toggle: obj?.toggle || 'APV',
      quantityAvalilable: 1
    };
    navigate(threeDviewRoute(projectId, selectedRun), { state: dataToSend });
  }
  const openDLI = () => {
    const obj = match(selectedRun, runsOptions)
    const dataToSend = {
      projectId: projectId,
      runId: selectedRun,
      runName: obj.name,
      toggle: obj?.toggle || 'APV',
      quantityAvalilable: 4
    };
    navigate(threeDviewRoute(projectId, selectedRun), { state: dataToSend });
  }

  function filterFilesByRunId(fileList, runId) {
    return fileList.filter(file => {
      const parts = file?.split('/');
      console.log("parts", parts)
      // Ensure there are at least 4 parts and the third part matches the runId
      return parts[2] == runId;
    });
  }

  function filterFilesByDatFile(fileList, runId) {
    return fileList.filter(file => {
      const parts = file.split('/');
      console.log("parts", parts)
      // Ensure there are at least 4 parts and the third part matches the runId
      return parts[4] == runId;
    });
  }

  function deleteAllFiles(fileList) {
    if (fileList.length == 0) {
      alert("No files to delete")
      return;
    }
    const confirmation = window.confirm(`Are you sure you want to delete ${fileList.length} files?`);

    if (confirmation) {
      // If the user confirms, delete all files
      while (fileList.length > 0) {
        deleteFile(fileList.pop()); // Removes the last element in the array
      }
      alert("All files have been deleted."); // Notify the user that the files were deleted
    } else {
      alert("File deletion was canceled."); // Notify the user that deletion was canceled
    }
  }


  async function deleteValueFrom3D(type) {
    try {
      const allFiles = await getAllFiles();
      // allFiles.forEach((file) => {
      console.log(" starting........", allFiles);

      const filteredFiles = filterFilesByRunId(allFiles, selectedRun);
      console.log("filterFIles", filteredFiles);
      if (type === '') {

        deleteAllFiles(filteredFiles)
      } else if (type === '1' || type === '2' || type === '4') {
        deleteAllFiles(filterFilesByDatFile(filteredFiles, type))
      }

      // });

    } catch (error) {
      console.error("Error downloading projects:", error);
    }
  }



  const delete3dScene = (type = '') => {

    deleteValueFrom3D(type)
  }

  const threeDTypeList = () => {
    // console.log("apv", selectedRun, currentSelectedToggle)

    if (currentSelectedToggle === 'APV')
      return [
        {
          text: "3D Scene \n Temperature Profile \n Daily Carbon Assimilation",
          buttons: [
            {
              actionText: "Open",
              handleClick: () => open3DviewScene(),
            },
            {
              actionText: "Clear cache",
              handleClick: () => delete3dScene(''),
            },
          ],
        },
      ]
    else if (currentSelectedToggle === 'ONLY AGRI') {
      return [

        {
          text: "3D Scene \n Temperature Profile / Daily Carbon Assimilation",
          buttons: [
            {
              actionText: "Open",
              handleClick: () => open3DviewScene(),
            },
            {
              actionText: "Clear cache",
              handleClick: () => delete3dScene(''),
            },
          ],
        },
      ]

    } else if (currentSelectedToggle === 'ONLY PV') {
      return [
        {
          text: "3D Scene",
          buttons: [
            {
              actionText: "Open",
              handleClick: () => open3DviewScene(),
            },
            {
              actionText: "Clear cache",
              handleClick: () => delete3dScene(''),
            },
          ],
        },
        {
          text: "Daily Light Integral (For Only PV)",
          buttons: [

            {
              actionText: "Open",
              handleClick: () => openDLI(),
            },
            {
              actionText: "Clear cache",
              handleClick: () => delete3dScene('4'),
            },
          ],
        },
      ]
    } else {
      return []
    }
  }

  const getGraphList = (toggle) => {
    const commonGrpahs = [
      {
        text: "Daily Air Temp, Humidity, Direct, Diffuse Rad",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                hourlyHeatRoute(
                  projectId,
                  selectedRun,
                  GRAPH_TYPE.DAILY_AIR_TEMP_HUMIDITY_DIRECT_DIFFUSE_RAD
                )
              ),
            disabled: !selectedRun,
          },
        ],
      },
    ];

    const agriGraphs = [
      {
        text: "Hourly Leaf Temperature across the year",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                hourlyHeatRoute(
                  projectId,
                  selectedRun,
                  GRAPH_TYPE.HOURLY_TEMPERATURE_ACROSS_THE_YEAR
                )
              ),
          },
        ],
      },
      {
        text: "Hourly Carbon Assimilation across the year",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                hourlyHeatRoute(
                  projectId,
                  selectedRun,
                  GRAPH_TYPE.HOURLY_CARBON_ASSIMILATION_ACROSS_THE_YEAR
                )
              ),
          },
        ],
      },
    ];

    const pvGraphs = [
      {
        text: "Bifacial Gain",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                hourlyHeatRoute(
                  projectId,
                  selectedRun,
                  GRAPH_TYPE.BIFACIAL_GAIN
                )
              ),
          },
        ],
      },
    ];

    if (toggle === "ONLY PV") {
      return [...pvGraphs, ...commonGrpahs];
    } else if (toggle === "ONLY AGRI") {
      return [...agriGraphs, ...commonGrpahs];
    } else if (toggle === "APV") {
      return [...agriGraphs, ...pvGraphs, ...commonGrpahs];
    }

    // Default case (optional)
    return [];
  };

  const getBarGraphList = () => {
    const agriGraphs = [
      {
        text: "Cumulative Carbon Assim / Plant",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                cumulativeAgriPVAcrossRoute(
                  projectId,
                  GRAPH_TYPE.CUMULATIVE_CARBON_ASSIM_PER_PLANT
                )
              ),
          },
        ],
      },
      {
        text: "Cumulative Carbon Assim / Ground",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                cumulativeAgriPVAcrossRoute(
                  projectId,
                  GRAPH_TYPE.CUMULATIVE_CARBON_ASSIM_PER_GROUND
                )
              ),
          },
        ],
      },
      {
        text: "Total Transpiration / Plant",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                cumulativeAgriPVAcrossRoute(
                  projectId,
                  GRAPH_TYPE.TOTAL_TRANSPIRATION
                )
              ),
          },
        ],
      },
    ];

    const onlyAgriGraphs = [
      {
        text: "System Economics( Agri Revenue, Total Revenue and Profit )",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                cumulativeAgriPVAcrossRoute(
                  projectId,
                  GRAPH_TYPE.SYSTEM_ECONOMICS_AGRI
                )
              ),
          },
        ],
      },
    ];

    const onlyPVGraphs = [
      {
        text: "System Economics( PV Revenue, Total Revenue and Profit )",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                cumulativeAgriPVAcrossRoute(
                  projectId,
                  GRAPH_TYPE.SYSTEM_ECONOMICS_PV
                )
              ),
          },
        ],
      },
    ];

    const pvGraphs = [
      {
        text: "System Economics( PV Revenue, Agri Revenue, Total Revenue and Profit )",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                cumulativeAgriPVAcrossRoute(
                  projectId,
                  GRAPH_TYPE.SYSTEM_ECONOMICS_PV
                )
              ),
          },
        ],
      },
    ];
    
    if (apvAgriRuns.length > 0 && pvRuns.length > 0) {
      return [...pvGraphs, ...agriGraphs];
    } else if (pvRuns.length > 0) {
      return onlyPVGraphs;
    } else if (apvAgriRuns.length > 0) {
      return [...onlyAgriGraphs, ...agriGraphs];
    }

    // Default case (optional)
    return [];
  };

  const getPVHourlyGraphList = () => {
    const pvAPVGraphs = [
      {
        text: "Hourly Bifacial Gain",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                pvSideHourlyRoute(
                  projectId,
                  selectedRun,
                  GRAPH_TYPE.HOURLY_BIFACIAL_GAIN
                )
              ),
          },
        ],
      },
      {
        text: "Hourly DC Power",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                pvSideHourlyRoute(
                  projectId,
                  selectedRun,
                  GRAPH_TYPE.HOURLY_DC_POWER
                )
              ),
          },
        ],
      },
    ];

    const onlyPVGraphs = [
      {
        text: "PPFD",
        buttons: [
          {
            actionText: "Open",
            handleClick: () =>
              navigate(
                pvSideHourlyRoute(
                  projectId,
                  selectedRun,
                  GRAPH_TYPE.PPFD
                )
              ),
          },
        ],
      },
    ];
    
    if(pvRuns.length > 0 && currentSelectedToggle === 'APV') {
      return pvAPVGraphs;
    } else if (pvRuns.length > 0 && currentSelectedToggle === 'ONLY PV') {
      return [...pvAPVGraphs, ...onlyPVGraphs];
    }

    // Default case (optional)
    return [];
  };
  
  return (
    <Box
      sx={{
        width: "100%",
        height: "100%",
        boxSizing: "border-box",
        background: theme.palette.background.faded,
        fontFamily: theme.palette.fontFamily.main,
        fontWeight: 500,
        fontSize: "14px",
        lineHeight: "27px",
      }}
    >
      <CustomTabs
        activeTab={activeTab}
        handleChange={(value) => handleTabChange(value)}
        tabs={[
          {
            label: "Data Across Runs",
            icon: <Replay />,
            component: (
              <Box sx={{ width: "100%", height: "100%" }}>
                <h1
                  style={{
                    width: "60%",
                    fontSize: "40px",
                    fontWeight: "500",
                    lineHeight: "54px",
                    color: theme.palette.text.main,
                  }}
                >
                  Post processing will show you all the graphical representation
                  of your runs.
                </h1>
                <GraphListCradContainer title="1D Smart Graphs">
                  <Grid container spacing={2} columns={12}>
                    {(apvAgriRuns.length > 0 ) &&
                      (
                        <Grid item xs={6}>
                          <GraphListCrad
                            headerText="Weekly Plots Agri Cropwise"
                            list={[

                              {
                                text: "Cumulative Carbon Assim / Plant",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.CUMULATIVE_CARBON_ASSIM_PER_PLANT
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Cumulative Carbon Assim / Ground",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.CUMULATIVE_CARBON_ASSIM_PER_GROUND
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Avg. Leaf Temperature",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.AVERAGE_LEAF_TEMPERATURE
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Light Absorbed / Plant",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.LIGHT_ABSORBED_PER_PLANT
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Light Absorbed / M2 Ground",
                                // text: (
                                //   <>
                                //     Light Absorbed / M<sup>2</sup> Ground
                                //   </>
                                // ),
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.LIGHT_ABSORBED_PER_M2_GROUND
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Cumulative Transpiration / Plant",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.CUMULATIVE_TRANSPIRATION_PER_PLANT
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Cumulative Transpiration / Ground",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.CUMULATIVE_TRANSPIRATION_PER_GROUND
                                        )
                                      ),
                                  },
                                ],
                              },
                            ]}
                          />
                        </Grid>
                      )
                    }

                    {(pvRuns.length > 0 ) &&
                      (
                        <Grid item xs={6}>
                          <GraphListCrad
                            headerText="Weekly Plots PV"
                            list={[
                              {
                                text: "Cumulative Energy Generation",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        pvSideWeeklyAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.CUMULATIVE_ENERGY_GENERATION
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Average Bifacial Gain",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        pvSideWeeklyAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.AVERAGE_BIFACIAL_GAIN
                                        )
                                      ),
                                  },
                                ],
                              },
                            ]}
                          />
                        </Grid>
                      )
                    }
                  </Grid>
                </GraphListCradContainer>
                <Grid container spacing={2} columns={12}>
                  {(true) &&
                    (
                      <Grid item xs={6}>
                        <GraphListCradContainer title="Cumulative Bar Charts">
                        <GraphListCrad list={getBarGraphList()} />
                          {/* <GraphListCrad
                            list={[
                              {
                                text: "System Economics",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        cumulativeAgriPVAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.SYSTEM_ECONOMICS
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Cumulative Carbon Assim / Plant",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        cumulativeAgriPVAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.CUMULATIVE_CARBON_ASSIM_PER_PLANT
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Cumulative Carbon Assim / Ground",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        cumulativeAgriPVAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.CUMULATIVE_CARBON_ASSIM_PER_GROUND
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Total Transpiration",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        cumulativeAgriPVAcrossRoute(
                                          projectId,
                                          GRAPH_TYPE.TOTAL_TRANSPIRATION
                                        )
                                      ),
                                  },
                                ],
                              },
                            ]}
                          /> */}
                        </GraphListCradContainer>
                      </Grid>
                    )
                  }
                </Grid>
              </Box>
            ),
          },
          {
            label: "Data Within Runs",
            icon: <Outbound sx={{ transform: "rotate(180deg)" }} />,
            component: (
              <Box sx={{ width: "100%", height: "100%" }}>
                <h1
                  style={{
                    width: "60%",
                    fontSize: "40px",
                    fontWeight: "500",
                    lineHeight: "54px",
                    color: theme.palette.text.main,
                  }}
                >
                  Post processing will show you all the graphical representation
                  of your runs.
                </h1>
                <Box
                  sx={{
                    width: "50%",
                    marginBottom: "2em",
                  }}
                >
                  <Box
                    sx={{
                      "& .MuiSelect-select": {
                        background: theme.palette.background.secondary,
                      },
                    }}
                  >
                    <CustomDropDown
                      name="list-of-runs"
                      value={selectedRun}
                      dataSet={runsOptions}
                      handleChange={(value) => handleRunChange(value) }
                      onChange={(e) => console.log(e)}
                      errors={[]}
                      noneNotRequire={true}
                    />
                  </Box>
                </Box>
                <Grid container spacing={2} columns={12} alignItems="stretch">
                  <Grid item xs={6}>
                    <GraphListCradContainer
                      title="Deep 3D Plots"
                      // actionText="Download All"
                      handleClick={() => alert("Download All")}
                    >
                      <GraphListCrad
                        list={threeDTypeList()}
                      />
                    </GraphListCradContainer>
                  </Grid>
                  <Grid item xs={6}>
                    <GraphListCradContainer title="2D Heat Map">
                      <GraphListCrad list={getGraphList(currentSelectedToggle)} />
                    </GraphListCradContainer>
                  </Grid>
                </Grid>
                <GraphListCradContainer title="1D Smart Graphs">
                  <Grid container spacing={2} columns={12}>
                  {(apvAgriRuns.length > 0  && (currentSelectedToggle === 'ONLY AGRI' || currentSelectedToggle === 'APV' )) &&
                    (
                      <>
                        <Grid item xs={6}>
                          <GraphListCrad
                            headerText="Hourly Plots For Agri Bedwise / Cropwise"
                            list={[
                              {
                                text: "Rate Of Carbon Assim / Plant",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideHourlyRoute(
                                          projectId,
                                          selectedRun,
                                          GRAPH_TYPE.RATE_OF_CARBON_ASSIM_PER_PLANT
                                        )
                                      ),
                                    disabled: !selectedRun,
                                  },
                                ],
                              },
                              {
                                text: "Rate Of Carbon Assim / Ground",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideHourlyRoute(
                                          projectId,
                                          selectedRun,
                                          GRAPH_TYPE.RATE_OF_CARBON_ASSIM_PER_GROUND
                                        )
                                      ),
                                    disabled: !selectedRun,
                                  },
                                ],
                              },
                              {
                                text: "Avg. Leaf Temperature",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideHourlyRoute(
                                          projectId,
                                          selectedRun,
                                          GRAPH_TYPE.AVERAGE_LEAF_TEMPERATURE
                                        )
                                      ),
                                    disabled: !selectedRun,
                                  },
                                ],
                              },
                              {
                                text: "Light Distribution Curves",
                                buttons: [
                                  // {
                                  //   actionText: "Open",
                                  //   handleClick: () => true,
                                  //   disabled: !selectedRun,
                                  // },
                                ],
                                type: "NESTED",
                                elements: [
                                  {
                                    text: "% Sunlit Leaves / Plant",
                                    buttons: [
                                      {
                                        actionText: "Open",
                                        handleClick: () =>
                                          navigate(
                                            agriSideHourlyRoute(
                                              projectId,
                                              selectedRun,
                                              GRAPH_TYPE.PERCENT_SUNLIT_LEAVES_PER_PLANT
                                            )
                                          ),
                                        disabled: !selectedRun,
                                      },
                                    ],
                                  },
                                  {
                                    text: "% Sunlit Leaves / Ground Area",
                                    buttons: [
                                      {
                                        actionText: "Open",
                                        handleClick: () =>
                                          navigate(
                                            agriSideHourlyRoute(
                                              projectId,
                                              selectedRun,
                                              GRAPH_TYPE.PERCENT_SUNLIT_LEAVES_PER_GROUND_AREA
                                            )
                                          ),
                                        disabled: !selectedRun,
                                      },
                                    ],
                                  },
                                  {
                                    text: "Light Absorbed / Plant",
                                    buttons: [
                                      {
                                        actionText: "Open",
                                        handleClick: () =>
                                          navigate(
                                            agriSideHourlyRoute(
                                              projectId,
                                              selectedRun,
                                              GRAPH_TYPE.LIGHT_ABSORBED_PER_PLANT
                                            )
                                          ),
                                        disabled: !selectedRun,
                                      },
                                    ],
                                  },
                                  {
                                    text: "Penetration Efficiency Metric",
                                    buttons: [
                                      {
                                        actionText: "Open",
                                        handleClick: () =>
                                          navigate(
                                            agriSideHourlyRoute(
                                              projectId,
                                              selectedRun,
                                              GRAPH_TYPE.PENETRATION_EFFICIENCY_METRIC
                                            )
                                          ),
                                        disabled: !selectedRun,
                                      },
                                    ],
                                  },
                                  {
                                    text: "% Of Sunlit Leaves Saturated / Plant",
                                    buttons: [
                                      {
                                        actionText: "Open",
                                        handleClick: () =>
                                          navigate(
                                            agriSideHourlyRoute(
                                              projectId,
                                              selectedRun,
                                              GRAPH_TYPE.PERCENT_OF_SUNLIT_LEAVES_SATURATED_PER_PLANT
                                            )
                                          ),
                                        disabled: !selectedRun,
                                      },
                                    ],
                                  },
                                  {
                                    text: "Saturation Extent / Plant",
                                    buttons: [
                                      {
                                        actionText: "Open",
                                        handleClick: () =>
                                          navigate(
                                            agriSideHourlyRoute(
                                              projectId,
                                              selectedRun,
                                              GRAPH_TYPE.SATURATION_EXTENT_PLANT
                                            )
                                          ),
                                        disabled: !selectedRun,
                                      },
                                    ],
                                  },
                                ],
                              },
                              // {
                              //   text: "Cumulative PPFD (DLI)",
                              //   buttons: [
                              //     {
                              //       actionText: "Open",
                              //       handleClick: () =>
                              //         navigate(
                              //           agriSideHourlyRoute(
                              //             projectId,
                              //             selectedRun,
                              //             GRAPH_TYPE.CUMULATIVE_PPFD_DLI
                              //           )
                              //         ),
                              //       disabled: !selectedRun,
                              //     },
                              //   ],
                              // },
                            ]}
                          />
                        </Grid>
                        <Grid item xs={6}>
                          <GraphListCrad
                            headerText="Weekly Plots For Agri Spanning The Year - Cropwise"
                            list={[
                              {
                                text: "Bi-Weekly Cumulative Carbon Assim / Plant",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyRoute(
                                          projectId,
                                          selectedRun,
                                          GRAPH_TYPE.BI_WEEKLY_CUMULATIVE_CARBON_ASSIM_PER_PLANT
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Bi-Weekly Cumulative Carbon Assim / Ground Area",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyRoute(
                                          projectId,
                                          selectedRun,
                                          GRAPH_TYPE.BI_WEEKLY_CUMULATIVE_CARBON_ASSIM_PER_GROUND_AREA
                                        )
                                      ),
                                  },
                                ],
                              },
                              {
                                text: "Avg. Leaf Temperature",
                                buttons: [
                                  {
                                    actionText: "Open",
                                    handleClick: () =>
                                      navigate(
                                        agriSideWeeklyRoute(
                                          projectId,
                                          selectedRun,
                                          GRAPH_TYPE.AVERAGE_LEAF_TEMPERATURE
                                        )
                                      ),
                                  },
                                ],
                              },
                            ]}
                          />
                        </Grid>
                      </>
                    )
                  }
                  {(pvRuns.length > 0 && (currentSelectedToggle === 'ONLY PV' || currentSelectedToggle === 'APV' )) &&
                    (
                      <Grid item xs={6}>
                        <GraphListCrad
                          headerText="Hourly Plots for PV"
                          list={getPVHourlyGraphList()}
                        />
                      </Grid>
                    )
                  }
                  {(pvRuns.length > 0 && (currentSelectedToggle === 'ONLY PV' || currentSelectedToggle === 'APV' )) &&
                    (
                      <Grid item xs={6}>
                        <GraphListCrad
                          headerText="Weekly Plots for PV Spanning The Year "
                          list={[
                            {
                              text: "Daily DLI",
                              buttons: [
                                {
                                  actionText: "Open",
                                  handleClick: () =>
                                    navigate(
                                      pvSideWeeklyRoute(
                                        projectId,
                                        selectedRun,
                                        GRAPH_TYPE.DAILY_DLI
                                      )
                                    ),
                                },
                              ],
                            },
                          ]}
                        />
                      </Grid>
                    )
                  }
                  </Grid>
                </GraphListCradContainer>
              </Box>
            ),
          },
        ]}
      />
    </Box>
  );
};

export default PostProcessor;
