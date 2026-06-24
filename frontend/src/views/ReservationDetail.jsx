import React from 'react';

import { useLocation, useNavigate } from 'react-router-dom';

const ReservationDetail = () => {

    const location = useLocation();

    const navigate = useNavigate();

    const reservation = location.state;

    // Si alguien entra manualmente a la ruta
    if (!reservation) {

        return (

            <div className="container mt-5 text-center text-white">

                <h2>No se encontró la reserva</h2>

                <button
                    className="btn btn-success mt-3"
                    onClick={() => navigate('/cart')}
                >
                    Volver al carrito
                </button>

            </div>
        );
    }

    return (

        <div className="container mt-4 text-white">

            {/* CABECERA */}
            <div className="d-flex justify-content-between align-items-center mb-4">

                <div>

                    <h2 className="text-success">
                        🎟️ Detalle de Reserva
                    </h2>

                    <p className="mb-1">
                        <strong>ID Reserva:</strong>
                        {" "}
                        {reservation.reservationId}
                    </p>

                    <p className="mb-1">
                        <strong>Estado:</strong>
                        {" "}
                        {reservation.reservationState}
                    </p>

                    <p className="mb-1">
                        <strong>Fecha Reserva:</strong>
                        {" "}
                        {new Date(
                            reservation.reservationDate
                        ).toLocaleString()}
                    </p>

                    <p className="mb-0">
                        <strong>Límite de Pago:</strong>
                        {" "}
                        {new Date(
                            reservation.paymentDeadline
                        ).toLocaleString()}
                    </p>

                </div>

            </div>

            {/* PAQUETES */}
            <div className="card bg-dark border-success mb-4">

                <div className="card-header border-success">

                    <h5 className="m-0">
                        📦 Paquetes Reservados
                    </h5>

                </div>

                <div className="card-body p-0">

                    <div className="table-responsive">

                        <table className="table table-dark table-hover mb-0">

                            <thead>

                                <tr>

                                    <th>ID</th>

                                    <th>Paquete</th>

                                    <th>Destino</th>

                                    <th>Precio</th>

                                </tr>

                            </thead>

                            <tbody>

                                {reservation.packages.map(pkg => (

                                    <tr key={pkg.packageId}>

                                        <td>{pkg.packageId}</td>

                                        <td>{pkg.packageName}</td>

                                        <td>{pkg.destination}</td>

                                        <td>
                                            $
                                            {pkg.packagePrice?.toLocaleString()}
                                        </td>

                                    </tr>
                                ))}

                            </tbody>

                        </table>

                    </div>

                </div>

            </div>

            {/* INFORMACIÓN CLIENTE */}
            <div className="card bg-dark border-success mb-4">

                <div className="card-header border-success">

                    <h5 className="m-0">
                        👥 Información de la Reserva
                    </h5>

                </div>

                <div className="card-body">

                    <p>
                        <strong>Número de pasajeros:</strong>
                        {" "}
                        {reservation.passengersNum}
                    </p>

                    <p>
                        <strong>Acompañantes:</strong>
                        {" "}
                        {reservation.companionsDetails || "Sin información"}
                    </p>

                    <p>
                        <strong>Solicitudes especiales:</strong>
                        {" "}
                        {reservation.specialRequests || "Sin información"}
                    </p>

                    <p className="mb-0">
                        <strong>Preferencias:</strong>
                        {" "}
                        {reservation.customerPreferences || "Sin información"}
                    </p>

                </div>

            </div>

            {/* RESUMEN FINANCIERO */}
            <div className="card bg-dark border-success mb-4">

                <div className="card-header border-success">

                    <h5 className="m-0">
                        💰 Resumen Financiero
                    </h5>

                </div>

                <div className="card-body">

                    <div className="d-flex justify-content-between mb-2">

                        <span>Subtotal:</span>

                        <strong>
                            $
                            {reservation.subtotalAmount?.toLocaleString()}
                        </strong>

                    </div>

                    <div className="d-flex justify-content-between mb-2">

                        <span>Descuentos:</span>

                        <strong className="text-danger">
                            - $
                            {reservation.discountAmount?.toLocaleString()}
                        </strong>

                    </div>

                    <hr className="border-secondary" />

                    <div className="d-flex justify-content-between">

                        <h5>Total Final:</h5>

                        <h5 className="text-success">
                            $
                            {reservation.totalAmount?.toLocaleString()}
                        </h5>

                    </div>

                </div>

            </div>

            {/* BOTONES */}
            <div className="d-flex justify-content-end gap-3 mb-5">

                <button
                    className="btn btn-secondary"
                    onClick={() => navigate('/packages')}
                >
                    Seguir explorando
                </button>

                <button
                    className="btn btn-success btn-lg"
                >
                    💳 Proceder al Pago
                </button>

            </div>

        </div>
    );
};

export default ReservationDetail;