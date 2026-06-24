import React, { useEffect, useState } from 'react';
import packageService from "../services/package-service";

const UserPackages = () => {

    const [packages, setPackages] = useState([]);
    const [selectedPackage, setSelectedPackage] = useState(null);
    const [showModal, setShowModal] = useState(false);

    const [showFilters, setShowFilters] = useState(false);
    const [filters, setFilters] = useState({
        destination: '',
        minPrice: '',
        maxPrice: '',
        duration: '',
        tripType: ''
    });

    useEffect(() => {
        loadPackages();
    }, []);

    const loadPackages = async () => {

        try {

            const response =
                await packageService.getPublicPackages();

            setPackages(response.data);

        } catch (error) {

            console.error("Error cargando paquetes:", error);
        }
    };

    const handleShowDetail = (pkg) => {

        setSelectedPackage(pkg);

        setShowModal(true);
    };

    const handleCloseModal = () => {

        setShowModal(false);
    };

    const addToCart = (pkg) => {

        const currentCart =
            JSON.parse(localStorage.getItem('cart')) || [];

        // Evita duplicados
        const exists = currentCart.some(
            item => item.packageId === pkg.packageId
        );

        if (exists) {

            alert("Este paquete ya está en el carrito");

            return;
        }

        const updatedCart = [...currentCart, pkg];

        localStorage.setItem(
            'cart',
            JSON.stringify(updatedCart)
        );

        alert(`${pkg.packageName} añadido al carrito`);
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

    const handleOpenFilters = () => {
        setShowFilters(true);
    };

    const applyFilters = async () => {

        try {

            const cleanFilters = {
                destination:
                    filters.destination
                        ? filters.destination.trim().toLowerCase()
                        : null,

                minPrice:
                    filters.minPrice || null,

                maxPrice:
                    filters.maxPrice || null,

                duration:
                    filters.duration || null,

                tripType:
                    filters.tripType
                        ? filters.tripType.toLowerCase()
                        : null
            };

            console.log("Filters:", filters);
            console.log("Clean filters:", cleanFilters);
            console.log(
                "tripType type:",
                typeof cleanFilters.tripType
            );
            console.log(
                "tripType value:",
                cleanFilters.tripType
            );

            const response =
                await packageService.searchPackages(cleanFilters);

            setPackages(response.data);

            handleCloseFilters();

        } catch (error) {

            console.error(
                "Error aplicando filtros:",
                error
            );
        }
    };

    const handleCloseFilters = () => {
        setShowFilters(false);
    };

    const handleFilterChange = (e) => {

        const { name, value } = e.target;

        setFilters(prev => ({
            ...prev,
            [name]: value
        }));
    };

    return (

        <div className="container mt-4">

            {/* CABECERA */}
            <div className="d-flex justify-content-between align-items-center mb-4">

                <h2 className="text-blue m-0">
                    🌎 Paquetes Turísticos Disponibles
                </h2>

                <button
                    className="btn btn-outline-info"
                    onClick={handleOpenFilters}
                >
                    🔎 Filtros
                </button>

            </div>

            {/* TABLA */}
            <div className="table-responsive">

                <table className="table table-dark table-hover align-middle">

                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Destino</th>
                            <th>Precio</th>
                            <th>Duración</th>
                            <th>Estado</th>
                            <th className="text-center">
                                Acciones
                            </th>
                        </tr>
                    </thead>

                    <tbody>

                        {packages.map(pkg => (

                            <tr key={pkg.packageId}>

                                <td>{pkg.packageId}</td>

                                <td>{pkg.packageName}</td>

                                <td>{pkg.destination}</td>

                                <td>
                                    ${pkg.prize?.toLocaleString()}
                                </td>

                                <td>
                                    {pkg.duration} días
                                </td>

                                <td>
                                    <span className={`badge ${getBadgeClass(pkg.packageState)}`}>
                                        {pkg.packageState}
                                    </span>
                                </td>

                                <td className="text-center">

                                    <div className="btn-group" role="group">

                                        {/* VER DETALLE */}
                                        <button
                                            className="btn btn-info btn-sm"
                                            title="Ver detalle"
                                            onClick={() => handleShowDetail(pkg)}
                                        >
                                            👁️
                                        </button>

                                        {/* AGREGAR CARRITO */}
                                        <button
                                            className="btn btn-success btn-sm"
                                            title="Agregar al carrito"
                                            onClick={() => addToCart(pkg)}
                                        >
                                            ➕
                                        </button>

                                    </div>

                                </td>

                            </tr>

                        ))}

                    </tbody>

                </table>

            </div>

            {/* MODAL FILTROS */}
            {showFilters && (

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

                            <div className="modal-content bg-dark text-white border-secondary">

                                <div className="modal-header border-secondary">

                                    <h5 className="modal-title">
                                        🔎 Filtrar Paquetes
                                    </h5>

                                    <button
                                        type="button"
                                        className="btn-close btn-close-white"
                                        onClick={handleCloseFilters}
                                    ></button>

                                </div>

                                <div className="modal-body">

                                    {/* DESTINO */}
                                    <div className="mb-3">

                                        <label className="form-label">
                                            Destino
                                        </label>

                                        <input
                                            type="text"
                                            className="form-control"
                                            name="destination"
                                            value={filters.destination}
                                            onChange={handleFilterChange}
                                            placeholder="Ej: Brasil"
                                        />

                                    </div>

                                    {/* PRECIO MÍNIMO */}
                                    <div className="mb-3">

                                        <label className="form-label">
                                            Precio mínimo
                                        </label>

                                        <input
                                            type="number"
                                            className="form-control"
                                            name="minPrice"
                                            value={filters.minPrice}
                                            onChange={handleFilterChange}
                                            placeholder="Ej: 100000"
                                        />

                                    </div>

                                    {/* PRECIO MÁXIMO */}
                                    <div className="mb-3">

                                        <label className="form-label">
                                            Precio máximo
                                        </label>

                                        <input
                                            type="number"
                                            className="form-control"
                                            name="maxPrice"
                                            value={filters.maxPrice}
                                            onChange={handleFilterChange}
                                            placeholder="Ej: 500000"
                                        />

                                    </div>

                                    {/* DURACIÓN */}
                                    <div className="mb-3">

                                        <label className="form-label">
                                            Duración (días)
                                        </label>

                                        <input
                                            type="number"
                                            className="form-control"
                                            name="duration"
                                            value={filters.duration}
                                            onChange={handleFilterChange}
                                            placeholder="Ej: 7"
                                        />

                                    </div>

                                    {/* TIPO EXPERIENCIA */}
                                    <div className="mb-3">

                                        <label className="form-label">
                                            Tipo de experiencia
                                        </label>

                                        <select
                                            className="form-select"
                                            name="tripType"
                                            value={filters.tripType}
                                            onChange={handleFilterChange}
                                        >

                                            <option value="">
                                                Todos
                                            </option>

                                            <option value="Aventura">
                                                Aventura
                                            </option>

                                            <option value="Relajo">
                                                Relajo
                                            </option>

                                            <option value="Cultura">
                                                Cultura
                                            </option>

                                        </select>

                                    </div>

                                </div>

                                <div className="modal-footer border-secondary">

                                    <button
                                        className="btn btn-secondary"
                                        onClick={handleCloseFilters}
                                    >
                                        Cerrar
                                    </button>

                                    <button
                                        className="btn btn-info"
                                        onClick={applyFilters}
                                    >
                                        Aplicar filtros
                                    </button>

                                </div>

                            </div>

                        </div>

                    </div>

                    <div className="modal-backdrop fade show"></div>

                </>
            )}

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
                                        Detalle: {selectedPackage?.packageName}
                                    </h5>

                                    <button
                                        type="button"
                                        className="btn-close btn-close-white"
                                        onClick={handleCloseModal}
                                    ></button>

                                </div>

                                <div className="modal-body">

                                    {selectedPackage && (

                                        <div className="row">

                                            <div className="col-md-6">

                                                <p>
                                                    <strong>📍 Destino:</strong>
                                                    {" "}
                                                    {selectedPackage.destination}
                                                </p>

                                                <p>
                                                    <strong>💰 Precio:</strong>
                                                    {" "}
                                                    ${selectedPackage.prize?.toLocaleString()}
                                                </p>

                                                <p>
                                                    <strong>⏳ Duración:</strong>
                                                    {" "}
                                                    {selectedPackage.duration} días
                                                </p>

                                                <p>
                                                    <strong>👥 Cupos:</strong>
                                                    {" "}
                                                    {selectedPackage.availableSlots}
                                                    {" / "}
                                                    {selectedPackage.totalSlots}
                                                </p>

                                            </div>

                                            <div className="col-md-6">

                                                <p>
                                                    <strong>🗓️ Inicio:</strong>
                                                    {" "}
                                                    {formatDate(selectedPackage.startDate)}
                                                </p>

                                                <p>
                                                    <strong>🏁 Fin:</strong>
                                                    {" "}
                                                    {formatDate(selectedPackage.endDate)}
                                                </p>

                                                <p>
                                                    <strong>🏔️ Tipo:</strong>
                                                    {" "}
                                                    {selectedPackage.tripType}
                                                </p>

                                                <p>
                                                    <strong>🌟 Categoría:</strong>
                                                    {" "}
                                                    {selectedPackage.category}
                                                </p>

                                            </div>

                                            <hr className="border-secondary" />

                                            <div
                                                className="col-12"
                                                style={{
                                                    maxHeight: '300px',
                                                    overflowY: 'auto'
                                                }}
                                            >

                                                <p>
                                                    <strong>📝 Descripción:</strong>
                                                    <br />
                                                    {selectedPackage.description}
                                                </p>

                                                <p>
                                                    <strong>🛠️ Servicios Incluidos:</strong>
                                                    <br />
                                                    {selectedPackage.includedServices}
                                                </p>

                                                <p>
                                                    <strong>⚠️ Restricciones:</strong>
                                                    <br />
                                                    {selectedPackage.restriction}
                                                </p>

                                                <p>
                                                    <strong>📜 Condiciones:</strong>
                                                    <br />
                                                    {selectedPackage.conditions}
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

                                    <button
                                        type="button"
                                        className="btn btn-success"
                                        onClick={() => {

                                            addToCart(selectedPackage);

                                            handleCloseModal();
                                        }}
                                    >
                                        ➕ Añadir al carrito
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
};

export default UserPackages;