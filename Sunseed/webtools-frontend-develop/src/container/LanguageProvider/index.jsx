/*
 *
 * LanguageProvider
 *
 * this component connects the redux state language locale to the
 * IntlProvider component and i18n messages (loaded from `app/translations`)
 */

import PropTypes from 'prop-types';
import React, { useEffect } from 'react';
import { IntlProvider } from 'react-intl';

const LanguageProvider = (props) => {

  useEffect(() => {
    // const lang = localStorage.getItem("locale");
    // if (lang) props.localeChange(lang);
    return () => {}
  }, [])
  return (
    <IntlProvider
      locale={props.locale}
      key={props.locale}
      messages={props.messages[props.locale]}
    >
      {React.Children.only(props.children)}
    </IntlProvider>
  );
}

LanguageProvider.propTypes = {
  locale: PropTypes.string,
  messages: PropTypes.object,
  children: PropTypes.element.isRequired,
  localeChange: PropTypes.func,
};


export default LanguageProvider;
