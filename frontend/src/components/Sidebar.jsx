import { useState } from "react"; // Importamos para los desplegables
import { keycloak } from "../main";
import { Link } from "react-router-dom";

const Sidebar = () => {
  const isAdmin = keycloak.hasRealmRole("ADMIN");
  const isUser = keycloak.hasRealmRole("USER");

  // Estados para controlar los desplegables
  const [openUser, setOpenUser] = useState(true);
  const [openAdmin, setOpenAdmin] = useState(false);

  // Estilo común para los "botones" de la lista
  const linkStyle = {
    display: "block",
    padding: "10px 15px",
    margin: "5px 20px",
    color: "#ccc",
    textDecoration: "none",
    fontSize: "0.9rem",
    border: "1px solid #333", // El "marco" que pediste
    borderRadius: "4px",
    transition: "background 0.3s"
  };

  const menuButtonStyle = {
    width: "100%",
    textAlign: "left",
    backgroundColor: "transparent",
    border: "none",
    color: "#0066cc",
    padding: "15px 20px",
    fontSize: "1rem",
    fontWeight: "bold",
    cursor: "pointer",
    display: "flex",
    justifyContent: "space-between"
  };

  return (
    <div className="sidebar" style={{ 
      backgroundColor: "#151515", 
      color: "white", 
      width: "250px", 
      height: "100vh", 
      position: "fixed",
      display: "flex",
      flexDirection: "column",
      borderRight: "1px solid #333"
    }}>
      <h2 style={{ padding: "20px", color: "#0066cc", textAlign: "center" }}>Agencia Viajes</h2>
      
      <nav style={{ flexGrow: 1 }}>
        {/* SECCIÓN USUARIO */}
        {(isUser || isAdmin) && (
          <div className="section">
            <button style={menuButtonStyle} onClick={() => setOpenUser(!openUser)}>
              MI CUENTA {openUser ? "▲" : "▼"}
            </button>
            {openUser && (
              <div className="submenu">
                <Link 
                    to="/perfil" 
                    style={{ 
                        display: "block", 
                        padding: "10px 20px", 
                        color: "white", 
                        textDecoration: "none",
                        border: "1px solid #333",
                        marginBottom: "5px",
                        borderRadius: "4px"
                    }}
                >
                    Ver perfil
                </Link>
                <Link 
                    to="/user/paquetes" 
                    style={{ 
                        display: "block", 
                        padding: "10px 20px", 
                        color: "white", 
                        textDecoration: "none",
                        border: "1px solid #333",
                        marginBottom: "5px",
                        borderRadius: "4px"
                    }}
                >
                    Ver paquetes
                </Link>
                <Link 
                    to="/carrito" 
                    style={{ 
                        display: "block", 
                        padding: "10px 20px", 
                        color: "white", 
                        textDecoration: "none",
                        border: "1px solid #333",
                        marginBottom: "5px",
                        borderRadius: "4px"
                    }}
                >
                    Ver carrito
                </Link>
                <Link to="/pagos" style={linkStyle}>Ver pagos</Link>
              </div>
            )}
          </div>
        )}

        {/* SECCIÓN ADMINISTRACIÓN */}
        {isAdmin && (
          <div className="section" style={{ marginTop: "10px" }}>
            <button style={menuButtonStyle} onClick={() => setOpenAdmin(!openAdmin)}>
              ADMINISTRACIÓN {openAdmin ? "▲" : "▼"}
            </button>
            {openAdmin && (
              <div className="submenu">
                <Link 
                    to="/admin/config-discount" 
                    style={{ 
                        display: "block", 
                        padding: "10px 20px", 
                        color: "white", 
                        textDecoration: "none",
                        border: "1px solid #333",
                        marginBottom: "5px",
                        borderRadius: "4px"
                    }}
                >
                    Ver descuentos (admin)
                </Link>
                <Link 
                    to="/admin/usuarios" 
                    style={{ 
                        display: "block", 
                        padding: "10px 20px", 
                        color: "white", 
                        textDecoration: "none",
                        border: "1px solid #333",
                        marginBottom: "5px",
                        borderRadius: "4px"
                    }}
                >
                    Ver usuarios (admin)
                </Link>
                <Link 
                    to="/admin/paquetes" 
                    style={{ 
                        display: "block", 
                        padding: "10px 20px", 
                        color: "white", 
                        textDecoration: "none",
                        border: "1px solid #333",
                        marginBottom: "5px",
                        borderRadius: "4px"
                    }}
                >
                    Ver paquetes (admin)
                </Link>
                <Link 
                    to="/admin/reservas" 
                    style={{ 
                        display: "block", 
                        padding: "10px 20px", 
                        color: "white", 
                        textDecoration: "none",
                        border: "1px solid #333",
                        marginBottom: "5px",
                        borderRadius: "4px"
                    }}
                >
                    Ver reservas (admin)
                </Link>
                <Link to="/admin/pagos" style={linkStyle}>Pagos</Link>
              </div>
            )}
          </div>
        )}
      </nav>

      {/* BOTÓN CERRAR SESIÓN */}
      <button 
        onClick={() => keycloak.logout({ redirectUri: window.location.origin })}
        style={{ 
          margin: "20px", 
          backgroundColor: "#d32f2f", // Un rojo para que destaque
          border: "none", 
          color: "white", 
          padding: "12px", 
          borderRadius: "4px",
          cursor: "pointer",
          fontWeight: "bold"
        }}
      >
        Cerrar Sesión
      </button>
    </div>
  );
};

export default Sidebar;