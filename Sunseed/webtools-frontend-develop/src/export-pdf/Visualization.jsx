import React from "react";
import styled from "styled-components";
import { BASE_URL_FOR_DOWNLOAD } from "../api/config";
;

import logo from "../assets/sunseedLogo.svg";
export default function Visualization({ images }) {

  const renderImage = () => {
    return images.map((child, index) => {


      return <>
        {child.viewUrl &&
          <section
            style={{
              width: "400px",
              display: "flex",
              flexDirection: "column",
              justifyContent: "center",
              boxSizing: "border-box",
              padding: "2em",
            }}
          >
            <h5>{child.runName}</h5>

            <img
              src={BASE_URL_FOR_DOWNLOAD + child.viewUrl}
              alt={child.runName + " view"}
              style={{
                width: "400px",
                height: "380px",
              }}
            />
          </section>
        }

        {/* {index !== images.length - 1 && <PageBreak />} */}
      </>
    })
  }
  const Container = styled.div`

 
  `;
  return (
    <Container>
      <h3 style={{

        marginTop: '50px',
        fontFamily: "Courier New",
        fontWeight: "normal",
        color: '#0f4761',
      }}>3.0 3D VISUALIZATION OF SIMULATIONS</h3>

      {renderImage()}

    </Container>
  );
}
