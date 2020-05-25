package com.themkers.facturacion.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FacturaDetalleMapperTest {

    private FacturaDetalleMapper facturaDetalleMapper;

    @BeforeEach
    public void setUp() {
        facturaDetalleMapper = new FacturaDetalleMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(facturaDetalleMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(facturaDetalleMapper.fromId(null)).isNull();
    }
}
