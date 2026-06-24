import httpClient from "./http-common";

const getAllUsers = () => {

    return httpClient.get("/users");
};

const getUserById = (id) => {

    return httpClient.get(`/users/${id}`);
};

const createUser = (data) => {

    return httpClient.post("/users", data);
};

const updateUser = (id, userData) => {

    return httpClient.put(`/users/${id}`, userData);
};

const deleteUser = (id) => {

    return httpClient.delete(`/users/${id}`);
};

const activateUser = (id) => {

    return httpClient.patch(`/users/${id}/active`);
};

const deactivateUser = (id) => {

    return httpClient.patch(`/users/${id}/inactive`);
};

export default {
    getAllUsers,
    getUserById,
    createUser,
    updateUser,
    deleteUser,
    activateUser,
    deactivateUser
};