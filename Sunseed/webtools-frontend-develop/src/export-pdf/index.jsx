import React, { useEffect, useRef, useState } from "react";
import html2pdf from "html2pdf.js";
import styled from "styled-components";

import FirstPage from "./FirstPage";
import TermsOfUse from "./TermsOfUse";
import IndexPage from "./IndexPage";
import SimTable from "./SimTable";
import Visualization from "./Visualization";
import Metereaology from "./Metereology";
import { getExportPdf } from "../api/runManager";

const AppContainer = styled.div`

  p {
    margin: 0;
    font-family: "Courier New";
    font-size: 11px;
    font-weight: normal;
    color: #000 !important;
  }
`;

const PdfContent = styled.div`
  box-sizing: border-box;
  width: 100%;
  margin: 0 auto;
  background: #fff;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  color: #333;

  @media screen and (max-width: 600px) {

    h1 {
      font-size: 24px;
    }

    p {
      font-size: 14px;
    }
  }

  /* Hide the content visually but keep it in the DOM */
  /* visibility: ${(props) => (props.isHidden ? "hidden" : "visible")};
  height: ${(props) => (props.isHidden ? "0" : "auto")};
  overflow: ${(props) => (props.isHidden ? "hidden" : "visible")}; */
`;

const PageBreak = styled.div`
  page-break-before: always;
  break-before: page;
`;

const ExportPDF = ({ setDownloadReport }) => {
  const contentRef = useRef();
  const [responses, setResponses] = useState(null);
  const [ids, setIds] = useState([]);
  const [projectIds, setProjectIds] = useState("");
  const [isHidden, setIsHidden] = useState(true);
  const [msg, setMsg] = useState("Data fetching is in progress...");

  const downloadPDF = () => {
    const element = contentRef.current;

    const options = {
      margin: [10, 10, 10, 10],

      filename: responses["project-detail"].projectName || "Generated PDF",
      image: { type: "jpeg", quality: 0.98 },
      html2canvas: {
        scale: 1.1,
        useCORS: true,
        logging: true, // Enable logs to debug issues
      },
      jsPDF: { unit: "mm", format: "a4", orientation: "portrait" },
      pagebreak: { mode: ["avoid-all", "css", "legacy"] },
    };

    html2pdf()
      .set(options)
      .from(element)
      .save()
      .then(() => {
        setMsg("PDF downloaded successfully!");
        setDownloadReport(false);
        // setIsHidden(true); // Hide content again after downloading
      });
  };

  useEffect(() => {
    if (ids.length && projectIds) {
      getExportPdf(projectIds, ids.join(",")).then((response) => {
        setResponses(response.data.data);
        setMsg("PDF is preparing, please wait...");
        // setIsHidden(false); // Show content for PDF generation
      });
    }
  }, [ids, projectIds]);

  useEffect(() => {
    const savedIds = JSON.parse(
      window.localStorage.getItem("selected-runs") || "[]"
    );
    const projectId = JSON.parse(
      window.localStorage.getItem("selected-project") || ""
    );

    setProjectIds(projectId);
    setIds(savedIds);
  }, []);

  const start = performance.now();




  useEffect(() => {
    if (responses) {
      setTimeout(() => {
        downloadPDF();

        const end = performance.now();
        console.log(`Component rendered in ${end - start} ms`);
      }, 2000)
    }
  }, [responses]);

  return (
    <AppContainer>
      {/* <h2>{msg}</h2> */}
      <div >
        {responses && (
          <PdfContent ref={contentRef}>
            <FirstPage projectDetails={responses["project-detail"]} />
            <TermsOfUse />
            <PageBreak />
            <IndexPage />
            <PageBreak />
            <SimTable runDetails={responses["run-detail"]} projectDetails={responses["project-detail"]} />
            <Visualization images={responses["3d-views"]} />
            <Metereaology outputs={responses["outputs"]} />
          </PdfContent>
        )}
      </div>
    </AppContainer>
  );
};

export default ExportPDF;
