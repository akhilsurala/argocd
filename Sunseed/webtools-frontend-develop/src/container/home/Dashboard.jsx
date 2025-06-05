import React from "react";
import { Grid } from "@mui/material";
import { styled } from "styled-components";

import { FormattedMessage } from "react-intl";
import messages from "./messages";
import CustomCard from "../../components/CustomCard/CustomCard";

import sim from "../../assets/apv-sim.svg";
import manage from "../../assets/apv-manage.svg";
import control from "../../assets/apv-control.svg";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath } from "../../utils/constant";

const Dashboard = () => {
  const navigate = useNavigate();
  const onCardBtnClick = () => {

    navigate(AppRoutesPath.APV_SIM);
  };

  return (
    <Container>

      <StyledGrid container spacing={3}>
        <GridItem item md={3}>
          <CustomCard
            title={<FormattedMessage {...messages.apvSim} />}
            subtitle={<FormattedMessage {...messages.apvSimOne} />}
            imgIcon={sim}
            handleClick={onCardBtnClick}
          />
        </GridItem>
        {/* <GridItem item md={3}>
          <CustomCard
            title={<FormattedMessage {...messages.apvControl} />}
            subtitle={<FormattedMessage {...messages.apvControlOne} />}
            imgIcon={manage}
            handleClick={onCardBtnClick}
          />
        </GridItem>
        <GridItem item md={3}>
          <CustomCard
            title={<FormattedMessage {...messages.apvManage} />}
            subtitle={<FormattedMessage {...messages.apvManageOne} />}
            imgIcon={control}
            handleClick={onCardBtnClick}
          />
        </GridItem> */}
      </StyledGrid>
    </Container>
  );
};

export default Dashboard;

const Container = styled.div`
  color: #ffffff;
  width: inherit;
  height: inherit;
`;

const StyledGrid = styled(Grid)`
  && {
    /* gap: 45px; */
    /* margin-left: -3rem; */
    width: 100%;
    padding-top: 1rem;

    @media screen and (max-width: 1200px) {
      /* gap: 30px; */
    }
  }
`;

const GridItem = styled(Grid)`
  && {
    padding-left: 0;
    width: calc(33.33% - 30px);
    max-width: calc(33.33% - 30px);
    flex: calc(33.33% - 30px);

    @media screen and (max-width: 1200px) {
      width: calc(50% - 20px);
      max-width: calc(50% - 20px);
      flex: calc(50% - 20px);
    }

    @media screen and (max-width: 768px) {
      width: 100%;
      max-width: 100%;
      flex: 100%;
    }
  }
`;
