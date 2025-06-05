// CropCycle.jsx
import React from 'react';
import styled from 'styled-components';

const CropCycleContainer = styled.div`
  background-color: #fff8c5;
  border: 2px solid #ffbb33;
  color: #333;
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  font-weight: bold;
  grid-column: ${({ startWeek, endWeek }) => `${startWeek} / ${endWeek + 1}`};
  
`;

const CropCycle = ({ title, startWeek, endWeek }) => {
    return (
        <CropCycleContainer startWeek={startWeek} endWeek={endWeek}>
            {title}
        </CropCycleContainer>
    );
};

export default CropCycle;
