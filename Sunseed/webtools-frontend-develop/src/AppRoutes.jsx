import React, { useEffect, useState } from "react";

import { useDispatch, useSelector } from "react-redux";
import { Route, Routes } from "react-router-dom";

// import StompClient from 'react-stomp-client';
import { IconButton, Snackbar, SnackbarContent } from "@mui/material";
import Typography from "@mui/material/Typography";
import Loading from "./components/Loading";
import { ThemeProvider } from "styled-components";
import { LightTheme } from "./utils/theme";
import { AppRoutesPath } from "./utils/constant";
import WrapperForLoginFlow from "./container/login/WrapperForLoginFlow";
import HomePageWrapper from "./container/home/HomePageWrapper";
import CompTest from "./CompTest";
import LoginForm from "./container/login/LoginForm";
import ForgotPassword from "./container/login/ForgotPasswordFlow";
import ForgotPasswordFlow from "./container/login/ForgotPasswordFlow";
import SignUpWrapper from "./container/login/SignUpWrapper";
import PageNotFound from "./container/PageNotFound";
import RecentProjectScreen from "./container/apv-sim/RecentProjectScreen";
import ProjectForm from "./container/apv-sim/ProjectForm";

import DashboardHome from "./container/home/Dashboard";
import WrapperForDashboard from "./container/dashboard/WrapperForDashboard";
import Dashboard from "./container/home/Dashboard";
import MiniDrawer from "./container/navigation/WrapperForMainScreen";
import WrapperForMainScreen from "./container/navigation/WrapperForMainScreen";
import CustomTable from "./components/custom-table/CustomTable";
import ProjectWrapper from "./container/apv-sim/ProjectWrapper";
import CreateProjectPage from "./container/CreateProjectPage";
import RunManagerPage from "./container/apv-sim/runManager/RunManagerPage";
import PrivateRoute from "./PrivateRoute";
import AdminWrapper from "./admin/components/AdminWrapper";
import AdminDashboard from "./admin/container/dashboard/AdminDashboard";
import AdminPvDatabase from "./admin/container/pv-database/AdminPvDatabase";
import AdminLoginForm from "./admin/container/sign-in/AdminLoginForm";
import PvModuleForm from "./admin/container/pv-database/PvModuleForm";
import UserLandingPage from "./admin/container/user-database/UserLandingPage";
import SoilLandingPage from "./admin/container/soil-database/SoilLandingPage";
import CropLandingPage from "./admin/container/crop-database/CropLandingPage";
import UserForm from "./admin/container/user-database/UserForm";
import CropForm from "./admin/container/crop-database/CropForm";
import SoilForm from "./admin/container/soil-database/SoilForm";
import ModeOfPvOperationLandingPage from "./admin/container/mode-of-pv-operation/ModeOfPvOperationLandingPage";
import ModeOfPvOperationForm from "./admin/container/mode-of-pv-operation/ModeOfPvOperation";
import ModuleConfigurationLandingPage from "./admin/container/module-configuration/ModuleConfigurationLandingPage";
import ModuleConfigurationForm from "./admin/container/module-configuration/ModuleConfigurationForm";
import TypeOfIrrigationLandingPage from "./admin/container/type-of-irrigation/TypeOfIrrigationLandingPage";
import TypeOfIrrigationForm from "./admin/container/type-of-irrigation/TypeOfIrrigationForm";
import ProtectionLayerForm from "./admin/container/protection-layer/ProtectionLayerForm";
import ProtectionLayerLandingPage from "./admin/container/protection-layer/ProtectionLayerLandingPage";
import PostProcessor from "./container/apv-sim/postProcessor/PostProcessor";
import KeyDelta from "./container/apv-sim/postProcessor/graphs/KeyDelta";
import HeatMap from "./container/apv-sim/postProcessor/graphs/HeatMap";
import DesignExplorer from "./container/apv-sim/postProcessor/graphs/DesignExplorer";
import AgriSideHourly from "./container/apv-sim/postProcessor/graphs/AgriSideHourly";
import WeeklyPlotsAgri from "./container/apv-sim/postProcessor/graphs/AgriSideWeekly";
import HourlyPlotsPV from "./container/apv-sim/postProcessor/graphs/PVSideHourly";
import AgriSideWeeklyAcross from "./container/apv-sim/postProcessor/graphs/AgriSideWeeklyAcross";
import PVSideWeeklyAcross from "./container/apv-sim/postProcessor/graphs/PVSideWeeklyAcross";
import { getCropMasterDataParameter } from "./api/userProfile";
import { setAgriCropsType } from "./redux/action/agriCropParameters";
import ProfilePage from "./container/user-profile/profilePage";
import { BASE_URL_WEBSOCKET_URL } from "./api/config";
import HourlyHeat from "./container/apv-sim/postProcessor/graphs/HourlyHeat";
import CumulativeAgriPVAcross from "./container/apv-sim/postProcessor/graphs/CumulativeAgriPVAcross";
import PostProcessorScreen from "./container/post-processor/PostProcessorScreen";
import StaticPageLandingPage from "./admin/container/static-pages/StaticPageLandingPage";
import StaticPageForm from "./admin/container/static-pages/StaticPageForm";
import { updateNotificationCount } from "./redux/action/homeAction";
import LandingPageView from "./admin/container/static-pages/LandingPageView";
import PvModuleViewPage from "./admin/container/pv-database/PvModuleViewPage";
import ModeOfPvOperationViewPage from "./admin/container/mode-of-pv-operation/ModeOfPvOperationViewPage";
import ModuleConfigurationViewPage from "./admin/container/module-configuration/ModuleConfigurationViewPage";
import CropDatabaseViewPage from "./admin/container/crop-database/CropDatabaseViewPage";
import ProtectionLayerViewPage from "./admin/container/protection-layer/ProtectionLayerViewPage";
import SoilDatabaseViewPage from "./admin/container/soil-database/SoilDatabaseViewPage";
import TypeOfIrrigationViewPage from "./admin/container/type-of-irrigation/TypeOfIrrigationViewPage";
import UserDatabaseViewPage from "./admin/container/user-database/UserDatabaseViewPage";
import WrapperForPostProcessingScreen from "./container/apv-sim/postProcessor/WrapperForPostProcessingScreen";

import ExportPDF from "./export-pdf/index";
import PVSideWeekly from "./container/apv-sim/postProcessor/graphs/PVSideWeekly";
import { connectWebSocket, disconnectWebSocket } from "./utils/websocket";
const AppRoutes = () => {
  const [errorMessage, setErrorMessage] = useState("");
  const [snakBarColor, setSnakBarColor] = useState("#C14040");
  const loading = useSelector((state) => state.homeDashboard.loading);

  const dispatch = useDispatch();

  useEffect(() => {
    const emailId = localStorage.getItem("emailId");
    const token = localStorage.getItem("apiToken");

    if (emailId && token) {
      connectWebSocket(emailId, token, dispatch); // Initialize WebSocket connection
    }

    return () => {
      disconnectWebSocket(); // Ensure WebSocket is disconnected
    };
  }, [dispatch]);

  useEffect(() => {
    function handleKeyDown(event) {
      if (event.key === "Control" || event.key === "Meta") {
        console.log("Pressed!!!!");
        // dispatch(setCtrlPressed(true));
      }
    }

    function handleKeyUp(event) {
      if (event.key === "Control" || event.key === "Meta") {
        // dispatch(setCtrlPressed(false));
      }
    }

    function handleWindowVariableAddition(event) {
      const addedVariableName = event.detail.variableName;

      setSnakBarColor(window["snackBargColor"]);
      setErrorMessage(window[addedVariableName]);
    }

    window.addEventListener(
      "windowVariableAdded",
      handleWindowVariableAddition
    );
    window.addEventListener("keydown", handleKeyDown);
    window.addEventListener("keyup", handleKeyUp);

    // Clean up the event listener when the component unmounts
    return () => {
      window.removeEventListener(
        "windowVariableAdded",
        handleWindowVariableAddition
      );
      window.removeEventListener("keydown", handleKeyDown);
      window.removeEventListener("keyup", handleKeyUp);
    };
  }, []);

  useEffect(() => {
    const getData = setTimeout(() => {
      if (errorMessage !== "") setErrorMessage("");
    }, 5000);

    return () => clearTimeout(getData);
  }, [errorMessage]);

  const vertical = "top";
  const horizontal = "center";

  const getCropMasterData = () => {
    console.log("getCropMasterData");
    getCropMasterDataParameter()
      .then((response) => {
        if (response.data.httpStatus === "OK") {
          // dispatch(setAgriCropsType(response.data.data))
          dispatch(
            setAgriCropsType(response?.data?.data.map((item) => ({ ...item })))
          );
        }
      })
      .catch((error) => {
        console.log(error);

        // alert(error.response.data.errorMessages[0])
      })
      .finally(() => {
        // setLoader(false);
      });
  };

  useEffect(() => {

    const userId = localStorage.getItem("apiToken");
    if (userId) {
      getCropMasterData();
    }
  }, []);

  return (
    <ThemeProvider theme={LightTheme}>
      <Snackbar
        open={errorMessage !== ""}
        anchorOrigin={{ vertical, horizontal }}
        sx={{ marginTop: "-24px" }}
      >
        <SnackbarContent
          sx={{
            backgroundColor: snakBarColor,
            width: "100vw",
            justifyContent: "center",
            padding: "4px 0px",
            fontFamily: "Montserrat",
            fontWeight: 500,
          }}
          message={
            <div style={{ whiteSpace: "pre-line", textAlign: "center" }}>
              {errorMessage.split("\n").map((line, index) => (
                <Typography key={index} variant="body2" sx={{ color: "#fff" }}>
                  {line}
                </Typography>
              ))}
            </div>
          }
        />
      </Snackbar>

      {loading && <Loading />}

      <Routes>
        <Route
          path={AppRoutesPath.COMP_TEST}
          element={
            <PrivateRoute path={AppRoutesPath.COMP_TEST}>
              <CompTest />
            </PrivateRoute>
          }
        />

        <Route element={<WrapperForLoginFlow />}>
          <Route path={AppRoutesPath.LOGIN} element={<LoginForm />} />
          <Route path={AppRoutesPath.SIGN_UP} element={<SignUpWrapper />} />
          <Route
            path={AppRoutesPath.FORGOT_PASSWORD}
            element={<ForgotPasswordFlow />}
          />
        </Route>

        <Route
          path={AppRoutesPath.APV_SIM}
          element={
            <PrivateRoute path={AppRoutesPath.APV_SIM}>
              <WrapperForMainScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<RecentProjectScreen />} />
        </Route>

        <Route
          path={AppRoutesPath.CREATE_PROJECT}
          element={
            <PrivateRoute path={AppRoutesPath.CREATE_PROJECT}>
              <WrapperForMainScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<CreateProjectPage />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.PRE_PROCESSOR}
          element={
            <PrivateRoute path={AppRoutesPath.PRE_PROCESSOR}>
              <WrapperForMainScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<ProjectWrapper />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.RUN_MANAGER}
          element={
            <PrivateRoute path={AppRoutesPath.RUN_MANAGER}>
              <WrapperForMainScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<RunManagerPage />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.POST_PROCESSOR}
          element={
            <PrivateRoute path={AppRoutesPath.POST_PROCESSOR}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<PostProcessor />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.KEY_DELTA}
          element={
            <PrivateRoute path={AppRoutesPath.KEY_DELTA}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<KeyDelta />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
            params.runId;
          }}
          action={({ params }) => {
            params.projectId;
            params.runId;
          }}
          path={AppRoutesPath.AGRI_SIDE_HOURLY}
          element={
            <PrivateRoute path={AppRoutesPath.AGRI_SIDE_HOURLY}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<AgriSideHourly />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
            params.runId;
          }}
          action={({ params }) => {
            params.projectId;
            params.runId;
          }}
          path={AppRoutesPath.AGRI_SIDE_WEEKLY}
          element={
            <PrivateRoute path={AppRoutesPath.AGRI_SIDE_WEEKLY}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<WeeklyPlotsAgri />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
            params.runId;
          }}
          action={({ params }) => {
            params.projectId;
            params.runId;
          }}
          path={AppRoutesPath.PV_SIDE_HOURLY}
          element={
            <PrivateRoute path={AppRoutesPath.PV_SIDE_HOURLY}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<HourlyPlotsPV />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
            params.runId;
          }}
          action={({ params }) => {
            params.projectId;
            params.runId;
          }}
          path={AppRoutesPath.PV_SIDE_WEEKLY}
          element={
            <PrivateRoute path={AppRoutesPath.PV_SIDE_WEEKLY}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<PVSideWeekly />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.AGRI_SIDE_WEEKLY_ACROSS}
          element={
            <PrivateRoute path={AppRoutesPath.AGRI_SIDE_WEEKLY_ACROSS}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<AgriSideWeeklyAcross />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.PV_SIDE_WEEKLY_ACROSS}
          element={
            <PrivateRoute path={AppRoutesPath.PV_SIDE_WEEKLY_ACROSS}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<PVSideWeeklyAcross />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
            params.runId;
          }}
          action={({ params }) => {
            params.projectId;
            params.runId;
          }}
          path={AppRoutesPath.HOURLY_HEAT}
          element={
            <PrivateRoute path={AppRoutesPath.HOURLY_HEAT}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<HourlyHeat />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.CUMULATIVE_AGRI_PV_ACROSS}
          element={
            <PrivateRoute path={AppRoutesPath.CUMULATIVE_AGRI_PV_ACROSS}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<CumulativeAgriPVAcross />} />
        </Route>

        <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.DESIGN_EXPLORER}
          element={
            <PrivateRoute path={AppRoutesPath.DESIGN_EXPLORER}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<DesignExplorer />} />
        </Route>

        <Route
          path={AppRoutesPath.DEFAULT}
          element={
            <PrivateRoute path={AppRoutesPath.DEFAULT}>
              <WrapperForDashboard />
            </PrivateRoute>
          }
        >
          <Route index element={<Dashboard />} />
        </Route>

        {/* <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.USER_PROFILE}
          element={
            <PrivateRoute path={AppRoutesPath.USER_PROFILE}>
              <WrapperForDashboard />
            </PrivateRoute>
          }
        >
          <Route index element={<ProfilePage />} />
        </Route> */}

        <Route
          path={AppRoutesPath.USER_PROFILE}
          element={
            <PrivateRoute path={AppRoutesPath.USER_PROFILE}>
              <ProfilePage />
            </PrivateRoute>
          }
        />

        <Route
          loader={({ params }) => {
            params.projectId;
          }}
          action={({ params }) => {
            params.projectId;
          }}
          path={AppRoutesPath.THREE_D_VIEW}
          element={
            <PrivateRoute path={AppRoutesPath.THREE_D_VIEW}>
              <WrapperForPostProcessingScreen />
            </PrivateRoute>
          }
        >
          <Route index element={<PostProcessorScreen />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN_HOME}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_HOME}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<StaticPageLandingPage />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN}>
              <AdminWrapper>
                <StaticPageLandingPage />
              </AdminWrapper>
            </PrivateRoute>
          }
        />
        <Route
          path={AppRoutesPath.ADMIN_PROFILE}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_PROFILE}>
              <AdminWrapper>
                <ProfilePage />
              </AdminWrapper>
            </PrivateRoute>
          }
        />

        <Route
          path={AppRoutesPath.ADMIN_STATIC_PAGE}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_STATIC_PAGE}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<StaticPageLandingPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_STATIC_PAGE_NEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_STATIC_PAGE_NEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<StaticPageForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_STATIC_PAGE_EDIT}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_STATIC_PAGE_EDIT}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<StaticPageForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_STATIC_PAGE_VIEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_STATIC_PAGE_VIEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<LandingPageView />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_PV_DATABASE}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_PV_DATABASE}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<AdminPvDatabase />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_PV_DATABASE_NEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_PV_DATABASE_NEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<PvModuleForm />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN_PV_DATABASE_VIEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_PV_DATABASE_VIEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<PvModuleViewPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_PV_DATABASE_EDIT}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_PV_DATABASE_EDIT}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<PvModuleForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_SOIL_DATABASE}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_SOIL_DATABASE}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<SoilLandingPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_SOIL_DATABASE_NEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_SOIL_DATABASE_NEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<SoilForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_SOIL_DATABASE_EDIT}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_SOIL_DATABASE_EDIT}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<SoilForm />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN_SOIL_DATABASE_VIEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_SOIL_DATABASE_VIEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<SoilDatabaseViewPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_CROP_DATABASE}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_CROP_DATABASE}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<CropLandingPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_CROP_DATABASE_NEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_CROP_DATABASE_NEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<CropForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_CROP_DATABASE_EDIT}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_CROP_DATABASE_EDIT}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<CropForm />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN_CROP_DATABASE_VIEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_CROP_DATABASE_VIEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<CropDatabaseViewPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_USER_DATABASE}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_USER_DATABASE}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<UserLandingPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_USER_DATABASE_NEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_USER_DATABASE_NEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<UserForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_USER_DATABASE_EDIT}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_USER_DATABASE_EDIT}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<UserForm />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN_USER_DATABASE_VIEW}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_USER_DATABASE_VIEW}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<UserDatabaseViewPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ModeOfPvOperationLandingPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE_NEW}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE_NEW}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ModeOfPvOperationForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE_EDIT}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE_EDIT}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ModeOfPvOperationForm />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE_VIEW}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE_VIEW}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ModeOfPvOperationViewPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ModuleConfigurationLandingPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE_NEW}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE_NEW}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ModuleConfigurationForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE_EDIT}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE_EDIT}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ModuleConfigurationForm />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE_VIEW}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE_VIEW}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ModuleConfigurationViewPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE}
          element={
            <PrivateRoute path={AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE}>
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ProtectionLayerLandingPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE_NEW}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE_NEW}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ProtectionLayerForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE_EDIT}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE_EDIT}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ProtectionLayerForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE_VIEW}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE_VIEW}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<ProtectionLayerViewPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<TypeOfIrrigationLandingPage />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE_NEW}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE_NEW}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<TypeOfIrrigationForm />} />
        </Route>

        <Route
          path={AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE_EDIT}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE_EDIT}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<TypeOfIrrigationForm />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE_VIEW}
          element={
            <PrivateRoute
              path={AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE_VIEW}
            >
              <AdminWrapper />
            </PrivateRoute>
          }
        >
          <Route index element={<TypeOfIrrigationViewPage />} />
        </Route>
        <Route
          path={AppRoutesPath.ADMIN_SIGN_IN}
          element={<AdminLoginForm />}
        />
        {/* <Route path={AppRoutesPath.DOWNLOAD_PDF} element={<ExportPDF />} /> */}
        <Route path={AppRoutesPath.WILD_CARD} element={<PageNotFound />} />
      </Routes>
    </ThemeProvider>
  );
};

export default AppRoutes;
