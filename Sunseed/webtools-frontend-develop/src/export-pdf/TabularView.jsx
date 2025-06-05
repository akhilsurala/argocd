import React, { useEffect, useState } from "react";

const apiData = [
  [
    "1990-1-1",
    1,
    17.5
  ],
  [
    "1990-1-1",
    2,
    17.2
  ],
  [
    "1990-1-1",
    3,
    17
  ],
  [
    "1990-1-1",
    4,
    16.2
  ],
  [
    "1990-1-1",
    5,
    12.4
  ],
  [
    "1990-1-1",
    6,
    11.8
  ],
  [
    "1990-1-1",
    7,
    12.6
  ],
  [
    "1990-1-1",
    8,
    14.4
  ],
  [
    "1990-1-1",
    9,
    17.3
  ],
  [
    "1990-1-1",
    10,
    20.1
  ],
  [
    "1990-1-1",
    11,
    22.6
  ],
  [
    "1990-1-1",
    12,
    24
  ],
  [
    "1990-1-1",
    13,
    24.9
  ],
  [
    "1990-1-1",
    14,
    25.2
  ],
  [
    "1990-1-1",
    15,
    25.7
  ],
  [
    "1990-1-1",
    16,
    26
  ],
  [
    "1990-1-1",
    17,
    26
  ],
  [
    "1990-1-1",
    18,
    25.1
  ],
  [
    "1990-1-1",
    19,
    23.7
  ]
]
// Table headers (hours)
const hours = [
  "00:00",
  "02:00",
  "04:00",
  "06:00",
  "08:00",
  "10:00",
  "12:00",
  "14:00",
  "16:00",
  "18:00",
  "20:00",
  "22:00",
];

const transformApiData = (apiData, hours) => {


  // Group data by day
  const groupedData = {};
  apiData?.forEach(([date, hourIndex, value]) => {
    if (!groupedData[date]) {
      groupedData[date] = Array(12).fill(null); // Initialize an array for the day
    }

    // Map hourIndex to corresponding position in the hours array
    const hourPosition = Math.ceil((hourIndex - 1) / 2); // Adjust for zero-based index

    if (hourPosition >= 0 && hourPosition < hours.length) {
      groupedData[date][hourPosition] = value;
    }
  });

  // Transform grouped data into the desired format
  const formattedData = Object.entries(groupedData).map(([day, values]) => {
    const formattedDate = new Date(day).toLocaleDateString("en-GB", {
      day: "numeric",
      month: "short",
    });
    return { day: formattedDate, values };
  });

  return formattedData;
};

export default ({ series }) => {




  // Rows with initial data
  const [data, setData] = useState([
    { day: "1 Jan", values: Array(12).fill(0) },
    { day: "15 Jan", values: Array(12).fill(0) },
    { day: "29 Jan", values: Array(12).fill(0) },
    { day: "..", values: Array(12).fill("") },
    { day: "..", values: Array(12).fill("") },
    { day: "..", values: Array(12).fill("") },
  ]);

  useEffect(() => {
    setData(transformApiData(series, hours));
  }, [series]);

  // Update cell values dynamically

  return (
    <div style={{ marginTop: '50px' }}>
      <h5>TABULAR VIEW</h5>
      <table
        border="1"
        cellSpacing="0"
        cellPadding="2"
        style={{ borderCollapse: "collapse" }}
      >
        <thead>
          <tr>
            <th>DoY</th>
            {hours.map((hour, index) => (
              <th key={index}>{hour}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, rowIndex) => (
            <tr key={rowIndex}>
              <td>{row.day}</td>
              {row.values.map((value, colIndex) => (
                <td key={colIndex}>{value}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

