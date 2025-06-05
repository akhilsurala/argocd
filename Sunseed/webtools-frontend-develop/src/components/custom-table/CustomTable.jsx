import React, { useEffect, useMemo, useState } from "react";
import {
  MRT_EditActionButtons,
  MaterialReactTable,
  useMaterialReactTable,
} from "material-react-table";
import {
  Box,
  Button,
  Checkbox,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Tooltip,
  Typography,
} from "@mui/material";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import CustomButton from "../CustomCard/CustomButton";
import styled, { useTheme } from "styled-components";
import { useNavigate } from "react-router-dom";
import { AppRoutesPath, preProcessorRoute } from "../../utils/constant";
import {
  CustomSvgIconForCheckBox,
  CustomSvgIconForCheckedBox,
  CustomSvgIconForDelete,
  CustomSvgIconForEdit,
  CustomSvgIconForRedirect,
} from "../../container/dashboard/CustomSvgIcon";
import CustomSearchBox from "../CustomSearchBox";
import { useDispatch } from "react-redux";
import { setCurrentProjectName } from "../../redux/action/preProcessorAction";
import { deleteProject } from "../../api/userProfile";
import ConfirmationDialog from "../ConfirmationDialog";

//nested data is ok, see accessorKeys in ColumnDef below

const validateRequired = (value) => !!value.length;
const validateEmail = (email) =>
  !!email.length &&
  email
    .toLowerCase()
    .match(
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    );

const CustomTable = ({
  data,
  setEditedUsers,
  setData,
  apiErrorMsg,
  setApiErrorMsg,
  setSearchBoxValue,
  searchBoxValue,
  callGetProjectApi,
}) => {
  // console.log("aerr", apiErrorMsg);
  //should be memoized or stable
  const theme = useTheme();

  const [open, setOpen] = useState(false);
  const [currentRow, setCurrentRow] = useState(null);

  const [rowSelection, setRowSelection] = useState({});
  const [validationErrors, setValidationErrors] = useState({});
  const [propsObj, setPropsObj] = useState(null);
  const dispatch = useDispatch();
  // console.log("apiErrorMsg", apiErrorMsg)
  //keep track of rows that have been edited
  const handleProjectValidation = (val) => {
    if (val.length === 0) {
      return "This field is required";
    }
    if (val?.length < 5) return "Minimum 5 characters are allowed";
    if (val?.length > 30) return "Maximum 30 characters are allowed";
    const regex = /^[a-zA-Z]/;
    if (!regex.test(val)) return "First character must be an alphabet";
  };
  const handleCommentValidation = (val) => {
    if (val?.length > 30) return "Maximum 30 characters are allowed";
  };

  const handleDeleteProject = (row) => {
    console.log(row.original);
    deleteProject(row.original.projectId, null)
      .then((response) => {
        console.log(response);
        callGetProjectApi();
      })
      .catch((error) => {
        console.log(error);
      });
  };

  useEffect(() => {
    if (propsObj && apiErrorMsg) {
      const { row, column, prevValue } = propsObj;

      const columnId = column.id;
      const rowIndex = row.index;
      const updatedData = [...data];

      updatedData[rowIndex][columnId] = prevValue;
      setData(updatedData);
      setValidationErrors({
        ...validationErrors,
        ["errorMsg"]: apiErrorMsg,
      });
      setPropsObj(null);
      setApiErrorMsg("");
    }
  }, [propsObj, apiErrorMsg]);

  const handleCellEdit = (props) => {
    const { row, column, value, cell, obj } = props;

    const columnId = column.id;
    const rowIndex = row.index;
    const prevValue = row.original[columnId];

    setPropsObj({ prevValue: prevValue, row, column });
    const validationError = handleProjectValidation(value);
    if (validationError) {
      // Reset the input field to the previous value
      const updatedData = [...data];
      updatedData[rowIndex][columnId] = prevValue;
      setData(updatedData);
      setValidationErrors({
        ...validationErrors,
        ["errorMsg"]: validationError,
      });
    } else {
      // Update the data state with the new value
      const updatedData = [...data];
      updatedData[rowIndex][columnId] = value;
      setData(updatedData);
      setValidationErrors({
        ...validationErrors,
        ["errorMsg"]: undefined,
      });

      setEditedUsers(obj);
    }
  };

  const handleCellEditForComment = ({ row, column, value, cell, obj }) => {
    // console.log("heeeeeee",)

    const columnId = column?.id;
    const rowIndex = row?.index;
    const prevValue = row?.original[columnId];

    setPropsObj({ prevValue: prevValue, row, column });
    const validationError = handleCommentValidation(value);
    if (validationError) {
      // Reset the input field to the previous value
      const updatedData = [...data];
      updatedData[rowIndex][columnId] = prevValue;
      setData(updatedData);
      setValidationErrors({
        ...validationErrors,
        ["errorMsg"]: validationError,
      });
    } else {
      // Update the data state with the new value
      const updatedData = [...data];
      updatedData[rowIndex][columnId] = value;
      setData(updatedData);
      setValidationErrors({
        ...validationErrors,
        ["errorMsg"]: undefined,
      });

      setEditedUsers(obj);
    }
  };
  // console.log("heeeeeee", validationErrors)

  const columns = useMemo(
    () => [
      {
        accessorKey: "projectId", //access nested data with dot notation
        header: "id",
        size: 30,
        enableEditing: false,
      },
      {
        accessorKey: "projectName", //access nested data with dot notation
        header: "Name",
        size: 150,

        muiEditTextFieldProps: ({ cell, row }) => ({
          type: "text",
          required: true,
          error: !!validationErrors?.[cell.id],
          helperText: validationErrors?.[cell.id],

          onBlur: (e) => {
            // console.log("hey", cell);/
            const obj = {
              projectId: cell.row.original.projectId,
              projectName: e.currentTarget.value,
            };
            return handleCellEdit({
              row: cell.row,
              column: cell.column,
              value: e.target.value,
              cell: cell,
              obj,
            });
          },
          // store edited user in state to be saved later
          // onBlur: (event) => {

          //   // console.log("hey", cell.row.original.projectId, event.currentTarget.value);
          //   const validationError = !validateEmail(event.currentTarget.value)
          //     ? 'Invalid email'
          //     : undefined;
          //   setValidationErrors({
          //     ...validationErrors,
          //     [cell.id]: validationError,
          //   });
          //   if (validationError) {

          //     console.log("heyer", event, cell.row.original.projectName, cell.row._valuesCache.projectName)
          //     return
          //   }
          //   setEditedUsers({ 'projectId': cell.row.original.projectId, "projectName": event.currentTarget.value })
          // },
        }),
        Cell: ({ renderedCellValue, row, table }) => (
          <Tooltip
            placement="right"
            componentsProps={{
              tooltip: {
                sx: {
                  // height: '18px',
                  // width: '18px',
                  // padding: '4px 4px',
                  bgcolor: "transparent",
                  // borderRadius: '50%'
                },
              },
            }}
            title={
              <div style={{ display: "flex", gap: "20px" }}>
                <IconButton
                  style={{
                    height: "18px",
                    width: "18px",
                    bgcolor: "#53988E",
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
              </div>
            }
          >
            {renderedCellValue ? (
              <span>{renderedCellValue}</span>
            ) : (
              <span>No Data</span>
            )}
          </Tooltip>
        ),
      },
      {
        accessorKey: "latitude", //access nested data with dot notation
        header: "Latitude",
        size: 150,
        enableEditing: false,
      },
      {
        accessorKey: "longitude", //access nested data with dot notation
        header: "Longitude",
        size: 150,
        enableEditing: false,
      },
      {
        accessorKey: "createdOn",
        header: "Created on",
        size: 150,
        enableEditing: false,
      },
      {
        accessorKey: "numberOfRuns", //access nested data with dot notation
        header: "Number of run",
        size: 150,
        enableEditing: false,
      },
      {
        accessorKey: "comments", //access nested data with dot notation
        header: "Comments",
        size: 150,
        muiEditTextFieldProps: ({ cell, row }) => ({
          type: "text",
          required: true,
          error: !!validationErrors?.[cell.id],
          helperText: validationErrors?.[cell.id],
          //store edited user in state to be saved later
          // onBlur: (event) => {

          //   setEditedUsers()
          // },

          onBlur: (event) => {
            const obj = {
              projectId: cell.row.original.projectId,
              comments: event.currentTarget.value,
            };
            // console.log("hey", cell)

            return handleCellEditForComment({
              row: cell.row,
              column: cell.column,
              value: event.target.value,
              cell: cell,
              obj,
            });
          },
        }),
        // Cell: ({ renderedCellValue, row, table }) =>
        // (
        //   <Tooltip

        //     placement="right"
        //     componentsProps={{
        //       tooltip: {
        //         sx: {
        //           bgcolor: "#53988E",
        //           borderRadius: '100%'
        //         }
        //       }
        //     }}
        //     title={
        //       <div >
        //         <CustomSvgIconForEdit
        //           onClick={() => console.log("hey", renderedCellValue)}
        //           sx={
        //             {
        //               color: "#fff",
        //             }
        //           } />
        //       </div>
        //     }
        //   >
        //     {renderedCellValue}
        //   </Tooltip>
        // )
      },
    ],
    [validationErrors]
  );
  const navigate = useNavigate();
  const onCardBtnClick = () => {
    navigate(AppRoutesPath.CREATE_PROJECT);
  };
  const buttonSet = {
    name: "cancel",
    validate: {},
    pattern: {},
    requiredMessage: "Project Location is required",
    componentType: "button",
    type: "Create New Project",
    label: "Create New Project", // Button label
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
    name: "submit",
    validate: {},
    pattern: {},
    requiredMessage: "Project Location is required",
    componentType: "button",
    label: "Delete Selection", // Button label
    variant: "contained", // Button variant
    className: "btn", // Custom class name for button
    testId: "Delete Selection", // Test ID for testing purposes
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
      // Functionality when button is clicked
      // e.g., handleSubmit() or any other action
    },
  };
  const table = useMaterialReactTable(
    {
      columns,
      data, //data must be memoized or stable (useState, useMemo, defined outside of this component, etc.)

      onRowSelectionChange: setRowSelection, //connect internal row selection state to your own

      getRowId: (row) => row.address, //give each row a more useful id
      state: { rowSelection }, //pass our managed row selection state to the table to use
      enableRowSelection: false,
      enableEditing: true,

      createDisplayMode: "row", // ('modal', and 'custom' are also available)
      editDisplayMode: "cell", // ('modal', 'row', 'table', and 'custom' are also available)
      enableRowActions: true,

      // enableClickToCopy: 'context-menu',
      // enableColumnPinning: true,
      // enableCellActions: true,
      enableColumnActions: false,
      enableSorting: false,
      enableDensityToggle: false,
      enableFullScreenToggle: false,
      enableToolbarInternalActions: false,
      enableColumnPinning: true, // make sure this is enabled

      positionActionsColumn: "last",
      initialState: {
        columnPinning: { right: ["mrt-row-actions"] },
      },
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

      renderBottomToolbarCustomActions: () => (
        <Box sx={{ display: "flex", gap: "1rem", alignItems: "center" }}>
          {Object.values(validationErrors).some((error) => !!error) && (
            <Typography color="error">
              {validationErrors["errorMsg"]}
            </Typography>
          )}
        </Box>
      ),
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
          <div style={{ width: "45%" }}>
            <div className="title">Recent Projects</div>
          </div>
          <div style={{ display: "flex" }}></div>

          <div style={{ display: "flex", gap: "10px", alignSelf: "self-end" }}>
            <CustomSearchBox
              setSearchBoxValue={setSearchBoxValue}
              searchBoxValue={searchBoxValue}
            />
            {Object.keys(rowSelection).length ? (
              <CustomButton key="cancel" {...buttonSetForCancel} />
            ) : (
              ""
            )}
            <CustomButton key="set" {...buttonSet} />
          </div>
        </div>
      ),

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
            backgroundColor: theme.palette.primary.main,

            borderRadius: "8px",
          },
        },
      },

      renderRowActions: ({ row }) => (
        <Box sx={{ display: "flex", gap: "1rem" }}>
          <Tooltip title="redirect">
            <IconButton
              color="#53988E"
              sx={{
                outline: "none !important",
              }}
              onClick={() => {
                navigate(preProcessorRoute(row.original.projectId));
                localStorage.setItem(
                  "currentProjectName",
                  row.original.projectName
                );
              }}
            >
              <div
                style={{ color: "#53988E", fontSize: "14px", fontWeight: 600 }}
              >
                Open
              </div>
              {/* <CustomSvgIconForRedirect sx={
              {
                color: "#53988E",
              }
            } /> */}
            </IconButton>
          </Tooltip>
          <Tooltip title="Delete">
            <IconButton
              sx={{
                outline: "none !important",
              }}
              color="#53988E"
              onClick={() => {
                setCurrentRow(row);
                setOpen(true);
              }}
            >
              <div
                style={{ color: "#53988E", fontSize: "14px", fontWeight: 600 }}
              >
                Delete
              </div>
              {/* <CustomSvgIconForDelete sx={
              {
                color: "#53988E",
              }
            } /> */}
            </IconButton>
          </Tooltip>
        </Box>
      ),

    },
    [rowSelection]
  );

  const handleDelete = () => {
    // console.log("currentRow", currentRow)
    handleDeleteProject(currentRow)
    setOpen(false);
  }
  return (
    <Container
      style={{
        maxWidth: "90vw",
      }}
    >
      <MaterialReactTable table={table} />
      <ConfirmationDialog open={open} onClose={setOpen} onConfirm={handleDelete} title="Confirmation" content="Once deleted, you will not be able to recover this project" />

    </Container>
  );
};

export default CustomTable;

const Container = styled.div`
  .MuiTableRow-root:nth-child(odd) {
    background-color: rgba(223, 228, 229, 0.24);
  }
  .MuiTableRow-root:nth-child(even) td {
    background-color: rgba(223, 228, 229, 0.24);
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
