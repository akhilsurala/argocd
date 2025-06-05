import React from "react";
import styled from "styled-components";
import SimTable from "./SimTable";
import HeatmapChart from "./HeatmapChart";
import HeatMap from "../container/apv-sim/postProcessor/graphs/HeatMap";
import { series } from "./heatmap";
import TabularView from "./TabularView";

export default function Metereaology({ outputs }) {
  const Container = styled.div`
    display: flex;
    flex-direction: column;
    align-items: start;
    text-align: start;
    justify-content: space-between;

   
  `;
  const PageBreak = styled.div`
    // border: 1px solid red;
    page-break-before: always;
    break-before: page;
  `;

  return (
    <Container>

      <PageBreak />

      <h3 style={{
        marginTop: '50px',
        fontFamily: "Courier New",
        fontWeight: "normal",
        color: '#0f4761',
      }}>4.0 METEREOLOGY PARAMETERS</h3>
      <h5 >4.1 HOURLY TEMPERATURE (°C)</h5>
      {/* <HeatmapChart /> */}
      {/* <HeatMap series={outputs[0].data} />
      <PageBreak /> */}

      <TabularView series={outputs[0].data} />
      <PageBreak />
      <h5>4.2 HOURLY RELATIVE HUMIDITY (%)</h5>
      {/* <HeatMap series={outputs[1].data} />
      <PageBreak /> */}

      <TabularView series={outputs[1].data} />
      <PageBreak />

      <h5>4.3 HOURLY DIRECT RADIATION (WATTS/M 2 )</h5>
      {/* <HeatMap series={outputs[2].data} />
      <PageBreak /> */}

      <TabularView series={outputs[2].data} />
      <PageBreak />

      <h5>4.4 HOURLY DIFFUSE RADIATION (WATTS/M 2 )</h5>
      {/* <HeatMap series={outputs[3].data} />
      <PageBreak /> */}

      <TabularView series={outputs[3].data} />
      <PageBreak />


    </Container>
  );
}
