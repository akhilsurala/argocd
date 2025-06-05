
import React, { useEffect, useState } from "react";

import { styled, useTheme } from '@mui/material/styles';
import Box from '@mui/material/Box';
import MuiDrawer from '@mui/material/Drawer';
import MuiAppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import List from '@mui/material/List';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import InboxIcon from '@mui/icons-material/MoveToInbox';
import MailIcon from '@mui/icons-material/Mail';

import logoWhite from "../../assets/logoWhite.svg";
import { CustomSvgIconForAward, CustomSvgIconForBook, CustomSvgIconForMyApp } from '../../container/dashboard/CustomSvgIcon';
import { Outlet, useNavigate, useParams } from 'react-router-dom';
import Footer from '../../container/home/Footer';
import VerticalStepper from '../../container/navigation/VerticalStepper';
import Header from '../../container/home/Header';
import { AppRoutesPath, getCurrentPath } from '../../utils/constant';
import { HomeIcon } from "../icons/HomeIcon";
import { PvIcon } from "../icons/PvIcon";
import { CropIcon } from "../icons/CropIcon";
import { ShadeNetIcon } from "../icons/ShadeNetIcon";
import { SoilIcon } from "../icons/SoilIcon";
import { ManageUserIcon } from "../icons/ManageUserIcon";
import { ModeOfPvOperationIcon } from "../icons/ModeOfPvOperationIcon";
import { ModuleConfigurationIcon } from "../icons/ModuleConfiguration";
import { TypeOfIrrigation } from "../icons/TypeOfIrrigation";
import { StaticPageIcon } from "../icons/StaticPageIcon";

const drawerWidth = 280;

const openedMixin = (theme) => ({
  width: drawerWidth,
  transition: theme.transitions.create('width', {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.enteringScreen,
  }),
  overflowX: 'hidden',
});

const closedMixin = (theme) => ({
  transition: theme.transitions.create('width', {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  overflowX: 'hidden',
  width: `calc(${theme.spacing(7)} + 1px)`,
  [theme.breakpoints.up('sm')]: {
    width: `calc(${theme.spacing(8)} + 1px)`,
  },
});

const DrawerHeader = styled('div')(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'flex-end',
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
}));

const AppBar = styled(MuiAppBar, {
  shouldForwardProp: (prop) => prop !== 'open',
})(({ theme, open }) => ({
  zIndex: theme.zIndex.drawer + 1,
  transition: theme.transitions.create(['width', 'margin'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  ...(open && {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  }),
}));

const Drawer = styled(MuiDrawer, { shouldForwardProp: (prop) => prop !== 'open' })(
  ({ theme, open }) => ({
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: 'nowrap',
    boxSizing: 'border-box',
    ...(open && {
      ...openedMixin(theme),
      '& .MuiDrawer-paper': openedMixin(theme),
    }),
    ...(!open && {
      ...closedMixin(theme),
      '& .MuiDrawer-paper': closedMixin(theme),
    }),
  }),
);

export default function AdminSideMenu() {

  const navigate = useNavigate();
  const [open, setOpen] = React.useState(false);
  const [selectedIndex, setSelectedIndex] = React.useState(0);
  const [activeTab, setActiveTab] = React.useState("Learning Resources");

  const currentPath = getCurrentPath();
  const fullPath = window.location.href.split("/");

  useEffect(() => {
    // if (currentPath === "home") setActiveTab("Dashboard");
    if (currentPath === "learning-resources" || fullPath.includes("learning-resource") ) setActiveTab("Learning Resources");
    if (currentPath === "pv-modules" || fullPath.includes("pv-module") ) setActiveTab("PV Database");
    if (currentPath === "mode-of-pv-operations" || fullPath.includes("mode-of-pv-operation") ) setActiveTab("Mode Of PV Operation");
    if (currentPath === "module-configurations" || fullPath.includes("module-configuration") ) setActiveTab("Module Configuration");
    if (currentPath === "protection-layers" || fullPath.includes("protection-layer") ) setActiveTab("Shade Net Database");
    if (currentPath === "type-of-irrigations" || fullPath.includes("type-of-irrigation") ) setActiveTab("Type Of Irrigation");
    if (currentPath === "crops" || fullPath.includes("crop") ) setActiveTab("Crop Database");
    if (currentPath === "soils" || fullPath.includes("soil") ) setActiveTab("Soil Database");
    if (currentPath === "users" || fullPath.includes("user") ) setActiveTab("Manage Users");
  }, [currentPath]);

  const handleItemClick = (tabName) => {
    setActiveTab(tabName);
    switch (tabName) {
      // case "Dashboard":
      //   navigate(AppRoutesPath.ADMIN_HOME);
      //   break;
      case "Learning Resources":
        navigate(AppRoutesPath.ADMIN_STATIC_PAGE);
        break;
      case "PV Database":
        navigate(AppRoutesPath.ADMIN_PV_DATABASE);
        break;
      case "Mode Of PV Operation":
        navigate(AppRoutesPath.ADMIN_MODE_OF_PV_OPERATION_DATABASE);
        break;
      case "Module Configuration":
        navigate(AppRoutesPath.ADMIN_MODULE_CONFIGURATION_DATABASE);
        break;
      case "Crop Database":
        navigate(AppRoutesPath.ADMIN_CROP_DATABASE);
        break;
      case "Soil Database":
        navigate(AppRoutesPath.ADMIN_SOIL_DATABASE);
        break;
      case "Type Of Irrigation":
        navigate(AppRoutesPath.ADMIN_TYPE_OF_IRRIGATION_DATABASE);
        break;
      case "Shade Net Database":
        navigate(AppRoutesPath.ADMIN_PROTECTION_LAYER_DATABASE);
        break;
      case "Manage Users":
        navigate(AppRoutesPath.ADMIN_USER_DATABASE);
        break;
    }
  };


  // const handleItemClick = (tabName) => {
  //   setActiveTab(AppRoutesPath.PROJECT);
  // };

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  const getList = (obj) => {
    return obj.map((object, index) => {
      const { msg, Component, key } = object;

      return (<ListItemStyled key={key} disablePadding sx={{ display: 'block' }}>
        <ListItemButton
          onClick={() => { handleItemClick(object?.key) }}

          sx={{

            minHeight: 48,
            justifyContent: open ? 'initial' : 'center',
            px: 2.5,

            "&:hover": {
              backgroundColor: `${activeTab === key ? '#EC9954' : "#DB8C47"}`,
            },
            "& .MuiTypography-body1.MuiListItemText-primary": { fontSize: '14px', fontFamily: 'Montserrat', fontWeight: `${activeTab === key ? 700 : 500}` },
            borderRadius: `${activeTab === key ? "10px" : ""}`,
            backgroundColor: `${activeTab === key ? '#EC9954' : ""}`,
            // color: '#fff', // Change text color on hover

          }}
        >

          <ListItemIcon
            sx={{
              minWidth: 0,
              mr: open ? 3 : 'auto',
              justifyContent: 'center',
            }}
          >
            <Component sx={{ color: '#fff' }} />


          </ListItemIcon>


          <ListItemText primary={msg} sx={{ opacity: open ? 1 : 0, color: '#fff', fontWeight: '600' }} />
        </ListItemButton>
        {/* {index === 0 &&
          <VerticalStepper onClick={handleItemClick} activeTab={activeTab} open={open} />} */}
      </ListItemStyled >)

    }
    )
  }

  return (
    <Box sx={{
      height: '100%',
      display: 'flex', "& .MuiDrawer-paper.MuiDrawer-paperAnchorLeft": {
        zIndex: '1201'
      }
    }}>
      {/* <CssBaseline /> */}
      <AppBar position="fixed" open={open}>

        <Header drawerWidth="65" drawerOpen={open} />
        <Footer />
      </AppBar>
      <Drawer variant="permanent" open={open}
        onMouseEnter={handleDrawerOpen}
        onMouseLeave={handleDrawerClose}
        sx={{
          '& .MuiPaper-root.MuiDrawer-paperAnchorLeft.MuiDrawer-paperAnchorDockedLeft': {
            background: '#DB8C47'
          }
        }}
      >
        <DrawerHeader style={{ justifyContent: 'center' }}>

          {open && <img src={logoWhite} alt="logo"
            style={{ cursor: "pointer" }}
            onClick={() => navigate(
              AppRoutesPath.ADMIN_HOME)} />}

        </DrawerHeader>
        <Divider />
        <List>
          {
            getList([
              // { key: "Dashboard", msg: "Dashboard", Component: HomeIcon },
              { key: "Learning Resources", msg: "Learning Resources", Component: StaticPageIcon },
              { key: "PV Database", msg: "PV Database", Component: PvIcon },
              { key: "Mode Of PV Operation", msg: "Mode Of PV Operation", Component: ModeOfPvOperationIcon },
              { key: "Module Configuration", msg: "Module Configuration", Component: ModuleConfigurationIcon },
              { key: "Crop Database", msg: "Crop Database", Component: CropIcon },
              { key: "Shade Net Database", msg: "Shade Net Database", Component: ShadeNetIcon },
              { key: "Soil Database", msg: "Soil Database", Component: SoilIcon },
              { key: "Type Of Irrigation", msg: "Type Of Irrigation", Component: TypeOfIrrigation },
              { key: "Manage Users", msg: "Manage Users", Component: ManageUserIcon },
            ])
          }
        </List>

      </Drawer>
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <DrawerHeader />
        <Outlet />
      </Box>
    </Box>
  );
}

const ListItemStyled = styled(ListItem)`
  cursor: pointer;
  && {
    padding: 10px 5px 10px 10px;
  }
`;

