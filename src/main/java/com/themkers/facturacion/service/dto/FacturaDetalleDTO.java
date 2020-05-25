package com.themkers.facturacion.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.themkers.facturacion.domain.FacturaDetalle} entity.
 */
public class FacturaDetalleDTO implements Serializable {
    
    private Long id;

    private Long productoId;

    private Long cantidad;

    private BigDecimal precioUnitario;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
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

        FacturaDetalleDTO facturaDetalleDTO = (FacturaDetalleDTO) o;
        if (facturaDetalleDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), facturaDetalleDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "FacturaDetalleDTO{" +
            "id=" + getId() +
            ", productoId=" + getProductoId() +
            ", cantidad=" + getCantidad() +
            ", precioUnitario=" + getPrecioUnitario() +
            "}";
    }
}
