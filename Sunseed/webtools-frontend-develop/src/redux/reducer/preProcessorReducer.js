import { SET_CROPS } from "../action-type/agriCropParameterActionType";
import { SET_AGRI_GENERAL_PARAMETER } from "../action-type/agriGeneralParameterActionType";
import {
  SELECTED_RUN_TYPE,
  SET_APV_TOGGLE,
  SET_PROJECT_NAME,
  SET_PV_PARAMETERS,
  SET_PV_PARAMETER_DETAILS,
  SET_RUN_ID,
  UPDATE_FORM_VALUE,
} from "../action-type/preProcessorActionType";

const initialState = {
  apvToggle: "APV",
  runId: "",
  updateFormValue: false,
  pvParameters: {},
  pvParameterDetails: {},
  selectedRunType:"All",
  currentProjectName:'',  
  agriGeneralParameters: {},
  agriCropParameterReducer: {
    crops: [],
  },
};

const preProcessorReducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case SET_APV_TOGGLE:
      return {
        ...state,
        apvToggle: action.payload.apvToggle,
      };

      case SET_PROJECT_NAME:
        return {
          ...state,
          currentProjectName: action.payload,
        };
    case SET_RUN_ID:
      return {
        ...state,
        runId: action.payload.runId,
      };
    case UPDATE_FORM_VALUE:
      return {
        ...state,
        updateFormValue: action.payload.updateFormValue,
      };
    case SET_PV_PARAMETERS:
      return {
        ...state,
        pvParameters: action.payload.pvParameters,
      };
    case SET_CROPS:
      return {
        ...state,
        agriCropParameterReducer: {
          ...state.agriCropParameterReducer,
          crops: action.payload,
        },
      };
    case SET_AGRI_GENERAL_PARAMETER:
      return {
        ...state,
        agriGeneralParameters: {
          ...action.payload,
        },
      };
    case SELECTED_RUN_TYPE:
      return {
        ...state,
        selectedRunType: action.payload.selectedRunType,
      };
    case SET_PV_PARAMETER_DETAILS:
      return {
        ...state,
        pvParameterDetails: action.payload.pvParameterDetails,
      };
    default:
      return state;
  }
};

export default preProcessorReducer;
