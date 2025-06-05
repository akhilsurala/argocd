import React from "react";

import logo from "../assets/sunseedLogo.svg";
import styled from "styled-components";
import dayjs from "dayjs";
export default function FirstPage({ projectDetails }) {
  const FirstPage = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 100vh;
    text-align: center;
    page-break-after: always;

    .mid {
      display: flex;
      flex-direction: column;
      align-items: start;
    }
    .bottom {
      display: flex;
      flex-direction: column;
      align-items: start;
    }

    h1 {
      color: #cb3737;
      margin: 0px;
      font-size: 36px;
      margin-bottom: 10px;
    }

    p {
      margin: 0px;
      font-size: 18px;
      margin-top: 10px;
    }
  `;
  const BodyText = styled.div`
h5,
p {
  margin: 0;
  font-family: "Courier New";
  font-size: 11px;
  font-weight: normal;
  color: #000 !important;
}
`;


  return (
    <FirstPage>

      <div></div>
      <BodyText>
        <div className="mid">
          <h1>APVSIM v1.0.3 (to think of a name)</h1>
          <p>DETAILED PROJECT REPORT</p>
          <p>PROJECT NAME – {projectDetails?.projectName}</p>
          <p>LOCATION - {projectDetails?.latitude} , {projectDetails?.longitude} </p>
          <p>DATE: {dayjs().format("DD MMMM YYYY")}</p>
        </div>

      </BodyText>
      <div className="bottom">
        <p>DEVELOPED BY:</p>
        <img
          src={logo}
          alt="Sunseed logo"
          style={{
            width: "200px",
            height: "30px",
          }}
        />
      </div>
    </FirstPage>
  );
}
