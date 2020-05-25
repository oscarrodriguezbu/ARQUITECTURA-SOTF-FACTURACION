package com.themkers.facturacion.service.mapper;


import com.themkers.facturacion.domain.*;
import com.themkers.facturacion.service.dto.FacturaDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Factura} and its DTO {@link FacturaDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FacturaMapper extends EntityMapper<FacturaDTO, Factura> {


    @Mapping(target = "clientes", ignore = true)
    @Mapping(target = "removeCliente", ignore = true)
    Factura toEntity(FacturaDTO facturaDTO);

    default Factura fromId(Long id) {
        if (id == null) {
            return null;
        }
        Factura factura = new Factura();
        factura.setId(id);
        return factura;
    }
}
