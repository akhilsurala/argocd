
import * as React from 'react';
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
import Header from '../home/Header';

import logoWhite from "../../assets/logoWhite.svg";
import { CustomSvgIconForAward, CustomSvgIconForBook, CustomSvgIconForMyApp } from '../dashboard/CustomSvgIcon';
import RecentProjectScreen from '../apv-sim/RecentProjectScreen';
import { Outlet, useNavigate, useParams } from 'react-router-dom';
import Footer from '../home/Footer';
import VerticalStepper from './VerticalStepper';
import { AppRoutesPath } from '../../utils/constant';
import { getProjects } from '../../api/userProfile';
import { ProjectProvider } from '../ProjectContext';

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

export default function WrapperForMainScreen() {
  const { projectId } = useParams();

  const navigate = useNavigate();
  const [open, setOpen] = React.useState(false);
  const [selectedIndex, setSelectedIndex] = React.useState(0);
  const [activeTab, setActiveTab] = React.useState("");

  const urlSplit = window.location.pathname.split("/");

  React.useEffect(() => {
    if (urlSplit.includes('project')) {
      setActiveTab(AppRoutesPath.PROJECT)
    }
    else {
      setActiveTab("")
    }

  }, [window.location.pathname])
  
  React.useEffect(() => {
    fetchProjectList();
  }, []);

  const fetchProjectList = ()=>{
    if (urlSplit.includes('post-processor')){
      getProjects()
      .then((response)=>{
        const data = response.data.data;
        const projectDetails = data.find((data)=>  data.projectId == projectId);
        localStorage.setItem(
          "post-processing-runs",
          JSON.stringify(projectDetails.runIds)
        );
      })
      .catch((error)=>{
        console.log(error);
      })
    }
  }


  const handleItemClick = (tabName) => {
    setActiveTab(AppRoutesPath.PROJECT);
  };

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
          onClick={() => { setSelectedIndex(index) }}

          sx={{

            minHeight: 48,
            justifyContent: open ? 'initial' : 'center',
            px: 2.5,

            "&:hover": {
              backgroundColor: `${selectedIndex === index ? '#EC9954' : "#DB8C47"}`,
            },
            "& .MuiTypography-body1.MuiListItemText-primary": { fontSize: '14px', fontFamily: 'Montserrat', fontWeight: `${selectedIndex === index ? 700 : 500}` },
            borderRadius: `${selectedIndex === index ? "10px" : ""}`,
            backgroundColor: `${selectedIndex === index ? '#EC9954' : ""}`,
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
        {index === 0 &&
          <VerticalStepper onClick={handleItemClick} activeTab={activeTab} open={open} />}
      </ListItemStyled >)

    }
    )
  }

  return (
    <ProjectProvider projectId={projectId}>
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
                AppRoutesPath.DEFAULT)} />}

          </DrawerHeader>
          <Divider />
          <List>
            {getList([{ key: 'apv-sim', msg: 'APV Sim', Component: CustomSvgIconForMyApp },
            // { key: 'apv-simab', msg: 'License Management', Component: CustomSvgIconForAward },
            // { key: 'apv-sima', msg: 'Learning Resources', Component: CustomSvgIconForBook }
          ])
            }

          </List>

        </Drawer>
        <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
          <DrawerHeader />
          <Outlet />
        </Box>
      </Box>
    </ProjectProvider>
  );
}



const ListItemStyled = styled(ListItem)`
  cursor: pointer;
  && {
    padding: 10px 5px 10px 10px;
  }
`;

