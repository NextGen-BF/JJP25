import React from "react";
import { Controller } from "react-hook-form";
import { TextField, Typography } from "@mui/material";
import { FormInputStyles } from "./FormInputStyles";

interface FormInputProps {
  defaultValue: string;
  type?: string;
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
  type = "text",
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
      <Typography sx={FormInputStyles.typography}>{label}</Typography>
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
            type={type}
            aria-label={label}
            fullWidth
            required={required}
            multiline={multiline}
            rows={rows}
            error={!!error}
            helperText={error ? error : " "}
            sx={{
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
