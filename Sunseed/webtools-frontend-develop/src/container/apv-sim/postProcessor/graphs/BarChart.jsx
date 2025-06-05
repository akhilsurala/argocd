import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import { useTheme } from "styled-components";

const BarChart = ({
  title = "",
  series,
  categories,
  minValueY = -1000000,
  maxValueY = 1000000,
  xTitle="",
  yTitle="",
  showBarLabels=false,
}) => {
  const theme = useTheme();
  
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
                }
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
          type: "column", // Change chart type to column for bar chart
        },
        yAxis: {
          title: {
            text: yTitle,
            style: getStyle(),
          },
          labels: {
            style: getStyle(),
          },
          // min: minValueY,
          // max: maxValueY,
        },
        xAxis: {
          title: {
            text: xTitle,
            style: getStyle(),
          },
          categories: categories,
          labels: {
            step: 1, // Show all labels
            rotation: -45, // Rotate labels if needed
            style: getStyle(),
            enabled: showBarLabels,
          },
        },
        tooltip: {
          formatter: function () {
            return `<b>${this.series.name}</b>: ${this.y.toFixed(2)}`;
          },
        },
        legend: {
          layout: "horizontal",
          align: "center",
          verticalAlign: "bottom",
          itemStyle: getStyle(),
          enabled: true,
        },
        plotOptions: {
          column: {
            borderRadius: 5, // Optional: Adds rounded corners to the bars
            pointPadding: 0.1, // Adds space between bars
            groupPadding: 0.2, // Adds space between different series groups
          },
        },
        series: series,
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

export default BarChart;
