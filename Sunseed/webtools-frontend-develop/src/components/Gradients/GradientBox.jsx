import React from 'react'
import styled from 'styled-components'

export default function GradientBox({ stops = [], height = "10px", width = "100%" }) {
    const gradientStyle = {
        width: width,
        height: height, /* Make gradient-box take the full height and width */
        borderRadius: '4px',
        background: `linear-gradient(to right, ${stops
            .map(stop => `${stop.color} ${stop.percentage}%`)
            .join(', ')})`,
    };

    return (
        <Container className="gradient-container" >
            <div style={gradientStyle}>
                {/* Add percentage markers dynamically */}
                {/* {stops.map((stop, index) => (
                    <div
                        key={index}
                        className="marker"
                        style={{ left: `${stop.percentage}%` }}
                    >
                        {stop.percentage}%
                    </div>
                ))} */}
            </div>
        </Container>
    );
}


const Container = styled.div`
  width: 100%;
  height: 100%;

  /* .gradient-box {
    width: 100%;
    height: 10px;  
    position: relative;
  }

  .marker {
    position: absolute;
    top: -20px; 
    transform: translateX(-50%); 
    color: black;
    font-size: 12px;
    font-weight: bold;
  } */
`;