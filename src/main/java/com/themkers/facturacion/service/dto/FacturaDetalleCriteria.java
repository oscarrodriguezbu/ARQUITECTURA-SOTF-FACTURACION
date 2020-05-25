package com.themkers.facturacion.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.BigDecimalFilter;

/**
 * Criteria class for the {@link com.themkers.facturacion.domain.FacturaDetalle} entity. This class is used
 * in {@link com.themkers.facturacion.web.rest.FacturaDetalleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /factura-detalles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FacturaDetalleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter productoId;

    private LongFilter cantidad;

    private BigDecimalFilter precioUnitario;

    public FacturaDetalleCriteria() {
    }

    public FacturaDetalleCriteria(FacturaDetalleCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.productoId = other.productoId == null ? null : other.productoId.copy();
        this.cantidad = other.cantidad == null ? null : other.cantidad.copy();
        this.precioUnitario = other.precioUnitario == null ? null : other.precioUnitario.copy();
    }

    @Override
    public FacturaDetalleCriteria copy() {
        return new FacturaDetalleCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getProductoId() {
        return productoId;
    }

    public void setProductoId(LongFilter productoId) {
        this.productoId = productoId;
    }

    public LongFilter getCantidad() {
        return cantidad;
    }

    public void setCantidad(LongFilter cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimalFilter getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimalFilter precioUnitario) {
        this.precioUnitario = precioUnitario;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FacturaDetalleCriteria that = (FacturaDetalleCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(productoId, that.productoId) &&
            Objects.equals(cantidad, that.cantidad) &&
            Objects.equals(precioUnitario, that.precioUnitario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        productoId,
        cantidad,
        precioUnitario
        );
    }

    @Override
    public String toString() {
        return "FacturaDetalleCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (productoId != null ? "productoId=" + productoId + ", " : "") +
                (cantidad != null ? "cantidad=" + cantidad + ", " : "") +
                (precioUnitario != null ? "precioUnitario=" + precioUnitario + ", " : "") +
            "}";
    }

}
