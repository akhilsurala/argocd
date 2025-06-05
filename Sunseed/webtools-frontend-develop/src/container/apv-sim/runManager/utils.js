export const getHourlySellingRatesRows = (data) => {
    function createData(hour, rate, id) {
      return { hour, rate, id };
    }

    const rows = Array.from({ length: 24 }, (_, i) => {
      const hour = `${String(i).padStart(2, "0")}:00 - ${String(i + 1).padStart(
        2,
        "0",
      )}:00`;
      return createData(hour, data[i], i);
    });
    return rows;
  };

  export const getProtectionLayerRows = (data) => {
    function createData(hour, rate, id) {
      return { hour, rate, id };
    }

    const rows = data?.map((data) => {
      const protectionLayerName = data.protectionLayerName;
      const height = data.height;
      const id = data.agriPvProtectionHeightId;
      return createData(protectionLayerName, height, id);
    });

    return rows;
  };

  export const statusColor = (value ) => {
    if (value === "running")
      return {     
       color: "#0079B3",
      backgroundColor: "#0079B31A",
     };
    if (value === "completed")
      return {
        color: "#6BAA44",
        backgroundColor: "#6BAA441A",
      };
    if (value === "queued")
      return {
        color: "#D3C11D",
        backgroundColor: "#D3C11D1A",
      };
    if (value === "holding")
      return {
        color: "#776274",
        backgroundColor: "#7762741A",
      };

    //change colors not defined in design
    if (value === "pause")
      return {
        color: "#6BAA44",
        backgroundColor: "#6BAA441A",
      };

      if (value === "failed")
      return {
        color: "#ff2c2c",
        backgroundColor: "#ff2c2c1A",
      };
    
      
    return { color: "#DB8C47", backgroundColor: "#DB8C471A" };
  };

  export const ColumnType = {
    text: 'TEXT',
    link: 'LINK',
    status: 'STATUS',
    toolTip: "TOOLTIP",
  }

  export const getColumnHeaders = ({
    key,
    header,
    size=150,
    enableEditing=false,
    disableResizing=false
}) => {
  return ({
    accessorKey: key,
    header: header,
    size: size,
    enableEditing: enableEditing,
    disableResizing: disableResizing
  });
}