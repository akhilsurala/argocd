import React, { useEffect, useState } from "react";

import Accordion from "@mui/material/Accordion";
import AccordionSummary from "@mui/material/AccordionSummary";
import AccordionDetails from "@mui/material/AccordionDetails";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

import { styled } from "styled-components";
import { useSelector } from "react-redux";

import DOMPurify from "dompurify";
import { getStaticPages } from "../../api/admin/staticPage";

const Documentation = () => {
  const [resources,setResources] = useState([]);

  useEffect(() => {
    
    getStaticPages()
    .then((res)=>{
        console.log(res);
        setResources(res?.data?.data)
    })
    .catch((error)=>{
        console.log(error);
    })
  }, []);


  return (
    <Container>
      {resources.map((resource, index) => (
        <CustomAccordion slotProps={{ transition: { unmountOnExit: true } }} key={index} >
          <AccordionSummary
            expandIcon={<ExpandMoreIcon />}
            aria-controls={`panel${index + 1}-content`}
            id={`panel${index + 1}-header`}
            sx={{
              fontFamily: 'Montserrat',
              fontSize: '16px',
              fontWeight: '600',
              lineHeight: '19.5px',
              textAlign: 'left',
              padding: '0px'

            }}
          >
            {resource.title}
          </AccordionSummary>
          <AccordionDetails sx={{
            fontFamily: 'Montserrat',
            fontSize: '14px',
            fontWeight: '500',
            lineHeight: '26px',
            textAlign: 'left',
            padding: '0px',

          }} >
            <div
              dangerouslySetInnerHTML={{
                __html: DOMPurify.sanitize(resource.description),
              }}
            />
          </AccordionDetails>
        </CustomAccordion>
      ))
      }
    </Container >
  );
};

// Custom Accordion styled component to remove borders
const CustomAccordion = styled(Accordion)`
  &.MuiAccordion-root {
    border-left: none;
    border-right: none;
    border-top: none;
    border-bottom: 1px solid #E3E3E3;
    box-shadow: none; /* Removes shadow if it looks like a border */
  }

  &.Mui-expanded {
    margin: 0;
  }

  &:before {
    display: none; /* Hides the default divider between accordions */
  }
    /* Remove the bottom border for the last accordion */
    &:last-child {
    border-bottom: none;
  }
`;

const Container = styled.div`
width: 75%;
padding: 30px;
height: 60vh;
overflow: auto;
top: 252px;
background-color: white;
left: 350px;
gap: 0px;
border-radius: 17px;
opacity: 0px;
&::-webkit-scrollbar {
    width: 6px; // Width of the scrollbar
  }

  &::-webkit-scrollbar-track {
    background: #D5D5D5; // Track color
  }

  &::-webkit-scrollbar-thumb {
    background-color: ${({ theme }) => theme.palette.primary.main}; // Thumb color
    border-radius: 8px; // Rounded edges
  }
`;

export default Documentation;
