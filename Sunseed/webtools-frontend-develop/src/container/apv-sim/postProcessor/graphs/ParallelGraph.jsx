import React from "react";
import Highcharts from "highcharts/highmaps";
import HighchartsReact from "highcharts-react-official";
import HighchartsMore from "highcharts/highcharts-more";
import ParallelCoordinates from "highcharts/modules/parallel-coordinates";

HighchartsMore(Highcharts);
ParallelCoordinates(Highcharts);

const ParallelGraph = ({
  title = "",
  data = [],
  height = 300,
  width = null,
  highlightedIndex,
}) => {
  const options = {
    chart: {
      type: "spline",
      parallelCoordinates: true,
      parallelAxes: {
        lineWidth: 2,
      },
      width: width,
      height: height,
    },
    credits: {
      enabled: false,
    },
    title: {
      text: title,
    },
    plotOptions: {
      series: {
        animation: false,
        marker: {
          enabled: false,
          states: {
            hover: {
              enabled: false,
            },
          },
        },
        lineWidth: 2,
        connectEnds: true,
        connectNulls: true,
        states: {
          hover: {
            halo: {
              size: 0,
            },
          },
        },
        events: {
          mouseOver: function () {
            this.group.toFront();
          },
        },
      },
    },
    tooltip: {
      formatter: function () {
        const point = this;
        const label = point.series.xAxis.categories[point.point.x];
        const unit = [
          "",
          "Degrees",
          "meters",
          "",
          "Degrees",
          "Degrees",
          "meters",
          "millimeters",
        ];
        return `${label}: ${point.y} ${unit[point.point.x]}`;
      },
      shared: false,
    },
    xAxis: {
      categories: [
        "Run ID",
        "Azimuth",
        "Length of One Row",
        "Pitch of Rows",
        "Tile Angle",
        "Max Angle of Tracking",
        "Height",
        "Gap Between Modules",
      ],
      offset: 10,
    },
    yAxis: [
      {
        labels: {
          x: 15,
        },
      },
      {
        labels: {
          x: 15,
        },
      },
      {
        min: 0,
        labels: {
          x: 15,
        },
      },
      {
        min: 0,
        labels: {
          x: 15,
        },
      },
      {
        min: 0,
        labels: {
          x: 15,
        },
      },
      {
        min: 0,
        labels: {
          x: 15,
        },
      },
      {
        min: 0,
        labels: {
          x: 15,
        },
      },
      {
        min: 0,
        labels: {
          x: 15,
        },
      },
    ],
    colors: ["rgba(11, 200, 200, 0.1)"],
    series: data.map(function (set, i) {
      return {
        name: "Runner " + i,
        data: set,
        shadow: false,
        lineWidth: highlightedIndex === i ? 4 : 2,
        color: highlightedIndex === i ? "red" : "yellow",
        zIndex: highlightedIndex === i ? 10 : 1,
        connectEnds: true,
      };
    }),
  };

  return <HighchartsReact highcharts={Highcharts} options={options} />;
};

export default ParallelGraph;
