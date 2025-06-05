import React from "react";
import styled from "styled-components";

const IndexPage = styled.div`
  margin-top: 50px;
  padding: 20px;

  h2 {
    font-size: 28px;
    margin: 0;
    font-family: Courier New;
    font-size: "16px";
    font-weight: "normal";
    color: "#0f4761";
 
    margin-bottom: 20px;
  }

  ul {
    list-style-type: none;
    padding: 0;
    font-size: 16px;
  }

  li {
    margin: 10px 0;
  }
`;

const IndexContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  font-family: Arial, sans-serif;
`;

const Text = styled.div`
  white-space: nowrap;
  color: #000 !important;
  margin-left: ${(props) =>
    props.marginLeft || 0}px; /* Adjust margin based on level */
`;

const Dots = styled.div`
  flex: 1;
  border-bottom: 3px dotted black;
  margin: 0 10px;
`;

const renderToc = (tocData, level = 0) => {
  const levelMargin = level * 20;
  return tocData.map((item, index) => (
    <li key={index}>
      <IndexContainer>
        <Text marginLeft={levelMargin}>{item.title}</Text>
        <Dots />
        <Text>{item.page}</Text>
      </IndexContainer>
      {item.subsections && <ul>{renderToc(item.subsections, level + 1)}</ul>}
    </li>
  ));
};

export default function IndexPageComponent() {
  const tocData = [
    { title: "TERMS OF USE", page: 2 },
    { title: "1.0 KEY PROJECT PARAMETERS", page: 5 },
    { title: "2.0 SIMULATION INPUTS", page: 5 },
    { title: "3.0 3D VISUALIZATION OF SYSTEMS", page: 6 },
    { title: "4.0 METEOROLOGY PARAMETERS", page: 7 },
    {
      title: "5.0 KEY OUTPUTS FOR EACH RUN",
      page: 11,
      subsections: [
        { title: "5.1 HOURLY CARBON ASSIMILATION/PLANT", page: 11 },
        { title: "5.2 HOURLY AVERAGE LEAF TEMP", page: 12 },
      ],
    },
    {
      title: "6.0 KEY OUTPUTS ACROSS RUNS",
      page: 13,
      subsections: [
        { title: "CUMULATIVE CARBON ASSIMILATION/PLANT", page: 13 },
        { title: "CUMULATIVE CARBON ASSIMILATION/GROUND AREA", page: 14 },
        {
          title: "CUMULATIVE DC ENERGY GENERATION/KW INSTALLED POWER",
          page: 15,
        },
        { title: "CUMULATIVE TRANSPIRATION RATES/PLANT", page: 16 },
      ],
    },
    { title: "YEARLY PV REVENUE/MW", page: 17 },
    { title: "YEARLY AGRI PROFITS/ACRE", page: 18 },
    { title: "YEARLY TOTAL REVENUE/ACRE", page: 18 },
  ];

  return (
    <IndexPage>
      <h3 style={{
        margin: 0,
        fontFamily: "Courier New",
        fontWeight: "normal",
        color: '#0f4761',
      }}>Contents</h3>
      <ul>{renderToc(tocData)}</ul>
    </IndexPage>
  );
}
