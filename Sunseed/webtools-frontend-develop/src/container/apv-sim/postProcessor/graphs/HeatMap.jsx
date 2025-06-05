import Highcharts from "highcharts/highmaps";
import HighchartsReact from "highcharts-react-official";
import HC_exporting from 'highcharts/modules/exporting'
import offlineOption from 'highcharts/modules/offline-exporting';
import HC_Data from "highcharts/modules/export-data";

// Initialize Highcharts modules
HC_exporting(Highcharts)
offlineOption(Highcharts);
HC_Data(Highcharts);

import { ArrowBackIos, Height } from "@mui/icons-material";
import { Box, Button } from "@mui/material";
import { get2DGraphData } from "../../../../api/graphs";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTheme } from "styled-components";

import Loading from "../../../../components/Loading";

import dayjs from "dayjs";

const HeatMap = ({ series, tooltipLabel = 'Temperature', setMinMaxDefaultValue, minMaxValue }) => {
  const theme = useTheme();
  // useEffect(() => {
  //   get2DGraphData(projectId, runId).then((res) => {
  //     if (res.data?.data?.resultSimulation?.length) {
  //       setHeatMapData(
  //         res.data?.data?.resultSimulation.map((datum) => [
  //           dayjs(datum.date).valueOf(),
  //           transformTime(datum.time),
  //           Math.round(Math.random() * 20, 1),
  //         ])
  //       );
  //     }
  //   });
  // }, []);

  // const transformTime = (time) => {
  //   let timeElements = time.split(":");

  //   return Number(
  //     `${Number(timeElements[0])}.${(Number(timeElements[1]) / 60) * 100}`
  //   );
  // };

  const formatTime = (time) => {
    const timeElements = String(time).split(".");
    const hours = timeElements[0];
    const minutes = (Number(timeElements[1]?.padEnd(2, "0")) * 60) / 100;
    return `${String(hours)?.padStart(2, "0")}:${String(minutes)?.padStart(
      2,
      "0"
    )}`;
  };

  // Preprocess series data to extract unique dates for the x-axis
  const processData = () => {
    const uniqueDates = []; // To hold unique dates
    let minValue = Number.MAX_VALUE;
    let maxValue = Number.MIN_VALUE;
    const processedData = series.map(([date, hour, value, week_interval]) => {
      const formattedDate = week_interval ? 'W' + week_interval.toString() : dayjs(date).format("YYYY-MM-DD");
      minValue = Math.min(minValue, value);
      maxValue = Math.max(maxValue, value);
      // Add to uniqueDates if not already included
      if (!uniqueDates.includes(formattedDate)) {
        uniqueDates.push(formattedDate);
      }

      // Map date to x-axis index (i.e., position of the date in the uniqueDates array)
      const xIndex = uniqueDates.indexOf(formattedDate);

      // Return the data point: [x (date index), y (hour), value]
      return [xIndex, hour, value];
    });

    // Debug processed data

    return { processedData, uniqueDates, minValue, maxValue };
  };

  const { processedData, uniqueDates, minValue, maxValue } = processData();
  // console.log("hey", processedData, uniqueDates);

  // Get formatted date to show '1 Jan' for the tooltip
  const formatDate = (date) => {
    return dayjs(date).isValid() ? dayjs(date).format("D MMM") : date; // Format date as '1 Jan'
  };

  const getStyle = () => {
    return {
      fontFamily: "Montserrat",
      fontSize: "1em",
      color: theme.palette.text.main,
      fontWeight: 500,
    };
  };
  // console.log("minMaxValue", minMaxValue)

  useEffect(() => {
    setMinMaxDefaultValue?.([minValue, maxValue])
  }, [minValue, maxValue])

  return (
    <HighchartsReact
      style={{ marginTop: '50px' }}
      highcharts={Highcharts}
      options={{
        chart: {
          height: 500,
        },
        credits: {
          enabled: false,
        },
        exporting: {
          chartOptions: { // specific options for the exported image
            plotOptions: {
              series: {
                dataLabels: {
                  enabled: false
                }
              }
            }
          },
          buttons: {
            contextButton: {
              menuItems: [
                "viewFullscreen",
                "printChart",
                "separator",
                "downloadPNG",
                "downloadJPEG",
                "downloadPDF",
                "downloadSVG",
                "separator",
                "downloadCSV",
                "downloadXLS",
              ]
            }
          },
          fallbackToExportServer: false,
        },
        title: {
          text: "",
        },
        subtitle: {
          text: `${tooltipLabel} variation by day and hour`,
          align: "left",
          x: 40,
          style: {
            fontFamily: "Montserrat",
            fontSize: "1em",
            color: theme.palette.text.secondary,
            fontWeight: 500,
          },
        },
        boost: {
          useGPUTranslations: true,
        },
        yAxis: {
          title: {
            text: null,
          },
          labels: {
            format: "{value}:00",
            style: getStyle(),
          },
          minPadding: 0,
          maxPadding: 0,
          startOnTick: false,
          endOnTick: false,
          tickPositions: [0, 6, 12, 18, 24],
          tickWidth: 1,
          min: 0,
          max: 23,
          reversed: true,
        },
        xAxis: {
          categories: uniqueDates.map(formatDate), // Use unique dates as x-axis categories
          labels: {
            rotation: -45, // Rotate for better visibility
            style: getStyle(),
          },
          tickInterval: 1, // Ensure there is one tick per date
        },
        colorAxis: {
          min: minMaxValue ? minMaxValue[0] : minValue,
          max: minMaxValue ? minMaxValue[1] : maxValue,
          stops: [
            [0, "#3060cf"],
            [0.5, "#fffbbc"],
            [0.9, "#c4463a"],
            [1, "#c4463a"],
          ],
          startOnTick: false,
          endOnTick: false,
          labels: {
            step: 1, // Show all labels
            // rotation: -45, // Rotate labels if needed
            format: "{value}",
            style: getStyle(),
          },
        },
        tooltip: {
          formatter: function () {
            const date = formatDate(this.series.xAxis.categories[this.point.x]); // Use the formatted date '1 Jan'
            const time = `${this.point.y}:00`; // Format the time
            return `
              <div style="font-size: 1em;">${tooltipLabel}</div>
              <br>${date} &nbsp;&nbsp;${time}:00 &nbsp;&nbsp;<b>${this.point.value.toFixed(2)}</b>
            `;
          },
          style: getStyle(),
        },
        series: [
          {
            boostThreshold: 100,
            borderWidth: 0,
            nullColor: "#EFEFEF",
            type: "heatmap",
            data: processedData, // Pass the correctly processed data
          },
        ],
      }}
    />
  );
};

export default HeatMap;
