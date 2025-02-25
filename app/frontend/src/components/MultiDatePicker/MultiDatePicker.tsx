import { FC } from "react";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { Button, Stack, Chip } from "@mui/material";
import dayjs, { Dayjs } from "dayjs";
import { MultiDatePickerConstants } from "../../constants/MultiDatePickerConstants";
import { MultiDatePickerStyles } from "./MultiDatePickerStyles";
import Box from "@mui/material/Box";

interface MultiDatePickerProps {
  selectedDates: Dayjs[];
  setSelectedDates: (dates: Dayjs[]) => void;
  error?: string;
}

const MultiDatePicker: FC<MultiDatePickerProps> = ({
  selectedDates,
  setSelectedDates,
  error,
}) => {
  const handleAddDate = (newDate: Dayjs | null) => {
    if (newDate && !selectedDates.some((date) => date.isSame(newDate, "day"))) {
      setSelectedDates([...selectedDates, newDate]);
    }
  };

  const handleRemoveDate = (dateToRemove: Dayjs) => {
    setSelectedDates(
      selectedDates.filter((date) => !date.isSame(dateToRemove, "day"))
    );
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box sx={{ ...MultiDatePickerStyles.boxWrapper }}>
        <Stack spacing={2}>
          <Box sx={MultiDatePickerStyles.datePickerReset}>
            <DatePicker
              label={MultiDatePickerConstants.SELECT_DATES}
              onChange={handleAddDate}
              minDate={dayjs()}
              maxDate={dayjs().add(MultiDatePickerConstants.MAX_YEARS, "year")}
              sx={{
                ...MultiDatePickerStyles.calendarIcon,
                ...MultiDatePickerStyles.calendarPicker,
                ...MultiDatePickerStyles.datePicker,
              }}
            />

            <Button variant="outlined" onClick={() => setSelectedDates([])}>
              Reset
            </Button>
          </Box>

          <Box sx={MultiDatePickerStyles.stackWrapper}>
            {selectedDates
              .sort((a, b) => (a.isBefore(b) ? -1 : 1))
              .map((date, index) => (
                <Chip
                  key={index}
                  label={date.format("DD/MM/YYYY")}
                  onDelete={() => handleRemoveDate(date)}
                  style={{
                    ...MultiDatePickerStyles.chip,
                  }}
                />
              ))}
          </Box>
        </Stack>
      </Box>
    </LocalizationProvider>
  );
};

export default MultiDatePicker;
