import React from "react";
import { Controller } from "react-hook-form";
import {
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
} from "@mui/material";

interface FormSelectProps {
  name: string;
  control: any;
  label: string;
  options: string[];
  required?: boolean;
  error?: string;
  sx?: object;
}

const FormSelect: React.FC<FormSelectProps> = ({
  name,
  control,
  label,
  options,
  required = false,
  error,
  sx,
}) => {
  return (
    <FormControl fullWidth sx={{ mb: 2 }}>
      <InputLabel>{label}</InputLabel>
      <Controller
        name={name}
        control={control}
        defaultValue=""
        rules={required ? { required: `${label} is required` } : {}}
        render={({ field }) => (
          <Select
            {...field}
            required={required}
            error={!!error}
            sx={{ ...sx }}
            label={label}
          >
            {options.map((option) => (
              <MenuItem key={option} value={option}>
                {option}
              </MenuItem>
            ))}
          </Select>
        )}
      />
      {error && <FormHelperText>{error}</FormHelperText>}
    </FormControl>
  );
};

export default FormSelect;
