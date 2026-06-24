import httpClient from "./http-common";

const getAllDiscount = () => {
    return httpClient.get("/discount-configs");
}

const updateDiscount = (id, data) => {
    return httpClient.patch(`/discount-configs/${id}`,data);
}

export default {
    getAllDiscount,
    updateDiscount
}