import React from "react";
import { Controller } from "react-hook-form";
import { Autocomplete, TextField, FormControl } from "@mui/material";

interface FormAutoCompleteProps {
  name: string;
  control: any;
  rules?: object;
  label: string;
  options: string[];
  required?: boolean;
  error?: string;
  sx?: object;
}

const FormAutoComplete: React.FC<FormAutoCompleteProps> = ({
  name,
  control,
  rules,
  label,
  options,
  required = false,
  error,
  sx,
}) => {
  return (
    <FormControl fullWidth sx={{ mb: 2 }} error={!!error}>
      <Controller
        name={name}
        control={control}
        defaultValue=""
        rules={required ? { ...rules, required: `${label} is required` } : {}}
        render={({ field }) => (
          <Autocomplete
            {...field}
            freeSolo // Allows user to type custom values
            options={options}
            onChange={(_, newValue) => field.onChange(newValue)}
            renderInput={(params) => (
              <TextField
                {...params}
                label={label}
                error={!!error}
                helperText={error}
              />
            )}
            sx={{ ...sx }}
          />
        )}
      />
    </FormControl>
  );
};

export default FormAutoComplete;
