import React from "react";
import { useForm, Controller } from "react-hook-form";
import {
  Box,
  TextField,
  Typography,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Button,
} from "@mui/material";
import { EventFormStyles } from "./EventFormStyles";
import "./EventFormStyles.scss";

const EventForm: React.FC = () => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm();

  const onSubmit = (data: any) => {
    console.log(data);
  };

  return (
    <Box sx={EventFormStyles.formContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className="event-form">
        <Box sx={EventFormStyles.backButton}>
          <Button variant="outlined">Back</Button>
        </Box>

        <Box sx={EventFormStyles.leftBox}>
          <Typography variant="h6">Event Title:</Typography>
          <Controller
            name="eventTitle"
            control={control}
            defaultValue=""
            rules={{ required: "Event Title is required" }}
            render={({ field }) => (
              <TextField
                {...field}
                label="Event Title"
                fullWidth
                required
                error={!!errors.eventTitle}
                sx={{ mb: 2 }}
              />
            )}
          />

          <Typography variant="h6">Description:</Typography>
          <Controller
            name="eventDescription"
            control={control}
            defaultValue=""
            rules={{ required: "Description is required" }}
            render={({ field }) => (
              <TextField
                {...field}
                label="Description"
                fullWidth
                required
                multiline
                rows={4}
                error={!!errors.eventDescription}
                sx={{ mb: 4 }}
              />
            )}
          />
        </Box>

        <Box sx={EventFormStyles.rightBox}>
          <Box sx={{ mb: 4 }}>
            <Typography variant="h6">Event Dates:</Typography>
            {/* <MultiDateCalendar /> */}
          </Box>

          {/* Selectors Box (Venue, Category, Age Restriction) */}
          <Box sx={EventFormStyles.selectorsBox}>
            <FormControl fullWidth sx={{ mb: 2 }}>
              <InputLabel>Venue</InputLabel>
              <Controller
                name="venue"
                control={control}
                defaultValue=""
                rules={{ required: "Venue is required" }}
                render={({ field }) => (
                  <Select {...field} required>
                    {["Venue 1", "Venue 2", "Venue 3"].map((venue) => (
                      <MenuItem key={venue} value={venue}>
                        {venue}
                      </MenuItem>
                    ))}
                  </Select>
                )}
              />
            </FormControl>

            <FormControl fullWidth sx={{ mb: 2 }}>
              <InputLabel>Category</InputLabel>
              <Controller
                name="category"
                control={control}
                defaultValue=""
                rules={{ required: "Category is required" }}
                render={({ field }) => (
                  <Select {...field} required>
                    {["Music", "Conference", "Sports"].map((category) => (
                      <MenuItem key={category} value={category}>
                        {category}
                      </MenuItem>
                    ))}
                  </Select>
                )}
              />
            </FormControl>

            <FormControl fullWidth sx={{ mb: 2 }}>
              <InputLabel>Age Restriction</InputLabel>
              <Controller
                name="ageRestriction"
                control={control}
                defaultValue=""
                render={({ field }) => (
                  <Select {...field}>
                    {["All Ages", "18+", "21+"].map((age) => (
                      <MenuItem key={age} value={age}>
                        {age}
                      </MenuItem>
                    ))}
                  </Select>
                )}
              />
            </FormControl>
          </Box>
        </Box>

        <Box sx={EventFormStyles.nextButton}>
          <Button variant="contained">Next</Button>
        </Box>
      </form>
    </Box>
  );
};

export default EventForm;
