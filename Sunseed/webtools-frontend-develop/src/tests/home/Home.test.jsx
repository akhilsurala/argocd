import React from "react";
import { getByTestId, render, screen } from "@testing-library/react";
import { expect, it } from "vitest";
import Footer from "../../container/home/Footer";
import ProvideWrapper from "../ProviderWrapper";
import HomePageWrapper from "../../container/home/HomePageWrapper";
import RecentProjectScreen from "../../container/apv-sim/RecentProjectScreen";

// describe("Footer component", () => {
//   it("renders title correctly", () => {
//     const { getByText } = render(
//       <ProvideWrapper>
//         <HomePageWrapper>
//           <RecentProjectScreen />
//         </HomePageWrapper>
//       </ProvideWrapper>
//     );
//     const title = getByText("Recent Projects");
//     expect(title).toBeInTheDocument();
//   });
//   it("create project button render on screen", () => {
//     const { getByText } = render(
//       <ProvideWrapper>
//         <HomePageWrapper>
//           <RecentProjectScreen />
//         </HomePageWrapper>
//       </ProvideWrapper>
//     );

//     const btn = screen.getByTestId("createProjectButton");
//     expect(btn).toBeInTheDocument();
//   });
// });

describe("test", () => {
  it("true", () => {
  })
})
