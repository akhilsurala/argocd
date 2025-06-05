import { CHANGE_LOCALE } from "../action-type/languageActionType";
  
  export const changeLocale = (payload) => ({
    type: CHANGE_LOCALE,
    payload,
  });
  