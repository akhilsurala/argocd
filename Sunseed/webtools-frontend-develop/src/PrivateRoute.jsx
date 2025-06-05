import React from "react";
import { Route, Navigate, useLocation, useParams } from "react-router-dom";
import { AppRoutesPath } from "./utils/constant";

export const allowedRoutes = (role, path) => {
  const routes = {
    user: new Set([
      AppRoutesPath.DEFAULT,
      AppRoutesPath.SIGN_UP,
      AppRoutesPath.LOGIN,
      AppRoutesPath.FORGOT_PASSWORD,
      AppRoutesPath.HOME,
      AppRoutesPath.APV_SIM,
      AppRoutesPath.CREATE_PROJECT,
      AppRoutesPath.COMP_TEST,
      AppRoutesPath.PRE_PROCESSOR,
      AppRoutesPath.POST_PROCESSOR,
      AppRoutesPath.THREE_D_VIEW,
      AppRoutesPath.KEY_DELTA,
      AppRoutesPath.AGRI_SIDE_HOURLY,
      AppRoutesPath.AGRI_SIDE_WEEKLY,
      AppRoutesPath.PV_SIDE_HOURLY,
      AppRoutesPath.PV_SIDE_WEEKLY,
      AppRoutesPath.AGRI_SIDE_WEEKLY_ACROSS,
      AppRoutesPath.PV_SIDE_WEEKLY_ACROSS,
      AppRoutesPath.HOURLY_HEAT,
      AppRoutesPath.CUMULATIVE_AGRI_PV_ACROSS,
      AppRoutesPath.HEAT_MAP,
      AppRoutesPath.DESIGN_EXPLORER,
      AppRoutesPath.PAGE_NOT_FOUND,
      AppRoutesPath.RUN_MANAGER,
      AppRoutesPath.USER_PROFILE,
    ]),
    admin: new Set([
      // AppRoutesPath.ADMIN,
      // AppRoutesPath.ADMIN_SIGN_IN,
      // AppRoutesPath.ADMIN_HOME,
      // AppRoutesPath.ADMIN_PV_DATABASE,
      // AppRoutesPath.ADMIN_CROP_DATABASE,
      // AppRoutesPath.ADMIN_SHADE_NET_DATABASE,
      // AppRoutesPath.ADMIN_SOIL_DATABASE,
      // AppRoutesPath.ADMIN_MANAGE_USERS,
    ]),
  };

  // Check if the role exists
  if (!routes[role]) {
    console.error(`Role ${role} does not exist.`);
    return false;
  }

  // Allow admin access to all routes except the ones specifically blocked
  if (role === "admin") {
    return path.startsWith("/admin") || !routes.user.has(path);
  }

  // Default to false if path is not explicitly allowed
  return routes[role].has(path) || false;
};

const constructFullPath = (location, params) => {
  let fullPath = location.pathname;
  const searchParams = new URLSearchParams(location.search);

  Object.keys(params).forEach((key) => {
    fullPath = fullPath.replace(`:${key}`, params[key]);
  });

  searchParams.forEach((value, key) => {
    fullPath += `${fullPath.includes("?") ? "&" : "?"}${key}=${value}`;
  });

  return fullPath;
};

export default function PrivateRoute({ children, path }) {
  const location = useLocation();
  const params = useParams();
  const userId = localStorage.getItem("apiToken");
  const roles = localStorage.getItem("roles") || [];

  const intendedRoute = constructFullPath(location, params);

  if (!userId) {
    localStorage.setItem("intendedRoute", intendedRoute);
    return <Navigate to={AppRoutesPath.LOGIN} />;
  } else {
    if (roles.includes("user") && roles.includes("admin")) return children;
    if (roles.includes("user")) {
      if (!allowedRoutes("user", path)) {
        localStorage.removeItem("intendedRoute");
        return <Navigate to={AppRoutesPath.PAGE_NOT_FOUND} />;
      }
    }
    if (roles.includes("admin")) {
      if (path === "/") return <Navigate to={AppRoutesPath.ADMIN_HOME} />;
      if (!allowedRoutes("admin", path)) {
        localStorage.removeItem("intendedRoute");
        return <Navigate to={AppRoutesPath.PAGE_NOT_FOUND} />;
      }
    }
  }
  localStorage.removeItem("intendedRoute");

  return children;
}
