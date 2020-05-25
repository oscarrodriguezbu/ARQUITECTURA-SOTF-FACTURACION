package com.themkers.facturacion.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.themkers.facturacion.web.rest.TestUtil;

public class FacturaDetalleDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FacturaDetalleDTO.class);
        FacturaDetalleDTO facturaDetalleDTO1 = new FacturaDetalleDTO();
        facturaDetalleDTO1.setId(1L);
        FacturaDetalleDTO facturaDetalleDTO2 = new FacturaDetalleDTO();
        assertThat(facturaDetalleDTO1).isNotEqualTo(facturaDetalleDTO2);
        facturaDetalleDTO2.setId(facturaDetalleDTO1.getId());
        assertThat(facturaDetalleDTO1).isEqualTo(facturaDetalleDTO2);
        facturaDetalleDTO2.setId(2L);
        assertThat(facturaDetalleDTO1).isNotEqualTo(facturaDetalleDTO2);
        facturaDetalleDTO1.setId(null);
        assertThat(facturaDetalleDTO1).isNotEqualTo(facturaDetalleDTO2);
    }
}
