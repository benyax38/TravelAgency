import { useEffect, useState } from "react";
import authService from "../services/auth-service";

export default function Profile() {

  /*
    useState: hook que permite guardar estados dentro del componente
    useState[a, b]: a = valor actual, b = función para actualizarlo
  */
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);

  const [formData, setFormData] = useState({
    phone: "",
    dni: "",
    nationality: ""
  });

  // Permite cargar el usuario al iniciar la página
  useEffect(() => {
    loadUser();
  }, []);

  /*
    loadUser
    Descripción: Obtiene la infomación del usuario desde el backend 
    y decide si debe mostrar el formulario para completar el perfil
  */
  const loadUser = () => {

    // Llama a una función del servicio (revisar)
    authService.syncCurrentUser()
      .then(res => {
        // Aquí se extrae el usuario y se guarda en user
        const data = res.data;
        setUser(data);

        // Verifica si al usuario le faltan atributos que no están en el formulario de registro de Keycloak
        const missingData =
          !data.phone || !data.dni || !data.nationality;

        // Si falta alguno, muestra el formulario para completar el perfil
        if (missingData) {
          setShowForm(true);
        }
      })
      .catch(err => {
        // Captura errores HTTP o backend
        console.error("Error cargando usuario", err);
      })
      .finally(() => {
        // Quita el Cargando...
        setLoading(false);
      })
  };

  /*
    handleChange
    Descripción: Permite actualizar el estado del formulario 
    a medida que el usuario escribe. "e" es el evento del input.
  */
  const handleChange = (e) => {

    setFormData({
      // Copia el estado anterior del formulario
      ...formData,
      // Actualiza solo el campo que cambió
      [e.target.name]: e.target.value
    });
  };

  /*
  handleSubmit
  Descripción: Maneja el envío del formulario.
  */
  const handleSubmit = (e) => {

    // Evita que el navegador recargue la página
    e.preventDefault();

    // Fusiona el usuario actual con los nuevos datos ingresados
    const updatedUser = {
      ...user,
      ...formData
    };

    // Actualiza el estado local del usuario
    setUser(updatedUser);
    // Oculta el formulario
    setShowForm(false);
  };

  /*
  Renderizado condicional:
    - Si loading es true, muestra "Cargando..."
    - Si showForm es true, muestra el formulario para completar el perfil
    - Si no, muestra la información del perfil
  */
  if (loading) {
    return <div className="text-white text-center mt-5">Cargando...</div>;
  }

  return (
    <div className="container mt-4 text-white">

      {/* Formulario condicional */}
      {showForm ? (
        /* showForm = true */
        /* Se crea una tarjeta bootstrap */
        <div className="card bg-dark border-warning p-4">

          {/* Título del formulario */}
          <h3 className="text-warning mb-3">
            Completa tu perfil
          </h3>

          {/* Envía el formulario al dar en el botón Guardar y continuar */}
          <form onSubmit={handleSubmit}>

            {/* Campos del formulario */}
            <div className="mb-3">
              <label className="text-white">Teléfono</label>
              <input
                name="phone"
                className="form-control bg-secondary text-white"
                value={formData.phone}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <label className="text-white">DNI</label>
              <input
                name="dni"
                className="form-control bg-secondary text-white"
                value={formData.dni}
                onChange={handleChange}
                required
              />
            </div>

            <div className="mb-3">
              <label className="text-white">Nacionalidad</label>
              <input
                name="nationality"
                className="form-control bg-secondary text-white"
                value={formData.nationality}
                onChange={handleChange}
                required
              />
            </div>

            <button className="btn btn-success w-100">
              Guardar y continuar
            </button>

          </form>
        </div>

      ) : (

        /* Card para mostrar perfil */
        <div className="card bg-dark border-success p-4">

          {/* Título del perfil */}
          <h3 className="text-light mb-3">
            👤 Mi Perfil
          </h3>
          
          <p className="text-white"><b>Username:</b> {user.username}</p>
          <p className="text-white"><b>Email:</b> {user.email}</p>
          <p className="text-white"><b>Nombre:</b> {user.firstName}</p>
          <p className="text-white"><b>Apellido:</b> {user.lastName}</p>
          <p className="text-white"><b>Teléfono:</b> {user.phone || "Teléfono no especificado"}</p>
          <p className="text-white"><b>DNI:</b> {user.dni || "DNI no especificado"}</p>
          <p className="text-white"><b>Nacionalidad:</b> {user.nationality || "Nacionalidad no especificada"}</p>
          <p className="text-white"><b>Estado:</b> {user.userState}</p>
          <p className="text-white"><b>Reservas pagadas:</b> {user.paidReservations}</p>

        </div>
      )}

    </div>
  );
}