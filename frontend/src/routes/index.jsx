import { Routes, Route } from 'react-router-dom';
import Home from '../views/Home';
import UserProfile from '../views/UserProfile';
import Packages from '../views/Packages';
import AdminConfigDiscount from '../views/AdminConfigDiscount/AdminConfigDiscount';
import AdminPackages from '../views/AdminPackages';
import AdminUsers from '../views/AdminUsers';
import AdminReservations from '../views/AdminReservation';
import UserPackages from '../views/UserPackages';
import UserReservation from '../views/UserReservation';
import Cart from '../views/Cart';
import PaymentPage from '../views/PaymentPage';
import NotFound from '../views/NotFound';

// Definimos el componente de rutas y lo exportamos por defecto
export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/perfil" element={<UserProfile />} />
      <Route path="/paquetes" element={<Packages />} />
      <Route path="/admin/paquetes" element={<AdminPackages />} />
      <Route path="/admin/config-discount" element={<AdminConfigDiscount />} />
      <Route path="/admin/usuarios" element={<AdminUsers />} />
      <Route path="/admin/reservas" element={<AdminReservations />} />
      <Route path="/user/paquetes" element={<UserPackages />} />
      <Route path="/user/reservas" element={<UserReservation />} />
      <Route path="/carrito" element={<Cart />} />
      <Route path="/payment" element={<PaymentPage />} />
      {/* Ruta 404 para cuando no encuentra la página */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}