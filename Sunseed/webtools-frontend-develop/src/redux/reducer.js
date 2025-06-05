import { combineReducers } from "redux";

import homeDashboardReducer from "./reducer/homeReducer";

import preProcessorReducer from "./reducer/preProcessorReducer";
import postProcessorReducer from "./reducer/postProcessorReducer";
const rootReducer = combineReducers({
  homeDashboard: homeDashboardReducer,
  preProcessor: preProcessorReducer,
  postProcessor: postProcessorReducer,
});

function combinedReducer(state, action) {
  if (action.type === "LOGOUT") {
    state = undefined; // Reset the entire state to its initial value
  }
  return rootReducer(state, action);
}

export default combinedReducer;
