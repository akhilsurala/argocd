import { styled } from "styled-components";

export const Container = styled.div`
width : 100%;
.email {
  font-size: 16px;
  font-weight: 500;
  line-height: 26px;
  letter-spacing: 0em;
  text-align: left;
  color: ${({ theme }) => theme.palette.text.main};
  margin-bottom: 6px;
}
.password {
  font-size: 16px;
  font-weight: 500;
  line-height: 26px;
  letter-spacing: 0em;
  text-align: left;
  color: ${({ theme }) => theme.palette.text.main};
  margin-bottom: 6px;
}
.btn {
  height: 44px;
  padding: 12px, 24px, 12px, 24px;
  border-radius: 8px;
  gap: 10px;
  background-color: ${({ theme }) => theme.palette.secondary.main};
  text-transform: capitalize;
  color:#FFFFFF
}
.rememberMeSection {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}
.loginContainer {
  width: 60%;
  /* height: 70%; */
  border-radius: 8px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  /* border-color: #e3e3e3; */
}

.loginTextContainer {
  text-align: left;
}

.loginText {
  font-size: 32px;
  font-weight: 700;
  line-height: 39px;
  letter-spacing: 0em;
  text-align: left;
  color: ${({ theme }) => theme.palette.text.main};
}
.loginSubText {
  font-size: 16px;
  font-weight: 500;
  line-height: 26px;
  letter-spacing: 0em;
  text-align: left;
  color: ${({ theme }) => theme.palette.text.light};
  strong {
      color: #00000080;
    }
}

.customBorder {
  width: 70px;
  height: 2px;
  border-radius: 2px;
  background-color: ${({ theme }) => theme.palette.secondary.main};
  margin: 6px 0px;
}
.forgotPassword {
  font-size: 14px;
  font-weight: 600;
  line-height: 26px;
  letter-spacing: 0em;
  cursor: pointer;
  color: ${({ theme }) => theme.palette.primary.main};
}
.createAccountSection {
  display: flex;
  justify-content: center;
}
.noMember {
  font-family: ${({ theme }) => theme.palette.fontFamily.main};
  font-size: 16px;
  font-weight: 500;
  line-height: 26px;
  letter-spacing: 0em;
  text-align: center;
  color:${({ theme }) => theme.palette.text.light};
}
.createAccount {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    background: none;
    border: none;
    padding: 0;
    margin: 0;
    cursor: pointer;
    text-transform: none;
    font-size: 16px;
    font-weight: 700;
    line-height: 26px;
    margin-left: 10px;
    letter-spacing: 0em;
    text-align: center;
    color: ${({ theme }) => theme.palette.primary.main};
  }
  .subWrapper{
    display: flex;
    justify-content: space-between;
  }
  .formContent {
    padding: 40px 20px 20px 20px;
  }
`;