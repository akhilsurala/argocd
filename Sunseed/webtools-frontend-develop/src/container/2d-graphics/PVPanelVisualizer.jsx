import React, { useEffect, useRef } from 'react';
import { Stage, Layer, Rect } from 'react-konva';
import { useSelector } from 'react-redux';

const Base = ({ x, y, width, height, color = '#FF8000' }) => {


  return (
    <Rect
      x={x}
      y={y}
      width={width}
      height={20}
      fill={color}
      stroke="orangered"
    />
  );
};

const Pole = ({ x, y, heightPoll, widthPoll = 10 }) => {


  return (
    <Rect
      x={x}
      y={y}
      width={widthPoll}
      height={heightPoll}
      fill="#00FF00"
      stroke="green"
    />
  );
};

const SolarPlate = ({ x, y, widthPoll, heightPoll = 10, angleInDegrees = 150, rotation }) => {

  return (
    <Rect

      x={x + widthPoll / 2}
      y={y}
      width={widthPoll}
      height={heightPoll}
      fill="#00FF00"
      stroke="green"
      rotation={angleInDegrees}
      offset={{
        x: widthPoll / 2,
      }}

    />
  );
};

// https://longviewcoder.com/2020/12/15/konva-rotate-a-shape-around-any-point/

const createSolarPlate = (margin, avilableWidth, avilableHeight, eachMeterOcoupiedPixel, solarWidthOccupiedPixel, pollHeigth, weightOfPoll, previousPlateWidth = 0, rotation) => {
  const xOfPole = margin + (solarWidthOccupiedPixel / 2) - (weightOfPoll / 2) + previousPlateWidth;
  const xOfPlate = margin + previousPlateWidth
  const yOfSolar = avilableHeight - pollHeigth;
  return <>
    <Pole key={previousPlateWidth} x={xOfPole} y={yOfSolar} heightPoll={pollHeigth} />
    <SolarPlate x={xOfPlate} y={yOfSolar} widthPoll={solarWidthOccupiedPixel} heightPoll={weightOfPoll} rotation={rotation} />
  </>
}

const createSolarPlates = (margin, avilableWidth, avilableHeight, eachMeterOcoupiedPixel, solarWidthOccupiedPixel, pollHeigth, weightOfPoll, gapInMeter, rotation) => {
  const arr = [];
  for (let i = 0; i < 5; i++) {
    const element = createSolarPlate(margin, avilableWidth, avilableHeight, eachMeterOcoupiedPixel, solarWidthOccupiedPixel, pollHeigth, weightOfPoll, ((solarWidthOccupiedPixel + (solarWidthOccupiedPixel * gapInMeter)) * i), rotation)

    arr.push(element)
  }
  return arr;

}
const PVPanelVisualizer = ({ width, height, plotLength, plotWidth, gap, rotation }) => {

  const heightOfPoll = useSelector((state) => state.preProcessor.pvParameters.height);

  const containerRef = useRef(null);
  const margin = 30;
  const avilableWidth = width - (margin * 2);
  const avilableHeight = height - (margin * 2);
  const eachMeterOcoupiedPixel = avilableWidth / plotWidth;

  const solarWidth = 1.5;
  const solarWidthOccupiedPixel = eachMeterOcoupiedPixel * solarWidth;

  const pollHeightInMeters = heightOfPoll;
  const pollHeigth = eachMeterOcoupiedPixel * pollHeightInMeters;
  const weightOfPoll = 10;



  return (
    <div ref={containerRef} style={{ width: '100%', height: '100%' }}>
      <Stage width={width} height={height}>
        <Layer>
          <Base x={margin} y={avilableHeight} width={eachMeterOcoupiedPixel * plotWidth} height={height} />

          {createSolarPlates(margin, avilableWidth, avilableHeight, eachMeterOcoupiedPixel, solarWidthOccupiedPixel, pollHeigth, weightOfPoll, gap / 1000, rotation)}

        </Layer>
      </Stage>
    </div>
  );
};

export default PVPanelVisualizer;
