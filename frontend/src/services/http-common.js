import axios from "axios";

export default axios.create({
  baseURL: "http://localhost:8080/api", // La URL de tu Spring Boot
  headers: {
    "Content-type": "application/json"
  }
});