import React from "react";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import Heatmap from "highcharts/modules/heatmap";
import Boost from "highcharts/modules/boost";

// Initialize Highcharts modules
Heatmap(Highcharts);
Boost(Highcharts);

const HeatmapChart = () => {
  const options = {
    chart: {
      type: "heatmap",
      marginTop: 40,
      marginBottom: 80,
      plotBorderWidth: 1,
    },
    title: {
      text: "Temperature Variation by Day and Hour through 2023",
    },
    xAxis: {
      categories: [
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
      ],
      title: { text: "Months" },
    },
    yAxis: {
      categories: ["0:00", "4:00", "8:00", "12:00", "16:00", "20:00"],
      title: { text: "Time of Day" },
    },
    colorAxis: {
      min: -10,
      max: 30,
      stops: [
        [0, "#3060cf"], // Blue for cold temperatures
        [0.5, "#fffbbc"], // Yellow for moderate temperatures
        [1, "#c4463a"], // Red for hot temperatures
      ],
      labels: {
        format: "{value}°C",
      },
    },
    series: [
      {
        name: "Temperature",
        borderWidth: 1,
        data: generateData(), // Replace with a function or static data
        dataLabels: {
          enabled: false,
        },
      },
    ],
    tooltip: {
      formatter: function () {
        return `<b>Month:</b> ${this.series.xAxis.categories[this.point.x]}<br>
                <b>Time:</b> ${this.series.yAxis.categories[this.point.y]}<br>
                <b>Temperature:</b> ${this.point.value}°C`;
      },
    },
  };

  // Generate random data for demo purposes
  function generateData() {
    const data = [];
    for (let x = 0; x < 12; x++) {
      for (let y = 0; y < 6; y++) {
        data.push([x, y, Math.floor(Math.random() * 40 - 10)]); // Random temperatures between -10°C and 30°C
      }
    }
    return data;
  }

  return <HighchartsReact highcharts={Highcharts} options={options} />;
};

export default HeatmapChart;
