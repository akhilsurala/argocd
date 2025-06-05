import { ErrorBoundary } from "react-error-boundary";
import { Provider } from "react-redux";
import "./App.css";

import AppRoutes from "./AppRoutes";
import ErrorPage from "./components/ErrorPage";
import store from "./redux/store";

function App() {
  return (
    <ErrorBoundary fallbackRender={ErrorPage}>
      <Provider store={store}>
        <AppRoutes />
      </Provider>
    </ErrorBoundary>
  );
}

export default App;
