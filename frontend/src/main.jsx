import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import 'bootstrap/dist/css/bootstrap.min.css';
import Keycloak from 'keycloak-js';

// 1. Configuración de Keycloak usando tus variables de entorno (.env)
const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL,   // http://localhost:8080
  realm: import.meta.env.VITE_KEYCLOAK_REALM, // TravelAgencyRealm
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID, // frontend-client
});

// Exponer la instancia temporalmente
window._keycloak = keycloak;

// 2. Inicialización de la sesión
keycloak.init({ 
  onLoad: 'login-required', // Obliga a loguearse para ver la app 
  checkLoginIframe: false 
}).then((authenticated) => {
  if (authenticated) {
    // IMPORTANTE: Configurar el refresco automático del token
    // Esto asegura que si el usuario pasa mucho tiempo en la app, 
    // el token no expire y el backend no lance un 401.
    setInterval(() => {
      keycloak.updateToken(70).then((refreshed) => {
        if (refreshed) {
          console.debug('Token refrescado con éxito');
        }
      }).catch(() => {
        console.error('Error al refrescar el token');
      });
    }, 60000); // Revisa cada minuto

    // 3. Si está autenticado, renderizamos la aplicación
    createRoot(document.getElementById('root')).render(
      <StrictMode>
        <App keycloak={keycloak} />
      </StrictMode>,
    );
  } else {
    // REEMPLAZO: En lugar de reload(), informamos el problema
    console.warn("El usuario no pudo ser autenticado por Keycloak.");
    // Opcionalmente podrías redirigir a una página de "Acceso Denegado"
  }
}).catch((error) => {
  // Aquí caen los errores de red o de configuración del .env
  console.error("Error crítico al conectar con Keycloak:", error);
  document.body.innerHTML = `
    <div style="color: red; text-align: center; margin-top: 50px;">
      <h1>Error de Autenticación</h1>
      <p>No se pudo establecer conexión con el servidor de identidad.</p>
    </div>
  `;
});
