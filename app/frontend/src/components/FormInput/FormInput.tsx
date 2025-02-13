import React from "react";
import { Controller } from "react-hook-form";
import { TextField, Typography } from "@mui/material";

interface FormInputProps {
  name: string;
  control: any;
  rules?: object;
  label: string;
  multiline?: boolean;
  rows?: number;
  required?: boolean;
  error?: string;
  sx?: object;
}

const FormInput: React.FC<FormInputProps> = ({
  name,
  control,
  rules,
  label,
  multiline = false,
  rows = 1,
  required = false,
  error,
  sx,
}) => {
  return (
    <>
      <Typography variant="h6">{label}</Typography>
      <Controller
        name={name}
        control={control}
        defaultValue=""
        rules={{
          ...rules,
          required: required ? `${label} is required` : false,
        }}
        render={({ field }) => (
          <TextField
            {...field}
            label={label}
            fullWidth
            required={required}
            multiline={multiline}
            rows={rows}
            error={!!error}
            helperText={error}
            sx={{ mb: 2, ...sx }}
          />
        )}
      />
    </>
  );
};

export default FormInput;
