import React from 'react';

const AdminPackages = () => {
    return (
        /* Añadimos color: "white" al estilo del contenedor */
        <div style={{ padding: '20px', color: 'white', minHeight: '100vh' }}>
            <h1 style={{ color: '#0066cc' }}>Gestión de Paquetes (ADMIN)</h1>
            <p>Aquí aparecerá la tabla para crear, editar y eliminar paquetes turísticos.</p>
            
            <div style={{ 
                border: '2px dashed #0066cc', 
                padding: '20px', 
                marginTop: '20px',
                borderRadius: '8px',
                backgroundColor: 'rgba(255, 255, 255, 0.05)' /* Un toque de gris para que no sea negro total */
            }}>
                <h3 style={{ color: '#0066cc' }}>[Espacio reservado para el Formulario]</h3>
            </div>
        </div>
    );
};

export default AdminPackages;