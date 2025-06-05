import React from "react";
import { render, screen } from "@testing-library/react";
import { it } from "vitest";
import Header from "../../container/home/Header";
import ProvideWrapper from "../ProviderWrapper";

describe("Header component", () => {
  it("renders without crashing", () => {
    render(
      <ProvideWrapper>
        <Header />
      </ProvideWrapper>
    );
  });

  it("renders the notifications icon", () => {
    const { getByTestId } = render(
      <ProvideWrapper>
        <Header />
      </ProvideWrapper>
    );
    expect(getByTestId("notifications-icon")).toBeInTheDocument();
  });
});
