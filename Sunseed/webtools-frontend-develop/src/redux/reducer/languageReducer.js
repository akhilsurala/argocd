/*
 *
 * LanguageProvider reducer
 *
 */
import { DEFAULT_LOCALE } from "../../i18n/i18n";
import { CHANGE_LOCALE } from "../action-type/languageActionType";

export const initialState = {
  locale: DEFAULT_LOCALE,
};

/* eslint-disable default-case, no-param-reassign */
const languageActionReducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case CHANGE_LOCALE:
      state.locale = action.locale;
      break;
    default:
      return state;
  }
};

export default languageActionReducer;
