import { useState, useEffect } from "react";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import Navigation from "./pages/Navigation";
import Footer from "./pages/Footer";
import "./App.css";

const App = () => {
  const [hello, setHello] = useState("");

  useEffect(() => {
    fetch("./api/home/")
      .then((response) => response.text())
      .then((data) => setHello(data));
  }, []);

  // to hide the navigation pane when the page is Login
  const location = useLocation();

  const navigate = useNavigate();

  // Check if the current route is "/login" or "/register"
  const hideNavigation = location.pathname === "/login" || location.pathname === "/register";

  const [isLoading, setLoading] = useState(true);

  useEffect(() => {
    // stores true if jwt exists else false (!! = converts to boolean)
    const isLoggedIn = !!localStorage.getItem("uid");

    if (
      !isLoggedIn &&
      location.pathname !== "/login" &&
      location.pathname !== "/register"
    ) {
      navigate("/login");
    } else {
      setLoading(false);
    }
  }, [location, navigate]);

  if (isLoading) {
    return null;
  }

  return (
    <div>
      {!hideNavigation && <Navigation />}
      <Outlet />
      <Footer />
    </div>
  );
};

export default App;
