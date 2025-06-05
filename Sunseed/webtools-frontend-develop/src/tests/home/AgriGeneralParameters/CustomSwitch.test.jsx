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
import CustomSwitch from "../../../container/apv-sim/agriGeneralPage/component/CustomSwitch";



describe("Custom Switch", () => {


    // Verify that the component renders without crashing
    it('should render without crashing', () => {

        const { result } = renderHook(() => useForm());
        const control = result.current.control;
        render(<CustomSwitch name="testSwitch" control={control} />);
    });


    // Ensure that the switch toggles between 'Yes' and 'No' states correctly
    it('should toggle between Yes and No states', () => {

        const { result } = renderHook(() => useForm());
        const control = result.current.control;
        render(<CustomSwitch name="testSwitch" control={control} />);

        const switchElement = screen.getByRole('checkbox');
        expect(switchElement).toBeInTheDocument();

        // Clicking the switch should change state to 'Yes'
        switchElement.click();
        expect(switchElement).toHaveAttribute('value', "true");

        // Clicking again should change state back to 'No'
        switchElement.click();
        expect(switchElement).toHaveAttribute('value', "false");


    });
});

