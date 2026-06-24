import { useState, useEffect } from "react";
import discountService from "../../services/discount-service";
import EditModal from "./EditModalDiscounts";
import "./AdminConfigDiscount.css";

function AdminConfigDiscount() {

    // Estados reactivos
    const [discounts, setDiscounts] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [selectedDiscount, setSelectedDiscount] = useState(null);

    useEffect(() => {
        fetchDiscounts();
    }, []);

    const fetchDiscounts = () => {
        discountService.getAllDiscount()
            .then(response => {
                setDiscounts(response.data);
            });
    }

    const discountLabels = {
        GROUP_DISCOUNT: "Grupal",
        FREQUENT_CUSTOMER: "Cliente frecuente",
        MULTI_PACKAGE: "Multi paquete"
    }

    const formatDate = (date) => {
        if (!date) {
            return "-";
        }
        return new Date(date)
            .toLocaleDateString();
    }

    const handleEdit = (discount) => {
        setSelectedDiscount(discount);
        setShowModal(true);
    }

    const handleCloseModal = () => {
        setShowModal(false);
        setSelectedDiscount(null);
    }

    const handleSave = (editedDiscount) => {

        const payload = {
            ...editedDiscount,
            percentage:
                editedDiscount.percentage === ""
                ? null
                : Number(editedDiscount.percentage),
            
            minPassengers:
                editedDiscount.minPassengers === ""
                ? null
                : Number(editedDiscount.minPassengers),

            minReservations:
                editedDiscount.minReservations === ""
                ? null
                : Number(editedDiscount.minReservations),

            periodDays:
                editedDiscount.periodDays === ""
                ? null
                : Number(editedDiscount.periodDays)
        };

        discountService.updateDiscount(
            payload.discountConfigId,
            payload
        )

        .then(() => {
            fetchDiscounts();
            handleCloseModal();
        })

        .catch(error => {
            console.log(error);
        });

    }

    const handleToggle = (discount) => {

        const payload = {
            active: !discount.active
        };

        discountService.updateDiscount(
            discount.discountConfigId,
            payload
        )

        .then(() => {
            fetchDiscounts();
        });

    }

    return (
        <div className="container mt-4">
            <h1 className="display-6 text-primary fw-bold mb-4 border-bottom pb-2">
                Configuración de descuentos
            </h1>

            <p className="text-secondary">
                Administra los descuentos disponibles
                ·
                Total configuraciones: {discounts.length}
            </p>

            <div className="table-responsive">
                <table className="table table-dark table-hover align-middle">

                    <thead className="discount-table-header">

                        <tr>

                            <th>ID</th>
                            <th>Tipo</th>
                            <th>Porcentaje</th>
                            <th>Mín. pasajeros</th>
                            <th>Mín. reservas</th>
                            <th>Período (días)</th>
                            <th>Fecha inicio</th>
                            <th>Fecha término</th>
                            <th>Acciones</th>

                        </tr>

                    </thead>


                    <tbody>

                        {discounts.map(discount => (

                            <tr key={discount.discountConfigId}>

                                <td>{discount.discountConfigId}</td>
                                <td>{discountLabels[discount.discountType]}</td>
                                <td>{discount.percentage}%</td>
                                <td>{discount.minPassengers ?? "-"}</td>
                                <td>{discount.minReservations ?? "-"}</td>
                                <td>{discount.periodDays ?? "-"}</td>
                                <td>{formatDate(discount.promotionStartDate)}</td>
                                <td>{formatDate(discount.promotionEndDate)}</td>
                                <td className="actions-cell">

                                    <button onClick={() => handleEdit(discount)}>
                                        Editar
                                    </button>

                                    <div className="form-check form-switch">

                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            checked={discount.active}
                                            onChange={() => handleToggle(discount)}
                                        />

                                    </div>

                                </td>

                            </tr>

                        ))}

                    </tbody>

                </table>

            </div>
            

            {showModal && (
                <div
                    className="modal fade show"
                    style={{
                    display: 'block',
                    backgroundColor: 'rgba(0,0,0,0.7)'
                    }}
                    tabIndex="-1"
                >
                    <EditModal

                        discount={selectedDiscount}

                        onClose={handleCloseModal}

                        onSave={handleSave}

                    />

                </div>

            )}
        </div>
    );
}

export default AdminConfigDiscount;