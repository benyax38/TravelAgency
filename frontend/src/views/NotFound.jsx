import { Link } from 'react-router-dom';

export default function NotFound() {
  const styles = {
    container: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      height: '100vh',
      backgroundColor: 'white', // Fondo oscuro de Keycloak
      color: 'white',
      textAlign: 'center',
      padding: '20px'
    },
    errorCode: {
      fontSize: '8rem',
      fontWeight: 'bold',
      color: '#0066cc', // Azul corporativo
      margin: 0,
      lineHeight: 1
    },
    message: {
      fontSize: '1.5rem',
      marginBottom: '30px',
      color: '#cccccc'
    },
    button: {
      padding: '12px 25px',
      backgroundColor: '#0066cc',
      color: 'white',
      textDecoration: 'none',
      borderRadius: '5px',
      fontWeight: 'bold',
      transition: 'background 0.3s'
    }
  };

  return (
    <div style={styles.container}>
      <h1 style={styles.errorCode}>404</h1>
      <p style={styles.message}>¡Ups! Parece que te has perdido en el trayecto.</p>
      <Link to="/" style={styles.button}>
        Volver al Inicio
      </Link>
    </div>
  );
}