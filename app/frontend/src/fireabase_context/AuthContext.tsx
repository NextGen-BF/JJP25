//React
import { createContext, useEffect, ReactNode, FC, useContext } from "react";
import { useNavigate } from "react-router-dom";

//Firebase Auth Context
import { auth, db, googleProvider } from "../firebase/firebase";
import {
  signInWithPopup,
  signOut as firebaseSignOut,
  onAuthStateChanged,
} from "firebase/auth";
import { doc, getDoc } from "firebase/firestore";

//Interfaces
import { UserRole } from "../components/SelectRoleComponent/SelectRole";

//Redux
import { useDispatch } from "react-redux";
import { setRole, setUser, signOutUser } from "../redux/slices/googleAuthSlice";

//Additional for component
import { SelectRoleConstants } from "../constants/SelectRoleConstants";

//Toastify
import { toast } from "react-toastify";

interface AuthContextType {
  signInWithGoogle: () => Promise<void>;
  signOutFromGoogle: () => Promise<void>;
}

export interface GoogleUser {
  uid: string;
  email: string | null;
  name: string | null;
  role: UserRole;
}

export const AuthContext = createContext<AuthContextType>({
  signInWithGoogle: async () => {},
  signOutFromGoogle: async () => {},
});

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: FC<AuthProviderProps> = ({ children }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const signInWithGoogle = async (): Promise<void> => {
    try {
      const result = await signInWithPopup(auth, googleProvider);
      const user = result.user;

      if (user) {
        const userRef = doc(
          db,
          SelectRoleConstants.DATABASE.USERS_COLLECTION,
          user.uid
        );
        const userDoc = await getDoc(userRef);

        if (userDoc.exists()) {
          const userInfo = userDoc.data() as GoogleUser;

          dispatch(setRole(userInfo.role));
        }
      }
    } catch (error) {
      toast.error(SelectRoleConstants.ERROR_MESSAGES.ERROR_OCCURED);
    }
  };

  const signOutFromGoogle = async (): Promise<void> => {
    try {
      firebaseSignOut(auth);
      dispatch(signOutUser());
    } catch (error) {
      toast.error(SelectRoleConstants.ERROR_MESSAGES.ERROR_OCCRED_SIGN_OUT);
    }
  };

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (currentUser) => {
      if (currentUser) {
        const userRef = doc(
          db,
          SelectRoleConstants.DATABASE.USERS_COLLECTION,
          currentUser.uid
        );
        const userDoc = await getDoc(userRef);

        if (userDoc.exists()) {
          dispatch(setUser(userDoc.data() as GoogleUser));
          navigate("/dashboard");
        } else {
          const uid = currentUser.uid;
          const email = currentUser.email;
          const name = currentUser.displayName;

          dispatch(setUser({ uid, email, name, role: null }));
          navigate(SelectRoleConstants.URLs.SELECT_ROLE_URL);
        }
      } else {
        dispatch(setUser(null));
      }
    });

    return () => unsubscribe();
  }, []);

  return (
    <AuthContext.Provider value={{ signInWithGoogle, signOutFromGoogle }}>
      {children}
    </AuthContext.Provider>
  );
};

export const UserAuth = () => useContext(AuthContext);
