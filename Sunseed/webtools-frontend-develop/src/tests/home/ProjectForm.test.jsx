import React from "react";
import {
  fireEvent,
  getByTestId,
  render,
  screen,
  waitFor,
} from "@testing-library/react";
import { expect, it } from "vitest";
import Footer from "../../container/home/Footer";
import ProvideWrapper from "../ProviderWrapper";
import HomePageWrapper from "../../container/home/HomePageWrapper";
import ProjectForm from "../../container/apv-sim/ProjectForm";

describe("test", () => {
  it("true", () => {
  })
})

// describe("Footer component", () => {
//   it("renders title correctly", () => {
//     const { getByText } = render(
//       <ProvideWrapper>
//         <HomePageWrapper>
//           <ProjectForm />
//         </HomePageWrapper>
//       </ProvideWrapper>
//     );
//     const title = getByText("New Project General Parameters");
//     expect(title).toBeInTheDocument();
//   });
//   it("create project name textfield rendered", () => {
//     render(
//       <ProvideWrapper>
//         <HomePageWrapper>
//           <ProjectForm />
//         </HomePageWrapper>
//       </ProvideWrapper>
//     );

//     const textField = screen.getByTestId("projectName");
//     expect(textField).toBeInTheDocument();
//   });
//   it("Check if project name label is rendered", () => {
//     render(
//       <ProvideWrapper>
//         <HomePageWrapper>
//           <ProjectForm />
//         </HomePageWrapper>
//       </ProvideWrapper>
//     );

//     const label = screen.getByText("Project Name");
//     expect(label).toBeInTheDocument();
//   });
//   it("Texfield should accept only numbers", () => {
//     render(
//       <ProvideWrapper>
//         <HomePageWrapper>
//           <ProjectForm />
//         </HomePageWrapper>
//       </ProvideWrapper>
//     );

//     const textField = screen.getByTestId("projectName").querySelector("input");
//     expect(textField).toBeInTheDocument();

//     fireEvent.change(textField, { target: { value: "Sunseed One" } });
//     expect(textField).toHaveValue("Sunseed One");
//     fireEvent.change(textField, { target: { value: "Sunseed Two" } });
//     expect(textField).toHaveValue("Sunseed Two");
//   });
//   it("Check if Create Run button is rendered on screen", () => {
//     render(
//       <ProvideWrapper>
//         <HomePageWrapper>
//           <ProjectForm />
//         </HomePageWrapper>
//       </ProvideWrapper>
//     );

//     const btn = screen.getByTestId("submitButton");
//     expect(btn).toBeInTheDocument();
//   });

//   it("Test if project Name validation is correctly integrated", async () => {
//     render(
//       <ProvideWrapper>
//         <HomePageWrapper>
//           <ProjectForm />
//         </HomePageWrapper>
//       </ProvideWrapper>
//     );

//     const textField = screen.getByTestId("projectName").querySelector("input");
//     expect(textField).toBeInTheDocument();

//     fireEvent.change(textField, {
//       target: {
//         value:
//           "write more than thirty characters to check max length validation",
//       },
//     });

//     expect(textField).toHaveValue(
//       "write more than thirty characters to check max length validation"
//     );

//     const button = screen.getByText("Create Run");
//     fireEvent.click(button);
//     const btn = screen.getByTestId("submitButton");
//     expect(btn).toBeInTheDocument();
//     fireEvent.click(btn);
//     const val = await screen.getByText("required");
//     expect(val).toBeInTheDocument();
//   });

// test cases are commented out because the fields are removed from the form.

// it("Check if default Fixed Land is selected", () => {
//   render(
//     <ProvideWrapper>
//       <HomePageWrapper>
//         <ProjectForm />
//       </HomePageWrapper>
//     </ProvideWrapper>
//   );

//   const radioButton1 = screen.getByLabelText("Fixed Land");
//   const radioButton2 = screen.getByLabelText("Fixed PV Capacity");
//   expect(radioButton1.checked).toBeTruthy();
//   expect(radioButton2.checked).not.toBeTruthy();
// });
// it("on click fixed pv capacity is selected", () => {
//   render(
//     <ProvideWrapper>
//       <HomePageWrapper>
//         <ProjectForm />
//       </HomePageWrapper>
//     </ProvideWrapper>
//   );

//   const radioButton1 = screen.getByLabelText("Fixed Land");
//   const radioButton2 = screen.getByLabelText("Fixed PV Capacity");
//   expect(radioButton1.checked).toBeTruthy();

//   fireEvent.click(radioButton2);
//   expect(radioButton2.checked).toBeTruthy();
// });
// it("Check if area textfield rendered successfully on toggle", () => {
//   render(
//     <ProvideWrapper>
//       <HomePageWrapper>
//         <ProjectForm />
//       </HomePageWrapper>
//     </ProvideWrapper>
//   );

//   const radioButton1 = screen.getByLabelText("Fixed Land");
//   expect(radioButton1.checked).toBeTruthy();
//   expect(screen.getByPlaceholderText("Enter Land Size")).toBeInTheDocument();
// });
// it("Check if fixed pv capacity textfield rendered successfully and removed on toggle", () => {
//   render(
//     <ProvideWrapper>
//       <HomePageWrapper>
//         <ProjectForm />
//       </HomePageWrapper>
//     </ProvideWrapper>
//   );

//   const radioButton1 = screen.getByLabelText("Fixed Land");
//   const radioButton2 = screen.getByLabelText("Fixed PV Capacity");
//   expect(radioButton1.checked).toBeTruthy();

//   fireEvent.click(radioButton2);
//   expect(radioButton2.checked).toBeTruthy();
//   expect(screen.getByPlaceholderText("Enter kw Value")).toBeInTheDocument();

//   fireEvent.click(radioButton1);
//   expect(radioButton1.checked).toBeTruthy();
//   expect(screen.queryByPlaceholderText("Enter kw Value")).not.toBeInTheDocument();

// });

// it("Test if project Name validation is correctly integrated", async () => {
//   render(
//     <ProvideWrapper>
//       <HomePageWrapper>
//         <ProjectForm />
//       </HomePageWrapper>
//     </ProvideWrapper>
//   );


//   const button = screen.getByText("Create Run");
//   fireEvent.click(button);
//   const btn = screen.getByTestId("submitButton");
//   expect(btn).toBeInTheDocument();
//   fireEvent.click(btn);
//   const val = screen.getByText("Project Name is required");
//   expect(val).toBeInTheDocument();
// });
// });
