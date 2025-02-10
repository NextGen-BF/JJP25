import { store } from "../../redux/store";

let isOpen;
store.subscribe(() => {
  isOpen = store.getState().sidebar.isOpen;
});

export const styles = {
  listSubheaderStyles: {
  },

  listItemTextStyles: {
    whiteSpace: "nowrap",
    color: "#000",
    textDecoration: "none",
    overflow: "hidden",
  },

  listItemButtonStyles: {
    justifyContent: "flex-start",
    pr:0,
    height: 48,
    overflow: "hidden"
  },

};
