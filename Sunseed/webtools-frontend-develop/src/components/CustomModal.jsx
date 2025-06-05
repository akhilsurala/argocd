// import { Box } from "@mui/system";
import React from "react";

import styled, { useTheme } from "styled-components";

import CloseIcon from "@mui/icons-material/Close";
import { Modal } from "@mui/material";

const CustomModal = ({ openModal, children, handleClose, title }) => {
  const theme = useTheme();
  const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: document.documentElement.clientWidth > 768 ? 500 : 327,
    bgcolor: "background.paper",
    boxShadow: 24,
    p: 4,
    backgroundColor: theme.palette.background.secondary,
    borderRadius: 8,
    outline:'none',
  };
  return (
    <div>
      <Modal
        open={openModal}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <BoxWrapper style={style}>
          <div className="titleWrapper">
            <div className="title">{title}</div>
            <CloseIcon onClick={handleClose} className="closeIcon" />
          </div>
          <div className="contentWrapper">

          {children}
          </div>
        </BoxWrapper>
      </Modal>
    </div>
  );
};

export default CustomModal;

/******************/
/***** Styles *****/
/******************/

const BoxWrapper = styled.section`
  .titleWrapper {
    display: flex;
    border-radius: 8px 8px 0px 0px;
    margin: 20px 14px;
    padding-bottom: 8px;
    border-bottom: 1px solid;
    border-color: ${({ theme }) => theme.palette.border.main};
}

.title {
    text-align: left;
    flex: 1;
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 16px;
    font-weight: 600;
    line-height: 19.5px;
    text-align: left;
    color:#252727
  }

  .contentWrapper {
    display: flex;
    color: ${({ theme }) => theme.palette.text.main};
    flex-direction: column;
    justify-content: center;
    margin: 0px 20px 10px 20px;
  }

  .closeIcon {
    cursor: pointer;
    margin-right: 10px;
    color: ${({ theme }) => theme.palette.text.main};
  }

`;
