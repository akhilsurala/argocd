// index.jsx
import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import CropCycle from './CropCycle';
import MonthGrid from './MonthGrid';
import { useDispatch, useSelector } from 'react-redux';
import { setSimulationGroundArea, setUrlDetails, setUrlDetailsWeeksTime } from '../../redux/action/postProcessorAction';

const TimelineContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(52, 1fr); /* Assuming 12 months, 4.5 weeks each */
  background-color: white;
  overflow-x: auto;
  padding: 16px;
  row-gap: 10px;

  &::-webkit-scrollbar {
    height: 10px;
  }
  &::-webkit-scrollbar-track {
    background: #f0f0f0;
  }
  &::-webkit-scrollbar-thumb {
    background-color: #53988E;
    border-radius: 8px;
    border: 2px solid #f0f0f0;
  }
  scrollbar-color: #53988E #f0f0f0;
  scrollbar-width: thin;

 
`;



// Helper function to get the week number from a date
const getWeekOfYear = (date) => {
  const firstJan = new Date(date.getFullYear(), 0, 1);
  const dayOfYear = Math.floor((date - firstJan) / (1000 * 60 * 60 * 24)) + 1;
  return Math.ceil(dayOfYear / 7);
};

// Function to process data
// const processData = (data) => {
//   const monthWeekMap = [];
//   const weekDataMap = {};

//   const months = [
//     "January", "February", "March", "April", "May", "June",
//     "July", "August", "September", "October", "November", "December"
//   ];

//   // Initialize monthWeekMap for each month with unique weeks as empty arrays
//   months.forEach((month) => {
//     monthWeekMap.push({ name: month, weeks: [] });
//   });

//   // Traverse through each scene
//   data?.forEach((scene) => {
//     const startTime = new Date(scene.startTime);
//     const monthIndex = startTime.getMonth();  // 0 for January, 1 for February, ...
//     const monthName = months[monthIndex];
//     const weekOfYear = `W${getWeekOfYear(startTime)}`;

//     // Add week to the month if it's not already added
//     if (!monthWeekMap[monthIndex].weeks.includes(weekOfYear)) {
//       monthWeekMap[monthIndex].weeks.push(weekOfYear);
//     }

//     // Initialize each week in weekDataMap if not already present
//     if (!weekDataMap[weekOfYear]) {
//       weekDataMap[weekOfYear] = [];
//     }

//     // Add scene data to the week
//     weekDataMap[weekOfYear].push(scene);
//   });

//   // Sort weeks in each month for consistent display
//   monthWeekMap.forEach((month) => {
//     month.weeks.sort((a, b) => parseInt(a.substring(1)) - parseInt(b.substring(1)));
//   });

//   return { monthWeekMap, weekDataMap };
// };

const groupScenesByWeek = (scenes) => {
  const result = {};

  scenes.forEach((scene) => {
    const { week } = scene;

    // Initialize the array for this week if it doesn't already exist
    if (!result[week]) {
      result[week] = [];
    }

    // Add the current scene to the array for this week
    result[week].push(scene);
  });

  return result;
};
function getWeekIndices(weekList, startWeek, endWeek) {
  const startIndex = weekList.indexOf(startWeek);
  const endIndex = weekList.indexOf(endWeek);

  if (startIndex === -1 || endIndex === -1) {
    return "Start or end week not found in the list.";
  }

  return { startIndex, endIndex };
}
const Timeline = ({clickedButton, setClickedButton}) => {

  const {
    simulationGrounArea: simulationGrounAreaInCaseOfNotNull
  } = useSelector((state) => state.postProcessor.linkDetailsAsPerHours);

  const months = useSelector(state => state.postProcessor.linkDetailsAsPerHours.months);
  const scenes = useSelector(state => state.postProcessor.linkDetailsAsPerHours.scenes);

  const weekIntervals = useSelector(state => state.postProcessor.linkDetailsAsPerHours.weekIntervals);
  const controlPanel = useSelector(state => state.postProcessor.linkDetailsAsPerHours.controlPanel);
  const [groundArea, setGroundArea] = useState({});

  const dispatch = useDispatch();
  const [monthWeekArray, setMonthWeekArray] = useState([]);
  // const [months, setMonths] = useState(null);
  const [weekDataMaps, setWeekDataMaps] = useState(null)


  useEffect(() => {
    if (scenes) {
      const weekDataMap = groupScenesByWeek(scenes)
      setWeekDataMaps(weekDataMap);
    }
  }, [scenes])



  useEffect(() => {
    const time = setTimeout(() => {
      // console.log("clicked", clickedButton, weekDataMaps)
      if (weekDataMaps && clickedButton)
        dispatch(setUrlDetailsWeeksTime(weekDataMaps[clickedButton]));

      if (controlPanel?.length > 0) {
        // clicked button 
        const grounArea = groundArea[clickedButton];
        if (grounArea) {

          dispatch(setSimulationGroundArea(grounArea));
          // console.log("this", grounArea)
        }
        else {

          let obj = {}
          controlPanel?.forEach((value, index) => {
            obj = {
              ...obj,
              ...value.weeks
            }
          })
          // console.log("that", obj)

          dispatch(setSimulationGroundArea(Object.values(obj)[0]));
        }
        // console.log("none", grounArea)

      }

      return () => clearTimeout(time);
    }, 50);

  }, [clickedButton, controlPanel, weekDataMaps])

  useEffect(() => {

    // let obj = {}
    // controlPanel?.forEach((value, index) => {
    //   obj = {
    //     ...obj,
    //     ...value.weeks
    //   }
    // })

    // if (controlPanel?.length === 0) {

    //   setGroundArea(simulationGrounAreaInCaseOfNotNull)
    // }
    // console.log("weeks", controlPanel, obj)


    // setGroundArea(obj)
  }, [controlPanel]);

  // useEffect(() => {
  //   console.log("weeks", groundArea)
  // }, [groundArea])
  return (
    <TimelineContainer>
      {months && <MonthGrid weekIntervals={weekIntervals} monthWeekArray={monthWeekArray} setMonthWeekArray={setMonthWeekArray} months={months} weekDataMaps={weekDataMaps}
        clickedButton={clickedButton}
        setClickedButton={setClickedButton} controlPanel={controlPanel} />}

      {controlPanel?.map((value, index) => {
        // console.log("weeks", Object.keys(value.weeks));
        const weekArr = Object.keys(value.weeks)

        // console.log("l", weekArr, value.weeks)
        const { startIndex, endIndex } = getWeekIndices(monthWeekArray, weekArr[0], weekArr[weekArr.length - 1])
        // console.log("index", startIndex, endIndex)
        return (
          <CropCycle title={value.cycleName} startWeek={startIndex + 1} endWeek={endIndex + 1} />
        );
      }, [])}


    </TimelineContainer>
  );
};

export default Timeline;


