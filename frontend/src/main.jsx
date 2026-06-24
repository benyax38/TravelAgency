import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import 'bootstrap/dist/css/bootstrap.min.css';
import Keycloak from 'keycloak-js';
import authService from './services/auth-service.js';

// Creación de la instancia Keycloak
// Se crea un objeto configurado con: URL del servidor, realm y client
export const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL,
  realm: import.meta.env.VITE_KEYCLOAK_REALM,
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
});

// Inicia la sesión del usuario y consulta si está autenticado
keycloak.init({ 
  // Si el usuario no tiene sesión, lo redirige al login de keycloak
  onLoad: 'login-required', 
  checkLoginIframe: false,
  // Esto es útil para evitar problemas de redirección tras el login
  pkceMethod: 'S256' 
}).then((authenticated) => {
  // Si el login fue exitoso, authenticated = true
  if (authenticated) {
    
    console.log("🔑 Token para Postman:", keycloak.token);

    // 🔥 Ver claims del JWT
    console.log("📦 Token parseado:", keycloak.tokenParsed);

    // Renovación automática del token
    keycloak.onTokenExpired = () => {

        // Si el token expira en menos de 30 segundos, lo renovamos
        keycloak.updateToken(30).then((refreshed) => {
            if (refreshed) {
                console.log("🔄 Token actualizado:", keycloak.token);
            }
        })
        .catch(() => {

            console.error(
                "❌ No se pudo refrescar el token"
            );

            keycloak.login();
        });
    };

    // Sincroniza el usuario con el backend
    authService.syncCurrentUser()
        // Si la sincronización es exitosa...
        .then((res) => {

            console.log("✅ Usuario sincronizado:", res.data);

            // Renderizamos la aplicación
            createRoot(document.getElementById('root')).render(
                <StrictMode>

                    {/* Ya no es estrictamente necesario pasar keycloak por props si lo exportas,
                        pero dejarlo aquí no rompe nada */}

                    <App keycloak={keycloak} />

                </StrictMode>,
            );
        })
        // Si falla la sincronización, no se renderiza la app
        .catch((error) => {

            console.error("❌ Error sincronizando usuario:", error);

            // Página de error en caso de fallo en la sincronización
            document.body.innerHTML = `
                <div style="
                    color: white;
                    background-color: #151515;
                    text-align: center;
                    height: 100vh;
                    display: flex;
                    flex-direction: column;
                    justify-content: center;
                ">
                    <h1 style="color: red;">
                        Error sincronizando usuario
                    </h1>

                    <p>
                        No se pudo sincronizar el usuario con el backend.
                    </p>
                </div>
            `;
        });
  } else {
    console.warn("El usuario no pudo ser autenticado.");
  }
})
// Captura errores en la conexión con keycloak
.catch((error) => {
  console.error("Error crítico al conectar con Keycloak:", error);
  document.body.innerHTML = `
    <div style="color: white; background-color: #151515; text-align: center; height: 100vh; display: flex; flex-direction: column; justify-content: center;">
      <h1 style="color: #0066cc;">Error de Autenticación</h1>
      <p>No se pudo conectar con Keycloak. Revisa si el contenedor está arriba.</p>
    </div>
  `;
});
