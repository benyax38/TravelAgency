import httpClient from "./http-common";

const syncCurrentUser = () => {
    return httpClient.get("/users/me");
};

const updateUser = (id, data) => {
    return httpClient.put(`/users/${id}`, data);
};

export default {
    syncCurrentUser,
    updateUser
};