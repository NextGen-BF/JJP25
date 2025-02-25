import { useForm, Controller } from "react-hook-form";
import { Box, Button, Typography } from "@mui/material";
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
import { TicketFormConstants } from "../../constants/TicketFormConstants";
import { toast } from "react-toastify";
import { FormInputStyles } from "../FormInput/FormInputStyles";

const TicketForm = () => {
  const dispatch = useDispatch();
  const tickets = useSelector((state: RootState) => state.ticket.tickets);
  const eventDates = useSelector((state: RootState) => state.event.event.dates);

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

  // to not dispatch update if the ticket info is not changed
  const [initialValues, setInitialValues] = useState<Ticket | null>(null);

  const handleChange = async (field: keyof Ticket, value: any) => {
    setValue(field, value);
    await trigger(field);
  };

  const onSubmit = async (data: Ticket) => {
    const isFormValid = await trigger();

    if (!isFormValid) return;

    if (editIndex !== null) {
      const hasChanges = JSON.stringify(data) !== JSON.stringify(initialValues);

      if (hasChanges) {
        dispatch(updateTicket({ index: editIndex, ticket: data }));
        toast.info(TicketFormConstants.TOAST_MESSAGES.TICKET_EDIT_SUCCESS);
      }

      setEditIndex(null);
      setInitialValues(null);
    } else {
      dispatch(addTicket(data));
      toast.success(TicketFormConstants.TOAST_MESSAGES.TICKET_ADD_SUCCESS);
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
    setInitialValues(ticket);
  };

  return (
    <Box sx={FormStyles.formContainer}>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="ticket-form"
        noValidate
      >
        <Box sx={TicketFormStyles.formTableBox}>
          <Box sx={TicketFormStyles.formInfoBox}>
            <Box sx={TicketFormStyles.leftBox}>
              <FormInput
                name={TicketFormConstants.NAMES.DESCRIPTION}
                control={control}
                label={TicketFormConstants.LABELS.DESCRIPTION}
                multiline={true}
                rows={2.2} // to align items in the form
                defaultValue=""
                error={errors.description?.message}
                required
                rules={{
                  required:
                    TicketFormConstants.VALIDATION_MESSAGES
                      .DESCRIPTION_REQUIRED,
                  minLength: {
                    value:
                      TicketFormConstants.VALIDATION_MESSAGES
                        .DESCRIPTION_MIN_VALUE,
                    message:
                      TicketFormConstants.VALIDATION_MESSAGES
                        .DESCRIPTION_MIN_LENGTH,
                  },
                }}
                onChange={(e) => handleChange("description", e.target.value)}
              />

              <FormSelect
                name={TicketFormConstants.NAMES.EVENT_DATE}
                control={control}
                label={TicketFormConstants.NAMES.EVENT_DATE}
                options={eventDates.map((date) =>
                  date.format("HH:mm DD/MM/YYYY")
                )}
                required
                error={errors.eventDate?.message}
                onChange={(e) => handleChange("eventDate", e.target.value)}
                defaultValue=""
              />
            </Box>

            <Box sx={TicketFormStyles.rightBox}>
              <FormSelect
                name={TicketFormConstants.NAMES.TICKET_TYPE}
                control={control}
                label={TicketFormConstants.LABELS.TICKET_TYPE}
                options={Object.values(TicketType)}
                required
                error={errors.ticketType?.message}
                onChange={(e) => handleChange("ticketType", e.target.value)}
              />

              <Box sx={TicketFormStyles.quantityPriceButtonBox}>
                <Box>
                  <FormInput
                    name={TicketFormConstants.NAMES.PRICE}
                    control={control}
                    label={TicketFormConstants.LABELS.PRICE}
                    type="number"
                    defaultValue="1"
                    required
                    error={errors.price?.message}
                    rules={{
                      min: {
                        value:
                          TicketFormConstants.VALIDATION_MESSAGES
                            .PRICE_MIN_VALUE,
                        message:
                          TicketFormConstants.VALIDATION_MESSAGES.PRICE_MIN,
                      },
                      max: {
                        value:
                          TicketFormConstants.VALIDATION_MESSAGES
                            .PRICE_MAX_VALUE,
                        message:
                          TicketFormConstants.VALIDATION_MESSAGES.PRICE_MAX,
                      },
                      validate: (value: number) => {
                        if (!/^\d+(\.\d{1,2})?$/.test(value.toString())) {
                          return TicketFormConstants.VALIDATION_MESSAGES
                            .PRICE_FORMAT;
                        }

                        if (
                          value.toString().length > 1 &&
                          value.toString().startsWith("0")
                        ) {
                          return TicketFormConstants.VALIDATION_MESSAGES
                            .PRICE_FORMAT;
                        }
                        return true;
                      },
                    }}
                    onChange={(e) => handleChange("price", e.target.value)}
                  />
                </Box>

                <Box>
                  <FormInput
                    name={TicketFormConstants.NAMES.QUANTITY}
                    control={control}
                    label={TicketFormConstants.LABELS.QUANTITY}
                    type="number"
                    defaultValue="0"
                    required
                    error={errors.quantity?.message}
                    rules={{
                      min: {
                        value:
                          TicketFormConstants.VALIDATION_MESSAGES
                            .QUANTITY_MIN_VALUE,
                        message:
                          TicketFormConstants.VALIDATION_MESSAGES.QUANTITY_MIN,
                      },
                      max: {
                        value:
                          TicketFormConstants.VALIDATION_MESSAGES
                            .QUANTITY_MAX_VALUE,
                        message:
                          TicketFormConstants.VALIDATION_MESSAGES.QUANTITY_MAX,
                      },
                      validate: (value: number) => {
                        if (!/^\d+$/.test(value.toString())) {
                          return TicketFormConstants.VALIDATION_MESSAGES
                            .QUANTITY_WHOLE_NUMBERS;
                        }

                        if (
                          value.toString().length > 1 &&
                          value.toString().startsWith("0")
                        ) {
                          return TicketFormConstants.VALIDATION_MESSAGES
                            .QUANTITY_LEADING_ZERO;
                        }
                        return true;
                      },
                    }}
                    onChange={(e) => handleChange("quantity", e.target.value)}
                  />
                </Box>

                <Box sx={TicketFormStyles.buttonBox}>
                  <Button type="submit" sx={TicketFormStyles.button}>
                    {editIndex === null
                      ? TicketFormConstants.LABELS.ADD_TICKET
                      : TicketFormConstants.LABELS.EDIT_TICKET}
                  </Button>
                </Box>
              </Box>
            </Box>
          </Box>

          <Box sx={TicketFormStyles.tableBox}>
            <Typography sx={{ pb: 1, ...FormInputStyles.typography }}>
              {TicketFormConstants.LABELS.CREATED_TICKETS}
            </Typography>
            <TicketTable onEdit={handleEdit} />
          </Box>
        </Box>
      </form>
    </Box>
  );
};

export default TicketForm;
