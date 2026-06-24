import axios from "axios";
import { keycloak } from '../main';

// Creamos la instancia base de Axios
const httpClient = axios.create({
  // Especifica la URL base para todas las peticiones al backend
  baseURL: import.meta.env.VITE_API_BASE_URL, // http://localhost:8090/api
  // Le indica al backend que envía un JSON
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Código que se ejecuta antes de cada petición HTTP.
 * Interceptor para añadir el Token de Keycloak en cada petición.
 * Esto evita tener que pasar el token manualmente en cada service.
 */
httpClient.interceptors.request.use(
  // config representa: URL, headers, método, body y toda la petición HTTP
  async (config) => {

    try {

      // Refresca token si expira pronto
      const refreshed = await keycloak.updateToken(30);

      if (refreshed) {
        console.log("🔄 Token renovado automáticamente:", keycloak.token);
      } else {
        // Solo para depuración, para verificar que el token que se envía es el actual
        console.log("✅ Token vigente, enviando petición...");
      }

      // Inserta token actualizado
      config.headers.Authorization = 
        `Bearer ${keycloak.token}`;

      return config;
    } catch (error) {

      console.error(
        "❌ Error actualizando token",
        error
      );

      // Redirige al login si falla refresh
      keycloak.login();
      return Promise.reject(error);
    }
  },
  (error) => Promise.reject(error)
);

export default httpClient;