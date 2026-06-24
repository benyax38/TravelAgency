import { useEffect, useState } from 'react';
import userService from '../services/user-service';

export default function AdminUsers() {

  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);

  const [editForm, setEditForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    nationality: '',
    dni: ''
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = () => {

    userService.getAllUsers()
      .then(res => setUsers(res.data))
      .catch(err => {
        console.error("Error al cargar usuarios", err);
        alert("Error al obtener usuarios");
      });
  };

  const handleOpenModal = (user) => {

    setSelectedUser(user);
    setShowModal(true);
  };

  const handleCloseModal = () => {

    setShowModal(false);
    setSelectedUser(null);
  };

  const handleOpenEditModal = (user) => {

    setSelectedUser(user);

    setEditForm({
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      email: user.email || '',
      phone: user.phone || '',
      nationality: user.nationality || '',
      dni: user.dni || ''
    });

    setShowEditModal(true);
  };

  const handleCloseEditModal = () => {

    setShowEditModal(false);
    setSelectedUser(null);
  };

  const handleInputChange = (e) => {

    const { name, value } = e.target;

    setEditForm(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSaveChanges = async () => {

    try {

      const payload = {
        phone: editForm.phone,
        dni: editForm.dni,
        nationality: editForm.nationality
      };

      await userService.updateUser(selectedUser.userId, payload);

      alert("Usuario actualizado correctamente");

      // Recargar tabla
      fetchUsers();

      // Cerrar modal
      handleCloseEditModal();

    } catch (error) {

      console.error("Error actualizando usuario", error);

      alert("Error al actualizar usuario");
    }
  };

  const handleActivateUser = async (userId) => {

    const confirmActivate = window.confirm(
      "¿Estás seguro de activar este usuario?"
    );

    if (!confirmActivate) return;

    try {

      await userService.activateUser(userId);

      alert("Usuario activado correctamente");

      // Refresca tabla
      fetchUsers();

    } catch (error) {

      console.error(
        "Error activando usuario",
        error
      );

      alert("Error al activar usuario");
    }
  };

  const handleDeactivateUser = async (userId) => {

    const confirmDeactivate = window.confirm(
      "¿Estás seguro de dejar este usuario inactivo?"
    );

    if (!confirmDeactivate) return;

    try {

      await userService.deactivateUser(userId);

      alert("Usuario desactivado correctamente");

      // Pequeña pausa de 300ms para asegurar la consistencia en el backend
      setTimeout(() => {
        fetchUsers();
      }, 300);

    } catch (error) {

      console.error(
        "Error desactivando usuario",
        error
      );

      alert("Error al desactivar usuario");
    }
  };

  const formatDate = (dateString) => {

    if (!dateString) return "N/A";

    return new Date(dateString).toLocaleDateString('es-CL', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getUserStateBadge = (state) => {

    switch (state) {

      case "ACTIVE":
        return "bg-success";

      case "BLOCKED":
        return "bg-warning text-dark";

      case "INACTIVE":
        return "bg-secondary";

      default:
        return "bg-dark";
    }
  };

  return (
    <div className="container mt-4">

      {/* CABECERA */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2 className="text-blue m-0">
          👥 Gestión de Usuarios (Admin)
        </h2>
      </div>

      {/* TABLA */}
      <div className="table-responsive">
        <table className="table table-dark table-hover align-middle">

          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Email</th>
              <th>Estado</th>
              <th className="text-center">Acciones</th>
            </tr>
          </thead>

          <tbody>

            {users.map(user => (

              <tr key={user.userId}>

                <td>{user.userId}</td>

                <td>
                  {user.firstName} {user.lastName}
                </td>

                <td>{user.email}</td>

                <td>
                  <span className={`badge ${getUserStateBadge(user.userState)}`}>
                    {user.userState}
                  </span>
                </td>

                <td className="text-center">

                  <div className="d-flex justify-content-center gap-2 flex-wrap">
                    {/* EDITAR */}
                    <button
                      className="btn btn-info btn-sm"
                      title="Ver detalle"
                      onClick={() => handleOpenModal(user)}
                    >
                      👁️
                    </button>

                    {/* EDITAR */}
                    <button
                      className="btn btn-warning btn-sm"
                      title="Editar usuario"
                      onClick={() => handleOpenEditModal(user)}
                    >
                      ✏️
                    </button>

                    {/* BLOQUEAR */}
                    <button
                      className="btn btn-danger btn-sm"
                      title="Bloquear usuario"
                      onClick={() => alert("Bloquear usuario próximamente")}
                    >
                      🔒
                    </button>

                    {/* ACTIVAR / DESACTIVAR */}
                    <button
                      className={`btn btn-sm ${
                        user.userState === "ACTIVE"
                          ? "btn-secondary"
                          : "btn-success"
                      }`}
                      title={
                        user.userState === "ACTIVE"
                          ? "Desactivar usuario"
                          : "Activar usuario"
                      }
                      onClick={() =>
                        user.userState === "ACTIVE"
                          ? handleDeactivateUser(user.userId)
                          : handleActivateUser(user.userId)
                      }
                    >
                      {user.userState === "ACTIVE" ? "⛔" : "✅"}
                    </button>

                  </div>

                </td>

              </tr>

            ))}

          </tbody>

        </table>
      </div>

      {/* MODAL DETALLE */}
      {showModal && (
        <>
          <div
            className="modal fade show"
            style={{
              display: 'block',
              backgroundColor: 'rgba(0,0,0,0.7)'
            }}
            tabIndex="-1"
          >

            <div className="modal-dialog modal-lg">

              <div className="modal-content bg-dark text-white border-secondary">

                <div className="modal-header border-secondary">

                  <h5 className="modal-title">
                    Detalle Usuario
                  </h5>

                  <button
                    type="button"
                    className="btn-close btn-close-white"
                    onClick={handleCloseModal}
                  ></button>

                </div>

                <div className="modal-body">

                  {selectedUser && (

                    <div className="row g-3">

                      {/* COLUMNA IZQUIERDA */}
                      <div className="col-md-6">

                        <p>
                          <strong>🆔 ID:</strong>{" "}
                          {selectedUser.userId}
                        </p>

                        <p>
                          <strong>👤 Username:</strong>{" "}
                          {selectedUser.username || "N/A"}
                        </p>

                        <p>
                          <strong>📛 Nombre:</strong>{" "}
                          {selectedUser.firstName || "N/A"}
                        </p>

                        <p>
                          <strong>📛 Apellido:</strong>{" "}
                          {selectedUser.lastName || "N/A"}
                        </p>

                        <p>
                          <strong>📧 Email:</strong>{" "}
                          {selectedUser.email || "N/A"}
                        </p>

                        <p>
                          <strong>📱 Teléfono:</strong>{" "}
                          {selectedUser.phone || "N/A"}
                        </p>

                      </div>

                      {/* COLUMNA DERECHA */}
                      <div className="col-md-6">

                        <p>
                          <strong>🪪 DNI:</strong>{" "}
                          {selectedUser.dni || "N/A"}
                        </p>

                        <p>
                          <strong>🌎 Nacionalidad:</strong>{" "}
                          {selectedUser.nationality || "N/A"}
                        </p>

                        <p>
                          <strong>✅ Estado:</strong>{" "}
                          
                          <span
                            className={`badge ${
                              selectedUser.userState === "ACTIVE"
                                ? "bg-success"
                                : selectedUser.userState === "BLOCKED"
                                ? "bg-warning text-dark"
                                : "bg-secondary"
                            }`}
                          >
                            {selectedUser.userState}
                          </span>

                        </p>

                        <p>
                          <strong>🎟️ Reservas Pagadas:</strong>{" "}
                          {selectedUser.paidReservations ?? 0}
                        </p>

                        <p className="text-break">
                          <strong>🔐 Keycloak ID:</strong>{" "}
                          {selectedUser.keycloakId || "N/A"}
                        </p>

                      </div>

                    </div>

                  )}

                </div>

                <div className="modal-footer border-secondary">

                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={handleCloseModal}
                  >
                    Cerrar
                  </button>

                </div>

              </div>

            </div>

          </div>

          <div className="modal-backdrop fade show"></div>
        </>
      )}

      {/* MODAL EDITAR */}
      {showEditModal && (
        <>
          <div
            className="modal fade show"
            style={{
              display: 'block',
              backgroundColor: 'rgba(0,0,0,0.7)'
            }}
            tabIndex="-1"
          >

            <div className="modal-dialog modal-lg">

              <div className="modal-content bg-dark text-white border-secondary">

                {/* HEADER */}
                <div className="modal-header border-secondary">

                  <h5 className="modal-title">
                    ✏️ Editar Usuario
                  </h5>

                  <button
                    type="button"
                    className="btn-close btn-close-white"
                    onClick={handleCloseEditModal}
                  ></button>

                </div>

                {/* BODY */}
                <div className="modal-body">

                  <form>

                    <div className="row g-3">

                      {/* NOMBRE */}
                      <div className="col-md-6">

                        <label className="form-label">
                          Nombre
                        </label>

                        <div className="form-control bg-dark text-white border-warning d-flex align-items-center">
                          {editForm.firstName || "N/A"}
                        </div>

                      </div>

                      {/* APELLIDO */}
                      <div className="col-md-6">

                        <label className="form-label">
                          Apellido
                        </label>

                        <div className="form-control bg-dark text-white border-warning d-flex align-items-center">
                          {editForm.lastName || "N/A"}
                        </div>

                      </div>

                      {/* EMAIL */}
                      <div className="col-md-6">

                        <label className="form-label">
                          Email
                        </label>

                        <div className="form-control bg-dark text-white border-warning d-flex align-items-center text-truncate">
                          {editForm.email || "N/A"}
                        </div>

                      </div>

                      {/* TELÉFONO */}
                      <div className="col-md-6">

                        <label className="form-label">
                          Teléfono
                        </label>

                        <input
                          type="text"
                          className="form-control bg-dark text-white border-secondary"
                          name="phone"
                          value={editForm.phone}
                          onChange={handleInputChange}
                        />

                      </div>

                      {/* NACIONALIDAD */}
                      <div className="col-md-6">

                        <label className="form-label">
                          Nacionalidad
                        </label>

                        <input
                          type="text"
                          className="form-control bg-dark text-white border-secondary"
                          name="nationality"
                          value={editForm.nationality}
                          onChange={handleInputChange}
                        />

                      </div>

                      {/* DNI */}
                      <div className="col-md-6">

                        <label className="form-label">
                          DNI
                        </label>

                        <input
                          type="text"
                          className="form-control bg-dark text-white border-secondary"
                          name="dni"
                          value={editForm.dni}
                          onChange={handleInputChange}
                        />

                      </div>

                    </div>

                  </form>

                </div>

                {/* FOOTER */}
                <div className="modal-footer border-secondary">

                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={handleCloseEditModal}
                  >
                    Cancelar
                  </button>

                  <button
                    type="button"
                    className="btn btn-warning"
                    onClick={handleSaveChanges}
                  >
                    Guardar Cambios
                  </button>

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