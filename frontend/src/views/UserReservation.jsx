import { useEffect, useState } from 'react';
import { useNavigate } from "react-router-dom";
import reservationService from '../services/reservation-service';

export default function AdminReservations() {

  const [reservations, setReservations] = useState([]);
  const [selectedReservation, setSelectedReservation] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchReservations();
  }, []);

  const fetchReservations = () => {

    reservationService.getMyReservations()
      .then(res => setReservations(res.data))
      .catch(err =>
        console.error("Error al cargar reservas", err)
      );
  };

  const handleOpenModal = (reservation) => {

    if (!reservation) {
        console.warn("Reservation null/undefined");
        return;
    }

    setSelectedReservation(reservation);
    setShowModal(true);

    console.log("RESERVATION DETAIL:", reservation);
    console.log("PACKAGES:", reservation?.reservationPackages);
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const formatDate = (dateString) => {

    if (!dateString) return "N/A";

    return new Date(dateString).toLocaleDateString(
      'es-CL',
      {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }
    );
  };

  const getBadgeClass = (state) => {

    switch(state) {

        case "PENDING":
        return "bg-warning text-dark";

        case "PAID":
        return "bg-success";

        case "ANULLED":
        return "bg-danger";

        case "EXPIRED":
        return "bg-secondary";

        default:
        return "bg-light text-dark";
    }
 };

 const handleContinuePayment = (reservation) => {

    navigate("/payment", {
        state: {
            reservation
        }
    });

};

  return (

    <div className="container mt-4">

        <div className="d-flex justify-content-between align-items-center mb-4">

        <h2 className="text-blue m-0">
            📋 Mis Reservas
        </h2>

        </div>

        <div className="table-responsive">

        <table className="table table-dark table-hover align-middle">

            <thead>

            <tr>
                <th>ID</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Total</th>
                <th className="text-center">Acciones</th>
            </tr>

            </thead>

            <tbody>

            {reservations.map(reservation => (

                <tr key={reservation.reservationId}>

                <td>
                    {reservation.reservationId}
                </td>

                <td>
                    {formatDate(reservation.reservationDate)}
                </td>

                <td>

                    <span
                    className={`badge ${getBadgeClass(reservation.reservationState)}`}
                    >
                    {reservation.reservationState}
                    </span>

                </td>

                <td>
                    ${Number(reservation.totalAmount || 0).toLocaleString()}
                </td>

                <td className="text-center">

                    <div className="btn-group">

                        <button
                            className="btn btn-info btn-sm"
                            title="Ver detalle"
                            onClick={() => handleOpenModal(reservation)}
                        >
                            👁️
                        </button>

                        {reservation.reservationState === "PENDING" && (
                            <button
                                className="btn btn-success btn-sm"
                                title="Continuar pago"
                                onClick={() => handleContinuePayment(reservation)}
                            >
                                💳
                            </button>
                        )}

                    </div>

                </td>

                </tr>

            ))}

            </tbody>

        </table>

        </div>

        {/* MODAL DETALLE */}
        {showModal && selectedReservation && (

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
                    Reserva #{selectedReservation?.reservationId}
                    </h5>

                    <button
                    type="button"
                    className="btn-close btn-close-white"
                    onClick={handleCloseModal}
                    ></button>

                </div>

                <div className="modal-body">

                    {selectedReservation && (

                    <div className="row g-3">

                        {/* COLUMNA IZQUIERDA */}
                        <div className="col-md-6">

                        <p>
                            <strong>🆔 ID Reserva:</strong>
                            {" "}
                            {selectedReservation.reservationId}
                        </p>

                        <p>
                            <strong>👤 Usuario:</strong>
                            {" "}
                            {selectedReservation.user?.username || "N/A"}
                        </p>

                        <p>
                            <strong>👥 Número pasajeros:</strong>
                            {" "}
                            {selectedReservation.passengersNum}
                        </p>

                        <p>
                            <strong>📌 Estado:</strong>
                            {" "}

                            <span
                            className={`badge ${getBadgeClass(selectedReservation.reservationState)}`}
                            >
                            {selectedReservation.reservationState}
                            </span>

                        </p>

                        <p>
                            <strong>🗓️ Fecha reserva:</strong>
                            {" "}
                            {formatDate(selectedReservation.reservationDate)}
                        </p>

                        <p>
                            <strong>⏰ Límite pago:</strong>
                            {" "}
                            {formatDate(selectedReservation.paymentDeadline)}
                        </p>

                        </div>

                        {/* COLUMNA DERECHA */}
                        <div className="col-md-6">

                        <p>
                            <strong>💵 Subtotal:</strong>
                            {" "}
                            ${selectedReservation.subtotalAmount?.toLocaleString()}
                        </p>

                        <p>
                            <strong>🏷️ Descuento:</strong>
                            {" "}
                            ${selectedReservation.discountAmount?.toLocaleString()}
                        </p>

                        <p>
                            <strong>💰 Total final:</strong>
                            {" "}
                            ${selectedReservation.totalAmount?.toLocaleString()}
                        </p>

                        <p>
                            <strong>🛒 Compra asociada:</strong>
                            {" "}
                            {selectedReservation.purchase?.purchaseId || "N/A"}
                        </p>

                        </div>

                        <hr className="border-secondary" />

                        {/* TEXTOS LARGOS */}
                        <div className="col-12">

                        <p>
                            <strong>👥 Detalle acompañantes:</strong>
                            <br />
                            {selectedReservation.companionsDetails || "Sin información"}
                        </p>

                        <p>
                            <strong>⭐ Solicitudes especiales:</strong>
                            <br />
                            {selectedReservation.specialRequests || "Sin información"}
                        </p>

                        <p>
                            <strong>⚙️ Preferencias cliente:</strong>
                            <br />
                            {selectedReservation.customerPreferences || "Sin información"}
                        </p>

                        </div>

                        <hr className="border-secondary" />

                        {/* PAQUETES */}
                        <div className="col-12">

                        <h5>📦 Paquetes Reservados</h5>

                        <ul className="list-group">

                            {selectedReservation.reservationPackages?.map(rp => (

                            <li
                                key={rp.reservationPackageId}
                                className="list-group-item bg-secondary text-white"
                            >

                                <div className="d-flex justify-content-between">

                                <div>

                                    <strong>
                                    {rp.tourPackage?.packageName || "Paquete"}
                                    </strong>

                                    <br />

                                    <small>
                                    ID paquete:
                                    {" "}
                                    {rp.tourPackage?.packageId}
                                    </small>

                                </div>

                                </div>

                            </li>

                            ))}

                        </ul>

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

    </div>
    );
}