import { FC } from "react";
import { Controller } from "react-hook-form";
import {
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
} from "@mui/material";

interface FormSelectProps {
  name: string;
  control: any;
  label: string;
  options: string[];
  required?: boolean;
  error?: string;
  sx?: object;
  defaultValue?: string;
  onChange?: (e: SelectChangeEvent<string | number | unknown>) => void;
}

const FormSelect: FC<FormSelectProps> = ({
  name,
  control,
  label,
  options,
  required = false,
  error,
  sx,
  defaultValue,
  onChange,
}) => {
  return (
    <FormControl fullWidth>
      <InputLabel>{label}</InputLabel>
      <Controller
        name={name}
        control={control}
        defaultValue={defaultValue}
        rules={required ? { required: `${label} is required` } : {}}
        render={({ field }) => (
          <Select
            {...field}
            required={required}
            error={!!error}
            sx={{ ...sx }}
            label={label}
            onChange={(e) => {
              field.onChange(e);
              if (onChange) {
                onChange(e);
              }
            }}
          >
            {options.map((option) => (
              <MenuItem key={option} value={option}>
                {option}
              </MenuItem>
            ))}
          </Select>
        )}
      />
      <FormHelperText sx={{ color: "error.main" }}>
        {error ? error : " "}
      </FormHelperText>
    </FormControl>
  );
};

export default FormSelect;
