import { createTheme } from "@mui/material";

export const LightTheme = createTheme({
  palette: {
    primary: {
      main: "#DB8C47",
      secondary: "#65AFA4",
      light: "#CBEFEA",
    },
    secondary: {
      main: "#53988E",
      dark: "#CC8C83",
      light: "#E6C6C1",
      faded: "#F0DBD8",
    },
    text: {
      main: "#474F50",
      light: "#474F5080",
      // secondary: '#434141',
    },
    background: {
      main: "#f9fafb",
      secondary: "#FFFFFF",
      faded:"#F4F5F7"
    },
    border:{
      main : '#E1D6C8',
      secondary:'#F9F6F5',
      light: "#E3E3E3"
    },
    fontFamily:{
      main : "Montserrat",
    },
    dropdown:{
      main:"#AD2B2B1F"
    },
    table:{
      main:"#252727"
    },
    miniCard:{
      main: '#fdf8f4',
      secondary: '#f2f7f6'
    }
  },
});
