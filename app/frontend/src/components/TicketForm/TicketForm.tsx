import { useForm, Controller } from "react-hook-form";
import { Box, Typography, Button } from "@mui/material";
import { TicketFormStyles } from "./TicketFormStyles";
import FormInput from "../FormInput/FormInput";
import FormSelect from "../FormSelect/FormSelect";
import { TicketType, Ticket } from "../../redux/slices/ticketSlice";
import { useDispatch, useSelector } from "react-redux";
import { useState } from "react";
import {
  addTicket,
  removeTicket,
  updateTicket,
} from "../../redux/slices/ticketSlice";
import { RootState } from "../../redux/store";
import { FormStyles } from "../../styles/FormStyles";
import "./TicketFormStyles.scss";
import TicketTable from "./TicketTable";

const TicketForm = () => {
  const dispatch = useDispatch();
  const tickets = useSelector((state: RootState) => state.ticket.tickets);

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    setValue,
    reset,
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

  const [editIndex, setEditIndex] = useState<number | null>(null);

  const handleChange = async (field: keyof Ticket, value: any) => {
    setValue(field, value);
    await trigger(field);
  };

  const onSubmit = async (data: Ticket) => {
    const isFormValid = await trigger();
    if (!isFormValid) return;

    if (editIndex !== null) {
      dispatch(updateTicket({ index: editIndex, ticket: data }));
      setEditIndex(null);
    } else {
      dispatch(addTicket(data));
    }
    reset();
  };

  const handleEdit = (index: number) => {
    const ticket = tickets[index];
    setValue("description", ticket.description);
    setValue("eventDate", ticket.eventDate);
    setValue("ticketType", ticket.ticketType);
    setValue("price", ticket.price);
    setValue("quantity", ticket.quantity);
    setEditIndex(index);
  };

  return (
    <Box sx={FormStyles.formContainer}>
      <form onSubmit={handleSubmit(onSubmit)} className="ticket-form">
        <Box sx={TicketFormStyles.formTableBox}>
          <Box sx={TicketFormStyles.formInfoBox}>
            <Box sx={TicketFormStyles.leftBox}>
              <FormInput
                name="description"
                control={control}
                label="Ticket Description"
                multiline={true}
                rows={2.2} // to align items in the form
                defaultValue=""
                required
                error={errors.description?.message}
                onChange={(e) => handleChange("description", e.target.value)}
              />

              <FormSelect
                name="eventDate"
                control={control}
                label="Event Date"
                options={["2025-03-01", "2025-03-02"]}
                required
                error={errors.eventDate?.message}
                onChange={(e) => handleChange("eventDate", e.target.value)}
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
                onChange={(e) => handleChange("ticketType", e.target.value)}
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
                      min: { value: 1, message: "Must be above 1" },
                      max: {
                        value: 999,
                        message: "Must be under 1000",
                      },
                      validate: (value: number) => {
                        if (!/^\d+$/.test(value.toString())) {
                          return "Whole numbers only";
                        }

                        if (
                          value.toString().length > 1 &&
                          value.toString().startsWith("0")
                        ) {
                          return "Leading zeros are not allowed";
                        }
                        return true;
                      },
                    }}
                    onChange={(e) => handleChange("quantity", e.target.value)}
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
                      min: { value: 0, message: "Price must be above 0" },
                      max: { value: 9999.99, message: "Must be under 10000" },
                      validate: (value: number) => {
                        if (!/^\d+(\.\d{1,2})?$/.test(value.toString())) {
                          return "Incorrect price format";
                        }

                        if (
                          value.toString().length > 1 &&
                          value.toString().startsWith("0")
                        ) {
                          return "Incorrect price format";
                        }
                        return true;
                      },
                    }}
                    onChange={(e) => handleChange("price", e.target.value)}
                  />
                </Box>

                <Button type="submit">Add</Button>
              </Box>
            </Box>
          </Box>

          <Box>
            <TicketTable onEdit={handleEdit} />
          </Box>
        </Box>
      </form>
    </Box>
  );
};

export default TicketForm;
