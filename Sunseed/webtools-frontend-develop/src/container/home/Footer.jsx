import { Typography } from "@mui/material";
import styled from "styled-components";



const Footer = () => {
  const year = new Date().getFullYear();
  return (
    <FooterTypography>
      @ SunSeed APV {year} | All Rights Reserved
    </FooterTypography>
  );
};

export default Footer;

const FooterTypography = styled(Typography)`
 && {
  width: 100%;
  position: fixed;
  bottom: 0;
  left: 0;
  height: 49px;
  background: #ffffff;
  color: #182c2b;
  justify-content: center;
  display: flex;
  align-items: center;
 }
`;
