import React from "react";
import { render  } from "@testing-library/react";
import { it } from "vitest";
import SideMenu from "../../container/home/SideMenu";
import ProvideWrapper from "../ProviderWrapper";

describe("SideMenu component", () => {
  it("renders without crashing", () => {
    render(
      <ProvideWrapper>
        <SideMenu />
      </ProvideWrapper>
    );
  });

  it("render all side nav menu", () => {
    const { getByText } = render(
      <ProvideWrapper>
        <SideMenu />
      </ProvideWrapper>
    );

    expect(getByText("Home")).toBeInTheDocument();
    expect(getByText("License Management")).toBeInTheDocument();
    expect(getByText("Learning Resources")).toBeInTheDocument();
    // expect(getByText("Account")).toBeInTheDocument();
    // expect(getByText("My Profile")).toBeInTheDocument();
    // expect(getByText("Log Out")).toBeInTheDocument();
  });


});
