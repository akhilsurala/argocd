import React, { useEffect, useMemo, useState } from "react";
import {
  MaterialReactTable,
  useMaterialReactTable,
} from "material-react-table";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Alert,
  Box,
  Button,
  IconButton,
  InputAdornment,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";
import CustomButton from "../../../components/CustomCard/CustomButton";
import styled, { useTheme } from "styled-components";
import { useNavigate } from "react-router-dom";
import {
  CustomSvgIconForCheckBox,
  CustomSvgIconForCheckedBox,
  CustomSvgIconForCopy,
  CustomSvgIconForCrown,
  CustomSvgIconForDelete,
  CustomSvgIconForEdit,
  CustomSvgIconForExpand,
  CustomSvgIconForRedirect,
} from "../../../container/dashboard/CustomSvgIcon";
import { preProcessorRoute } from "../../../utils/constant";
import CustomSearchBox from "../../../components/CustomSearchBox";
import CustomModal from "../../../components/CustomModal";
import BedPatternWithChip from "./BedPatternWithChip";
import HourlyRatesInTabularForm from "./HourlyRatesInTabularForm";
import AddedBedBlock from "../agriCropParameter/AddedBedBlock";
import {
  addToRunningState,
  deleteRun,
  setControlFlag,
} from "../../../api/runManager";
import {
  ColumnType,
  getColumnHeaders,
  getHourlySellingRatesRows,
  getProtectionLayerRows,
  statusColor,
} from "./utils";
import {
  statusText,
  columnHeaders,
  lastColumnHeader,
  columnHeadersArray,
} from "./constants";
import GenerateCell from "./GenerateCell";
import CustomRunsListCard from "../../../components/CustomRunsListCard";
import CustomRadioChip from "../../../components/CustomRadioChip";
import InfoIcon from "@mui/icons-material/Info";
import { ExpandMore } from "@mui/icons-material";
import { useSelector } from "react-redux";
import CustomInputField from "../agriGeneralPage/component/CustomInputField";
import CustomToolTip from "../../../components/CustomToolTip";

//nested data is ok, see accessorKeys in ColumnDef below
const validateRequired = (value) => !!value.length;
const validateEmail = (email) =>
  !!email.length &&
  email
    .toLowerCase()
    .match(
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    );

const CustomTableForRuns = ({
  data,
  refetch,
  injectSubRows,
  searchBoxValue,
  setSearchBoxValue,
  subRowsLoading,
  detachSubRows,
  projectId,
  handleHoldingBayApiCall,
  isLoading,
  setShouldReFetchRuns,
  moveToSimulateRuns
}) => {
  //should be memoized or stable
  const theme = useTheme();
  const [hourlyRatesData, setHourlyRatesData] = useState();
  const [rowSelection, setRowSelection] = useState({});
  const [selectedRows, setSelectedRows] = useState([]);
  const [validationErrors, setValidationErrors] = useState({});
  const [openBedsModal, setOpenBedsModal] = useState(false);
  const [openHourlyRatesModal, setHourlyRatesModal] = useState(false);
  const [interBedPatternData, setInterBedPatternData] = useState([]);
  const [openBedDetails, setOpenDetails] = useState(false);
  const [openCropRates, setOpenCropRates] = useState(false);
  const [cycleDetails, setCycleDetails] = useState();
  const [cropRateDetails, setCropRatesDetails] = useState();
  const [labelsForModalTable, setLabelsForModalTable] = useState();
  const [hourlySellingRatesTitle, setHourlySellingRatesTitle] = useState();
  const [suffix, setSuffix] = useState();
  const [openMoveToInbox, setOpenMoveToInbox] = useState(false);
  const [finalRows, setFinalRows] = useState([]);


  const crops = useSelector(
    (state) => state.preProcessor.agriCropParameterReducer.crops
  );

  function getCropNameById(id) {
    const crop = crops.find((crop) => crop.id === id);
    return crop ? crop.name : null;
  }

  //keep track of rows that have been edited

  const handleInterBedPattern = (value) => {
    setInterBedPatternData(value);
    setOpenBedsModal((prev) => !prev);
  };

  // console.log("data", data);


  const handleHourlySellingRates = (data, type) => {
    if (type === "hourlyRates") {
      setHourlyRatesData(getHourlySellingRatesRows(data));
      setLabelsForModalTable(["Hours", "Rate/hour"]);
      setHourlySellingRatesTitle("Hourly Selling Rates");
      setSuffix("");
    } else if (type === "protectionLayer") {
      setHourlyRatesData(getProtectionLayerRows(data));
      setLabelsForModalTable(["Protection Layer Name", "Height"]);
      setHourlySellingRatesTitle("Protection Layer");
      setSuffix("meters");
    }

    setHourlyRatesModal((prev) => !prev);
  };

  const [expandedAccordion, setExpandedAccordion] = useState(false);
  const [expandedAccordionForRates, setExpandedAccordionForRates] =
    useState(false);

  // Function to handle accordion click
  const handleAccordionChange = (cycleName) => (event, isExpanded) => {
    setExpandedAccordion(isExpanded ? cycleName : false);
  };
  const handleAccordionChangeForRates = (id) => (event, isExpanded) => {
    setExpandedAccordionForRates(isExpanded ? id : false);
  };

  const handleBedDetails = (data, row, e) => {
    if (row?.original?.cycleBedDetails)
      setCycleDetails(row?.original?.cycleBedDetails);
    setOpenDetails((prev) => !prev);
  };

  useEffect(() => {
    if (isLoading) {
      setExpandedRows([]);
    }
  }, [isLoading])

  const getInputProps = (label) => {
    return {
      endAdornment: (
        <InputAdornment position="end">
          <Typography
            sx={{
              fontFamily: "Montserrat",
              fontSize: "14px",
              fontStyle: "italic",
              fontWeight: "500",
              color: "#53988E80",
            }}
          >
            {label}
          </Typography>
        </InputAdornment>
      ),
    };
  };

  const handleCropRates = (data, row, e) => {
    if (row?.original?.economicParameter)
      setCropRatesDetails(row?.original?.economicParameter);
    setOpenCropRates((prev) => !prev);
  };
  useEffect(() => {
    if (cycleDetails && cycleDetails.length > 0) {
      setExpandedAccordion(cycleDetails[cycleDetails.length - 1].cycleName); // Default to last accordion
    }
  }, [cycleDetails]);

  const handleMoveToInboxClose = () => {
    setOpenMoveToInbox(!openMoveToInbox);
  };

  const extractRunIdAndStatus = () => {
    let results = [];
    const targetRunIdSet = new Set(selectedRows);

    function traverse(rows, isChild = false) {
      rows.forEach((row) => {
        if (targetRunIdSet.has(row.runId)) {
          results.push({
            runId: row.runId,
            runName: row.runName,
            status: row.status,
            type: isChild ? "child" : "parent",
          });
        }

        if (Array.isArray(row.subRows)) {
          traverse(row.subRows, true);
        }
      });
    }

    traverse(data);
    // console.log("Results: ", results);
    setFinalRows(moveToFront(results, "type", "parent"));
  };

  const handleRunDelete = (runId) => {
    const filteredRows = finalRows.filter((row) => row.runId !== runId);

    if (!filteredRows?.length) setOpenMoveToInbox(false);
    setFinalRows(moveToFront(filteredRows, "type", "parent"));
  };

  function moveToFront(arr, property, value) {
    const index = arr.findIndex((obj) => obj[property] === value);
    if (index !== -1) {
      const [item] = arr.splice(index, 1);
      arr.unshift(item);
    }
    return arr;
  }

  const handleMoveToRunningBay = () => {
    // console.log("Selected Rows: ", selectedRows);
    moveToSimulateRuns(selectedRows);
    setOpenMoveToInbox(!openMoveToInbox);
    extractRunIdAndStatus();
  };

  const handleMoveToRunningBayFinal = () => {
    // console.log("Final Rows: ", finalRows);
    setOpenMoveToInbox(!openMoveToInbox);
    if (finalRows.length) {
      addToRunningState(projectId, {
        runId: finalRows
          .filter((r) => r.status === "holding")
          .map((row) => row.runId),
      })
        .then((response) => {
          handleHoldingBayApiCall();
          setSelectedRows([]);
          setRowSelection({});
          setFinalRows([]);
        })
        .catch((error) => {
          console.log(error);
        });
    }
  };
  const navigateToPreProcessor = (row) => {
    navigate(preProcessorRoute(row.original.projectId), {
      state: { runId: row.original.runId },
    });
  };

  const navigateToPreProcessorForClonning = (row) => {
    navigate(preProcessorRoute(row.original.projectId), {
      state: {
        runId: row.original.runId,
        isCloned: true,
      },
    });
  };

  const deleteRunInHoldingState = (row) => {
    deleteRun(row.original.projectId, row.original.runId)
      .then((response) => {
        if (response.data.httpStatus === "OK") {
          // setShouldReFetchRuns(true);
          handleResetExpand();
          refetch();
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  const [expandedRows, setExpandedRows] = useState([]);
  const [resetExpand, setResetExpand] = useState(false);
  useEffect(() => {
    if (resetExpand) {
      setExpandedRows([]);
      setResetExpand(false);
    }
  }, [resetExpand]);



  const handleExpandCollapse = (rowId) => {
    setExpandedRows((prevExpandedRows) =>
      prevExpandedRows.includes(rowId)
        ? prevExpandedRows.filter((id) => id !== rowId)
        : [...prevExpandedRows, rowId]
    );
  };


  const handleResetExpand = () => {
    setResetExpand(true);
    table.toggleAllRowsExpanded(false);
  };

  const handleControlChange = (row, checked) => {
    let agriControl =
      row.simulationType === "Only Agri" && checked && !row.agriControl;
    let pvControl =
      row.simulationType === "Only PV" && checked && !row.pvControl;

    setControlFlag(row.projectId, row.runId, agriControl, pvControl).then(
      (res) => {
        handleResetExpand();
        refetch();
      }
    );
  };

  const columns = useMemo(
    () => [
      {
        ...getColumnHeaders({
          key: "runName",
          header: "Run Name",
          size: 150,
          disableResizing: true,
          enableEditing: false,
        }),
        Cell: ({ renderedCellValue, row }) => (
          <GenerateCell
            type={ColumnType.toolTip}
            text={renderedCellValue}
            title={
              <div style={{ display: "flex", gap: "20px" }}>
                {((row.original.runStatus === "holding" &&
                  row.original.isMaster &&
                  !row.original.variantExist) ||
                  (!row.original.isMaster &&
                    row.original.runStatus === "holding")) && (
                    <IconButton
                      style={{
                        height: "18px",
                        width: "18px",
                        bgcolor: "#53988E",
                      }}
                      onClick={() => {
                        navigateToPreProcessor(row);
                      }}
                    >
                      <CustomSvgIconForEdit
                        sx={{
                          height: "18px",
                          width: "18px",
                          padding: "6px 6px",
                          bgcolor: "#53988E",
                          color: "#fff",
                          borderRadius: "50%",
                        }}
                      />
                    </IconButton>
                  )}
                {(row.original.simulationType === "APV") && (
                  <IconButton
                    style={{
                      height: "18px",
                      width: "18px",
                      bgcolor: "#53988E",
                    }}
                    onClick={() => {
                      navigateToPreProcessorForClonning(row);
                    }}
                  >
                    <CustomSvgIconForCopy
                      sx={{
                        height: "18px",
                        width: "18px",
                        padding: "6px 6px",
                        bgcolor: "#53988E",
                        color: "#fff",
                        borderRadius: "50%",
                      }}
                    />
                  </IconButton>
                )}

                {/* <IconButton
                  style={{
                    height: "18px",
                    width: "18px",
                    bgcolor: "#53988E",
                  }}
                >
                  <CustomSvgIconForCrown
                    sx={{
                      height: "18px",
                      width: "18px",
                      padding: "6px 6px",
                      bgcolor: "#53988E",
                      color: "#fff",
                      borderRadius: "50%",
                    }}
                  />
                </IconButton> */}
                {((row.original.runStatus === "holding" &&
                  row.original.isMaster &&
                  !row.original.variantExist) ||
                  (!row.original.isMaster &&
                    row.original.runStatus === "holding")) && (
                    <IconButton
                      style={{
                        height: "18px",
                        width: "18px",
                        bgcolor: "#53988E",
                        margin: "0px",
                      }}
                      onClick={() => {
                        // console.log("888", (row.original.runStatus === "holding" && row.original.isMaster && !row.original.variantExist))
                        deleteRunInHoldingState(row);
                      }}
                    >
                      <CustomSvgIconForDelete
                        sx={{
                          height: "18px",
                          width: "18px",
                          padding: "6px 6px",
                          bgcolor: "#53988E",
                          color: "#fff",
                          borderRadius: "50%",
                        }}
                      />
                    </IconButton>
                  )}
              </div>
            }
          />
        ),
      },
      {
        ...getColumnHeaders({
          key: "makeControl",
          header: (
            <div style={{ display: "flex", alignItems: "center" }}>
              <span>Make Control</span>
              <CustomToolTip
                toolTipMessage="Select a control run for PV and Agri to see comparision with an 
ideal scenario and observe difference with the rest of your Runs"
              />
            </div>
          ),
          size: 150,
          enableEditing: false,
        }),
        Cell: ({ renderedCellValue, row }) => {
          // console.log('ROW: ', row.original);
          // console.log('renderedCellValue: ', renderedCellValue);
          return (
            <CustomRadioChip
              label={
                row.original.simulationType === "APV"
                  ? "Not Applicable"
                  : row.original.simulationType === "Only Agri"
                    ? "Agri Control"
                    : "PV Control"
              }
              value={
                row.original.simulationType === "APV"
                  ? null
                  : row.original.simulationType === "Only Agri"
                    ? row.original.agriControl
                    : row.original.pvControl
              }
              onChange={(e) =>
                handleControlChange(row.original, e.target.checked)
              }
              disabled={row.original.simulationType === "APV"}
              optional={row.original.simulationType === "APV"}
            />
          );
        },
      },
      {
        ...getColumnHeaders({
          key: "status",
          header: "Status",
          size: 150,
          enableEditing: false,
        }),
        Cell: ({ renderedCellValue, row }) => (
          <GenerateCell
            type={ColumnType.status}
            text={statusText[renderedCellValue]}
            value={renderedCellValue}
          />
        ),
      },
      ...columnHeadersArray.map(({ key, label }) =>
        getColumnHeaders({
          key: key, // the key of the column
          header: label, // the human-readable label of the column
          size: 150, // specify any default or custom size you want
          enableEditing: false, // configure other settings like before
        })
      ),

      {
        ...getColumnHeaders({
          key: "agriPvProtectionHeight",
          header: "Protection Layer",
          size: 150,
          enableEditing: false,
        }),
        Cell: ({ renderedCellValue, row }) => (
          <GenerateCell
            type={ColumnType.link}
            text={renderedCellValue?.length ? "View Protection Layer" : ""}
            handleClick={() =>
              handleHourlySellingRates(renderedCellValue, "protectionLayer")
            }
          />
        ),
      },

      {
        ...getColumnHeaders({
          key: `cycleBedDetails`,
          header: `Crop Cycles`,
          size: 150,
          enableEditing: false,
        }),
        Cell: ({ renderedCellValue, row }) => {
          return (
            <GenerateCell
              type={ColumnType.link}
              text={renderedCellValue?.length ? "View Cycle" : ""}
              handleClick={(e) => handleBedDetails(renderedCellValue, row, e)}
            />
          );
        },
      },
      {
        ...getColumnHeaders({
          key: "economicParameter",
          header: `Crop Rates`,
          size: 150,
          enableEditing: false,
        }),
        Cell: ({ renderedCellValue, row }) => (
          <GenerateCell
            type={ColumnType.link}
            text={renderedCellValue?.length ? "View Rates" : ""}
            handleClick={(e) => handleCropRates(renderedCellValue, row, e)}
          />
        ),
      },

      {
        ...getColumnHeaders({
          key: "hourlySellingRates",
          header: "Hourly Selling Rates",
          size: 150,
          enableEditing: false,
        }),
        Cell: ({ renderedCellValue, row }) => (
          <GenerateCell
            type={ColumnType.link}
            text={renderedCellValue?.length ? "View Rates" : ""}
            handleClick={() =>
              handleHourlySellingRates(renderedCellValue, "hourlyRates")
            }
          />
        ),
      },

      ...lastColumnHeader.map(({ key, label }) =>
        getColumnHeaders({
          key: key, // the key of the column
          header: label, // the human-readable label of the column
          size: 150, // specify any default or custom size you want
          enableEditing: false, // configure other settings like before
        })
      ),
    ],
    [validationErrors]
  );

  const navigate = useNavigate();
  const onCardBtnClick = () => {
    navigate(preProcessorRoute(projectId));
  };

  const buttonSet = {
    validate: {},
    pattern: {},
    requiredMessage: "Project Location is required",
    key: "Create New Master",
    componentType: "button",
    type: "Create New Master",
    label: "Create New Master", // Button label
    variant: "contained", // Button variant
    className: "btn", // Custom class name for button
    testId: "submitButton", // Test ID for testing purposes
    sx: {
      // width: '50%',
      borderRadius: "6px",
      textTransform: "capitalize",
      backgroundColor: theme.palette.secondary.main,
      ":hover": {
        backgroundColor: theme.palette.secondary.main,
      },
    },
    onClick: onCardBtnClick,
  };

  const buttonSetForCancel = {
    validate: {},
    pattern: {},
    key: "Move To Run Box",
    componentType: "button",
    label: "Move To Run Box", // Button label
    variant: "contained", // Button variant
    className: "btn", // Custom class name for button
    sx: {
      textTransform: "capitalize",
      borderRadius: "6px",
      backgroundColor: "transparent",
      color: theme.palette.text.main,
      ":hover": {
        backgroundColor: "transparent",
      },
    },
    onClick: () => {
      handleMoveToRunningBay();
    },
  };

  // const handleRowSelection = (rowIds) => {
  //   const currentRowSelection = rowIds();

  //   console.log("Current Row Selection: ", currentRowSelection, rowSelection);
  //   if (
  //     !Object.keys(currentRowSelection)?.length ||
  //     !Object.keys(rowSelection)?.length
  //   ) {
  //     setRowSelection(rowIds);
  //   } else {
  //     let diffDomain = false;
  //     Object.keys(rowSelection).forEach((oldKey) => {
  //       Object.keys(currentRowSelection).forEach((newKey) => {
  //         if (newKey[0] !== oldKey[0]) diffDomain = true;
  //       });
  //     });

  //     setRowSelection(diffDomain ? currentRowSelection : rowIds);
  //   }
  // };

  const getParentId = (key) => key.split('.')[0];

  const handleRowSelection = (rowIds) => {
    const currentRowSelection = rowIds();
    // console.log("Current Row Selection: ", currentRowSelection, rowSelection);

    const newSelectionKeys = Object.keys(currentRowSelection);
    const prevSelectionKeys = Object.keys(rowSelection);

    if (newSelectionKeys.length === 0) {
      setRowSelection(rowIds);
      return;
    }

    const newParentId = getParentId(newSelectionKeys[0]);
    const prevParentId = prevSelectionKeys.length ? getParentId(prevSelectionKeys[0]) : null;

    if (!prevParentId || newParentId === prevParentId) {
      //  If the new selection belongs to the same parent, merge the selections
      setRowSelection({ ...rowSelection, ...currentRowSelection });
    } else {
      //  If a different parent is selected, reset the selection
      setRowSelection(currentRowSelection);
    }
  };



  // console.log("Data: ", data);

  const table = useMaterialReactTable(
    {
      columns,
      data, //data must be memoized or stable (useState, useMemo, defined outside of this component, etc.)
      layoutMode: "grid-no-grow",
      onRowSelectionChange: handleRowSelection, //connect internal row selection state to your own
      enableSelectAll: false,
      getRowId: (row) => row.address, //give each row a more useful id
      state: { rowSelection, isLoading: isLoading }, //pass our managed row selection state to the table to use
      enableRowSelection: true,
      enableEditing: true,
      displayColumnDefOptions: {
        "mrt-row-select": {
          size: 0,
        },
      },
      autoResetPageIndex: false,
      createDisplayMode: "row", // ('modal', and 'custom' are also available)
      editDisplayMode: "cell", // ('modal', 'row', 'table', and 'custom' are also available)
      expandedRows: { expandedRows },
      onExpandedRowsChange: { setExpandedRows },
      enableColumnResizing: true,
      enableColumnActions: false,

      enableSorting: false,
      enableDensityToggle: false,
      enableFullScreenToggle: false,
      enableToolbarInternalActions: false,
      enableExpanding: true,
      positionActionsColumn: "last",
      enableExpandAll: false, //hide expand all double arrow in column header
      muiSelectCheckboxProps: {
        icon: <CustomSvgIconForCheckBox sx={{ color: "white" }} />, // Use your custom SVG icon for the checkbox
        checkedIcon: (
          <CustomSvgIconForCheckedBox sx={{ color: "transparent" }} />
        ),
      },
      muiSelectAllCheckboxProps: {
        icon: <CustomSvgIconForCheckBox sx={{ color: "white" }} />,
        checkedIcon: (
          <CustomSvgIconForCheckedBox sx={{ color: "transparent" }} />
        ),
      },
      enableColumnFilters: false,
      //optionally, use single-click to activate editing mode instead of default double-click
      muiTableBodyCellProps: ({ cell, column, table }) => ({
        onClick: () => {
          table.setEditingCell(cell); //set editing cell
          //optionally, focus the text field
          queueMicrotask(() => {
            const textField = table.refs.editInputRefs.current[column.id];
            if (textField) {
              textField.focus();
              textField.select?.();
            }
          });
        },
      }),

      renderTopToolbarCustomActions: ({ table }) => (
        <div
          className="subWrapper"
          style={{
            width: "100%",
            display: "flex",
            gap: "20px",
            justifyContent: "space-between",
            padding: "20px",
          }}
        >
          <div>
            <div className="title">All Runs</div>
          </div>

          <div style={{ display: "flex", gap: "10px", alignSelf: "self-end" }}>
            {/* {Object.keys(rowSelection).length ? <CustomButton {...buttonSetForCancel} /> : ""} */}
            <CustomSearchBox
              searchBoxValue={searchBoxValue}
              setSearchBoxValue={setSearchBoxValue}
            />
            <CustomButton {...buttonSetForCancel} />
            <CustomButton {...buttonSet} />
          </div>
        </div>
      ),

      renderRowActions: ({ row }) => (
        <Box sx={{ display: "flex", gap: "1rem" }}>
          <Tooltip title="redirect">
            <IconButton
              color="#53988E"
              onClick={() =>
                navigate(preProcessorRoute(row.original.projectId))
              }
            >
              <CustomSvgIconForRedirect
                sx={{
                  color: "#53988E",
                }}
              />
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton color="#53988E">
              <CustomSvgIconForDelete
                sx={{
                  color: "#53988E",
                }}
              />
            </IconButton>
          </Tooltip>
        </Box>
      ),
      initialState: {
        columnPinning: {
          left: ["mrt-row-expand", "mrt-row-select", "runName"],
        },
      },


      autoResetPageIndex: false,
      // Backend dependency.
      getRowCanExpand: (row) => {
        // console.log("Can Expand: ", row.original.hasChildRuns);
        return row.original.hasChildRuns;
      },
      enablePagination: true,
      paginationDisplayMode: "pages",
      muiPaginationProps: {
        color: "secondary",
        rowsPerPageOptions: [10, 20, 30],
        shape: "rounded",
        variant: "outlined",
      },
      // muiExpandButtonProps: ({ row, table }) => ({

      //   children: row.getIsExpanded() ? (
      //     <CustomSvgIconForExpand
      //       sx={{
      //         outline: "none",
      //       }}
      //       onClick={() => {
      //         // console.log('Detach');
      //         // console.log('Row: ', row.original.runId);
      //         detachSubRows(row.original.runId);
      //       }}
      //     />
      //   ) : (
      //     <CustomSvgIconForExpand
      //       sx={{
      //         transform: "rotate(180deg)",
      //       }}
      //       onClick={() => {
      //         // console.log('Inject')
      //         // console.log('Row: ', row.original.runId);
      //         injectSubRows(row.original.runId);
      //       }}
      //       disabled={row.originalSubRows?.length}
      //     />
      //   ),
      // }),
      muiExpandButtonProps: ({ row }) => {
        const hasSubRows = row.original.variantExist;

        if (!hasSubRows) {
          // Return an empty object to hide the expand button
          return { style: { display: "none" } };
        }

        return {
          children:
            expandedRows.indexOf(row.id) !== -1 ? (
              <CustomSvgIconForExpand
                onClick={() => {
                  detachSubRows(row.original.runId);
                  handleExpandCollapse(row.id);
                }}
              />
            ) : (
              <CustomSvgIconForExpand
                sx={{
                  transform: "rotate(180deg)",
                }}
                onClick={() => {
                  injectSubRows(row.original.runId);
                  handleExpandCollapse(row.id);
                }}
                disabled={row.originalSubRows?.length}
              />
            ),
          sx: { outline: "none !important" },
        };
      },

      muiTableContainerProps: {
        sx: {
          maxHeight: "400px",
          "&::-webkit-scrollbar": {
            height: 6,
            // backgroundColor: theme.palette.background.secondary,
          },
          "&::-webkit-scrollbar-track": {
            boxShadow: `#D5D5D5`,
          },
          "&::-webkit-scrollbar-thumb": {
            backgroundColor: theme.palette.secondary.main,

            borderRadius: "8px",
          },
        },
      },
    },
    [rowSelection]
  );

  // useEffect(() => {
  //   const arr = [];
  //   Object.keys(rowSelection).map((key) => {

  //     // console.log("rowSelection", key, rowSelection, data);
  //     if (!key.includes('.') || key.length === 1) {
  //       arr.push(data[key].runId);
  //     } else {
  //       const targetRow = key.split(".");
  //       if (data[targetRow[0]].subRows[targetRow[1]]) {
  //         arr.push(data[targetRow[0]].subRows[targetRow[1]].runId);
  //       }
  //     }
  //   });

  //   let parent = "";
  //   let childCount = 0;
  //   Object.keys(rowSelection).map((key) => {
  //     if (!key.includes('.') || key.length === 1) {
  //       parent = data[key].runId;
  //     } else {
  //       childCount += 1;
  //     }
  //   });

  //   if (!parent && childCount > 0) {
  //     const id = Object.keys(rowSelection)[0].split(".")[0];
  //     const runId = data[id]?.runId;
  //     if (childCount === data[id].subRows?.length) {
  //       arr.push(runId);
  //     }
  //   }

  //   // console.log("Selected Rows1: ", rowSelection);

  //   setSelectedRows(arr);
  // }, [rowSelection]);


  useEffect(() => {
    const arr = [];
    let parent = "";
    let childCount = 0;

    // console.log("rowSelection", rowSelection, data);
    Object.keys(rowSelection).forEach((key) => {
      const keyParts = key.split(".");
      const isParent = keyParts.length === 1;

      if (isParent) {
        arr.push(data[key].runId);
        parent = data[key].runId;
      } else {
        const [parentIndex, childIndex] = keyParts;
        if (data[parentIndex]?.subRows?.[childIndex]) {
          arr.push(data[parentIndex].subRows[childIndex].runId);
        }
        childCount++;
      }
    });

    if (!parent && childCount > 0) {
      const firstKey = Object.keys(rowSelection)[0];
      const parentIndex = firstKey.split(".")[0];
      const runId = data[parentIndex]?.runId;

      if (childCount === data[parentIndex]?.subRows?.length) {
        arr.push(runId);
      }
    }

    setSelectedRows(arr);
    // console.log("Selected Rows: ", arr);
  }, [rowSelection]);

  return (
    <Container
      style={{
        maxWidth: "92vw",
      }}
    >
      <MaterialReactTable
        table={table}
        render isLoading={true}
      />

      <CustomModal
        openModal={openBedsModal}
        title="Crop Cycle"
        handleClose={handleInterBedPattern}
        children={<BedPatternWithChip data={interBedPatternData} />}
      />
      <CustomModal
        openModal={openHourlyRatesModal}
        title={hourlySellingRatesTitle}
        handleClose={handleHourlySellingRates}
        children={
          <HourlyRatesInTabularForm
            data={hourlyRatesData}
            labels={labelsForModalTable}
            suffix={suffix}
          />
        }
      />
      <CustomModal
        openModal={openBedDetails}
        title="Crop Cycle Details"
        handleClose={handleBedDetails}
        children={
          // <div>hello</div>
          <div
            style={{
              maxHeight: "70vh", // Set a max height
              overflowY: "auto", // Make it scrollable when content exceeds the max height
              paddingRight: "10px", // Optional: Add some space for the scrollbar
              "&::-webkit-scrollbar": {
                width: 6,
                // backgroundColor: theme.palette.background.secondary,
              },
              "&::-webkit-scrollbar-track": {
                boxShadow: `#D5D5D5`,
              },
              "&::-webkit-scrollbar-thumb": {
                backgroundColor: theme.palette.primary.main,

                borderRadius: "8px",
              },
            }}
            className="custom-scroll"
          >
            {cycleDetails?.map((obj) => {
              const { interBedPattern } = obj;

              return (
                <CustomAccordion
                  key={obj.cycleName}
                  expanded={expandedAccordion === obj.cycleName}
                  onChange={handleAccordionChange(obj.cycleName)}
                >
                  <AccordionSummary
                    aria-controls={`${obj.cycleName}-content`}
                    id={`${obj.cycleName}-header`}
                    sx={{
                      "& .Mui-expanded": {
                        minHeight: "unset", // Remove the min-height
                      },
                      "& .MuiAccordionSummary-content": {
                        margin: "0px",
                      },
                    }}
                    expandIcon={<ExpandMore />}
                  >
                    <div
                      style={{
                        alignSelf: "center",
                        fontFamily: "Montserrat",
                        fontSize: "17px",
                        fontWeight: "600",
                      }}
                    >
                      {obj.cycleName}
                    </div>
                  </AccordionSummary>
                  <AccordionDetails
                    style={{
                      display: "flex",
                      flexDirection: "column",
                    }}
                  >
                    {" "}
                    <div
                      style={{
                        borderBottom: "1px dashed #E0E0E0",
                        height: "1px",
                      }}
                    ></div>
                    <div style={{ marginTop: "20px" }}>
                      {getTitleWithToolTip("Starting Date")}
                      <TextField
                        id="outlined-basic"
                        sx={{
                          width: "100%",
                          marginBottom: "20px",
                        }}
                        disabled
                        placeholder="Search"
                        value={obj.cycleStartDate}
                        size="small"
                        InputProps={{
                          style: {
                            height: "40px",
                            padding: "4px 8px",
                            borderRadius: "8px",
                            fontFamily: theme.palette.fontFamily.main,
                            borderColor: "#C7C9CA",
                            fontSize: "14px",
                          },
                        }}
                      />
                    </div>
                    <AddedBedBlock
                      addedBed={obj.cycleBedDetails}
                      showDeleteIcon={false}
                    />
                    {interBedPattern.length > 0 && (
                      <BedPatternWithChip
                        data={interBedPattern}
                        removeCross={true}
                      />
                    )}
                  </AccordionDetails>
                </CustomAccordion>
              );
            })}
          </div>
        }
      />
      <CustomModal
        openModal={openCropRates}
        title="Crop Rates"
        handleClose={handleCropRates}
        children={
          // <div>hello</div>
          <div div>
            {cropRateDetails?.map((obj) => {
              // console.log("obj", obj);
              return (
                <CustomAccordion
                  key={obj.id}
                  expanded={expandedAccordionForRates === obj.id}
                  onChange={handleAccordionChangeForRates(obj.id)}
                >
                  <AccordionSummary
                    aria-controls={`${obj.cycleName}-content`}
                    id={`${obj.cycleName}-header`}
                    sx={{
                      "& .Mui-expanded": {
                        minHeight: "unset", // Remove the min-height
                      },
                      "& .MuiAccordionSummary-content": {
                        margin: "0px",
                      },
                    }}
                    expandIcon={<ExpandMore />}
                  >
                    <div
                      style={{
                        alignSelf: "center",
                        fontFamily: "Montserrat",
                        fontSize: "17px",
                        fontWeight: "600",
                      }}
                    >
                      {getCropNameById(obj.cropId)}
                    </div>
                  </AccordionSummary>
                  <AccordionDetails
                    style={{
                      display: "flex",
                      flexDirection: "column",
                    }}
                  >
                    {" "}
                    <div
                      style={{
                        borderBottom: "1px dashed #E0E0E0",
                        height: "1px",
                      }}
                    ></div>
                    {getTitleWithToolTip("Reference Yield")}
                    <div style={{ display: "flex", gap: "10px" }}>
                      <TextField
                        value={obj.minReferenceYieldCost}
                        style={{
                          width: "100%",
                        }}
                        sx={{
                          borderColor: "#E0E0E0",
                          width: "-webkit-fill-available",
                          "& :focus": {
                            outline: 0,
                          },
                        }}
                        InputProps={getInputProps("tonne/acre")}
                        variant="outlined"
                        autoComplete="off"
                        size="small"
                      ></TextField>
                      {/* <TextField
                        value={obj.maxReferenceYieldCost}
                        style={{
                          width: "100%",
                        }}
                        sx={{
                          borderColor: "#E0E0E0",
                          width: "-webkit-fill-available",
                          "& :focus": {
                            outline: 0,
                          },
                        }}
                        InputProps={getInputProps("tonne/acre")}
                        variant="outlined"
                        autoComplete="off"
                        size="small"
                      ></TextField> */}
                    </div>
                    {getTitleWithToolTip("Input Cost of Crop")}
                    <div style={{ display: "flex", gap: "10px" }}>
                      <TextField
                        value={obj.minInputCostOfCrop}
                        style={{
                          width: "100%",
                        }}
                        sx={{
                          borderColor: "#E0E0E0",
                          width: "-webkit-fill-available",
                          "& :focus": {
                            outline: 0,
                          },
                        }}
                        InputProps={getInputProps("rs/plant")}
                        variant="outlined"
                        autoComplete="off"
                        size="small"
                      ></TextField>
                      {/* <TextField
                        value={obj.maxInputCostOfCrop}
                        style={{
                          width: "100%",
                        }}
                        sx={{
                          borderColor: "#E0E0E0",
                          width: "-webkit-fill-available",
                          "& :focus": {
                            outline: 0,
                          },
                        }}
                        InputProps={getInputProps("rs/plant")}
                        variant="outlined"
                        autoComplete="off"
                        size="small"
                      ></TextField> */}
                    </div>
                    {getTitleWithToolTip("Selling Price of Crop")}
                    <div style={{ display: "flex", gap: "10px" }}>
                      <TextField
                        value={obj.minSellingCostOfCrop}
                        style={{
                          width: "100%",
                        }}
                        sx={{
                          borderColor: "#E0E0E0",
                          width: "-webkit-fill-available",
                          "& :focus": {
                            outline: 0,
                          },
                        }}
                        InputProps={getInputProps("rs/kg")}
                        variant="outlined"
                        autoComplete="off"
                        size="small"
                      ></TextField>
                      {/* <TextField
                        value={obj.maxSellingCostOfCrop}
                        style={{
                          width: "100%",
                        }}
                        sx={{
                          borderColor: "#E0E0E0",
                          width: "-webkit-fill-available",
                          "& :focus": {
                            outline: 0,
                          },
                        }}
                        InputProps={getInputProps("rs/kg")}
                        variant="outlined"
                        autoComplete="off"
                        size="small"
                      ></TextField> */}
                    </div>
                  </AccordionDetails>
                </CustomAccordion>
              );
            })}
          </div>
        }
      />
      <CustomModal
        openModal={openMoveToInbox}
        title="Alert"
        handleClose={handleMoveToInboxClose}
        children={
          <>
            <Alert severity="info" sx={{ marginBottom: "1em" }}>
              Only runs with status 'To Simulate' can move to 'Run Box'.
            </Alert>
            {finalRows.map((row, index) => (
              <CustomRunsListCard
                key={index}
                row={row}
                status={statusText[row.status]}
                onDelete={() => handleRunDelete(row.runId)}
                disabled={row.status !== "holding"}
              />
            ))}
            <Box
              sx={{
                display: "flex",
                justifyContent: "flex-end",
                marginTop: "2em",
              }}
            >
              <Button
                variant="contained"
                disableElevation
                sx={{
                  height: "34px",
                  textTransform: "capitalize",
                  borderRadius: "6px",
                  marginLeft: "1em",
                  background: theme.palette.background.secondary,
                  color: theme.palette.text.light,
                  border: `1px solid ${theme.palette.border.light}`,
                  padding: ".75em",
                  "&:hover": {
                    background: theme.palette.background.secondary,
                    color: theme.palette.text.light,
                    border: `1px solid ${theme.palette.border.light}`,
                  },
                }}
                onClick={handleMoveToInboxClose}
              >
                Cancel
              </Button>
              <Button
                variant="contained"
                disableElevation
                sx={{
                  height: "34px",
                  textTransform: "capitalize",
                  borderRadius: "6px",
                  marginLeft: "1em",
                  background: theme.palette.primary.secondary,
                  padding: ".75em",
                  "&:hover": {
                    background: theme.palette.primary.secondary,
                  },
                }}
                onClick={handleMoveToRunningBayFinal}
                disabled={
                  !finalRows.filter((row) => row.status === "holding")?.length
                }
              >
                Confirm
              </Button>
            </Box>
          </>
        }
      />
    </Container>
  );
};

const getTitleWithToolTip = (label, tootlTipMsg) => {
  return (
    <>
      <div
        style={{
          marginTop: "20px",
          marginBottom: "10px",
          display: "flex", //styleName: Body1;,
          fontFamily: "Montserrat",
          fontSize: "16px",
          fontWeight: "500",
          lineHeight: "26px",
          textAlign: "left",
        }}
      >
        <div style={{ marginRight: "5px" }}>{label}</div>
      </div>
    </>
  );
};

export default CustomTableForRuns;

const Container = styled.div`
  .custom-scroll {
    scrollbar-width: thin; /* Firefox */
    scrollbar-color: #888 #f0f0f0; /* Thumb and track color */

    /* Chrome, Safari, Edge */
    &::-webkit-scrollbar {
      width: 8px;
    }

    &::-webkit-scrollbar-track {
      background: #f0f0f0; /* Track color */
    }

    &::-webkit-scrollbar-thumb {
      background-color: #888; /* Scrollbar thumb color */
      border-radius: 8px;
      border: 2px solid #f0f0f0; /* Adds padding around thumb */
    }

    &::-webkit-scrollbar-thumb:hover {
      background-color: #555; /* Thumb color on hover */
    }
  }

  overflow: hidden;
  .MuiTableRow-root:nth-child(odd) {
    background-color: rgba(223, 228, 229, 1);
  }
  .MuiTableRow-root:nth-child(even) td {
    background-color: rgba(223, 228, 229, 1);
  }
  .title {
    box-sizing: border-box;
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 700;
    line-height: 24.38px;
    text-align: left;
    color: ${({ theme }) => theme.palette.text.main};
  }
  .subWrapper {
    display: flex;
    gap: 20px;
    justify-content: space-between;
  }
`;

const CustomAccordion = styled(Accordion)`
  margin-bottom: 20px;

  &.MuiAccordion-root {
    border: 1px solid #e0e0e0;
    border-radius: 9px;

    box-shadow: none; /* Removes shadow if it looks like a border */
  }

  &.Mui-expanded {
    margin: 0;
  }
`;
