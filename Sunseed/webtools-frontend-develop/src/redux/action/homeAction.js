import {
  UPDATE_USER_INFO,
  SET_LOADING,
  SET_NOTIFICATION_COUNT,
} from '../action-type/homeActionType';

export const userProfileUpdated = (payload) => ({
  type: UPDATE_USER_INFO,
  payload,
});

export const setGlobalLoading = (payload) => ({
  type: SET_LOADING,
  payload,
});

export const updateNotificationCount = (payload) => ({
  type: SET_NOTIFICATION_COUNT,
  payload,
});