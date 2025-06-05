import { styled } from "styled-components";

export const Container = styled.div`
  padding: 20px;
  .wrapper {
    min-height: calc(100vh - 200px);
    border: 1px solid #ffffff;
    padding: 24px;
    background-color: #ffffff;
    border-radius: 16px;
    max-width: max(40%, 500px);
  }
  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 700;
    line-height: 24.38px;
    text-align: left;
    color: ${({ theme }) => theme.palette.text.main};
  }
  .formContainer {
    margin: 30px 4px;
  }
  .titleWrapper {
    display: flex;
  }

  .prevButton {
    font-family: "Open Sans";
    font-size: 14px;
    font-weight: 700;
    text-transform: capitalize;
    background: transparent;
    color: grey;
    &:hover {
      background-color: transparent;
    }
    align-self: flex-end;
    width: 140px;
  }
  .submitButton {
    font-family: "Open Sans";
    font-size: 14px;
    font-weight: 700;

    margin-left: 20px;
    text-transform: capitalize;
    background: ${({ theme }) => theme.palette.secondary.main};
    &:hover {
      background-color: ${({ theme }) => theme.palette.secondary.main};
    }

    align-self: flex-end;
    width: 140px;
  }

  .navigationWrapper {
    font-family: Montserrat;
    font-size: 16px;
    font-weight: 600;
    line-height: 26px;
    text-align: left;
    text-underline-position: from-font;
    text-decoration-skip-ink: none;
    color:#474F50;
    margin-bottom:12px;
    cursor: pointer;
    display: flex;
    gap: 10px;
  }
`;
