import { FC } from "react";
import { Controller } from "react-hook-form";
import { Autocomplete, TextField, FormControl } from "@mui/material";

interface FormAutoCompleteProps {
  defaultValue?: string;
  name: string;
  control: any;
  rules?: object;
  label: string;
  options: string[];
  required?: boolean;
  error?: string;
  sx?: object;
  onChange?: (value: string) => void;
}

const FormAutoComplete: FC<FormAutoCompleteProps> = ({
  defaultValue,
  name,
  control,
  rules,
  label,
  options,
  required = false,
  error,
  sx,
  onChange,
}) => {
  return (
    <FormControl fullWidth error={!!error}>
      <Controller
        name={name}
        control={control}
        defaultValue=""
        rules={required ? { ...rules, required: `${label} is required` } : {}}
        render={({ field }) => (
          <Autocomplete
            {...field}
            freeSolo // Allows user to type custom values
            defaultValue={defaultValue}
            options={options}
            onChange={(_, newValue) => {
              field.onChange(newValue);
              if (onChange) {
                onChange(newValue as string);
              }
            }}
            renderInput={(params) => (
              <TextField
                {...params}
                label={label}
                error={!!error}
                helperText={error ? error : " "}
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
