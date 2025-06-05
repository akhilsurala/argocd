import { ThemeProvider } from "styled-components";
import { BrowserRouter } from "react-router-dom";
import { Provider } from "react-redux";
import store from "../redux/store";
import { LightTheme } from "../utils/theme";


function ProvideWrapper({ children }) {
  return (
    <Provider store={store}>
      <BrowserRouter>
        <ThemeProvider theme={LightTheme}> {children}</ThemeProvider>
      </BrowserRouter>
    </Provider>
  );
}

export default ProvideWrapper;
