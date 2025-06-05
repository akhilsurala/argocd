// MonthGrid.jsx
import React, { useEffect, useState } from 'react';
import styled from 'styled-components';

const MonthContainer = styled.div`
  display: grid;
  grid-row: 1;
  gap: 8px;
  grid-template-columns: repeat(5, 1fr); /* Adjust this based on the number of weeks per month */
  border-right: 1px solid #ccc;

`;

const MonthTitle = styled.h5`


  grid-column: span 5;
  margin-bottom: 8px;
  color: #474F50;
  text-align: center;
`;

const WeekButton = styled.button`
  outline: none;
  font-weight: 500;

  grid-row: 2;
  grid-column: span 1; // Each week occupies one column
  padding: 8px 12px;
  color: #474F50;
  background-color: #fff;
  border-radius: 4px;

  cursor: pointer;
  text-align: center;

  &:hover {
    background-color: #d0e0ff;
  }
`;

const MonthGrid = ({ weekIntervals, months, clickedButton, setClickedButton, weekDataMaps, monthWeekArray, setMonthWeekArray }) => {


    useEffect(() => {
        if (weekDataMaps) {
            const sortedKeys = [...new Set(Object.keys(weekDataMaps).sort((a, b) => parseInt(a.slice(1)) - parseInt(b.slice(1))))];

            const [firstKey] = sortedKeys;
            setClickedButton(firstKey)
        }
    }, [months, weekDataMaps])

    useEffect(() => {
        setMonthWeekArray((months.map((month, index) => { return month.weeks })).flat())
    }, [months])

    const handleClick = (event, clickedItem) => {
        if ((clickedItem in weekDataMaps)) {

            // console.log("clicking", weekDataMaps[clickedItem])
            setClickedButton(clickedItem);
        }
    };
    return (
        <>
            {months.map((month, index) => (
                <MonthContainer style={{ gridColumn: `span ${month.weeks.length}` }} key={`${month.name}-${index}`}>
                    <MonthTitle  >{month.name}</MonthTitle>

                </MonthContainer>
            ))}
            {monthWeekArray.map((week, index) => (
                <WeekButton style={{
                    outline: 'none',
                    border: 'none',
                    whiteSpace: 'nowrap',
                    fontSize: '14px',
                    backgroundColor: clickedButton === week ? '#53988E' : '#fff',
                    color: clickedButton === week ? 'white' : weekDataMaps && week in weekDataMaps ? '#000000' : '#b0b4b4',
                    borderRadius: '20px',
                }} key={week} onClick={(e) => handleClick(e, week)}>
                    {`W${weekIntervals[index]}`}
                </WeekButton>
            ))}

        </>
    );
};

export default MonthGrid;
