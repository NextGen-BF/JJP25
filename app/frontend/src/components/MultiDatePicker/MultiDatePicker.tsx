import React from "react";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { Button, Stack, Chip } from "@mui/material";
import dayjs, { Dayjs } from "dayjs";
import { MultiDatePickerConstants } from "../../constants/MultiDatePickerConstants";
import { MultiDatePickerStyles } from "./MultiDatePickerStyles";
import Box from "@mui/material/Box";
import { DeleteOutline, DeleteOutlineRounded } from "@mui/icons-material";

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
      <Box sx={{ ...MultiDatePickerStyles.boxWrapper }}>
        <Stack spacing={2}>
          <DatePicker 
            label={MultiDatePickerConstants.SELECT_DATES}
            onChange={handleAddDate}
            minDate={dayjs()}
            maxDate={dayjs().add(MultiDatePickerConstants.MAX_YEARS, 'year')}
            sx={{ ...MultiDatePickerStyles.calendarIcon, ...MultiDatePickerStyles.calendarPicker }} 
          />

          <Stack direction="row" spacing={1} flexWrap="wrap"
          sx = {MultiDatePickerStyles.stackWrapper}>
            {selectedDates
              .sort((a, b) => a.isBefore(b) ? -1 : 1)
              .map((date, index) => (
                <Chip
                  key={index}
                  label={date.format("YYYY-MM-DD")}
                  onDelete={() => handleRemoveDate(date)}
                  style={{
                    ...MultiDatePickerStyles.chip,
                  }}
                />
              ))}
          </Stack>

          <Button variant="outlined" onClick={() => setSelectedDates([])}>
            Reset
          </Button>
        </Stack>
      </Box>
    </LocalizationProvider>
  );
};

export default MultiDatePicker;
