import { useEffect, useState } from 'react';
import packageService from '../services/package-service';

export default function AdminPackages() {
  const [packages, setPackages] = useState([]);
  const [selectedPackage, setSelectedPackage] = useState(null);
  const [showModal, setShowModal] = useState(false);

  // 🔥 Nuevo estado para el formulario de creación
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [formData, setFormData] = useState({
    packageName: "", destination: "", description: "",
    startDate: "", duration: 0, prize: 0,
    includedServices: "", conditions: "", restriction: "",
    totalSlots: 0, tripType: "", season: "", category: ""
  });

  useEffect(() => {
    fetchPackages();
  }, []);

  const fetchPackages = () => {
    packageService.getAllPackages()
      .then(res => setPackages(res.data))
      .catch(err => console.error("Error al cargar paquetes", err));
  };

  // Manejador de cambios en los inputs
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  // Función para enviar el formulario (MEJORADA)
  const handleCreatePackage = (e) => {
    e.preventDefault();
    
    // Convertimos strings a números y formateamos fecha
    const payload = { 
      ...formData, 
      startDate: formData.startDate + ":00", // Añade segundos para el formato ISO local
      duration: parseInt(formData.duration),
      prize: parseFloat(formData.prize),
      totalSlots: parseInt(formData.totalSlots)
    };
    
    packageService.createPackage(payload)
  .then((res) => {

    alert("¡Paquete creado con éxito!");

    // Agrega inmediatamente el nuevo paquete a la tabla
    setPackages(prev => [...prev, res.data]);

    setShowCreateModal(false);

    // Reset formulario
    setFormData({
      packageName: "",
      destination: "",
      description: "",
      startDate: "",
      duration: 0,
      prize: 0,
      includedServices: "",
      conditions: "",
      restriction: "",
      totalSlots: 0,
      tripType: "",
      season: "",
      category: ""
    });
  })
      .catch(err => {

        console.error("Error al crear", err);

        const errors = err.response?.data;

        // Si el backend devolvió errores de validación
        if (errors && typeof errors === "object") {

            const messages = Object.values(errors);

            alert(messages.join("\n"));

        } else {

            alert("Servidor no disponible");
        }
    });
  };

  const handleCancelPackage = (id) => {

    if (!window.confirm("¿Cancelar este paquete?")) {
        return;
    }

    packageService.cancelPackage(id)
        .then(() => {

            setPackages(prev =>
                prev.map(pkg =>
                    pkg.packageId === id
                        ? { ...pkg, packageState: "CANCELLED" }
                        : pkg
                )
            );
        })
        .catch(err => {
            console.error(err);
            alert("Error al cancelar paquete");
        });
  };

  const handleOpenModal = (pkg) => {
    setSelectedPackage(pkg);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString('es-CL', {
      year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  };

  const getBadgeClass = (state) => {

    switch(state) {

        case "AVAILABLE":
            return "bg-success";

        case "SOLD_OUT":
            return "bg-warning text-dark";

        case "NOT_AVAILABLE":
            return "bg-secondary";

        case "CANCELLED":
            return "bg-danger";

        default:
            return "bg-light text-dark";
    }
};

  return (
    <div className="container mt-4">
      {/* CABECERA CON BOTÓN CREAR */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="text-blue m-0">📦 Gestión de Paquetes (Admin)</h2>
        <button 
          className="btn btn-success" 
          onClick={() => setShowCreateModal(true)}>
          ➕ Crear Nuevo Paquete
        </button>
      </div>
      
      <div className="table-responsive">
        <table className="table table-dark table-hover align-middle">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Destino</th>
              <th>Precio</th>
              <th>Estado</th>
              <th className="text-center">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {packages.map(pkg => (
              <tr key={pkg.packageId}>
                <td>{pkg.packageId}</td>
                <td>{pkg.packageName}</td>
                <td>{pkg.destination}</td>
                <td>${pkg.prize?.toLocaleString()}</td>
                <td>
                  <span className={`badge ${getBadgeClass(pkg.packageState)}`}>
                    {pkg.packageState}
                  </span>
                </td>
                <td className="text-center">
                  {/* GRUPO DE BOTONES PEQUEÑOS */}
                  <div className="btn-group" role="group">
                    <button 
                      className="btn btn-info btn-sm" 
                      title="Ver detalle"
                      onClick={() => handleOpenModal(pkg)}
                    >
                      👁️
                    </button>
                    <button 
                      className="btn btn-warning btn-sm" 
                      title="Editar"
                      onClick={() => alert("Editando: " + pkg.packageName)}
                    >
                      ✏️
                    </button>
                    <button 
                      className="btn btn-danger btn-sm" 
                      title="Eliminar"
                      onClick={() => handleCancelPackage(pkg.packageId)}
                    >
                      🗑️
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* 🔥 MODAL DE CREACIÓN (NUEVO) */}
      {showCreateModal && (
        <>
          <div className="modal fade show" style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.8)' }} tabIndex="-1">
            <div className="modal-dialog modal-xl"> {/* modal-xl para tener espacio */}
              <div className="modal-content bg-dark text-white border-success">
                <form onSubmit={handleCreatePackage}>
                  <div className="modal-header border-success">
                    <h5 className="modal-title text-success">Nuevo Paquete Turístico</h5>
                    <button type="button" className="btn-close btn-close-white" onClick={() => setShowCreateModal(false)}></button>
                  </div>
                  
                  <div className="modal-body">
                    <div className="row g-3">
                      {/* Sección 1: Información Básica */}
                      <div className="col-md-6">
                        <label className="form-label">Nombre del Paquete</label>
                        <input type="text" name="packageName" value={formData.packageName} className="form-control bg-secondary text-white border-0" required onChange={handleInputChange} />
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Destino</label>
                        <input type="text" name="destination" value={formData.destination} className="form-control bg-secondary text-white border-0" required onChange={handleInputChange} />
                      </div>

                      {/* Sección 2: Logística y Comercial */}
                      <div className="col-md-3">
                        <label className="form-label">Precio ($)</label>
                        <input type="number" name="prize" value={formData.prize} className="form-control bg-secondary text-white border-0" required onChange={handleInputChange} />
                      </div>
                      <div className="col-md-3">
                        <label className="form-label">Cupos Totales</label>
                        <input type="number" name="totalSlots" value={formData.totalSlots} className="form-control bg-secondary text-white border-0" required onChange={handleInputChange} />
                      </div>
                      <div className="col-md-3">
                        <label className="form-label">Fecha Inicio</label>
                        <input type="datetime-local" name="startDate" value={formData.startDate} className="form-control bg-secondary text-white border-0" required onChange={handleInputChange} />
                      </div>
                      <div className="col-md-3">
                        <label className="form-label">Duración (Días)</label>
                        <input type="number" name="duration" value={formData.duration} className="form-control bg-secondary text-white border-0" required onChange={handleInputChange} />
                      </div>

                      {/* Sección 3: Categorización */}
                      <div className="col-md-4">
                        <label className="form-label">Tipo de Viaje</label>
                        <select name="tripType" value={formData.tripType} className="form-select bg-secondary text-white border-0" onChange={handleInputChange}>
                          <option value="">Seleccionar...</option>
                          <option value="Aventura">Aventura</option>
                          <option value="Relajo">Relajo</option>
                          <option value="Cultura">Cultura</option>
                        </select>
                      </div>
                      <div className="col-md-4">
                        <label className="form-label">Temporada</label>
                        <select name="season" value={formData.season} className="form-select bg-secondary text-white border-0" onChange={handleInputChange}>
                          <option value="">Seleccionar...</option>
                          <option value="Verano">Verano</option>
                          <option value="Invierno">Invierno</option>
                        </select>
                      </div>
                      <div className="col-md-4">
                        <label className="form-label">Categoría</label>
                        <select name="category" value={formData.category} className="form-select bg-secondary text-white border-0" onChange={handleInputChange}>
                          <option value="">Seleccionar...</option>
                          <option value="Económico">Económico</option>
                          <option value="Premium">Premium</option>
                        </select>
                      </div>

                      {/* Sección 4: Textos Largos */}
                      <div className="col-md-6">
                        <label className="form-label">Descripción</label>
                        <textarea name="description" value={formData.description} className="form-control bg-secondary text-white border-0" rows="2" onChange={handleInputChange}></textarea>
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Servicios Incluidos</label>
                        <textarea name="includedServices" value={formData.includedServices} className="form-control bg-secondary text-white border-0" rows="2" onChange={handleInputChange}></textarea>
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Condiciones</label>
                        <textarea name="conditions" value={formData.conditions} className="form-control bg-secondary text-white border-0" rows="2" onChange={handleInputChange}></textarea>
                      </div>
                      <div className="col-md-6">
                        <label className="form-label">Restricciones</label>
                        <textarea name="restriction" value={formData.restriction} className="form-control bg-secondary text-white border-0" rows="2" onChange={handleInputChange}></textarea>
                      </div>
                    </div>
                  </div>
                  
                  <div className="modal-footer border-success">
                    <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>Cancelar</button>
                    <button type="submit" className="btn btn-success">💾 Guardar Paquete</button>
                  </div>
                </form>
              </div>
            </div>
          </div>
          <div className="modal-backdrop fade show"></div>
        </>
        )}

      {/* MODAL DE DETALLE (BOTÓN EDITAR ELIMINADO) */}
      {showModal && (
        <>
          <div className="modal fade show" style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.7)' }} tabIndex="-1">
            <div className="modal-dialog modal-lg">
              <div className="modal-content bg-dark text-white border-secondary">
                <div className="modal-header border-secondary">
                  <h5 className="modal-title">Detalle: {selectedPackage?.packageName}</h5>
                  <button type="button" className="btn-close btn-close-white" onClick={handleCloseModal}></button>
                </div>
                
                <div className="modal-body">
                  {selectedPackage && (
                    <div className="row">
                      <div className="col-md-6">
                        <p><strong>📍 Destino:</strong> {selectedPackage.destination}</p>
                        <p><strong>💰 Precio:</strong> ${selectedPackage.prize?.toLocaleString()}</p>
                        <p><strong>⏳ Duración:</strong> {selectedPackage.duration} días</p>
                        <p><strong>👥 Cupos:</strong> {selectedPackage.availableSlots} / {selectedPackage.totalSlots}</p>
                      </div>
                      <div className="col-md-6">
                        <p><strong>🗓️ Inicio:</strong> {formatDate(selectedPackage.startDate)}</p>
                        <p><strong>🏁 Fin:</strong> {formatDate(selectedPackage.endDate)}</p>
                        <p><strong>🏔️ Tipo:</strong> {selectedPackage.tripType}</p>
                        <p><strong>🌟 Categoría:</strong> {selectedPackage.category}</p>
                      </div>
                      <hr className="border-secondary" />
                      <div className="col-12" style={{ maxHeight: '300px', overflowY: 'auto' }}>
                        <p><strong>📝 Descripción:</strong><br/>{selectedPackage.description}</p>
                        <p><strong>🛠️ Servicios Incluidos:</strong><br/>{selectedPackage.includedServices}</p>
                        <p><strong>⚠️ Restricciones:</strong><br/>{selectedPackage.restriction}</p>
                        <p><strong>📜 Condiciones:</strong><br/>{selectedPackage.conditions}</p>
                      </div>
                    </div>
                  )}
                </div>
                
                <div className="modal-footer border-secondary">
                  <button type="button" className="btn btn-secondary" onClick={handleCloseModal}>Cerrar</button>
                </div>
              </div>
            </div>
          </div>
          <div className="modal-backdrop fade show"></div>
        </>
      )}
    </div>
  );
}