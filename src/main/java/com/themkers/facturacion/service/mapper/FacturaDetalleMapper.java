package com.themkers.facturacion.service.mapper;


import com.themkers.facturacion.domain.*;
import com.themkers.facturacion.service.dto.FacturaDetalleDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link FacturaDetalle} and its DTO {@link FacturaDetalleDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FacturaDetalleMapper extends EntityMapper<FacturaDetalleDTO, FacturaDetalle> {



    default FacturaDetalle fromId(Long id) {
        if (id == null) {
            return null;
        }
        FacturaDetalle facturaDetalle = new FacturaDetalle();
        facturaDetalle.setId(id);
        return facturaDetalle;
    }
}
