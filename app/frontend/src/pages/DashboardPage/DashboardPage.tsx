import { FC } from "react";
import { Navigate } from "react-router-dom";
import { useSelector } from "react-redux";

import { RootState } from "../../redux/store";

import axios from "axios";

const DashboardPage: FC = () => {
  const { user } = useSelector((state: RootState) => state.googleAuth);

  const handleFirebaseAuthorization = async () => {
    try {
      const response = await axios.get(import.meta.env.VITE_FIREBASE_TEST_REQUEST);

      //Just a simple backend request to see if the Firebase token is valid
      console.log(response);
    } catch (error) {

    }
  }
  return (
    <>
      <div>DashboardPage</div>
      {user?.name ? (
        <p>Welcome, {user.name}</p>
      ) : (
        <Navigate to={"/login"} />
      )}
      <button onClick={handleFirebaseAuthorization}>Check Firebase Authorization</button>
    </>
  );
};

export default DashboardPage;
