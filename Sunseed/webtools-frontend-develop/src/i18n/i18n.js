/**
 * i18n.js
 *
 * This will setup the i18n language files and locale data for your app.
 *
 *   IMPORTANT: This file is used by the internal build
 *   script `extract-intl`, and must use CommonJS module syntax
 *   You CANNOT use import/export in this file.
 */
import enTranslationMessages from "../translations/en.json";
import frTranslationMessages from "../translations/fr.json";

const DEFAULT_LOCALE = "fr";  

// prettier-ignore
const appLocales = [
  'en',
  'fr',
];

const formatTranslationMessages = (locale, messages) => {
  const defaultFormattedMessages =
    locale !== DEFAULT_LOCALE
      ? formatTranslationMessages(DEFAULT_LOCALE, enTranslationMessages)
      : {};
  const flattenFormattedMessages = (formattedMessages, key) => {
    const formattedMessage =
      !messages[key] && locale !== DEFAULT_LOCALE
        ? defaultFormattedMessages[key]
        : messages[key];
    return Object.assign(formattedMessages, { [key]: formattedMessage });
  };
  return Object.keys(messages).reduce(flattenFormattedMessages, {});
};

const translationMessages = {
  en: formatTranslationMessages("en", enTranslationMessages),
  fr: formatTranslationMessages("fr", frTranslationMessages),
};

export { appLocales as appLocales, formatTranslationMessages as formatTranslationMessages, translationMessages as translationMessages, DEFAULT_LOCALE as DEFAULT_LOCALE  };
