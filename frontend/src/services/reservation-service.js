import httpClient from "./http-common";

const createReservation = (data) => {
  return httpClient.post("/reservations", data);
};

const getAllReservations = () => {
  return httpClient.get('/reservations');
};

export default { createReservation, getAllReservations };