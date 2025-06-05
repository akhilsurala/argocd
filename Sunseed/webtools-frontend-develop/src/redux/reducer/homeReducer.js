import {
  SET_LOADING,
  SET_NOTIFICATION_COUNT,
  UPDATE_USER_INFO,
} from '../action-type/homeActionType';

const initialState = {
  loading: false,
  notificationcount: 0,
  userProfileUpdated: false,
};

const homeDashboard = (state = initialState, action = {}) => {
  switch (action.type) {
    case UPDATE_USER_INFO:
      return {
        ...state,
        userProfileUpdated: action.payload,
      };
    case SET_LOADING:
      return {
        ...state,
        loading: action.payload.loading,
      };
    case SET_NOTIFICATION_COUNT:
      return {
        ...state,
        notificationcount: Number(action.payload),
      };
    default:
      return state;
  }
};

export default homeDashboard;
