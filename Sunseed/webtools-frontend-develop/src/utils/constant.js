import { BASE_URL_FOR_DOWNLOAD } from "../api/config";
export const AppRoutesPath = {
  DEFAULT: "/",
  SIGN_UP: "/sign-up",
  LOGIN: "/sign-in",
  FORGOT_PASSWORD: "/forgot-password",
  CREATE_PROJECT: "/create-project",
  APV_SIM: "/apv-sim",
  PRE_PROCESSOR: "/apv-sim/project/:projectId/pre-processor",
  POST_PROCESSOR: "/apv-sim/project/:projectId/post-processor",

  THREE_D_VIEW: "/apv-sim/project/:projectId/post-processor/3d-view/:runId",
  KEY_DELTA: "/apv-sim/project/:projectId/post-processor/key-delta-graphs",
  AGRI_SIDE_HOURLY:
    "/apv-sim/project/:projectId/post-processor/agri-side-hourly/:runId",
  AGRI_SIDE_WEEKLY:
    "/apv-sim/project/:projectId/post-processor/agri-side-weekly/:runId",
  PV_SIDE_HOURLY:
    "/apv-sim/project/:projectId/post-processor/pv-side-hourly/:runId",
  PV_SIDE_WEEKLY:
    "/apv-sim/project/:projectId/post-processor/pv-side-weekly/:runId",
  AGRI_SIDE_WEEKLY_ACROSS:
    "/apv-sim/project/:projectId/post-processor/agri-side-weekly-across",
  PV_SIDE_WEEKLY_ACROSS:
    "/apv-sim/project/:projectId/post-processor/pv-side-weekly-across",
  HOURLY_HEAT: "/apv-sim/project/:projectId/post-processor/hourly-heat/:runId",
  CUMULATIVE_AGRI_PV_ACROSS:
    "/apv-sim/project/:projectId/post-processor/cumulative-agri-pv-across",
  HEAT_MAP: "/apv-sim/project/:projectId/post-processor/heat-map/:runId",
  DESIGN_EXPLORER: "/apv-sim/project/:projectId/post-processor/design-explorer",
  RUN_MANAGER: "/apv-sim/project/:projectId/run-manager",
  COMP_TEST: "/comp-test",
  PROJECT: "/project",
  WILD_CARD: "*",
  PAGE_NOT_FOUND: "*",
  ADMIN: "/admin",
  ADMIN_HOME: "/admin/home",
  ADMIN_SIGN_IN: "/admin/sign-in",
  ADMIN_PV_DATABASE: "/admin/pv-modules",
  ADMIN_PV_DATABASE_NEW: "/admin/pv-module/new",
  ADMIN_PV_DATABASE_EDIT: "/admin/pv-module/edit/:id",
  ADMIN_PV_DATABASE_VIEW: "/admin/pv-module/view/:id",
  USER_PROFILE: "/user-profile",
  ADMIN_PROFILE: "/admin/user-profile",
  ADMIN_ARTICLE: "/admin/articles",
  ADMIN_ARTICLE_NEW: "/admin/article/new",
  ADMIN_ARTICLE_EDIT: "/admin/article/edit/:id",
  ADMIN_ARTICLE_VIEW: "/admin/article/view/:id",
  DASHBOARD_ARTICLE_VIEW: "/article/:id",
  ADMIN_STATIC_PAGE: "/admin/static-page",
  ADMIN_STATIC_PAGE_NEW: "/admin/static-page/new",
  ADMIN_STATIC_PAGE_EDIT: "/admin/static-page/edit/:id",
  ADMIN_STATIC_PAGE_VIEW: "/admin/static-page/view/:id",
  ADMIN_CROP_DATABASE: "/admin/crops",
  ADMIN_CROP_DATABASE_NEW: "/admin/crop/new",
  ADMIN_CROP_DATABASE_EDIT: "/admin/crop/edit/:id",
  ADMIN_CROP_DATABASE_VIEW: "/admin/crop/view/:id",

  ADMIN_SOIL_DATABASE: "/admin/soils",
  ADMIN_SOIL_DATABASE_NEW: "/admin/soil/new",
  ADMIN_SOIL_DATABASE_EDIT: "/admin/soil/edit/:id",
  ADMIN_SOIL_DATABASE_VIEW: "/admin/soil/view/:id",

  ADMIN_USER_DATABASE: "/admin/users",
  ADMIN_USER_DATABASE_NEW: "/admin/user/new",
  ADMIN_USER_DATABASE_EDIT: "/admin/user/edit/:id",
  ADMIN_USER_DATABASE_VIEW: "/admin/user/view/:id",

  ADMIN_MODE_OF_PV_OPERATION_DATABASE: "/admin/mode-of-pv-operations",
  ADMIN_MODE_OF_PV_OPERATION_DATABASE_NEW: "/admin/mode-of-pv-operation/new",
  ADMIN_MODE_OF_PV_OPERATION_DATABASE_EDIT:
    "/admin/mode-of-pv-operation/edit/:id",
  ADMIN_MODE_OF_PV_OPERATION_DATABASE_VIEW:
    "/admin/mode-of-pv-operation/view/:id",

  ADMIN_MODULE_CONFIGURATION_DATABASE: "/admin/module-configurations",
  ADMIN_MODULE_CONFIGURATION_DATABASE_NEW: "/admin/module-configuration/new",
  ADMIN_MODULE_CONFIGURATION_DATABASE_EDIT:
    "/admin/module-configuration/edit/:id",
  ADMIN_MODULE_CONFIGURATION_DATABASE_VIEW:
    "/admin/module-configuration/view/:id",

  ADMIN_PROTECTION_LAYER_DATABASE: "/admin/protection-layers",
  ADMIN_PROTECTION_LAYER_DATABASE_NEW: "/admin/protection-layer/new",
  ADMIN_PROTECTION_LAYER_DATABASE_EDIT: "/admin/protection-layer/edit/:id",
  ADMIN_PROTECTION_LAYER_DATABASE_VIEW: "/admin/protection-layer/view/:id",

  ADMIN_TYPE_OF_IRRIGATION_DATABASE: "/admin/type-of-irrigations",
  ADMIN_TYPE_OF_IRRIGATION_DATABASE_NEW: "/admin/type-of-irrigation/new",
  ADMIN_TYPE_OF_IRRIGATION_DATABASE_EDIT: "/admin/type-of-irrigation/edit/:id",
  ADMIN_TYPE_OF_IRRIGATION_DATABASE_VIEW: "/admin/type-of-irrigation/view/:id",

  ADMIN_SHADE_NET_DATABASE: "/admin/shade-net-database",

  DOWNLOAD_PDF: "/download-pdf",
};

export const sampleOpticsFileUrl = "/sampleOpticsFile.xlsx";
export const ModuleMaskPatternDropdown = [
  {
    "id": "10",
    "name": "10",
  },
  {
    "id": "110",
    "name": "110",
  },
  {
    "id": "1110",
    "name": "1110",
  }
];

export const typeOfModules = [
  {
    "id": "L",
    "name": "L",
  },
  {
    "id": "P",
    "name": "P",
  },
  {
    "id": "L'",
    "name": "L'",
  },
  {
    "id": "P'",
    "name": "P'",
  }
];

export const typeOfPVConfigurations = [
  {
    "id": "Fixed Tilt",
    "name": "Fixed Tilt",
  },
  {
    "id": "Single Axis Tracking",
    "name": "Single Axis Tracking",
  },
];

export const preProcessorRoute = (projectId) => {
  return `/apv-sim/project/${projectId}/pre-processor`;
};

export const runManagerRoute = (projectId) => {
  return `/apv-sim/project/${projectId}/run-manager`;
};

export const postProcessorRoute = (projectId) => {
  return `/apv-sim/project/${projectId}/post-processor`;
};

export const keyDeltaGraphRoute = (projectId) => {
  return `/apv-sim/project/${projectId}/post-processor/key-delta-graphs`;
};

export const agriSideHourlyRoute = (projectId, runId, graphType) => {
  return `/apv-sim/project/${projectId}/post-processor/agri-side-hourly/${runId}?graphType=${graphType}`;
};

export const agriSideWeeklyRoute = (projectId, runId, graphType) => {
  return `/apv-sim/project/${projectId}/post-processor/agri-side-weekly/${runId}?graphType=${graphType}`;
};

export const pvSideWeeklyRoute = (projectId, runId, graphType) => {
  return `/apv-sim/project/${projectId}/post-processor/pv-side-weekly/${runId}?graphType=${graphType}`;
};

export const pvSideHourlyRoute = (projectId, runId, graphType) => {
  return `/apv-sim/project/${projectId}/post-processor/pv-side-hourly/${runId}?graphType=${graphType}`;
};

export const agriSideWeeklyAcrossRoute = (projectId, graphType) => {
  return `/apv-sim/project/${projectId}/post-processor/agri-side-weekly-across?graphType=${graphType}`;
};

export const pvSideWeeklyAcrossRoute = (projectId, graphType) => {
  return `/apv-sim/project/${projectId}/post-processor/pv-side-weekly-across?graphType=${graphType}`;
};

export const hourlyHeatRoute = (projectId, runId, graphType) => {
  return `/apv-sim/project/${projectId}/post-processor/hourly-heat/${runId}?graphType=${graphType}`;
};

export const cumulativeAgriPVAcrossRoute = (projectId, graphType) => {
  return `/apv-sim/project/${projectId}/post-processor/cumulative-agri-pv-across?graphType=${graphType}`;
};

export const heatMapRoute = (projectId, runId) => {
  return `/apv-sim/project/${projectId}/post-processor/heat-map/${runId}`;
};
export const threeDviewRoute = (projectId, runId) => {
  return `/apv-sim/project/${projectId}/post-processor/3d-view/${runId}`;
};

export const designExplorerGraphRoute = (projectId) => {
  return `/apv-sim/project/${projectId}/post-processor/design-explorer`;
};

export const editStaticPage = (id) => {
  return `/admin/static-page/edit/${id}`;
};
export const viewStaticPage = (page, id) => {
  return `/admin/${page}/view/${id}`;
};

export const editPvModule = (id) => {
  return `/admin/pv-module/edit/${id}`;
};

export const editCrop = (id) => {
  return `/admin/crop/edit/${id}`;
};

export const editSoil = (id) => {
  return `/admin/soil/edit/${id}`;
};

export const editModeOfPvOperation = (id) => {
  return `/admin/mode-of-pv-operation/edit/${id}`;
};

export const editModuleConfiguration = (id) => {
  return `/admin/module-configuration/edit/${id}`;
};

export const editTypeOfIrrigation = (id) => {
  return `/admin/type-of-irrigation/edit/${id}`;
};

export const editProtectionLayer = (id) => {
  return `/admin/protection-layer/edit/${id}`;
};

export const editUser = (id) => {
  return `/admin/user/edit/${id}`;
};

export const downloadURL = (fileName) => {
  return `${BASE_URL_FOR_DOWNLOAD}${fileName}`;
};

export const getCurrentPath = () => {
  return window.location.href
    .split("/")
    .filter(Boolean)
    .slice(-1)[0]
    ?.toLowerCase();
};

export const getApvToggle = (val) => {
  if (val === "Only PV") return "Only PV";
  else if (val === "Only Agri") return "Only Agri";
  else if (val === "Only Pv") return "Only PV";
  else if (val === "Only Agri") return "Only Agri";
  else return "APV";
};

export const months = [
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December",
];

export const changeDateFormat = (d) => {
  if (!d) return;
  const date = new Date(d);
  const day = date.getDate();
  const month = date.getMonth();
  const year = date.getFullYear();

  // Get hours and convert to 12-hour format
  let hours = date.getHours();
  const period = hours >= 12 ? "pm" : "am";
  hours = hours % 12 || 12; // Convert to 12-hour format and handle 0 as 12

  // Format: "Date 1pm"
  return `${day} ${months[month]} ${year}, ${hours}${period}`;
};

export const ADMIN_STATIC_PAGE = {
  id: "ID",
  title: "Title",
  createdAt: "Created At",
  updatedAt: "Updated At",
  action: "Action",
};

export const ADMIN_PV_MODULE = {
  id: "ID",
  pdc0: "PDC0",
  moduleType: "Model Name",
  length: "Length",
  width: "Width",
  createdAt: "Created At",
  updatedAt: "Updated At",
  action: "Action",
};

export const ADMIN_CROP = {
  id: "ID",
  name: "Name",
  createdAt: "Created At",
  updatedAt: "Updated At",
  action: "Action",
};

export const ADMIN_SOIL = {
  id: "ID",
  soilName: "Name",
  createdAt: "Created At",
  updatedAt: "Updated At",
  action: "Action",
};

export const ADMIN_MODULE_CONFIGURATION = {
  id: "ID",
  name: "Name",
  numberOfModules: "Number of Modules",
  typeOfModule: "Type of Module",
  ordering: "Order",
  createdAt: "Created At",
  updatedAt: "Updated At",
  action: "Action",
};

export const ADMIN_MODE_OF_PV_OPERATION = {
  id: "ID",
  modeOfOperation: "Name",
  createdAt: "Created At",
  updatedAt: "Updated At",
  action: "Action",
};

export const ADMIN_PROTECTION_LAYER = {
  protectionLayerId: "ID",
  protectionLayerName: "Name",
  createdAt: "Created At",
  updatedAt: "Updated At",
  action: "Action",
};

export const ADMIN_TYPE_OF_IRRIGATION = {
  id: "ID",
  irrigationType: "Name",
  createdAt: "Created At",
  updatedAt: "Updated At",
  action: "Action",
};

export const ADMIN_USER = {
  id: "ID",
  firstName: "First Name",
  lastName: "Last Name",
  emailId: "Email",
  roles: "Type",
  action: "Action",
};

export const GRAPH_DOWNLOAD_STATUS = {
  toDownload: "TO_DOWNLOAD",
  downloading: "DOWNLOADING",
  downloaded: "DOWNLOADED",
};

export const GRAPH_TYPE = {
  CUMULATIVE_CARBON_ASSIM_PER_PLANT: "cumulative-carbon-assim-per-plant",
  CUMULATIVE_CARBON_ASSIM_PER_GROUND: "cumulative-carbon-assim-per-ground",
  LIGHT_ABSORBED_PER_M2_GROUND: "light-absorbed-per-m2-ground",
  TRANSPIRATION_PER_PLANT: "transpiration-per-plant",
  TRANSPIRATION_PER_GROUND: "transpiration-per-ground",
  CUMULATIVE_TRANSPIRATION_PER_PLANT: "cumulative-transpiration-per-plant",
  CUMULATIVE_TRANSPIRATION_PER_GROUND: "cumulative-transpiration-per-ground",
  CUMULATIVE_ENERGY_GENERATION: "cumulative-energy-generation",
  AVERAGE_BIFACIAL_GAIN: "average-bifacial-gain",
  RATE_OF_CARBON_ASSIM_PER_PLANT: "rate-of-carbon-assim-per-plant",
  RATE_OF_CARBON_ASSIM_PER_GROUND: "rate-of-carbon-assim-per-ground",
  AVERAGE_LEAF_TEMPERATURE: "average-leaf-temperature",
  PERCENT_SUNLIT_LEAVES_PER_PLANT: "percent-sunlit-leaves-per-plant",
  PERCENT_SUNLIT_LEAVES_PER_GROUND_AREA:
    "percent-sunlit-leaves-per-ground-area",
  LIGHT_ABSORBED_PER_PLANT: "light-absorbed-per-plant",
  PENETRATION_EFFICIENCY_METRIC: "penetration-efficiency-metric",
  PERCENT_OF_SUNLIT_LEAVES_SATURATED_PER_PLANT:
    "percent-of-sunlit-leaves-sturated-per-plant",
  SATURATION_EXTENT_PLANT: "saturation-extent-plant",
  PPFD: "ppfd",
  CUMULATIVE_PPFD_DLI: "cumulative-ppfd-dli",
  DAILY_AIR_TEMP_HUMIDITY_DIRECT_DIFFUSE_RAD: "daily-air-temp",
  BI_WEEKLY_CUMULATIVE_CARBON_ASSIM_PER_PLANT:
    "bi-weekly-cumulative-carbon-assim-per-plant",
  BI_WEEKLY_CUMULATIVE_CARBON_ASSIM_PER_GROUND_AREA:
    "bi-weekly-cumulative-carbon-assim-per-ground-area",
  DAILY_DLI: "daily-dli",
  HOURLY_BIFACIAL_GAIN: "hourly-bifacial-gain",
  HOURLY_DC_POWER: "hourly-dc-power",
  SYSTEM_ECONOMICS_PV: "pv-revenue-per-mega-watt",
  SYSTEM_ECONOMICS_AGRI: "agri-revenue",
  TOTAL_TRANSPIRATION: "total-transpiration",
  HOURLY_TEMPERATURE_ACROSS_THE_YEAR: "hourly-temperature-across-the-year",
  HOURLY_CARBON_ASSIMILATION_ACROSS_THE_YEAR:
    "hourly-carbon-assimilation-across-the-year",
  BIFACIAL_GAIN: "bifacial-gain",
};
// {
//       label: "PV Revenue Per Mega Watt",
//       value: "pv-revenue-per-mega-watt",
//       unit: "",
//     },
//     {
//       label: "PV Revenue Per Acre",
//       value: "pv-revenue-per-acre",
//       unit: "",
//     },
//     {
//       label: "Agri Revenue",
//       value: "agri-revenue",
//       unit: "",
//     },
//     {
//       label: "Total Revenue",
//       value: "total-revenue",
//       unit: "",
//     },
//     {
//       label: "Profit",
//       value: "profit",
//       unit: "",
//     },