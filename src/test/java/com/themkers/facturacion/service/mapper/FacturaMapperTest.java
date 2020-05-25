package com.themkers.facturacion.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FacturaMapperTest {

    private FacturaMapper facturaMapper;

    @BeforeEach
    public void setUp() {
        facturaMapper = new FacturaMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(facturaMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(facturaMapper.fromId(null)).isNull();
    }
}
