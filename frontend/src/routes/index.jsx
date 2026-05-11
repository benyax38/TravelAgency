import { Routes, Route } from 'react-router-dom';
import Home from '../views/Home';
import Profile from '../views/Profile';
import Packages from '../views/Packages';
import AdminPackages from '../views/AdminPackages';
import NotFound from '../views/NotFound';

// Definimos el componente de rutas y lo exportamos por defecto
export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/perfil" element={<Profile />} />
      <Route path="/paquetes" element={<Packages />} />
      <Route path="/admin/paquetes" element={<AdminPackages />} />
      
      {/* Ejemplo de ruta para Administración */}
      <Route path="/admin/usuarios" element={<h1>Gestión de Usuarios (Admin)</h1>} />
      
      {/* Ruta 404 para cuando no encuentra la página */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}