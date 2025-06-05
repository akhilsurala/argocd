import React, { useState } from "react";
import styled from "styled-components";

// Styled table for basic layout
const StyledTable = styled.table`
  border-collapse: collapse;
  text-align: left;
  font-size: 14px;
  th,
  td {
    border: 1px solid #000;
    padding: 8px;
  }

  th {
    background-color: #f3f3f3;
    width: 80px;
  }

  td {
    vertical-align:top
  }
  td ul {
    margin: 0;
    padding: 0;
  }

  td li {
    list-style-type: none;
  }
`;

const Container = styled.div`
  margin-top: 30px;
`;

// Style for the blue text after '='
const BlueText = styled.span`
  color: blue;
`;

const PageBreak = styled.div`
  page-break-before: always;
  break-before: page;
`;


const SimulationTable = ({ data, projectDetails }) => {
  const formatParameter = (param) => {
    const parts = param.split("=");
    return parts.length > 1 ? (
      <>
        {parts[0]}=<BlueText>{parts.slice(1).join("=")}</BlueText>
      </>
    ) : (
      param
    );
  };
  const formatPvParameter = (payload) => {
    const keysToInclude = [
      "pvModule",
      "modeOfOperationId",
      "moduleConfigs",
      "soilType",
      "moduleMaskPattern",
      "tiltIfFt",
      "maxAngleOfTracking",
      "height",
      "lengthOfOneRow",
      "gapBetweenModules",
      "pitchOfRows",
      "azimuth",
    ];

    const keyValuePairs = keysToInclude.map((key) => {
      let value = null;

      // Resolve nested values if applicable
      if (key === "pvModule") value = payload.pvModule?.moduleName || null;
      else if (key === "modeOfOperationId")
        value = payload.modeOfOperationId?.modeOfOperation || null;
      else if (key === "moduleConfigs")
        value = payload.moduleConfigs?.[0]?.moduleConfig || null;

      else if (key === "soilType")
        value = payload.preProcessorToggle.soilType?.soilName || null;

      else if (key === "azimuth")
        value = payload.preProcessorToggle.azimuth || null;
      else if (key === "pitchOfRows")
        value = payload.preProcessorToggle.pitchOfRows || null;
      else if (key === "lengthOfOneRow")
        value = payload.preProcessorToggle.lengthOfOneRow || null;
      else value = payload[key] ?? null;

      // Format the keys into sentence case
      const formattedKey = key
        .replace(/([A-Z])/g, " $1") // Add space before capital letters
        .replace(/^[a-z]/, (char) => char.toUpperCase()) // Capitalize the first letter
        .toLowerCase();

      return `${formattedKey} = ${value}`;
    });

    return keyValuePairs;
  };


  const formatAgriGeneralParameters = (data) => {
    if (!data) return [];

    const {
      irrigationType,
      soilId,
      tempControl,
      trail,
      minTemp,
      maxTemp,
      isMulching,
      bedParameter,
    } = data;

    return [
      `Irrigation type = ${irrigationType.irrigationType ?? "null"}`,
      `Temperature control = ${tempControl ?? "null"}`,
      `Trail = ${trail ?? "null"}`,
      `Minimum temperature = ${minTemp ?? "null"}`,
      `Maximum temperature = ${maxTemp ?? "null"}`,
      `Is mulching = ${isMulching ? "true" : "false"}`,
      `Bed width = ${bedParameter?.bedWidth ?? "null"}`,
      `Bed height = ${bedParameter?.bedHeight ?? "null"}`,
      `Angle of bed = ${bedParameter?.bedAngle ?? "null"}`,
      `Bed azimuth = ${bedParameter?.bedAzimuth == 0 ? "ALONG" : "ACROSS" ?? "null"}`,
      `Bed CC = ${bedParameter?.bedcc ?? "null"}`,
      `Start point offset = ${bedParameter?.startPointOffset ?? "null"}`,
    ];
  };


  const formatAgriCropParameters = (data) => {
    if (!data?.cycles) return [];

    return data.cycles.map((cycle, cycleIndex) => {
      const { name: cycleName, startDate: cycleStartDate, cycleBedDetails } = cycle;

      // Start with cycle-level details
      const cycleParameters = [
        `Cycle name = ${cycleName ?? "null"}`,
        `Cycle start date = ${cycleStartDate ?? "null"}`
      ];

      // Add details for each bed in the cycle
      if (cycleBedDetails && cycleBedDetails.length > 0) {
        cycleBedDetails.forEach((bed, bedIndex) => {
          const {
            bedName,
            cropName,
            cropId1,
            o1,
            s1,
            o2,
            optionalCropName,
            optionalO1,
            optionalS1,
            optionalO2,
            stretch,
            optionalStretch,
          } = bed;

          cycleParameters.push(
            `Bed ${bedIndex + 1} name = ${bedName ?? "null"}`,
            `Crop name = ${cropName ?? "null"}`,
            `Crop ID = ${cropId1 ?? "null"}`,
            `O1 = ${o1 ?? "null"}`,
            `S1 = ${s1 ?? "null"}`,
            `O2 = ${o2 ?? "null"}`,
            `Optional crop name = ${optionalCropName ?? "null"}`,
            `Optional O1 = ${optionalO1 ?? "null"}`,
            `Optional S1 = ${optionalS1 ?? "null"}`,
            `Optional O2 = ${optionalO2 ?? "null"}`,
            `Stretch = ${stretch ?? "null"}`,
            `Optional stretch = ${optionalStretch ?? "null"}`
          );
        });
      }

      return cycleParameters;
    });
  };

  const formatEconomicParameters = (data) => {
    if (!data) return [];

    const { cropDtoSet, economicMultiCropResponseList, hourlySellingRates } = data;

    const cropDetails = economicMultiCropResponseList?.flatMap((crop) => {
      const cropName =
        cropDtoSet?.find((dto) => dto.id === crop.cropId)?.name || "Unknown crop";

      return [
        `${cropName} -`,
        `Ref-Yield = ${crop.minReferenceYieldCost};`,
        `Input-Cost = ${crop.minInputCostOfCrop};`,
        `Selling-Cost = ${crop.minSellingCostOfCrop};`,
      ];
    });

    const hourlyTariffs = `Hourly PV Tariffs From Midnight = (${hourlySellingRates?.join(
      ", "
    )})`;

    return [...cropDetails, hourlyTariffs];
  };


  // console.log("hey", projectDetails)
  return (
    <Container>
      <StyledTable>
        <h2 style={{
          margin: 0,
          fontFamily: "Courier New",
          fontWeight: "normal",
          color: '#0f4761',
        }}>1.0 KEY PROJECT PARAMETERS</h2>
        <div className="mid">

          <p>NAME OF PROJECT:– {projectDetails?.projectName}</p>
          <p>LATITUDE: - {projectDetails?.latitude}  </p>
          <p>LONGITUDE:: -  {projectDetails?.longitude} </p>
        </div>
        <PageBreak />

        <h2 style={{
          margin: 0,
          marginTop: '40px',
          fontFamily: "Courier New",
          fontWeight: "normal",
          color: '#0f4761',
        }}>2.0 SIMULATION INPUTS</h2>



        {data?.map((row, index) => (
          <div style={{ marginTop: '50px' }} key={index}>
            <thead>
              <tr>
                <th>RUN NAME</th>
                <th>TYPE</th>
                <th>PV PARAMETERS</th>
                <th>AGRI GEN. PARAMETERS</th>
                <th>AGRI CROP PARAMETERS</th>
                <th>ECONOMIC PARAMETERS</th>
              </tr>
            </thead>

            <tbody>
              <tr key={index}>
                <td>{row.runName}</td>
                <td>{row.type}</td>
                <td>
                  {row.pvParameters ? (
                    <ul>
                      {formatPvParameter(row.pvParameters).map((param, i) => (
                        <li key={i}>{formatParameter(param)}</li>
                      ))}
                    </ul>
                  ) : (
                    "-"
                  )}
                </td>
                <td>
                  {row.agriGenPayload ? (
                    <ul>
                      {formatAgriGeneralParameters(row.agriGenPayload).map(
                        (param, i) => (
                          <li key={i}>{formatParameter(param)}</li>
                        )
                      )}
                    </ul>
                  ) : (
                    "-"
                  )}
                </td>


                <td>
                  {row.agriCropParameters ? (
                    <ul>
                      {formatAgriCropParameters(row.agriCropParameters).map((cycle, cycleIndex) => (
                        <li key={cycleIndex}>
                          <ul>
                            <strong><li>{`Cycle ${cycleIndex + 1}`}</li></strong>
                            {cycle.map((param, paramIndex) => (
                              <li key={paramIndex}>{formatParameter(param)}</li>
                            ))}
                          </ul>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    "-"
                  )}
                </td>

                <td>
                  {row.economicParameters ? (
                    <ul>
                      {formatEconomicParameters(row.economicParameters).map((param, i) => (
                        <li key={i}>{formatParameter(param)}</li>
                      ))}
                    </ul>
                  ) : (
                    "-"
                  )}
                </td>

              </tr>

            </tbody>
            <PageBreak />
          </div>
        ))}
      </StyledTable>
    </Container>
  );
};

export default function SimTable({ runDetails, projectDetails }) {
  // console.log("runDetails", runDetails);

  const [tableData, setTableData] = useState(null);



  React.useEffect(() => {
    if (!runDetails) return

    const tableD = runDetails.map((run) => ({
      runName: run.runName,
      type: run.preProcessorToggles.toggle,
      pvParameters: { ...run.pvParameters, preProcessorToggle: run.preProcessorToggles },
      agriGenPayload: run.agriGeneralParameter,
      agriCropParameters: run.cropParameters,
      economicParameters: run.economicParameters,
    }));
    // console.log("tableData", tableD)
    setTableData(tableD)
  }, [runDetails]);

  /*
  const tableData = [
    {
      runName: "R1",
      type: "APV-MASTER",

      
      agriGenPayload: {
        id: 307,
        projectId: 32,
        runId: null,
        agriPvProtectionHeight: [],
        irrigationType: 1,
        soilId: null,
        tempControl: "none",
        trail: null,
        minTemp: null,
        maxTemp: null,
        isMulching: false,
        bedParameter: {
          id: 307,
          bedWidth: 500.0,
          bedHeight: 5.0,
          bedAngle: 45.0,
          bedAzimuth: 0.0,
          bedcc: 1.0,
          startPointOffset: 2.0,
        },
        status: "draft",
        cloneId: null,
        isMaster: null,
      },
      pvParameters: {
        pvModule: {
          moduleName: "TOPCON",
        },
        modeOfOperationId: {
          modeOfOperation: "Fixed Tilt",
        },
        moduleConfigs: [
          {
            moduleConfig: "1P",
          },
        ],
        soilType: null,
        moduleMaskPattern: "",
        tiltIfFt: 45.0,
        maxAngleOfTracking: null,
        height: 5.0,
        lengthOfOneRow: 5.0,
        gapBetweenModules: 5.0,
        pitchOfRows: 5.0,
        azimuth: 56.0,
      },
      agriCropParameters: {
        "id": 319,
        "runId": 428,
        "cloneId": 427,
        "isMaster": false,
        "projectId": 31,
        "cycles": [
          {
            "id": 473,
            "cycleName": "cr1",
            "cycleStartDate": "2024-12-31",
            "cycleBedDetails": [
              {
                "id": 603,
                "bedName": "Bed 1",
                "cropId1": 11,
                "cropName": "almond",
                "o1": 0,
                "s1": 100,
                "o2": 1,
                "optionalCropType": null,
                "optionalCropName": null,
                "optionalO1": null,
                "optionalS1": null,
                "optionalO2": null,
                "stretch": 11,
                "optionalStretch": null
              }
            ],
            "interBedPattern": []
          }, {
            "id": 474,
            "cycleName": "cr1",
            "cycleStartDate": "2024-12-31",
            "cycleBedDetails": [
              {
                "id": 603,
                "bedName": "Bed 1",
                "cropId1": 11,
                "cropName": "almond",
                "o1": 0,
                "s1": 100,
                "o2": 1,
                "optionalCropType": null,
                "optionalCropName": null,
                "optionalO1": null,
                "optionalS1": null,
                "optionalO2": null,
                "stretch": 11,
                "optionalStretch": null
              }
            ],
            "interBedPattern": []
          }
        ]
      },
      economicParameters: {
        "economicId": 227,
        "runId": 428,
        "cloneId": 427,
        "isMaster": false,
        "cropDtoSet": [
          {
            "id": 11,
            "name": "almond",
            "createdAt": "2024-11-22T04:39:37.850442Z",
            "updatedAt": "2024-12-24T05:54:19.923571Z"
          }
        ],
        "currency": {
          "currencyId": 1,
          "currency": "INR"
        },
        "economicMultiCropResponseList": [
          {
            "id": 215,
            "cropId": 11,
            "minReferenceYieldCost": 12,
            "maxReferenceYieldCost": 1000000000,
            "minInputCostOfCrop": 12,
            "maxInputCostOfCrop": 1000000000,
            "minSellingCostOfCrop": 122,
            "maxSellingCostOfCrop": 1000000000,
            "cultivationArea": null,
            "createdAt": "2024-12-27T05:55:27.015611Z",
            "updatedAt": "2024-12-27T05:55:27.015716Z"
          }
        ],
        "hourlySellingRates": [
          2,
          3,
          4,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0,
          0
        ],
        "createdAt": "2024-12-27T05:55:26.948375Z",
        "updatedAt": "2024-12-27T05:55:27.049334Z",
        "economicParameter": true
      },
    },
    { runName: "R3", type: "APV" },
    { runName: "ST1", type: "APV" },
  ];
  */

  return <SimulationTable data={tableData} projectDetails={projectDetails} />;
}
