import axios from "axios";

// Creamos la instancia base de Axios
const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // http://localhost:8090/api/v1
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Interceptor para añadir el Token de Keycloak en cada petición.
 * Esto evita tener que pasar el token manualmente en cada service.
 */
httpClient.interceptors.request.use(
  (config) => {
    // Obtenemos el token guardado en el almacenamiento local o sesión
    // Nota: Dependiendo de tu implementación en main.jsx, podrías necesitar
    // acceder al objeto keycloak globalmente o pasar el token aquí.
    const token = window._keycloak?.token; 

    console.log("Token enviado:", token);

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default httpClient;