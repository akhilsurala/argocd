import React from "react";
import { styled } from "styled-components";

const ProjectCard = ({projectName}) => {
  return (
    <Container>
      <div className="projectTitle">{projectName}</div>
      <div className="cardBody">
        <div className="runCountContainer">
          <div className="count">8</div>
          <div className="countText">No of runs</div>
        </div>
        <div className="locationContainer">
          <div className="location">Assam</div>
          <div className="locationText">Location</div>
        </div>
      </div>
    </Container>
  );
};

export default ProjectCard;

const Container = styled.div`
  background-color: ${({ theme }) => theme.palette.border.secondary};
  border-radius: 18px;
  min-width: 200px;
  padding: 20px;
  color: ${({ theme }) => theme.palette.text.main};

  .cardBody {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
    gap: 20px;
  }
  .projectTitle {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 14px;
    font-weight: 600;
    line-height: 28px;
    text-align: left;
  }
  .count,
  .location {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 600;
    line-height: 24.26px;
    text-align: left;
    color: ${({ theme }) => theme.palette.text.main};
  }

  .countText,
  .locationText {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 13px;
    font-weight: 600;
    line-height: 23px;
    text-align: left;
    color: #474f5080;
  }
`;
