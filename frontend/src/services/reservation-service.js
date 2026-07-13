import httpClient from "./http-common";

const createReservation = (data) => {
  return httpClient.post("/reservations", data);
};

const getAllReservations = () => {
  return httpClient.get('/reservations');
};

const getMyReservations = () => {
  return httpClient.get('/reservations/my');
};

export default { createReservation, getAllReservations, getMyReservations };