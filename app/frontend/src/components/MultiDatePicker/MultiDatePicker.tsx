import { FC, useState } from "react";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DateTimePicker } from "@mui/x-date-pickers/DateTimePicker";
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
  const [tempDate, setTempDate] = useState<Dayjs | null>(null);

  const handleAcceptDate = (newDate: Dayjs | null) => {
    if (
      newDate &&
      !selectedDates.some((date) => date.isSame(newDate, "minute"))
    ) {
      setSelectedDates([...selectedDates, newDate]);
    }
    setTempDate(null);
  };

  const handleRemoveDate = (dateToRemove: Dayjs) => {
    setSelectedDates(
      selectedDates.filter((date) => !date.isSame(dateToRemove, "minute"))
    );
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box sx={{ ...MultiDatePickerStyles.boxWrapper }}>
        <Stack spacing={2}>
          <Box sx={MultiDatePickerStyles.datePickerReset}>
            <DateTimePicker
              label={MultiDatePickerConstants.SELECT_DATES}
              minDate={dayjs()}
              maxDate={dayjs().add(MultiDatePickerConstants.MAX_YEARS, "year")}
              value={tempDate}
              onChange={setTempDate}
              onAccept={handleAcceptDate}
              slotProps={{ actionBar: { actions: [] } }}
              sx={{
                ...MultiDatePickerStyles.calendarIcon,
                ...MultiDatePickerStyles.calendarPicker,
                ...MultiDatePickerStyles.datePicker,
              }}
            />

            <Button variant="outlined" onClick={() => setSelectedDates([])}>
              {MultiDatePickerConstants.RESET}
            </Button>
          </Box>

          <Box sx={MultiDatePickerStyles.stackWrapper}>
            {selectedDates
              .sort((a, b) => (a.isBefore(b) ? -1 : 1))
              .map((date, index) => (
                <Chip
                  key={index}
                  label={date.format("HH:mm DD/MM/YYYY")}
                  onDelete={() => handleRemoveDate(date)}
                  sx={{
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
