import React from 'react';
import ReactDOM from 'react-dom/client'; // Use createRoot from react-dom/client
import { BrowserRouter } from 'react-router-dom';
import App from './App.jsx';
import LanguageProvider from './container/LanguageProvider/index.jsx';
import { translationMessages } from './i18n/i18n.js';
import './index.css';

const render = (messages) => {
  ReactDOM.createRoot(document.getElementById('root')).render(
    <LanguageProvider messages={messages} locale="en">
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </LanguageProvider>
  );
};

if (!window.Intl) {
  new Promise((resolve) => {
    resolve(import('intl'));
  })
    .then(() => Promise.all([import('intl/locale-data/jsonp/en.js')]))
    .then(() => render(translationMessages))
    .catch((err) => {
      throw err;
    });
} else {
  render(translationMessages);
}

