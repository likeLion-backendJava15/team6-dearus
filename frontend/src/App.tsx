import { HashRouter, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Diary from "./pages/Diary";
import ProtectedRoute from "./ProtectedRoute";

export default function App() {
  return (
    <HashRouter>
      <Routes>
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          }
        />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/diary/:id" element={<Diary />} />
      </Routes>
    </HashRouter>
  );
}