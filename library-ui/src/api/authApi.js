import api from "./axios";

export const setUserData = (data) => {
  localStorage.setItem("token", data.token);
  localStorage.setItem("fullName", decodeURIComponent(data.fullName));
  localStorage.setItem("userId", data.id);
  if (data.email) {
    localStorage.setItem("email", decodeURIComponent(data.email));
  }
};

export const login = async (email, password) => {
  const { data } = await api.post("/auth/login", {
    email,
    password,
  });

  setUserData(data);
  return data;
};

export const register = async (user) => {
  const { data } = await api.post("/auth/register", user);

  setUserData(data);
  return data;
};

export const loginWithGoogle = () => {
  window.location.href =
    "http://localhost:9000/oauth2/authorization/google";
};

export const completeGoogleRegistration = async (user) => {
  const { data } = await api.post(
    "/onboarding/complete-google",
    user
  );

  setUserData(data);
  return data;
};

export const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("fullName");
  localStorage.removeItem("userId");
  localStorage.removeItem("email");
};

export const fetchNeighborhoods = async () => {
  const { data } = await api.get("/neighborhoods");
  return data; 
};