import Header from '../home/Header'
import React from 'react';
import styled from 'styled-components';

import logo from "../../assets/logo.svg";
import CustomTabs from './CustomTabs';
import { Box, Stack } from '@mui/material';
import Footer from '../home/Footer';
import Dashboard from '../home/Dashboard';
import { Outlet } from 'react-router-dom';

export default function WrapperForDashboard() {
    return (
        <Container>

            <Header />
            <CustomTabs />

            <Footer />
        </Container>
    )
}


const Container = styled.div`
width: inherit;
height: inherit ;
/* padding-top:65px;
padding-bottom:65px; */

`;