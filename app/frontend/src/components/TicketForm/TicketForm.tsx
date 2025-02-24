import { useForm, Controller } from "react-hook-form";
import { Box, Typography, Button } from "@mui/material";
import { TicketFormStyles } from "./TicketFormStyles";
import FormInput from "../FormInput/FormInput";
import FormSelect from "../FormSelect/FormSelect";
import { TicketType, Ticket } from "../../redux/slices/ticketSlice";
import { useDispatch, useSelector } from "react-redux";
import { addTicket, removeTicket } from "../../redux/slices/ticketSlice";
import { RootState } from "../../redux/store";
import { FormStyles } from "../../styles/FormStyles";
import "./TicketFormStyles.scss";

const TicketForm = () => {
  const dispatch = useDispatch();
  const { tickets } = useSelector((state: RootState) => state.ticket);

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    setValue,
  } = useForm({
    defaultValues: {
      description: "",
      eventDate: "",
      ticketType: TicketType.GENERAL_ADMISSION,
      price: 0,
      quantity: 0,
    },
    mode: "onChange",
    reValidateMode: "onSubmit",
  });

  const onSubmit = (data: Ticket) => {
    dispatch(addTicket(data));
  };

  const handleTicketRemoval = (index: number) => {
    dispatch(removeTicket(index));
  };

  return (
    <Box sx={FormStyles.formContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className="ticket-form">
        <Box sx={TicketFormStyles.leftBox}>
          <FormInput
            name="description"
            control={control}
            label="Ticket Description"
            multiline={true}
            rows={5}
            defaultValue=""
            required
            error={errors.description?.message}
          />

          <FormSelect
            name="eventDate"
            control={control}
            label="Event Date"
            options={["2025-03-01", "2025-03-02"]} // Replace with dates from redux event state
            required
            error={errors.eventDate?.message}
          />
        </Box>

        <Box sx={TicketFormStyles.rightBox}>
          <FormSelect
            name="ticketType"
            control={control}
            label="Ticket Type"
            options={Object.values(TicketType)}
            required
            error={errors.ticketType?.message}
          />

          <Box sx={TicketFormStyles.quantityPriceButtonBox}>
            <Box>
              <FormInput
                name="quantity"
                control={control}
                label="Quantity"
                type="number"
                defaultValue="0"
                required
                error={errors.quantity?.message}
                rules={{
                  min: { value: 0, message: "Price must be at least 0" },
                }} // Minimum value for price
              />
            </Box>

            <Box>
              <FormInput
                name="price"
                control={control}
                label="Price"
                type="number"
                defaultValue="1"
                required
                error={errors.price?.message}
                rules={{
                  min: { value: 1, message: "Quantity must be at least 1" },
                }} // Minimum value for quantity
              />
            </Box>

            <Button type="submit">Add Ticket</Button>
          </Box>
        </Box>
      </form>

      <Box></Box>
    </Box>
  );
};

export default TicketForm;
