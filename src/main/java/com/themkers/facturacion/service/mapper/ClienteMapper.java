package com.themkers.facturacion.service.mapper;


import com.themkers.facturacion.domain.*;
import com.themkers.facturacion.service.dto.ClienteDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cliente} and its DTO {@link ClienteDTO}.
 */
@Mapper(componentModel = "spring", uses = {FacturaMapper.class})
public interface ClienteMapper extends EntityMapper<ClienteDTO, Cliente> {

    @Mapping(source = "factura.id", target = "facturaId")
    ClienteDTO toDto(Cliente cliente);

    @Mapping(source = "facturaId", target = "factura")
    Cliente toEntity(ClienteDTO clienteDTO);

    default Cliente fromId(Long id) {
        if (id == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setId(id);
        return cliente;
    }
}
