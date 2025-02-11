import {Box, TextField, Select, Stack} from "@mui/material"
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import { useState } from "react";
import { Dayjs } from "dayjs";
import { updateEvent } from "../../redux/slices/eventSlice";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

const EventForm = () => {
    const dispatch = useDispatch();
    const { event } = useSelector((state: RootState) => state.event);

    const [selectedDates, setSelectedDates] = useState<Dayjs[]>(event.dates);

    const handleDateChange = (newDate: Dayjs | null) => {
        if (newDate && !selectedDates.some(date => date.isSame(newDate, "day"))) {
            setSelectedDates([...selectedDates, newDate]);
        }
    }

    const handleSaveDate = () => {
        dispatch(updateEvent({ dates: selectedDates }));
    }

    return;
}