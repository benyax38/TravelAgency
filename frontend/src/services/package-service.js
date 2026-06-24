import httpClient from "./http-common";

const createPackage = (data) => {
    return httpClient.post('/packages', data);
};

const getAllPackages = () => {
    return httpClient.get("/packages");
};

const getPublicPackages = () => {
    return httpClient.get("/packages/public");
};

const searchPackages = (filters) => {
    return httpClient.get("/packages/search", {
        params: filters
    });
};

const cancelPackage = (id) => {
    return httpClient.put(`/packages/${id}/cancel`);
};

export default { 
    getAllPackages,
    getPublicPackages,
    createPackage,
    searchPackages,
    cancelPackage
};