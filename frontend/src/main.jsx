import React from "react";
import ReactDOM from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import App from "./App.jsx";
import Register from "./pages/Register";
import Login from "./pages/Login";
import Personal from "./pages/Personal";
import Group from "./pages/Group";
import GroupProfile from "./pages/GroupProfile";
import UserProfile from "./pages/UserProfile";

const isLoggedIn = !!localStorage.getItem("uid");
const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      { index: true, element: isLoggedIn ? <Personal /> : <Login /> },
      { path: "register", element: <Register /> },
      { path: "personal", element: <Personal /> },
      { path: "group", element: <Group /> },
      { path: "login", element: <Login /> },
      { path: "userProfile", element: <UserProfile /> },
      { path: "groupProfile", element: <GroupProfile /> },
    ],
  },
]);

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
