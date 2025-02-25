import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../redux/store";
import { removeTicket } from "../../redux/slices/ticketSlice";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import { TablePagination } from "@mui/material";
import Paper from "@mui/material/Paper";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { TicketFormStyles, TicketTableStyles } from "./TicketFormStyles";
import { useState, FC, ChangeEvent } from "react";
import { TicketTableConstants } from "../../constants/TicketFormConstants";

interface TicketTableProps {
  onEdit: (index: number) => void; // Function to handle editing
}

const TicketTable: FC<TicketTableProps> = ({ onEdit }) => {
  const dispatch = useDispatch();
  const { tickets } = useSelector((state: RootState) => state.ticket);

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(3);

  const handlePageChange = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleRowsPerPageChange = (event: ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const displayedTickets = tickets.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage
  );

  const handleTicketRemoval = (index: number) => {
    dispatch(removeTicket(index));
  };

  return (
    <Paper>
      <TableContainer>
        <Table
          sx={TicketTableStyles.ticketTable}
          size="small"
          aria-label={TicketTableConstants.ARIA_LABELS.TICKET_TABLE}
        >
          <TableHead>
            <TableRow>
              <TableCell sx={TicketTableStyles.tableCell}>
                {TicketTableConstants.HEADERS.NUMBER}
              </TableCell>
              <TableCell sx={TicketTableStyles.tableCell}>
                {TicketTableConstants.HEADERS.TICKET_TYPE}
              </TableCell>
              <TableCell sx={TicketTableStyles.tableCell}>
                {TicketTableConstants.HEADERS.EVENT_DATE}
              </TableCell>
              <TableCell align="right" sx={TicketTableStyles.tableCell}>
                {TicketTableConstants.HEADERS.PRICE}
              </TableCell>
              <TableCell align="right" sx={TicketTableStyles.tableCell}>
                {TicketTableConstants.HEADERS.QUANTITY}
              </TableCell>
              <TableCell align="center" sx={TicketTableStyles.tableCell}>
                {TicketTableConstants.HEADERS.ACTIONS}
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {displayedTickets.map((ticket, index) => (
              <TableRow key={index}>
                <TableCell sx={TicketTableStyles.tableCell}>
                  {page * rowsPerPage + index + 1}
                </TableCell>
                <TableCell sx={TicketTableStyles.tableCell}>
                  {ticket.ticketType}
                </TableCell>
                <TableCell sx={TicketTableStyles.tableCell}>
                  {ticket.eventDate}
                </TableCell>
                <TableCell align="right" sx={TicketTableStyles.tableCell}>
                  ${ticket.price}
                </TableCell>
                <TableCell align="right" sx={TicketTableStyles.tableCell}>
                  {ticket.quantity}
                </TableCell>
                <TableCell align="center" sx={TicketTableStyles.tableCell}>
                  <IconButton
                    size="small"
                    onClick={() => onEdit(page * rowsPerPage + index)}
                  >
                    <EditIcon sx={TicketTableStyles.editIcon} />
                  </IconButton>
                  <IconButton
                    size="small"
                    onClick={() =>
                      handleTicketRemoval(page * rowsPerPage + index)
                    }
                  >
                    <DeleteIcon sx={TicketTableStyles.deleteIcon} />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <TablePagination
        rowsPerPageOptions={
          TicketTableConstants.PAGINATION.ROWS_PER_PAGE_OPTIONS
        }
        component="div"
        count={tickets.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handlePageChange}
        onRowsPerPageChange={handleRowsPerPageChange}
        sx={TicketTableStyles.ticketTablePagination}
      />
    </Paper>
  );
};

export default TicketTable;
