import { SET_AGRI_GENERAL_PARAMETER } from '../action-type/agriGeneralParameterActionType';
import {
    SET_LOADING
  } from '../action-type/homeActionType';
  
  export const setAgriGeneralParametersInRedux = (payload) => ({
    type: SET_AGRI_GENERAL_PARAMETER,
    payload,
  });
  