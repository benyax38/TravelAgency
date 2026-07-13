import { useLocation } from "react-router-dom";

function PaymentPage() {

    const location = useLocation();

    const reservation = location.state?.reservation;

    if (!reservation) {

        return (

            <div className="container mt-5">

                <h3>
                    Reserva no encontrada
                </h3>

            </div>

        );
    }

    return (

        <div className="container py-5">

            <div className="row">

                {/* RESUMEN */}
                <div className="col-md-6">

                    <div className="card bg-dark text-white">

                        <div className="card-header">

                            Resumen de Compra

                        </div>

                        <div className="card-body">

                            <p>

                                Reserva N°
                                {reservation.reservationId}

                            </p>

                            <p>

                                Pasajeros:
                                {reservation.passengersNum}

                            </p>

                            <p>

                                Subtotal:
                                ${reservation.subtotalAmount}

                            </p>

                            <p>

                                Descuento:
                                ${reservation.discountAmount}

                            </p>

                            <hr />

                            <h5>

                                Total:
                                ${reservation.totalAmount}

                            </h5>

                        </div>

                    </div>

                </div>

                {/* FORMULARIO */}
                <div className="col-md-6">

                    <div className="card bg-dark text-white">

                        <div className="card-header">

                            Pago Simulado

                        </div>

                        <div className="card-body">

                            <div className="mb-3">

                                <label>
                                    Número de tarjeta
                                </label>

                                <input
                                    type="text"
                                    className="form-control"
                                />

                            </div>

                            <div className="mb-3">

                                <label>
                                    Fecha expiración
                                </label>

                                <input
                                    type="text"
                                    placeholder="MM/AA"
                                    className="form-control"
                                />

                            </div>

                            <div className="mb-3">

                                <label>
                                    CVV
                                </label>

                                <input
                                    type="password"
                                    className="form-control"
                                />

                            </div>

                            <button
                                className="btn btn-success w-100"
                            >
                                Pagar
                            </button>

                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default PaymentPage;