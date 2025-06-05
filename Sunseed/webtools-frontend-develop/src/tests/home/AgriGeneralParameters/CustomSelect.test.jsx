import { describe, expect, it, vi } from "vitest";
import {
  act,
  fireEvent,
  getByRole,
  render,
  renderHook,
  screen,
  waitFor,
  within,
} from "@testing-library/react";
import ProvideWrapper from "../../ProviderWrapper";
import { useForm } from "react-hook-form";
import { TextField } from "@mui/material";
import CustomSelect from "../../../container/apv-sim/agriGeneralPage/component/CustomSelect";



describe("should work proper", () => {


  it('should render Select component with provided data', () => {
    // Arrange
    const { result } = renderHook(() => useForm());
    const name = 'testName';
    const control = result.current.control;
    const errors = {};
    const data = [
      { value: 'value1', label: 'Label 1' },
      { value: 'value2', label: 'Label 2' },
      { value: 'value3', label: 'Label 3' },
    ];

    // Act
    render(
      <ProvideWrapper>
        <CustomSelect name={name} control={control} errors={errors} data={data} />
      </ProvideWrapper>);

    // Assert
    const selectElement = screen.getByRole('combobox');
    expect(selectElement).toBeInTheDocument();
    expect(selectElement).toHaveAttribute('id', 'demo-simple-select');

    // act(() => {
    //     fireEvent.click(screen.getAllByRole());
    // })

    fireEvent.mouseDown(screen.getByTestId('id').firstChild);

    const menuItems = screen.getAllByRole('option');
    expect(menuItems).toHaveLength(data.length);
    data.forEach((item, index) => {
      expect(menuItems[index]).toHaveTextContent(item.label);
      expect(menuItems[index]).toHaveAttribute('data-value', item.value);
    });
  });


  // Renders a Select component with no data.
  it('should render Select component with no data', () => {
    // Arrange
    const { result } = renderHook(() => useForm());
    const name = 'testName';
    const control = result.current.control;
    const errors = {};
    const data = [];

    // Act
    render(<ProvideWrapper>
      <CustomSelect name={name} control={control} errors={errors} data={data} />
    </ProvideWrapper>);

    // Assert
    const selectElement = screen.getByRole('combobox');
    expect(selectElement).toBeInTheDocument();

    fireEvent.mouseDown(screen.getByTestId('id').firstChild);


    expect(selectElement).toHaveAttribute('id', 'demo-simple-select');

    const menuItems = screen.queryAllByRole('option');

    expect(menuItems).toHaveLength(0);
  });
});

