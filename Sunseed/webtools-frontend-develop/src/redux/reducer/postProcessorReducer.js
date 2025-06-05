import {
  SET_ACTIVE_TAB,
  SET_CURRENT_SELECTED_RUN,
  SET_RUN_OPTIONS,
  SET_SIMULATION_GROUND_AREA,
  SET_URL_DETAILS,
  SET_URL_DETAILS_WEEK_TIME,
  UPDATE_MIN_MAX_FOR_3D_VIEWER,
} from "../action-type/postProcessorActionType";

const postProcessorReducer = (
  state = {
    activeTab: 0,
    runOptions: [],
    minMax:[],
    currentSelectedRun: null,
    currentlySelectedWeekTime:[],

    linkDetailsAsPerHours: {      
        // "scenes": [
        //   {
        //     "geometry": null,
        //     "material": null,
        //     "time": "2024-07-08 01:30:00 UTC",
        //     "carbon_assimilation": null,
        //     "temperature": null,
        //     "helios": {
        //       "min": 0,
        //       "max": 0,
        //       "url": ""
        //     }
        //   },
        //   {
        //     "geometry": null,
        //     "material": null,
        //     "time": "2024-07-08 02:30:00 UTC",
        //     "carbon_assimilation": null,
        //     "temperature": null,
        //     "helios": {
        //       "min": 0,
        //       "max": 0,
        //       "url": ""
        //     }
        //   },
        //   {
        //     "geometry": null,
        //     "material": null,
        //     "carbon_assimilation": null,
        //     "temperature": null,
        //     "time": "2024-07-08 03:30:00 UTC",
        //     "helios": {
        //       "min": 0,
        //       "max": 0,
        //       "url": ""
        //     }
        //   },
        //   {
        //     "geometry": null,
        //     "material": null,
        //     "time": "2024-07-08 04:30:00 UTC",
        //     "carbon_assimilation": null,
        //     "temperature": null,
        //     "helios": {
        //       "min": 0,
        //       "max": 0,
        //       "url": ""
        //     }
        //   },
        //   {
        //     "geometry": null,
        //     "material": null,
        //     "time": "2024-07-08 05:30:00 UTC",
        //     "carbon_assimilation": null,
        //     "temperature": null,
        //     "helios": {
        //       "min": 0,
        //       "max": 0,
        //       "url": ""
        //     }
        //   },
        //   {
        //     "geometry": null,
        //     "material": null,
        //     "time": "2024-07-08 06:30:00 UTC",
        //     "carbon_assimilation": null,
        //     "temperature": null,
        //     "helios": {
        //       "min": 0,
        //       "max": 0,
        //       "url": ""
        //     }
        //   }
        // ]
      },
  },
  action = {}
) => {
  switch (action.type) {
    case SET_ACTIVE_TAB:
      return {
        ...state,
        activeTab: action.payload,
      }; 
       case SET_SIMULATION_GROUND_AREA:
        return {
          ...state,
          linkDetailsAsPerHours: {
            ...state.linkDetailsAsPerHours,
            simulationGroundArea: action.payload
          }
        }; 
     case SET_CURRENT_SELECTED_RUN:
      return {
        ...state,
        currentSelectedRun: action.payload,
      };

    case SET_RUN_OPTIONS:
      return {
        ...state,
        runOptions: action.payload,
      };
      case SET_URL_DETAILS:
          return {
            ...state,
            linkDetailsAsPerHours: action.payload,
          }; 
      case SET_URL_DETAILS_WEEK_TIME:
              return {
                ...state,
                currentlySelectedWeekTime: action.payload,
              }; 
        
    case UPDATE_MIN_MAX_FOR_3D_VIEWER:
        return {
          ...state,
          minMax: action.payload,
        };

    default:
      return state;
  }
};

export default postProcessorReducer;
