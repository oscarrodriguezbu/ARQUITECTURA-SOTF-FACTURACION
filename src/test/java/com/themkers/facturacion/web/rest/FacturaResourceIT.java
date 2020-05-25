package com.themkers.facturacion.web.rest;

import com.themkers.facturacion.FacturacionmicroservicioApp;
import com.themkers.facturacion.domain.Factura;
import com.themkers.facturacion.domain.Cliente;
import com.themkers.facturacion.repository.FacturaRepository;
import com.themkers.facturacion.service.FacturaService;
import com.themkers.facturacion.service.dto.FacturaDTO;
import com.themkers.facturacion.service.mapper.FacturaMapper;
import com.themkers.facturacion.service.dto.FacturaCriteria;
import com.themkers.facturacion.service.FacturaQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link FacturaResource} REST controller.
 */
@SpringBootTest(classes = FacturacionmicroservicioApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class FacturaResourceIT {

    private static final Instant DEFAULT_FECHA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final BigDecimal DEFAULT_VALOR = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR = new BigDecimal(2);
    private static final BigDecimal SMALLER_VALOR = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_FECHA_PAGO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_PAGO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaMapper facturaMapper;

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private FacturaQueryService facturaQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacturaMockMvc;

    private Factura factura;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createEntity(EntityManager em) {
        Factura factura = new Factura()
            .fecha(DEFAULT_FECHA)
            .valor(DEFAULT_VALOR)
            .fechaPago(DEFAULT_FECHA_PAGO);
        return factura;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Factura createUpdatedEntity(EntityManager em) {
        Factura factura = new Factura()
            .fecha(UPDATED_FECHA)
            .valor(UPDATED_VALOR)
            .fechaPago(UPDATED_FECHA_PAGO);
        return factura;
    }

    @BeforeEach
    public void initTest() {
        factura = createEntity(em);
    }

    @Test
    @Transactional
    public void createFactura() throws Exception {
        int databaseSizeBeforeCreate = facturaRepository.findAll().size();

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);
        restFacturaMockMvc.perform(post("/api/facturas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(facturaDTO)))
            .andExpect(status().isCreated());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeCreate + 1);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFecha()).isEqualTo(DEFAULT_FECHA);
        assertThat(testFactura.getValor()).isEqualTo(DEFAULT_VALOR);
        assertThat(testFactura.getFechaPago()).isEqualTo(DEFAULT_FECHA_PAGO);
    }

    @Test
    @Transactional
    public void createFacturaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = facturaRepository.findAll().size();

        // Create the Factura with an existing ID
        factura.setId(1L);
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacturaMockMvc.perform(post("/api/facturas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(facturaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllFacturas() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList
        restFacturaMockMvc.perform(get("/api/facturas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(factura.getId().intValue())))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())))
            .andExpect(jsonPath("$.[*].valor").value(hasItem(DEFAULT_VALOR.intValue())))
            .andExpect(jsonPath("$.[*].fechaPago").value(hasItem(DEFAULT_FECHA_PAGO.toString())));
    }
    
    @Test
    @Transactional
    public void getFactura() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get the factura
        restFacturaMockMvc.perform(get("/api/facturas/{id}", factura.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(factura.getId().intValue()))
            .andExpect(jsonPath("$.fecha").value(DEFAULT_FECHA.toString()))
            .andExpect(jsonPath("$.valor").value(DEFAULT_VALOR.intValue()))
            .andExpect(jsonPath("$.fechaPago").value(DEFAULT_FECHA_PAGO.toString()));
    }


    @Test
    @Transactional
    public void getFacturasByIdFiltering() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        Long id = factura.getId();

        defaultFacturaShouldBeFound("id.equals=" + id);
        defaultFacturaShouldNotBeFound("id.notEquals=" + id);

        defaultFacturaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFacturaShouldNotBeFound("id.greaterThan=" + id);

        defaultFacturaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFacturaShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllFacturasByFechaIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fecha equals to DEFAULT_FECHA
        defaultFacturaShouldBeFound("fecha.equals=" + DEFAULT_FECHA);

        // Get all the facturaList where fecha equals to UPDATED_FECHA
        defaultFacturaShouldNotBeFound("fecha.equals=" + UPDATED_FECHA);
    }

    @Test
    @Transactional
    public void getAllFacturasByFechaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fecha not equals to DEFAULT_FECHA
        defaultFacturaShouldNotBeFound("fecha.notEquals=" + DEFAULT_FECHA);

        // Get all the facturaList where fecha not equals to UPDATED_FECHA
        defaultFacturaShouldBeFound("fecha.notEquals=" + UPDATED_FECHA);
    }

    @Test
    @Transactional
    public void getAllFacturasByFechaIsInShouldWork() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fecha in DEFAULT_FECHA or UPDATED_FECHA
        defaultFacturaShouldBeFound("fecha.in=" + DEFAULT_FECHA + "," + UPDATED_FECHA);

        // Get all the facturaList where fecha equals to UPDATED_FECHA
        defaultFacturaShouldNotBeFound("fecha.in=" + UPDATED_FECHA);
    }

    @Test
    @Transactional
    public void getAllFacturasByFechaIsNullOrNotNull() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fecha is not null
        defaultFacturaShouldBeFound("fecha.specified=true");

        // Get all the facturaList where fecha is null
        defaultFacturaShouldNotBeFound("fecha.specified=false");
    }

    @Test
    @Transactional
    public void getAllFacturasByValorIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where valor equals to DEFAULT_VALOR
        defaultFacturaShouldBeFound("valor.equals=" + DEFAULT_VALOR);

        // Get all the facturaList where valor equals to UPDATED_VALOR
        defaultFacturaShouldNotBeFound("valor.equals=" + UPDATED_VALOR);
    }

    @Test
    @Transactional
    public void getAllFacturasByValorIsNotEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where valor not equals to DEFAULT_VALOR
        defaultFacturaShouldNotBeFound("valor.notEquals=" + DEFAULT_VALOR);

        // Get all the facturaList where valor not equals to UPDATED_VALOR
        defaultFacturaShouldBeFound("valor.notEquals=" + UPDATED_VALOR);
    }

    @Test
    @Transactional
    public void getAllFacturasByValorIsInShouldWork() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where valor in DEFAULT_VALOR or UPDATED_VALOR
        defaultFacturaShouldBeFound("valor.in=" + DEFAULT_VALOR + "," + UPDATED_VALOR);

        // Get all the facturaList where valor equals to UPDATED_VALOR
        defaultFacturaShouldNotBeFound("valor.in=" + UPDATED_VALOR);
    }

    @Test
    @Transactional
    public void getAllFacturasByValorIsNullOrNotNull() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where valor is not null
        defaultFacturaShouldBeFound("valor.specified=true");

        // Get all the facturaList where valor is null
        defaultFacturaShouldNotBeFound("valor.specified=false");
    }

    @Test
    @Transactional
    public void getAllFacturasByValorIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where valor is greater than or equal to DEFAULT_VALOR
        defaultFacturaShouldBeFound("valor.greaterThanOrEqual=" + DEFAULT_VALOR);

        // Get all the facturaList where valor is greater than or equal to UPDATED_VALOR
        defaultFacturaShouldNotBeFound("valor.greaterThanOrEqual=" + UPDATED_VALOR);
    }

    @Test
    @Transactional
    public void getAllFacturasByValorIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where valor is less than or equal to DEFAULT_VALOR
        defaultFacturaShouldBeFound("valor.lessThanOrEqual=" + DEFAULT_VALOR);

        // Get all the facturaList where valor is less than or equal to SMALLER_VALOR
        defaultFacturaShouldNotBeFound("valor.lessThanOrEqual=" + SMALLER_VALOR);
    }

    @Test
    @Transactional
    public void getAllFacturasByValorIsLessThanSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where valor is less than DEFAULT_VALOR
        defaultFacturaShouldNotBeFound("valor.lessThan=" + DEFAULT_VALOR);

        // Get all the facturaList where valor is less than UPDATED_VALOR
        defaultFacturaShouldBeFound("valor.lessThan=" + UPDATED_VALOR);
    }

    @Test
    @Transactional
    public void getAllFacturasByValorIsGreaterThanSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where valor is greater than DEFAULT_VALOR
        defaultFacturaShouldNotBeFound("valor.greaterThan=" + DEFAULT_VALOR);

        // Get all the facturaList where valor is greater than SMALLER_VALOR
        defaultFacturaShouldBeFound("valor.greaterThan=" + SMALLER_VALOR);
    }


    @Test
    @Transactional
    public void getAllFacturasByFechaPagoIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fechaPago equals to DEFAULT_FECHA_PAGO
        defaultFacturaShouldBeFound("fechaPago.equals=" + DEFAULT_FECHA_PAGO);

        // Get all the facturaList where fechaPago equals to UPDATED_FECHA_PAGO
        defaultFacturaShouldNotBeFound("fechaPago.equals=" + UPDATED_FECHA_PAGO);
    }

    @Test
    @Transactional
    public void getAllFacturasByFechaPagoIsNotEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fechaPago not equals to DEFAULT_FECHA_PAGO
        defaultFacturaShouldNotBeFound("fechaPago.notEquals=" + DEFAULT_FECHA_PAGO);

        // Get all the facturaList where fechaPago not equals to UPDATED_FECHA_PAGO
        defaultFacturaShouldBeFound("fechaPago.notEquals=" + UPDATED_FECHA_PAGO);
    }

    @Test
    @Transactional
    public void getAllFacturasByFechaPagoIsInShouldWork() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fechaPago in DEFAULT_FECHA_PAGO or UPDATED_FECHA_PAGO
        defaultFacturaShouldBeFound("fechaPago.in=" + DEFAULT_FECHA_PAGO + "," + UPDATED_FECHA_PAGO);

        // Get all the facturaList where fechaPago equals to UPDATED_FECHA_PAGO
        defaultFacturaShouldNotBeFound("fechaPago.in=" + UPDATED_FECHA_PAGO);
    }

    @Test
    @Transactional
    public void getAllFacturasByFechaPagoIsNullOrNotNull() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        // Get all the facturaList where fechaPago is not null
        defaultFacturaShouldBeFound("fechaPago.specified=true");

        // Get all the facturaList where fechaPago is null
        defaultFacturaShouldNotBeFound("fechaPago.specified=false");
    }

    @Test
    @Transactional
    public void getAllFacturasByClienteIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);
        Cliente cliente = ClienteResourceIT.createEntity(em);
        em.persist(cliente);
        em.flush();
        factura.addCliente(cliente);
        facturaRepository.saveAndFlush(factura);
        Long clienteId = cliente.getId();

        // Get all the facturaList where cliente equals to clienteId
        defaultFacturaShouldBeFound("clienteId.equals=" + clienteId);

        // Get all the facturaList where cliente equals to clienteId + 1
        defaultFacturaShouldNotBeFound("clienteId.equals=" + (clienteId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFacturaShouldBeFound(String filter) throws Exception {
        restFacturaMockMvc.perform(get("/api/facturas?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(factura.getId().intValue())))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())))
            .andExpect(jsonPath("$.[*].valor").value(hasItem(DEFAULT_VALOR.intValue())))
            .andExpect(jsonPath("$.[*].fechaPago").value(hasItem(DEFAULT_FECHA_PAGO.toString())));

        // Check, that the count call also returns 1
        restFacturaMockMvc.perform(get("/api/facturas/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFacturaShouldNotBeFound(String filter) throws Exception {
        restFacturaMockMvc.perform(get("/api/facturas?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFacturaMockMvc.perform(get("/api/facturas/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingFactura() throws Exception {
        // Get the factura
        restFacturaMockMvc.perform(get("/api/facturas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFactura() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();

        // Update the factura
        Factura updatedFactura = facturaRepository.findById(factura.getId()).get();
        // Disconnect from session so that the updates on updatedFactura are not directly saved in db
        em.detach(updatedFactura);
        updatedFactura
            .fecha(UPDATED_FECHA)
            .valor(UPDATED_VALOR)
            .fechaPago(UPDATED_FECHA_PAGO);
        FacturaDTO facturaDTO = facturaMapper.toDto(updatedFactura);

        restFacturaMockMvc.perform(put("/api/facturas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(facturaDTO)))
            .andExpect(status().isOk());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
        Factura testFactura = facturaList.get(facturaList.size() - 1);
        assertThat(testFactura.getFecha()).isEqualTo(UPDATED_FECHA);
        assertThat(testFactura.getValor()).isEqualTo(UPDATED_VALOR);
        assertThat(testFactura.getFechaPago()).isEqualTo(UPDATED_FECHA_PAGO);
    }

    @Test
    @Transactional
    public void updateNonExistingFactura() throws Exception {
        int databaseSizeBeforeUpdate = facturaRepository.findAll().size();

        // Create the Factura
        FacturaDTO facturaDTO = facturaMapper.toDto(factura);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturaMockMvc.perform(put("/api/facturas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(facturaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Factura in the database
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteFactura() throws Exception {
        // Initialize the database
        facturaRepository.saveAndFlush(factura);

        int databaseSizeBeforeDelete = facturaRepository.findAll().size();

        // Delete the factura
        restFacturaMockMvc.perform(delete("/api/facturas/{id}", factura.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Factura> facturaList = facturaRepository.findAll();
        assertThat(facturaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
