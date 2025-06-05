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
import SelectWithInput from "../../../container/apv-sim/agriGeneralPage/component/SelectWithInput";



describe("SelectWithInput Component", () => {

    // Component renders without crashing
    it('should render without crashing when provided with valid props', () => {

        const selectComponentData = [{ value: '1', label: 'Option 1' }];
        const fields = [{ id: 1, protectionId: '', height: '' }];
        const register = vi.fn();
        const append = vi.fn();
        const remove = vi.fn();
        const errors = {};

        render(<SelectWithInput selectComponentData={selectComponentData} fields={fields} register={register} append={append} remove={remove} errors={errors} />);
    });

    // Select dropdown initializes with default value
    it('should initialize select dropdown with default value', () => {
        const selectComponentData = [{ value: '1', label: 'Option 1' }];
        const fields = [{ id: 1, protectionId: '', height: '' }];
        const register = vi.fn();
        const append = vi.fn();
        const remove = vi.fn();
        const errors = {};

        render(<SelectWithInput selectComponentData={selectComponentData} fields={fields} register={register} append={append} remove={remove} errors={errors} />);

        // Add your assertions here to check if the select dropdown initializes with the default value
    });

    // Select dropdown updates state on user selection
    // it('should update state on user selection', () => {
    //     const selectComponentData = [{ value: '1', label: 'Option 1' }];
    //     const fields = [{ id: 1, protectionId: '', height: '' }];

    //     const { result } = renderHook(() => useForm());
    //     const name = 'testName';
    //     const control = result.current.control;
    //     const register = vi.fn();
    //     const append = vi.fn();
    //     const remove = vi.fn();
    //     const errors = {};

    //     const { container } = render(<SelectWithInput selectComponentData={selectComponentData} fields={fields} register={register} append={append} remove={remove} errors={errors} />);

    //     const selectDropdown = container.querySelector('#demo-simple-select-standard');

    //     fireEvent.mouseDown(selectDropdown.firstChild);


    //     const regularOption = screen.getByText('Option 1')
    //     fireEvent.click(regularOption)

    //     screen.debug()

    //     expect(selectDropdown.value).toBe('1');
    // });

});

