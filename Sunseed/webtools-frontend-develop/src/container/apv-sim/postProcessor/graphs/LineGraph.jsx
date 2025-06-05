import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import HC_exporting from 'highcharts/modules/exporting'
import offlineOption from 'highcharts/modules/offline-exporting';
import HC_Data from "highcharts/modules/export-data";

// Initialize Highcharts modules
HC_exporting(Highcharts)
offlineOption(Highcharts);
HC_Data(Highcharts);

import { useTheme } from "styled-components";

const LineGraph = ({
  title = "",
  minValueY = -1000000,
  maxValueY = 1000000,
  xTitle,
  yTitle,
  series,
}) => {
  const theme = useTheme();

  const generateSeries = () => {
    const tempSeries = [];

    series.map((ele) => {
      tempSeries.push({
        name: ele.name || "",
        // data: limitData(ele.data, minValueY, maxValueY),
        data: ele.data,
        color: ele.color,
        marker: {
          enabled: true,
        },
        lineWidth: 2,
      });
    });

    return tempSeries;
  };

  const limitData = (data, min, max) => {
    const temp = [];
    data.forEach((datum) => {
      if (datum[1] >= min && datum[1] <= max) {
        temp.push(datum);
      }
    });

    return temp;
  };

  const getStyle = () => {
    return {
      fontFamily: "Montserrat",
      fontSize: "1em",
      color: theme.palette.text.main,
    };
  };

  const getCategories = () => {
    const temp = [];
    series.forEach((ele) => {
      ele.data.forEach((datum) => temp.push(datum[0]));
    });
    return temp;
  };

  return (
    <HighchartsReact
      highcharts={Highcharts}
      options={{
        events: {
          load() {
            setTimeout(this.reflow.bind(this), 0);
          },
        },
        exporting: {
          chartOptions: { // specific options for the exported image
            plotOptions: {
              series: {
                dataLabels: {
                  enabled: false
                },
              }
            }
          },
          buttons: {
            contextButton: {
              // menuItems: ['downloadPNG', 'downloadJPEG', 'downloadPDF', 'downloadSVG'],
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
          text: title,
          align: "left",
          x: 0,
          style: {
            fontFamily: "Montserrat",
            fontSize: "1em",
            color: theme.palette.text.main,
            fontWeight: 600,
          },
        },
        chart: {
          polar: true,
          type: "line",
        },

        yAxis: {
          title: {
            text: yTitle,
            style: getStyle(),
          },
          // startOnTick: false,
          // endOnTick: false,
          labels: {
            style: getStyle(),
          },
        },
        tooltip: {
          valueDecimals: 2, // Fix to 2 decimal places
        },
        xAxis: {
          title: {
            text: xTitle,
            style: getStyle(),
          },
           labels: {
            step: 1, // Show all labels
            rotation: -45, // Rotate labels if needed
            style: {
              fontSize: '12px', // Adjust font size
            },
          },
          categories: getCategories(),
          // labels: {
          //   formatter: function () {
          //     return this.value;
          //   },
          // },
        },
        // tooltip: {
        //   formatter: function () {
        //     const date = formatDate(this.series.xAxis.categories[this.point.x]); // Use the formatted date '1 Jan'
        //     const time = `${this.point.y}:00`; // Format the time
        //     return `
        //       <div style="font-size: 1em;">Temperature</div>
        //       <br>${date} &nbsp;&nbsp;${time}:00 &nbsp;&nbsp;<b>${this.point.value.toFixed(2)}</b> ℃
        //     `;
        //   },
        //   style: getStyle(),
        // },
        legend: {
          layout: "horizontal",
          align: "center",
          verticalAlign: "bottom",
          itemStyle: getStyle(),
          enabled: true,
        },
        plotOptions: {
          series: {
            label: {
              connectorAllowed: true,
            },
            pointPlacement: "on",
          },
        },
        series: generateSeries(),
        responsive: {
          rules: [
            {
              condition: {
                maxWidth: 800,
              },
              chartOptions: {
                legend: {
                  layout: "horizontal",
                  align: "center",
                  verticalAlign: "bottom",
                },
              },
            },
          ],
        },
      }}
    />
  );
};

export default LineGraph;
