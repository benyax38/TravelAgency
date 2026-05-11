import { BrowserRouter as Router } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import AppRoutes from './routes';

function App() {
  return (
    <Router>
      {/* Contenedor Flex: obliga a que los hijos se pongan uno al lado del otro */}
      <div style={{ display: 'flex', minHeight: '100vh' }}>
        
        {/* Lado Izquierdo: Sidebar fija */}
        <div style={{ width: '280px', flexShrink: 0 }}>
          <Sidebar />
        </div>

        {/* Lado Derecho: Contenido dinámico que crece para ocupar el resto */}
        <main style={{ flexGrow: 1, padding: '20px' }}>
          <AppRoutes />
        </main>

      </div>
    </Router>
  );
}

export default App;
