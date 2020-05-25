package com.themkers.facturacion.web.rest;

import com.themkers.facturacion.FacturacionmicroservicioApp;
import com.themkers.facturacion.domain.FacturaDetalle;
import com.themkers.facturacion.repository.FacturaDetalleRepository;
import com.themkers.facturacion.service.FacturaDetalleService;
import com.themkers.facturacion.service.dto.FacturaDetalleDTO;
import com.themkers.facturacion.service.mapper.FacturaDetalleMapper;
import com.themkers.facturacion.service.dto.FacturaDetalleCriteria;
import com.themkers.facturacion.service.FacturaDetalleQueryService;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link FacturaDetalleResource} REST controller.
 */
@SpringBootTest(classes = FacturacionmicroservicioApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class FacturaDetalleResourceIT {

    private static final Long DEFAULT_PRODUCTO_ID = 1L;
    private static final Long UPDATED_PRODUCTO_ID = 2L;
    private static final Long SMALLER_PRODUCTO_ID = 1L - 1L;

    private static final Long DEFAULT_CANTIDAD = 1L;
    private static final Long UPDATED_CANTIDAD = 2L;
    private static final Long SMALLER_CANTIDAD = 1L - 1L;

    private static final BigDecimal DEFAULT_PRECIO_UNITARIO = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRECIO_UNITARIO = new BigDecimal(2);
    private static final BigDecimal SMALLER_PRECIO_UNITARIO = new BigDecimal(1 - 1);

    @Autowired
    private FacturaDetalleRepository facturaDetalleRepository;

    @Autowired
    private FacturaDetalleMapper facturaDetalleMapper;

    @Autowired
    private FacturaDetalleService facturaDetalleService;

    @Autowired
    private FacturaDetalleQueryService facturaDetalleQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFacturaDetalleMockMvc;

    private FacturaDetalle facturaDetalle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FacturaDetalle createEntity(EntityManager em) {
        FacturaDetalle facturaDetalle = new FacturaDetalle()
            .productoId(DEFAULT_PRODUCTO_ID)
            .cantidad(DEFAULT_CANTIDAD)
            .precioUnitario(DEFAULT_PRECIO_UNITARIO);
        return facturaDetalle;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FacturaDetalle createUpdatedEntity(EntityManager em) {
        FacturaDetalle facturaDetalle = new FacturaDetalle()
            .productoId(UPDATED_PRODUCTO_ID)
            .cantidad(UPDATED_CANTIDAD)
            .precioUnitario(UPDATED_PRECIO_UNITARIO);
        return facturaDetalle;
    }

    @BeforeEach
    public void initTest() {
        facturaDetalle = createEntity(em);
    }

    @Test
    @Transactional
    public void createFacturaDetalle() throws Exception {
        int databaseSizeBeforeCreate = facturaDetalleRepository.findAll().size();

        // Create the FacturaDetalle
        FacturaDetalleDTO facturaDetalleDTO = facturaDetalleMapper.toDto(facturaDetalle);
        restFacturaDetalleMockMvc.perform(post("/api/factura-detalles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(facturaDetalleDTO)))
            .andExpect(status().isCreated());

        // Validate the FacturaDetalle in the database
        List<FacturaDetalle> facturaDetalleList = facturaDetalleRepository.findAll();
        assertThat(facturaDetalleList).hasSize(databaseSizeBeforeCreate + 1);
        FacturaDetalle testFacturaDetalle = facturaDetalleList.get(facturaDetalleList.size() - 1);
        assertThat(testFacturaDetalle.getProductoId()).isEqualTo(DEFAULT_PRODUCTO_ID);
        assertThat(testFacturaDetalle.getCantidad()).isEqualTo(DEFAULT_CANTIDAD);
        assertThat(testFacturaDetalle.getPrecioUnitario()).isEqualTo(DEFAULT_PRECIO_UNITARIO);
    }

    @Test
    @Transactional
    public void createFacturaDetalleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = facturaDetalleRepository.findAll().size();

        // Create the FacturaDetalle with an existing ID
        facturaDetalle.setId(1L);
        FacturaDetalleDTO facturaDetalleDTO = facturaDetalleMapper.toDto(facturaDetalle);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFacturaDetalleMockMvc.perform(post("/api/factura-detalles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(facturaDetalleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FacturaDetalle in the database
        List<FacturaDetalle> facturaDetalleList = facturaDetalleRepository.findAll();
        assertThat(facturaDetalleList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllFacturaDetalles() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList
        restFacturaDetalleMockMvc.perform(get("/api/factura-detalles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facturaDetalle.getId().intValue())))
            .andExpect(jsonPath("$.[*].productoId").value(hasItem(DEFAULT_PRODUCTO_ID.intValue())))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD.intValue())))
            .andExpect(jsonPath("$.[*].precioUnitario").value(hasItem(DEFAULT_PRECIO_UNITARIO.intValue())));
    }
    
    @Test
    @Transactional
    public void getFacturaDetalle() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get the facturaDetalle
        restFacturaDetalleMockMvc.perform(get("/api/factura-detalles/{id}", facturaDetalle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(facturaDetalle.getId().intValue()))
            .andExpect(jsonPath("$.productoId").value(DEFAULT_PRODUCTO_ID.intValue()))
            .andExpect(jsonPath("$.cantidad").value(DEFAULT_CANTIDAD.intValue()))
            .andExpect(jsonPath("$.precioUnitario").value(DEFAULT_PRECIO_UNITARIO.intValue()));
    }


    @Test
    @Transactional
    public void getFacturaDetallesByIdFiltering() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        Long id = facturaDetalle.getId();

        defaultFacturaDetalleShouldBeFound("id.equals=" + id);
        defaultFacturaDetalleShouldNotBeFound("id.notEquals=" + id);

        defaultFacturaDetalleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFacturaDetalleShouldNotBeFound("id.greaterThan=" + id);

        defaultFacturaDetalleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFacturaDetalleShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllFacturaDetallesByProductoIdIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where productoId equals to DEFAULT_PRODUCTO_ID
        defaultFacturaDetalleShouldBeFound("productoId.equals=" + DEFAULT_PRODUCTO_ID);

        // Get all the facturaDetalleList where productoId equals to UPDATED_PRODUCTO_ID
        defaultFacturaDetalleShouldNotBeFound("productoId.equals=" + UPDATED_PRODUCTO_ID);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByProductoIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where productoId not equals to DEFAULT_PRODUCTO_ID
        defaultFacturaDetalleShouldNotBeFound("productoId.notEquals=" + DEFAULT_PRODUCTO_ID);

        // Get all the facturaDetalleList where productoId not equals to UPDATED_PRODUCTO_ID
        defaultFacturaDetalleShouldBeFound("productoId.notEquals=" + UPDATED_PRODUCTO_ID);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByProductoIdIsInShouldWork() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where productoId in DEFAULT_PRODUCTO_ID or UPDATED_PRODUCTO_ID
        defaultFacturaDetalleShouldBeFound("productoId.in=" + DEFAULT_PRODUCTO_ID + "," + UPDATED_PRODUCTO_ID);

        // Get all the facturaDetalleList where productoId equals to UPDATED_PRODUCTO_ID
        defaultFacturaDetalleShouldNotBeFound("productoId.in=" + UPDATED_PRODUCTO_ID);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByProductoIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where productoId is not null
        defaultFacturaDetalleShouldBeFound("productoId.specified=true");

        // Get all the facturaDetalleList where productoId is null
        defaultFacturaDetalleShouldNotBeFound("productoId.specified=false");
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByProductoIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where productoId is greater than or equal to DEFAULT_PRODUCTO_ID
        defaultFacturaDetalleShouldBeFound("productoId.greaterThanOrEqual=" + DEFAULT_PRODUCTO_ID);

        // Get all the facturaDetalleList where productoId is greater than or equal to UPDATED_PRODUCTO_ID
        defaultFacturaDetalleShouldNotBeFound("productoId.greaterThanOrEqual=" + UPDATED_PRODUCTO_ID);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByProductoIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where productoId is less than or equal to DEFAULT_PRODUCTO_ID
        defaultFacturaDetalleShouldBeFound("productoId.lessThanOrEqual=" + DEFAULT_PRODUCTO_ID);

        // Get all the facturaDetalleList where productoId is less than or equal to SMALLER_PRODUCTO_ID
        defaultFacturaDetalleShouldNotBeFound("productoId.lessThanOrEqual=" + SMALLER_PRODUCTO_ID);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByProductoIdIsLessThanSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where productoId is less than DEFAULT_PRODUCTO_ID
        defaultFacturaDetalleShouldNotBeFound("productoId.lessThan=" + DEFAULT_PRODUCTO_ID);

        // Get all the facturaDetalleList where productoId is less than UPDATED_PRODUCTO_ID
        defaultFacturaDetalleShouldBeFound("productoId.lessThan=" + UPDATED_PRODUCTO_ID);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByProductoIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where productoId is greater than DEFAULT_PRODUCTO_ID
        defaultFacturaDetalleShouldNotBeFound("productoId.greaterThan=" + DEFAULT_PRODUCTO_ID);

        // Get all the facturaDetalleList where productoId is greater than SMALLER_PRODUCTO_ID
        defaultFacturaDetalleShouldBeFound("productoId.greaterThan=" + SMALLER_PRODUCTO_ID);
    }


    @Test
    @Transactional
    public void getAllFacturaDetallesByCantidadIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where cantidad equals to DEFAULT_CANTIDAD
        defaultFacturaDetalleShouldBeFound("cantidad.equals=" + DEFAULT_CANTIDAD);

        // Get all the facturaDetalleList where cantidad equals to UPDATED_CANTIDAD
        defaultFacturaDetalleShouldNotBeFound("cantidad.equals=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByCantidadIsNotEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where cantidad not equals to DEFAULT_CANTIDAD
        defaultFacturaDetalleShouldNotBeFound("cantidad.notEquals=" + DEFAULT_CANTIDAD);

        // Get all the facturaDetalleList where cantidad not equals to UPDATED_CANTIDAD
        defaultFacturaDetalleShouldBeFound("cantidad.notEquals=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByCantidadIsInShouldWork() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where cantidad in DEFAULT_CANTIDAD or UPDATED_CANTIDAD
        defaultFacturaDetalleShouldBeFound("cantidad.in=" + DEFAULT_CANTIDAD + "," + UPDATED_CANTIDAD);

        // Get all the facturaDetalleList where cantidad equals to UPDATED_CANTIDAD
        defaultFacturaDetalleShouldNotBeFound("cantidad.in=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByCantidadIsNullOrNotNull() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where cantidad is not null
        defaultFacturaDetalleShouldBeFound("cantidad.specified=true");

        // Get all the facturaDetalleList where cantidad is null
        defaultFacturaDetalleShouldNotBeFound("cantidad.specified=false");
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByCantidadIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where cantidad is greater than or equal to DEFAULT_CANTIDAD
        defaultFacturaDetalleShouldBeFound("cantidad.greaterThanOrEqual=" + DEFAULT_CANTIDAD);

        // Get all the facturaDetalleList where cantidad is greater than or equal to UPDATED_CANTIDAD
        defaultFacturaDetalleShouldNotBeFound("cantidad.greaterThanOrEqual=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByCantidadIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where cantidad is less than or equal to DEFAULT_CANTIDAD
        defaultFacturaDetalleShouldBeFound("cantidad.lessThanOrEqual=" + DEFAULT_CANTIDAD);

        // Get all the facturaDetalleList where cantidad is less than or equal to SMALLER_CANTIDAD
        defaultFacturaDetalleShouldNotBeFound("cantidad.lessThanOrEqual=" + SMALLER_CANTIDAD);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByCantidadIsLessThanSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where cantidad is less than DEFAULT_CANTIDAD
        defaultFacturaDetalleShouldNotBeFound("cantidad.lessThan=" + DEFAULT_CANTIDAD);

        // Get all the facturaDetalleList where cantidad is less than UPDATED_CANTIDAD
        defaultFacturaDetalleShouldBeFound("cantidad.lessThan=" + UPDATED_CANTIDAD);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByCantidadIsGreaterThanSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where cantidad is greater than DEFAULT_CANTIDAD
        defaultFacturaDetalleShouldNotBeFound("cantidad.greaterThan=" + DEFAULT_CANTIDAD);

        // Get all the facturaDetalleList where cantidad is greater than SMALLER_CANTIDAD
        defaultFacturaDetalleShouldBeFound("cantidad.greaterThan=" + SMALLER_CANTIDAD);
    }


    @Test
    @Transactional
    public void getAllFacturaDetallesByPrecioUnitarioIsEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where precioUnitario equals to DEFAULT_PRECIO_UNITARIO
        defaultFacturaDetalleShouldBeFound("precioUnitario.equals=" + DEFAULT_PRECIO_UNITARIO);

        // Get all the facturaDetalleList where precioUnitario equals to UPDATED_PRECIO_UNITARIO
        defaultFacturaDetalleShouldNotBeFound("precioUnitario.equals=" + UPDATED_PRECIO_UNITARIO);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByPrecioUnitarioIsNotEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where precioUnitario not equals to DEFAULT_PRECIO_UNITARIO
        defaultFacturaDetalleShouldNotBeFound("precioUnitario.notEquals=" + DEFAULT_PRECIO_UNITARIO);

        // Get all the facturaDetalleList where precioUnitario not equals to UPDATED_PRECIO_UNITARIO
        defaultFacturaDetalleShouldBeFound("precioUnitario.notEquals=" + UPDATED_PRECIO_UNITARIO);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByPrecioUnitarioIsInShouldWork() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where precioUnitario in DEFAULT_PRECIO_UNITARIO or UPDATED_PRECIO_UNITARIO
        defaultFacturaDetalleShouldBeFound("precioUnitario.in=" + DEFAULT_PRECIO_UNITARIO + "," + UPDATED_PRECIO_UNITARIO);

        // Get all the facturaDetalleList where precioUnitario equals to UPDATED_PRECIO_UNITARIO
        defaultFacturaDetalleShouldNotBeFound("precioUnitario.in=" + UPDATED_PRECIO_UNITARIO);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByPrecioUnitarioIsNullOrNotNull() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where precioUnitario is not null
        defaultFacturaDetalleShouldBeFound("precioUnitario.specified=true");

        // Get all the facturaDetalleList where precioUnitario is null
        defaultFacturaDetalleShouldNotBeFound("precioUnitario.specified=false");
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByPrecioUnitarioIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where precioUnitario is greater than or equal to DEFAULT_PRECIO_UNITARIO
        defaultFacturaDetalleShouldBeFound("precioUnitario.greaterThanOrEqual=" + DEFAULT_PRECIO_UNITARIO);

        // Get all the facturaDetalleList where precioUnitario is greater than or equal to UPDATED_PRECIO_UNITARIO
        defaultFacturaDetalleShouldNotBeFound("precioUnitario.greaterThanOrEqual=" + UPDATED_PRECIO_UNITARIO);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByPrecioUnitarioIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where precioUnitario is less than or equal to DEFAULT_PRECIO_UNITARIO
        defaultFacturaDetalleShouldBeFound("precioUnitario.lessThanOrEqual=" + DEFAULT_PRECIO_UNITARIO);

        // Get all the facturaDetalleList where precioUnitario is less than or equal to SMALLER_PRECIO_UNITARIO
        defaultFacturaDetalleShouldNotBeFound("precioUnitario.lessThanOrEqual=" + SMALLER_PRECIO_UNITARIO);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByPrecioUnitarioIsLessThanSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where precioUnitario is less than DEFAULT_PRECIO_UNITARIO
        defaultFacturaDetalleShouldNotBeFound("precioUnitario.lessThan=" + DEFAULT_PRECIO_UNITARIO);

        // Get all the facturaDetalleList where precioUnitario is less than UPDATED_PRECIO_UNITARIO
        defaultFacturaDetalleShouldBeFound("precioUnitario.lessThan=" + UPDATED_PRECIO_UNITARIO);
    }

    @Test
    @Transactional
    public void getAllFacturaDetallesByPrecioUnitarioIsGreaterThanSomething() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        // Get all the facturaDetalleList where precioUnitario is greater than DEFAULT_PRECIO_UNITARIO
        defaultFacturaDetalleShouldNotBeFound("precioUnitario.greaterThan=" + DEFAULT_PRECIO_UNITARIO);

        // Get all the facturaDetalleList where precioUnitario is greater than SMALLER_PRECIO_UNITARIO
        defaultFacturaDetalleShouldBeFound("precioUnitario.greaterThan=" + SMALLER_PRECIO_UNITARIO);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFacturaDetalleShouldBeFound(String filter) throws Exception {
        restFacturaDetalleMockMvc.perform(get("/api/factura-detalles?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(facturaDetalle.getId().intValue())))
            .andExpect(jsonPath("$.[*].productoId").value(hasItem(DEFAULT_PRODUCTO_ID.intValue())))
            .andExpect(jsonPath("$.[*].cantidad").value(hasItem(DEFAULT_CANTIDAD.intValue())))
            .andExpect(jsonPath("$.[*].precioUnitario").value(hasItem(DEFAULT_PRECIO_UNITARIO.intValue())));

        // Check, that the count call also returns 1
        restFacturaDetalleMockMvc.perform(get("/api/factura-detalles/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFacturaDetalleShouldNotBeFound(String filter) throws Exception {
        restFacturaDetalleMockMvc.perform(get("/api/factura-detalles?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFacturaDetalleMockMvc.perform(get("/api/factura-detalles/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingFacturaDetalle() throws Exception {
        // Get the facturaDetalle
        restFacturaDetalleMockMvc.perform(get("/api/factura-detalles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFacturaDetalle() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        int databaseSizeBeforeUpdate = facturaDetalleRepository.findAll().size();

        // Update the facturaDetalle
        FacturaDetalle updatedFacturaDetalle = facturaDetalleRepository.findById(facturaDetalle.getId()).get();
        // Disconnect from session so that the updates on updatedFacturaDetalle are not directly saved in db
        em.detach(updatedFacturaDetalle);
        updatedFacturaDetalle
            .productoId(UPDATED_PRODUCTO_ID)
            .cantidad(UPDATED_CANTIDAD)
            .precioUnitario(UPDATED_PRECIO_UNITARIO);
        FacturaDetalleDTO facturaDetalleDTO = facturaDetalleMapper.toDto(updatedFacturaDetalle);

        restFacturaDetalleMockMvc.perform(put("/api/factura-detalles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(facturaDetalleDTO)))
            .andExpect(status().isOk());

        // Validate the FacturaDetalle in the database
        List<FacturaDetalle> facturaDetalleList = facturaDetalleRepository.findAll();
        assertThat(facturaDetalleList).hasSize(databaseSizeBeforeUpdate);
        FacturaDetalle testFacturaDetalle = facturaDetalleList.get(facturaDetalleList.size() - 1);
        assertThat(testFacturaDetalle.getProductoId()).isEqualTo(UPDATED_PRODUCTO_ID);
        assertThat(testFacturaDetalle.getCantidad()).isEqualTo(UPDATED_CANTIDAD);
        assertThat(testFacturaDetalle.getPrecioUnitario()).isEqualTo(UPDATED_PRECIO_UNITARIO);
    }

    @Test
    @Transactional
    public void updateNonExistingFacturaDetalle() throws Exception {
        int databaseSizeBeforeUpdate = facturaDetalleRepository.findAll().size();

        // Create the FacturaDetalle
        FacturaDetalleDTO facturaDetalleDTO = facturaDetalleMapper.toDto(facturaDetalle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFacturaDetalleMockMvc.perform(put("/api/factura-detalles")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(facturaDetalleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FacturaDetalle in the database
        List<FacturaDetalle> facturaDetalleList = facturaDetalleRepository.findAll();
        assertThat(facturaDetalleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteFacturaDetalle() throws Exception {
        // Initialize the database
        facturaDetalleRepository.saveAndFlush(facturaDetalle);

        int databaseSizeBeforeDelete = facturaDetalleRepository.findAll().size();

        // Delete the facturaDetalle
        restFacturaDetalleMockMvc.perform(delete("/api/factura-detalles/{id}", facturaDetalle.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FacturaDetalle> facturaDetalleList = facturaDetalleRepository.findAll();
        assertThat(facturaDetalleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
