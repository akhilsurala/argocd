import React, { useEffect, useState } from "react";
import CustomTable from "../../components/CustomTable";
import { Box, Grid } from "@mui/material";
import styled from "styled-components";
import { blockAdminUser, getAdminUsers, updateAdminUser } from "../../../api/admin/user";
import CustomSuccessPage from "../../components/CustomSuccessPage";
import { ADMIN_USER, AppRoutesPath, editUser, viewStaticPage } from "../../../utils/constant";
import { useNavigate } from "react-router-dom";
import { myDebounce } from "../../../utils/debounce";

const UserLandingPage = () => {
  const navigate = useNavigate();
  const [reload, setReload] = useState(true);
  const [data, setData] = useState([]);
  const [headers, setHeaders] = useState([]);
  const [searchText, setSearchText] = useState('');

  const labels = ADMIN_USER

  const callGetUsersApi= ()=>{
    getAdminUsers(searchText)
      .then((response) => {
        setData(response.data.data);
        setHeaders(Object.keys(ADMIN_USER).filter(key => key !== 'action'));
      })
      .catch((error) => {
        console.log(error);
      });
  }

  useEffect(() => {
    myDebounce(callGetUsersApi, {}, 500);
  }, [reload, searchText]);

  const handleNewItem = () => {
    navigate(AppRoutesPath.ADMIN_USER_DATABASE_NEW);
  };
  const handleEditItem = (e, row) => {
    navigate(editUser(row.id), {state: data});
  };
  const handleBlockItem = (e, row) => {
    const payload = {
      id: row.id,
      isActive: !row.isActive,
    }
    blockAdminUser(row.id, payload)
      .then((response) => {
        setReload(!reload);
      })
      .catch((error) => {
        console.log(error);
      });
  };
  const handleViewItem = (e, {id}) => {
    navigate(viewStaticPage("user",id));
  };
  const handleDeleteItem = (e, id) => {
    navigate(AppRoutesPath.ADMIN_USER_DATABASE_NEW);
  };

  return (
    <Container
      style={{
        padding: "20px",
        display: "flex",
        flexDirection: "column",
        gap: "20px",
      }}
    >
      <CustomTable
        title="Users"
        createButtonLabel={"Add User"}
        showEyeIcon={false}
        showBlockcon={true}
        showEditIcon={true}
        showRedirectIcon={true}
        showDeleteIcon={false}
        data={data}
        headers={headers}
        labels={labels}
        handleNewItem={handleNewItem}
        handleEditItem={handleEditItem}
        searchText={searchText}
        setSearchText={setSearchText}
        handleBlockItem={handleBlockItem}
        handleViewItem={handleViewItem}
        handleDeleteItem={handleDeleteItem}
      />
    </Container>
  );
};

export default UserLandingPage;

const Container = styled.div`
  min-height: calc(100vh - 200px);

  .cardContainer {
    background-color: #ffffff;
    height: 320px;
    top: 556px;
    left: 262px;
    gap: 0px;
    border-radius: 16px;
    opacity: 0px;
    padding: 40px 20px 0px 20px;
    display: flex;
    flex-direction: column;
  }
  .title {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 20px;
    font-weight: 700;
    line-height: 24.38px;
    text-align: left;
  }
  .count {
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    font-size: 80px;
    font-weight: 700;
    line-height: 97.52px;
    flex: 1;
    margin: 50px auto;
  }
  .userWrapper {
    text-align: center;
    font-family: ${({ theme }) => theme.palette.fontFamily.main};
    color: ${({ theme }) => theme.palette.primary.main};
    font-size: 12px;
    font-weight: 600;
    line-height: 22px;
    padding: 8px;
    border-top: 1px solid;
    border-color: ${({ theme }) => theme.palette.border.light};
    cursor: pointer;
  }
`;
