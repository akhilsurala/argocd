import { styled } from "styled-components";

export const Container = styled.div`
padding: 20px;
  .wrapper {
    min-height: calc(100vh - 200px);
    border: 1px solid #ffffff;
    padding: 24px;
    background-color: #ffffff;
    border-radius: 16px;
    // max-width: max(40%, 500px);
  }
  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 700;
    line-height: 24.38px;
    text-align: left;
    color: #252727;
  }
  .formContainer {
    margin: 30px 4px;
  }
  .prevBtn {
    height: 44px;
    padding: 12px, 24px, 12px, 24px;
    border-radius: 8px;
    gap: 10px;
    color: #25272759;
    background-color: ${({ theme }) => theme.palette.background.secondary};
    text-transform: capitalize;
    border: 1px solid;
    border-color: ${({ theme }) => theme.palette.border.main};
    font-weight: 700;
  }
`;