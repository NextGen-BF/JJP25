import React from "react";
import { Controller } from "react-hook-form";
import { TextField, Typography } from "@mui/material";

interface FormInputProps {
  defaultValue: string;
  name: string;
  control: any;
  rules?: object;
  label: string;
  multiline?: boolean;
  rows?: number;
  required?: boolean;
  error?: string;
  sx?: object;
  onChange?: (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => void;
}

const FormInput: React.FC<FormInputProps> = ({
  defaultValue,
  name,
  control,
  rules,
  label,
  multiline = false,
  rows = 1,
  required = false,
  error,
  sx,
  onChange,
}) => {
  return (
    <>
      <Typography variant="h6">{label}</Typography>
      <Controller
        name={name}
        control={control}
        defaultValue={defaultValue}
        rules={{
          ...rules,
          required: required ? `${label} is required` : false,
        }}
        render={({ field }) => (
          <TextField
            {...field}
            aria-label={label}
            fullWidth
            required={required}
            multiline={multiline}
            rows={rows}
            error={!!error}
            helperText={error ? error : " "}
            sx={{
              mb: 2,
              ...sx,
            }}
            onChange={(e) => {
              field.onChange(e);
              if (onChange) {
                onChange(e);
              }
            }}
          />
        )}
      />
    </>
  );
};

export default FormInput;
