import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchUsers } from "../../redux/services/services";
import { AppDispatch, RootState } from "../../redux/store";

const MockUserList: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { users, error, status } = useSelector((state: RootState) => state.users)

  useEffect(() => {
    if (status === "idle") {
      dispatch(fetchUsers());
    }
  }, [dispatch]);

  return (
    <div>
      <h2>User List</h2>
      {status === "loading" && <p>Loading...</p>}
      {status === "failed" && <p>Error: {error}</p>}
      {status === "succeeded" && (
        <ul>
          {users.map((user) => (
            <li key={user.id}>
              {user.name} - {user.email}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default MockUserList;
