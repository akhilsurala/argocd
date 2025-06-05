import {
  SELECTED_RUN_TYPE,
  SET_APV_TOGGLE,
  SET_PROJECT_NAME,
  SET_PV_PARAMETERS,
  SET_PV_PARAMETER_DETAILS,
  SET_RUN_ID,
  UPDATE_FORM_VALUE
} from "../action-type/preProcessorActionType";

  export const setApvToggle = (payload) => ({
    type: SET_APV_TOGGLE,
    payload,
  });
  
  export const setCurrentProjectName = (payload) => ({
    type: SET_PROJECT_NAME,
    payload,
  });

  export const setRunId = (payload) => ({
    type: SET_RUN_ID,
    payload,
  });
  
  export const setUpdateFormValue = (payload) => ({
    type: UPDATE_FORM_VALUE,
    payload,
  });

  export const setPvParameters = (payload) => ({
    type: SET_PV_PARAMETERS,
    payload,
  });

  export const setSelectedRunType = (payload) => ({
    type: SELECTED_RUN_TYPE,
    payload,
  });

  export const setPvParameterDetails = (payload) => ({
    type: SET_PV_PARAMETER_DETAILS,
    payload,
  });
  