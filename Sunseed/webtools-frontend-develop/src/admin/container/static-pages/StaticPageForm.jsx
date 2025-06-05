import React, { useEffect, useCallback, useMemo, useRef, useState } from "react";
import styled from "styled-components";
import { useTheme } from "styled-components";
import { Box, Button, Stack } from "@mui/material";

import { AppRoutesPath } from "../../../utils/constant";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import { getAdminStaticPage, saveAdminStaticPage, updateAdminStaticPage } from "../../../api/admin/staticPage";
import { Container } from "../../components/RichTextEditorPageCss";

import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';

import { useForm } from "react-hook-form";
import CustomInputField from "../../../container/apv-sim/agriGeneralPage/component/CustomInputField";
import AdminBackNavigation from "../../components/AdminBackNavigation";

const StaticPageForm = () => {
  const theme = useTheme();
  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams();
  const [loader,setLoader] = useState(false);
  const pageHeading = id ? "Edit Learning Resource" : "Add Learning Resource";

  const quill = useRef();

  function handler() {
    console.log(value);
  }

  const {
    handleSubmit,
    control,
    watch,
    register,
    setValue,
    formState: { errors, },
  } = useForm({
    mode: 'all',
  });

  useEffect(() => {
    register("articleContent", { required: true, minLength: 30 });
  }, [register]);

  const onEditorStateChange = (editorState) => {
    setValue("articleContent", editorState);
  };

  const onSubmit = (data) => {
    const payload = {
      ...(id && { id: id }),
      title: data.title,
      description: data.articleContent,
      summary: data.title,
      hide: !!data.hide,
      pageType: 'icense management',
    }
    const apiFunction = id ? updateAdminStaticPage : saveAdminStaticPage;
    const apiParameters = id ? [id, payload] : [payload];
    
    apiFunction(...apiParameters)
      .then((response) => {
        navigate(AppRoutesPath.ADMIN_STATIC_PAGE);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const articleContent = watch("articleContent");

  const imageHandler = useCallback(() => {
    // Create an input element of type 'file'
    const input = document.createElement("input");
    input.setAttribute("type", "file");
    input.setAttribute("accept", "image/*");
    input.click();

    // When a file is selected
    input.onchange = () => {
      const file = input.files[0];
      const reader = new FileReader();

      // Read the selected file as a data URL
      reader.onload = () => {
        const imageUrl = reader.result;
        const quillEditor = quill.current.getEditor();

        // Get the current selection range and insert the image at that index
        const range = quillEditor.getSelection(true);
        quillEditor.insertEmbed(range.index, "image", imageUrl, "user");
      };

      reader.readAsDataURL(file);
    };
  }, []);

  const modules = useMemo(
    () => ({
      toolbar: {
        container: [
          [{ header: [2, 3, 4, false] }],
          ["bold", "italic", "underline", "blockquote"],
          [{ color: [] }],
          [
            { list: "ordered" },
            { list: "bullet" },
            { indent: "-1" },
            { indent: "+1" },
          ],
          ["link", "image"],
          ["clean"],
        ],
        handlers: {
          image: imageHandler,
        },
      },
      clipboard: {
        matchVisual: true,
      },
    }),
    [imageHandler]
  );

  const formats = [
    "header",
    "bold",
    "italic",
    "underline",
    "strike",
    "blockquote",
    "list",
    "bullet",
    "indent",
    "link",
    "image",
    "color",
    "clean",
  ];

  useEffect(() => {
    if(id){
      setLoader(true)
      getAdminStaticPage(id)
        .then((response) => {
          const data = response.data.data;
          setLoader(false);
          setValue('title', response.data.data.title);
          setValue('summary', response.data.data.summary);
          setValue('articleContent', response.data.data.description);
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }, [id]);
  
  const handlePCancelButton = () => {
    navigate(AppRoutesPath.ADMIN_STATIC_PAGE);
  };

  return (
    <Container>
      {/* <AdminBackNavigation title="Back to Learning" onClick={handlePCancelButton} /> */}
      <Box className="wrapper">
        <Box className="title">{pageHeading}</Box>
        <Box className="formContainer" sx={{ display: 'flex', flexDirection: 'column'}}>


        <div style={{ marginRight: '5px', fontWeight:500, fontFamily: theme.palette.fontFamily.main,color: '#252727' }} >Title</div>
        <Gap />

        <Box sx={{ width: '100%'}}>
          <CustomInputField name={'title'} type={'text'} noFlotingValue={true} control={control} errors={errors} disabled={false} rules={{
            required: "Enter title", 
            pattern: {
              value : /^(?=.*[a-zA-Z]).+$/,
              message : "Invalid format"},
            maxLength: {
              value: 100,
              message: "Maximum 100 characters are allowed"
            }
          }} />
        </Box>

        {/* <div style={{ marginRight: '5px', fontWeight:500, fontFamily: theme.palette.fontFamily.main }} >Summary</div>
        <Gap />
        <CustomInputField name={'summary'} type={'text'} noFlotingValue={true} control={control} errors={errors} disabled={false} rules={{
          required: "Enter summary", 
          pattern: {
            value : /^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$/,
            message : "Invalid format"},
          maxLength: {
            value: 100,
            message: "Maximum 100 characters are allowed"
          }
        }} /> */}

        {/* Summary Input */}
        {/* <input
          type="text"
          placeholder="Summary"
          {...register("summary", { required: true, minLength: 10 })}
        />
        <p className="Error">{errors.summary && "Summary is required (min 10 chars)"}</p> */}
        <Gap />
        <div style={{ marginRight: '5px', fontWeight:500, fontFamily: theme.palette.fontFamily.main,color: '#252727' }} >Description</div>
        <Gap />
        <Stack spacing={4}>
        <ReactQuill
          ref={(el) => (quill.current = el)}
          theme="snow"
          style={{ height: '50vh', overflow: 'auto', color:'#000' }} 
          value={articleContent}
          formats={formats}
          modules={modules}
          onChange={onEditorStateChange}
          // onBlur={onEditorStateChange}
        />
        <p style={{ backgroundColor: 'inherit', color: theme.palette.primary.main, margin: '0px' }}>{errors.articleContent && "Enter valid content (min 30 chars)"}</p>
        
        <Box sx={{ display: 'flex', alignSelf: "flex-end", gap: '10px' }}>
          <Button
              type="reset"
              className="prevBtn"
              data-testid="previousButton"
              onClick={(e) => {
                e.preventDefault();
                handlePCancelButton();
              }}
              sx={{
                "&:hover": {
                  backgroundColor: theme.palette.background.secondary,
                },
                ...({
                  color: '#474F5080',
                  border: '1px solid',
                  borderRadius: '8px',
                  alignSelf: "flex-end",
                  width: "140px",
                }),
              }}
            >
              Cancel
            </Button>
         <Button
            type="submit"
            className="btn"
            data-testid="submitButton"
            onClick={handleSubmit(onSubmit)}
            sx={{
              "&:hover": {
                backgroundColor: theme.palette.secondary.main,
              },
              color: "white",
              backgroundColor: theme.palette.secondary.main,
              alignSelf: "flex-end",
              width: "100px",
              borderRadius: '8px',
              padding: '8px 12px',
            }}
          >
            Submit
          </Button>
        </Box>
         
        </Stack>
        </Box>
      </Box>
    </Container>
  );
};

export default StaticPageForm;

const Gap = styled.div`
  padding: 10px;
`