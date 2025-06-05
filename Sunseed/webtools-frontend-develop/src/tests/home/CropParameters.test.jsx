import { describe, expect, it } from "vitest";
import ProjectWrapper from "../../container/apv-sim/ProjectWrapper";
import {
  fireEvent,
  getByRole,
  render,
  screen,
  waitFor,
  within,
} from "@testing-library/react";
import ProvideWrapper from "../ProviderWrapper";
import HomePageWrapper from "../../container/home/HomePageWrapper";
import CustomDropDown from "../../components/CustomDropDown";

describe("test", () => {
  it("true", () => {
  })
})
/*
describe("test CropParameters form", () => {
  it("Check if all the fields are rendered", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const button = screen.getByText("Next");
    expect(button).toBeInTheDocument();

    const croppingCycle = screen.getByTestId("croppingCycle");
    expect(croppingCycle).toBeInTheDocument();
    const bedType = screen.getByTestId("bedType");
    expect(bedType).toBeInTheDocument();
    const addCrops = screen.getByTestId("addCrops");
    expect(addCrops).toBeInTheDocument();
    const spacing = screen.getByTestId("spacing");
    expect(spacing).toBeInTheDocument();
    expect(screen.getByText("Date of Sowing")).toBeInTheDocument();
    const croppingPattern = screen.getByTestId("croppingPattern");
    expect(croppingPattern).toBeInTheDocument();
    const startPointOffset = screen.getByTestId("startPointOffset");
    expect(startPointOffset).toBeInTheDocument();
    const bedCC = screen.getByTestId("bedCC");
    expect(bedCC).toBeInTheDocument();
    const bedAzimuth = screen.getByTestId("bedAzimuth");
    expect(bedAzimuth).toBeInTheDocument();
  });
});

describe("test CropParameters submit button", () => {
  it("Check if submit button is rendered", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const button = screen.getByText("Next");
    expect(button).toBeInTheDocument();

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();
  });
  it("Check if submit button is working on click", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const button = screen.getByText("Next");
    expect(button).toBeInTheDocument();

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(
        screen.getByText("Cropping Cycle is required")
      ).toBeInTheDocument();
      expect(screen.getByText("Add Crops is required")).toBeInTheDocument();
    });
  });
});

describe("test cases for cropping cycle field", () => {
  it("placeholder rendered successfully", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );
    const val = expect(screen.getAllByText("Select Cycle"));
    expect(screen.getAllByLabelText("Select Cycle")).toHaveLength(2);
  });

  it("Check if cropping cycle is rendered", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const croppingCycle = screen.getByTestId("croppingCycle");
    expect(croppingCycle).toBeInTheDocument();
  });
  // it("test dropdown select functionality", () => {
  //   const data = {
  //     key: "CroppingCycle",
  //       label: "Cropping Cycle",
  //       //   handleChange: handleChange,
  //       isRequired: true,
  //       placeHolder: "Select Cycle",
  //       dataSet: ["Rabi", "Kharif", "Zaid", "Rabi-Kharif"],
  //       name: "CroppingCycle",
  //       validate: {},
  //       pattern: {},
  //       inputProps: {
  //       },
  //       componentType: "selectDropdown",
  //       maxLength: {},
  //       testId: "croppingCycle",
  //   }
  //   render(
  //     <ProvideWrapper>
  //       <HomePageWrapper>
  //         <ProjectWrapper>
  //           <CustomDropDown {...data} />
  //           </ProjectWrapper>
  //       </HomePageWrapper>
  //     </ProvideWrapper>
  //   );

  //   const croppingCycle = screen.getByTestId("croppingCycle");
  //   expect(croppingCycle).toBeInTheDocument();
  //   fireEvent.mouseDown(croppingCycle);
  //   const listbox = screen.getByRole("listbox", { name: "Select Cycle" });
  // });
});

describe("test cases for Spacing field", () => {
  it("placeholder rendered successfully", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );
    expect(screen.getByPlaceholderText("Enter Spacing")).toBeInTheDocument();
  });

  it("Check if Spacing textField is rendered", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const spacing = screen.getByTestId("spacing");
    expect(spacing).toBeInTheDocument();
  });

  it("Test if textfield is changing values on every input", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const textField = screen.getByPlaceholderText("Enter Spacing");

    fireEvent.change(textField, { target: { value: "some random input" } });
    expect(textField).toHaveValue("some random input");
  });
  it("Field required validation working properly on submit button click", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(screen.getByText("Spacing is required")).toBeInTheDocument();
    });
  });

  it("Error message for non-numeric values validation", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    const textField = screen.getByPlaceholderText("Enter Spacing");

    fireEvent.change(textField, { target: { value: "some random input" } });
    expect(textField).toHaveValue("some random input");

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(
        screen.getByText(
          "Please enter an integer value within the range of 0 to 10 for Spacing"
        )
      ).toBeInTheDocument();
    });
  });
  it("text field should take values between 1 to 10 only", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    const textField = screen.getByPlaceholderText("Enter Spacing");

    fireEvent.change(textField, { target: { value: "20" } });
    expect(textField).toHaveValue("20");

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(
        screen.getByText(
          "Please enter an integer value within the range of 0 to 10 for Spacing"
        )
      ).toBeInTheDocument();
    });

    fireEvent.change(textField, { target: { value: "-20" } });
    expect(textField).toHaveValue("-20");

    expect(
      screen.getByText(
        "Please enter an integer value within the range of 0 to 10 for Spacing"
      )
    ).toBeInTheDocument();
  });
  it("No Error message should display on valid input values", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    const textField = screen.getByPlaceholderText("Enter Spacing");

    fireEvent.change(textField, { target: { value: "5" } });
    expect(textField).toHaveValue("5");

    await waitFor(() => {
      expect(
        screen.queryByText(
          "Please enter an integer value within the range of 0 to 10 for Spacing"
        )
      ).not.toBeInTheDocument();
    });

  });
});

describe("test cases for Cropping Pattern field", () => {
  it("placeholder rendered successfully", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );
    expect(screen.getByPlaceholderText("Enter Pattern")).toBeInTheDocument();
  });

  it("Check if Spacing textField is rendered", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const croppingPattern = screen.getByTestId("croppingPattern");
    expect(croppingPattern).toBeInTheDocument();
  });

  it("Test if textfield is changing values on every input", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const textField = screen.getByPlaceholderText("Enter Pattern");

    fireEvent.change(textField, { target: { value: "some random input" } });
    expect(textField).toHaveValue("some random input");
  });
  it("Field required validation working properly on submit button click", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(screen.getByText("Cropping Pattern is required")).toBeInTheDocument();
    });
  });

  it("Error message for non-numeric values validation", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    const textField = screen.getByPlaceholderText("Enter Pattern");

    fireEvent.change(textField, { target: { value: "some random input" } });
    expect(textField).toHaveValue("some random input");

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(
        screen.getByText(
          "Please enter an integer value within the range of 0 to 10"
        )
      ).toBeInTheDocument();
    });
  });
  it("text field should take values between 1 to 10 only", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    const textField = screen.getByPlaceholderText("Enter Pattern");

    fireEvent.change(textField, { target: { value: "20" } });
    expect(textField).toHaveValue("20");

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(
        screen.getByText(
          "Please enter an integer value within the range of 0 to 10"
        )
      ).toBeInTheDocument();
    });

    fireEvent.change(textField, { target: { value: "-20" } });
    expect(textField).toHaveValue("-20");

    expect(
      screen.getByText(
        "Please enter an integer value within the range of 0 to 10"
      )
    ).toBeInTheDocument();
  });
  it("No Error message should display on valid input values", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    const textField = screen.getByPlaceholderText("Enter Pattern");

    fireEvent.change(textField, { target: { value: "5" } });
    expect(textField).toHaveValue("5");

    await waitFor(() => {
      expect(
        screen.queryByText(
          "Please enter an integer value within the range of 0 to 10"
        )
      ).not.toBeInTheDocument();
    });

  });
});

describe("test cases for Start Point Offset field", () => {
  it("placeholder rendered successfully", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );
    expect(screen.getByPlaceholderText("Enter Point Offset")).toBeInTheDocument();
  });

  it("Check if Spacing textField is rendered", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const startPointOffset = screen.getByTestId("startPointOffset");
    expect(startPointOffset).toBeInTheDocument();
  });

  it("Test if textfield is changing values on every input", () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const textField = screen.getByPlaceholderText("Enter Point Offset");

    fireEvent.change(textField, { target: { value: "some random input" } });
    expect(textField).toHaveValue("some random input");
  });
  it("Field required validation working properly on submit button click", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(screen.getByText("Start Point Offset is required")).toBeInTheDocument();
    });
  });

  it("Error message for non-numeric values validation", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    const textField = screen.getByPlaceholderText("Enter Point Offset");

    fireEvent.change(textField, { target: { value: "some random input" } });
    expect(textField).toHaveValue("some random input");

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(
        screen.getByText(
          "Please enter an integer value within the range of 0 to 10"
        )
      ).toBeInTheDocument();
    });
  });
  it("text field should take values between 1 to 10 only", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    const textField = screen.getByPlaceholderText("Enter Point Offset");

    fireEvent.change(textField, { target: { value: "20" } });
    expect(textField).toHaveValue("20");

    fireEvent.click(btn);
    // Wait for the error message to appear on the screen
    await waitFor(() => {
      expect(
        screen.getByText(
          "Please enter an integer value within the range of 0 to 10"
        )
      ).toBeInTheDocument();
    });

    fireEvent.change(textField, { target: { value: "-20" } });
    expect(textField).toHaveValue("-20");

    expect(
      screen.getByText(
        "Please enter an integer value within the range of 0 to 10"
      )
    ).toBeInTheDocument();
  });
  it("No Error message should display on valid input values", async () => {
    render(
      <ProvideWrapper>
        <HomePageWrapper>
          <ProjectWrapper />
        </HomePageWrapper>
      </ProvideWrapper>
    );

    const btn = screen.getByTestId("submitButton");
    expect(btn).toBeInTheDocument();

    const textField = screen.getByPlaceholderText("Enter Point Offset");

    fireEvent.change(textField, { target: { value: "5" } });
    expect(textField).toHaveValue("5");

    await waitFor(() => {
      expect(
        screen.queryByText(
          "Please enter an integer value within the range of 0 to 10"
        )
      ).not.toBeInTheDocument();
    });

  });
});
*/