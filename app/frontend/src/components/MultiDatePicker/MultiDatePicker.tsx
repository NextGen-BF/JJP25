import React, { useState } from "react";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { Button, Stack, Chip } from "@mui/material";
import dayjs, { Dayjs } from "dayjs";

interface MultiDatePickerProps {
  selectedDates: Dayjs[];
  setSelectedDates: (dates: Dayjs[]) => void;
}

const MultiDatePicker: React.FC<MultiDatePickerProps> = ({ selectedDates, setSelectedDates }) => {
  const handleAddDate = (newDate: Dayjs | null) => {
    if (newDate && !selectedDates.some((date) => date.isSame(newDate, "day"))) {
      setSelectedDates([...selectedDates, newDate]);
    }
  };

  const handleRemoveDate = (dateToRemove: Dayjs) => {
    setSelectedDates(selectedDates.filter((date) => !date.isSame(dateToRemove, "day")));
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Stack spacing={2}>
        {/* Date Picker */}
        <DatePicker label="Select a date" onChange={handleAddDate} />

        {/* Display Selected Dates as Chips */}
        <Stack direction="row" spacing={1} flexWrap="wrap">
          {selectedDates.map((date, index) => (
            <Chip
              key={index}
              label={date.format("YYYY-MM-DD")}
              onDelete={() => handleRemoveDate(date)}
            />
          ))}
        </Stack>

        {/* Reset Button */}
        <Button variant="outlined" onClick={() => setSelectedDates([])}>
          Reset
        </Button>
      </Stack>
    </LocalizationProvider>
  );
};

export default MultiDatePicker;
