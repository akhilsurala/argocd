import {
  SET_ACTIVE_TAB,
  SET_CURRENT_SELECTED_RUN,
  SET_RUN_OPTIONS,
  SET_SIMULATION_GROUND_AREA,
  SET_URL_DETAILS,
  SET_URL_DETAILS_WEEK_TIME,
  UPDATE_MIN_MAX_FOR_3D_VIEWER,
} from "../action-type/postProcessorActionType";


export const setActiveTab = (payload) => ({
  type: SET_ACTIVE_TAB,
  payload,
});

export const setSimulationGroundArea = (payload) => ({
  type: SET_SIMULATION_GROUND_AREA,
  payload,
});



export const setCurrentSelectedRun = (payload) => ({
  type: SET_CURRENT_SELECTED_RUN,
  payload,
});

export const setRunOptionsAcross = (payload) => ({
  type: SET_RUN_OPTIONS,
  payload,
});


export const setUrlDetails = (payload) => ({
  type: SET_URL_DETAILS,
  payload,
});


export const setUrlDetailsWeeksTime = (payload) => ({
  type: SET_URL_DETAILS_WEEK_TIME,
  payload,
});

export const updateMinMaxFor3dView = (payload) => ({
  type: UPDATE_MIN_MAX_FOR_3D_VIEWER,
  payload,
});

  