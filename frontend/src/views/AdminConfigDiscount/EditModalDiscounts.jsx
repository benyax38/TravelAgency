import { useState } from "react";

function DiscountModal({
    discount,
    onClose,
    onSave
}) {

    if (!discount) {
        return null;
    }

    const [editedDiscount, setEditedDiscount] = useState({
        ...discount
    });

    const handleChange = (attribute, value) => {
        setEditedDiscount({
            ...editedDiscount,
            [attribute]: value
        });
    };

    return (
        <div className="modal-dialog modal-lg">

            <div className="modal-content bg-dark text-white border-secondary">

                <div className="modal-header border-secondary">

                    <h5 className="modal-title">
                        Editar descuento
                    </h5>

                </div>

                <div className="modal-body">

                    <div className="mb-3">
                        <label className="form-label">ID</label>

                        <input
                            className="form-control bg-danger-subtle border-danger"
                            value={editedDiscount.discountConfigId}
                            disabled
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Tipo</label>

                        <input
                            className="form-control bg-danger-subtle border-danger"
                            value={editedDiscount.discountType}
                            disabled
                        />
                    </div>

                    <div className="mb-3">
                        <label className="form-label">Porcentaje</label>

                        <input
                            className="form-control"
                            type="number"
                            value={editedDiscount.percentage}
                            onChange={(e) =>
                                handleChange(
                                    "percentage",
                                    e.target.value
                                )
                            }
                        />
                    </div>

                    {editedDiscount.discountType === "GROUP_DISCOUNT" && (
                        <div className="mb-3">

                            <label className="form-label">Mínimo pasajeros</label>

                            <input
                                className="form-control"
                                type="number"
                                value={editedDiscount.minPassengers ?? ""}
                                onChange={(e) =>
                                    handleChange(
                                        "minPassengers",
                                        e.target.value
                                    )
                                }
                            />

                        </div>
                    )}

                    {editedDiscount.discountType === "FREQUENT_CUSTOMER" && (
                        <div className="mb-3">

                            <label className="form-label">Mínimo reservas</label>

                            <input
                                className="form-control"
                                type="number"
                                value={editedDiscount.minReservations ?? ""}
                                onChange={(e) =>
                                    handleChange(
                                        "minReservations",
                                        e.target.value
                                    )
                                }
                            />

                        </div>
                    )}

                    {editedDiscount.discountType === "MULTI_PACKAGE" && (
                        <div className="mb-3">

                            <label className="form-label">Período (días)</label>

                            <input
                                className="form-control"
                                type="number"
                                value={editedDiscount.periodDays ?? ""}
                                onChange={(e) =>
                                    handleChange(
                                        "periodDays",
                                        e.target.value
                                    )
                                }
                            />

                        </div>
                    )}

                    <div className="mb-3">

                        <label className="form-label">Fecha inicio</label>

                        <input
                            className="form-control"
                            type="date"
                            value={editedDiscount.promotionStartDate?.slice(0,10) ?? ""}
                            onChange={(e) =>
                                handleChange(
                                    "promotionStartDate",
                                    e.target.value
                                )
                            }
                        />

                    </div>

                    <div className="mb-3">

                        <label className="form-label">Fecha término</label>

                        <input
                            className="form-control"
                            type="date"
                            value={editedDiscount.promotionEndDate?.slice(0,10) ?? ""}
                            onChange={(e) =>
                                handleChange(
                                    "promotionEndDate",
                                    e.target.value
                                )
                            }
                        />

                    </div>

                </div>

                
                <div className="modal-footer border-secondary">

                    <button
                        className="btn btn-secondary" 
                        onClick={onClose}
                    >
                        Cancelar
                    </button>

                    <button 
                        className="btn btn-secondary"
                        onClick={() => onSave(editedDiscount)}
                    >
                        Guardar
                    </button>

                </div>

            </div>

        </div>
    );
}

export default DiscountModal;