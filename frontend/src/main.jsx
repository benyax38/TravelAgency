import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import 'bootstrap/dist/css/bootstrap.min.css';
import Keycloak from 'keycloak-js';

// 1. Configuración de Keycloak
// AÑADIMOS 'export' para que otros componentes (como Sidebar) puedan usarlo
export const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
});

// Exponer la instancia para depuración en consola (opcional)
window._keycloak = keycloak;

// 2. Inicialización de la sesión
keycloak.init({ 
  onLoad: 'login-required', 
  checkLoginIframe: false,
  // Esto es útil para evitar problemas de redirección tras el login
  pkceMethod: 'S256' 
}).then((authenticated) => {
  if (authenticated) {
    // Configurar el refresco automático del token
    setInterval(() => {
      keycloak.updateToken(70).then((refreshed) => {
        if (refreshed) {
          console.debug('Token refrescado con éxito');
        }
      }).catch(() => {
        console.error('Error al refrescar el token');
      });
    }, 60000);

    // 3. Renderizamos la aplicación
    createRoot(document.getElementById('root')).render(
      <StrictMode>
        {/* Ya no es estrictamente necesario pasar keycloak por props si lo exportas, 
            pero dejarlo aquí no rompe nada */}
        <App keycloak={keycloak} />
      </StrictMode>,
    );
  } else {
    console.warn("El usuario no pudo ser autenticado.");
  }
}).catch((error) => {
  console.error("Error crítico al conectar con Keycloak:", error);
  document.body.innerHTML = `
    <div style="color: white; background-color: #151515; text-align: center; height: 100vh; display: flex; flex-direction: column; justify-content: center;">
      <h1 style="color: #0066cc;">Error de Autenticación</h1>
      <p>No se pudo conectar con Keycloak. Revisa si el contenedor está arriba.</p>
    </div>
  `;
});
