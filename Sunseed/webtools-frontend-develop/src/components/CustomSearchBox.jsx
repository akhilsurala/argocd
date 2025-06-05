import { InputAdornment, TextField } from '@mui/material'
import React from 'react'
import { CustomSvgIconForSearch } from '../container/dashboard/CustomSvgIcon'

export default function CustomSearchBox({ setSearchBoxValue, searchBoxValue }) {

    const handleTextSearch = (value) => {
        setSearchBoxValue(value)
    }
    return (
        <TextField
            id="outlined-basic"
            value={searchBoxValue}
            onChange={(e) => handleTextSearch(e.target.value)}
            size='small'
            autoComplete='off'
            InputProps={{
                startAdornment: (
                    <InputAdornment position="start">
                        <CustomSvgIconForSearch />
                    </InputAdornment>
                ),
            }}
            placeholder='Search'
            sx={{
                "& .MuiOutlinedInput-root": {
                    "& fieldset": {
                        borderRadius: "8px",
                    },
                },
            }}
        />
    )
}
