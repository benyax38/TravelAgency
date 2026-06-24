import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import reservationService from '../services/reservation-service';

const Cart = () => {

    const navigate = useNavigate();

    const [cartItems, setCartItems] = useState([]);

    const [showModal, setShowModal] = useState(false);

    const [formData, setFormData] = useState({

        userId: 1,

        passengersNum: 1,

        companionsDetails: "",

        specialRequests: "",

        customerPreferences: ""
    });

    // Cargar carrito
    useEffect(() => {

        const savedCart =
            JSON.parse(localStorage.getItem('cart')) || [];

        setCartItems(savedCart);

    }, []);

    const handleOpenModal = () => {

        setShowModal(true);
    };

    const handleCloseModal = () => {

        setShowModal(false);
    };

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const removeFromCart = (packageId) => {

        const updatedCart = cartItems.filter(
            item => item.packageId !== packageId
        );

        setCartItems(updatedCart);

        localStorage.setItem(
            'cart',
            JSON.stringify(updatedCart)
        );
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const payload = {

                packageIds: cartItems.map(
                    item => item.packageId
                ),

                passengersNum:
                    parseInt(formData.passengersNum),

                companionsDetails:
                    formData.companionsDetails.trim() || null,

                specialRequests:
                    formData.specialRequests.trim() || null,

                customerPreferences:
                    formData.customerPreferences.trim() || null
            };

            console.log(payload);

            const response =
                await reservationService.createReservation(
                    payload
                );

            const reservation =
                response.data;

            alert("¡Reservas realizadas con éxito!");

            // Vaciar carrito
            localStorage.removeItem('cart');

            setCartItems([]);

            handleCloseModal();

            // Navegar al resumen
            navigate(
                `/reservation-detail/${reservation.reservationId}`,
                {
                    state: reservation
                }
            );

        } catch (error) {

            console.error(error);

            alert("Error al procesar las reservas");
        }
    };

    const total = cartItems.reduce(
        (sum, item) => sum + item.prize,
        0
    );

    return (

        <div className="container mt-4">

            {/* CABECERA */}
            <div className="d-flex justify-content-between align-items-center mb-4">

                <h2 className="m-0">
                    🛒 Mi Carrito
                </h2>

                <h4 className="text-success m-0">
                    Total parcial: ${total.toLocaleString()}
                </h4>

            </div>

            {/* TABLA */}
            <div className="table-responsive">

                <table className="table table-dark table-hover align-middle">

                    <thead>

                        <tr>

                            <th>ID</th>

                            <th>Paquete</th>

                            <th>Destino</th>

                            <th>Precio</th>

                            <th className="text-center">
                                Acciones
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        {cartItems.length > 0 ? (

                            cartItems.map(pkg => (

                                <tr key={pkg.packageId}>

                                    <td>{pkg.packageId}</td>

                                    <td>{pkg.packageName}</td>

                                    <td>{pkg.destination}</td>

                                    <td>
                                        ${pkg.prize?.toLocaleString()}
                                    </td>

                                    <td className="text-center">

                                        <button
                                            className="btn btn-danger btn-sm"
                                            onClick={() => removeFromCart(pkg.packageId)}
                                        >
                                            🗑️
                                        </button>

                                    </td>

                                </tr>
                            ))

                        ) : (

                            <tr>

                                <td
                                    colSpan="5"
                                    className="text-center"
                                >

                                    El carrito está vacío

                                </td>

                            </tr>
                        )}

                    </tbody>

                </table>

            </div>

            {/* BOTÓN GLOBAL */}
            {cartItems.length > 0 && (

                <div className="d-flex justify-content-end mt-3">

                    <button
                        className="btn btn-success btn-lg"
                        onClick={handleOpenModal}
                    >
                        💳 Reservar Todos los Paquetes
                    </button>

                </div>
            )}

            {/* MODAL */}
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

                        <div className="modal-dialog">

                            <div className="modal-content bg-dark text-white border-success">

                                <form onSubmit={handleSubmit}>

                                    <div className="modal-header border-success">

                                        <h5 className="modal-title">
                                            Finalizar Reservas
                                        </h5>

                                        <button
                                            type="button"
                                            className="btn-close btn-close-white"
                                            onClick={handleCloseModal}
                                        ></button>

                                    </div>

                                    <div className="modal-body">

                                        {/* RESUMEN */}
                                        <div className="mb-4">

                                            <h6 className="text-success">
                                                Paquetes seleccionados:
                                            </h6>

                                            <ul className="list-group">

                                                {cartItems.map(pkg => (

                                                    <li
                                                        key={pkg.packageId}
                                                        className="list-group-item bg-dark text-white border-secondary"
                                                    >

                                                        {pkg.packageName}
                                                        {" - "}
                                                        ${pkg.prize?.toLocaleString()}

                                                    </li>
                                                ))}

                                            </ul>

                                        </div>

                                        {/* FORMULARIO */}
                                        <div className="mb-3">

                                            <label className="form-label">
                                                Número de pasajeros
                                            </label>

                                            <input
                                                type="number"
                                                name="passengersNum"
                                                value={formData.passengersNum}
                                                onChange={handleChange}
                                                className="form-control bg-secondary text-white border-0"
                                                required
                                            />

                                        </div>

                                        <div className="mb-3">

                                            <label className="form-label">
                                                Acompañantes
                                            </label>

                                            <textarea
                                                name="companionsDetails"
                                                value={formData.companionsDetails}
                                                onChange={handleChange}
                                                className="form-control bg-secondary text-white border-0"
                                                rows="2"
                                            ></textarea>

                                        </div>

                                        <div className="mb-3">

                                            <label className="form-label">
                                                Solicitudes especiales
                                            </label>

                                            <input
                                                type="text"
                                                name="specialRequests"
                                                value={formData.specialRequests}
                                                onChange={handleChange}
                                                className="form-control bg-secondary text-white border-0"
                                            />

                                        </div>

                                        <div className="mb-3">

                                            <label className="form-label">
                                                Preferencias
                                            </label>

                                            <input
                                                type="text"
                                                name="customerPreferences"
                                                value={formData.customerPreferences}
                                                onChange={handleChange}
                                                className="form-control bg-secondary text-white border-0"
                                            />

                                        </div>

                                    </div>

                                    <div className="modal-footer border-success">

                                        <button
                                            type="button"
                                            className="btn btn-secondary"
                                            onClick={handleCloseModal}
                                        >
                                            Cancelar
                                        </button>

                                        <button
                                            type="submit"
                                            className="btn btn-success"
                                        >
                                            💳 Confirmar Reservas
                                        </button>

                                    </div>

                                </form>

                            </div>

                        </div>

                    </div>

                    <div className="modal-backdrop fade show"></div>

                </>
            )}
        </div>
    );
};

export default Cart;