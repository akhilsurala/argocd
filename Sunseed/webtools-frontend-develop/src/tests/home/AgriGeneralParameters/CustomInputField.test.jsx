import { describe, expect, it } from "vitest";
import {
    fireEvent,
    getByRole,
    render,
    renderHook,
    screen,
    waitFor,
    within,
} from "@testing-library/react";
import CustomInputField from "../../../container/apv-sim/agriGeneralPage/component/CustomInputField";
import ProvideWrapper from "../../ProviderWrapper";
import { useForm } from "react-hook-form";
import { TextField } from "@mui/material";



describe("Custom Input Field", () => {
    it('should render a TextField component with the given props and default values', () => {
        // Arrange
        const { result } = renderHook(() => useForm());
        const name = 'testName';
        const control = result.current.control;
        const errors = {};
        const rules = {};
        const placeholder = 'testPlaceholder';
        const type = 'text';

        // Act
        render(
            <ProvideWrapper>
                <CustomInputField name={name} control={control} errors={errors} rules={rules} placeholder={placeholder} type={type} />
            </ProvideWrapper>
        );

        // Assert
        expect(screen.getByRole('textbox')).toBeInTheDocument();
        expect(screen.getByRole('textbox')).toHaveAttribute('type', type);
        expect(screen.getByRole('textbox')).toHaveAttribute('placeholder', placeholder);
    });
})

// Renders an error message when errors object has a message for the given name
it('should render an error message when errors object has a message for the given name', () => {
    // Arrange
    const { result } = renderHook(() => useForm());
    const name = 'testName';
    const control = result.current.control;
    const errors = { testName: { message: 'Test error message' } };
    const rules = {};
    const placeholder = 'testPlaceholder';
    const type = 'text';

    // Act
    render(
        <ProvideWrapper>
            <CustomInputField name={name} control={control} errors={errors} rules={rules} placeholder={placeholder} type={type} />
        </ProvideWrapper>
    );

    // Assert
    expect(screen.getByText('Test error message')).toBeInTheDocument();
});

// Renders a TextField component with an empty string as placeholder when passed an invalid placeholder prop
it('should render a TextField component with an empty string as placeholder when passed an invalid placeholder prop', () => {
    // Arrange
    const { result } = renderHook(() => useForm());
    const name = 'testName';
    const control = result.current.control;
    const errors = {};
    const rules = {};
    const placeholder = null;
    const type = 'text';

    // Act
    render(
        <ProvideWrapper>
            <CustomInputField name={name} control={control} errors={errors} rules={rules} placeholder={placeholder} type={type} />
        </ProvideWrapper>
    );

    // Assert
    expect(screen.getByRole('textbox')).toHaveAttribute('placeholder', 'Enter value');
});

