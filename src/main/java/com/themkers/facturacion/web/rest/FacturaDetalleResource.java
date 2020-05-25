package com.themkers.facturacion.web.rest;

import com.themkers.facturacion.service.FacturaDetalleService;
import com.themkers.facturacion.web.rest.errors.BadRequestAlertException;
import com.themkers.facturacion.service.dto.FacturaDetalleDTO;
import com.themkers.facturacion.service.dto.FacturaDetalleCriteria;
import com.themkers.facturacion.service.FacturaDetalleQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.themkers.facturacion.domain.FacturaDetalle}.
 */
@RestController
@RequestMapping("/api")
public class FacturaDetalleResource {

    private final Logger log = LoggerFactory.getLogger(FacturaDetalleResource.class);

    private static final String ENTITY_NAME = "facturacionmicroservicioFacturaDetalle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FacturaDetalleService facturaDetalleService;

    private final FacturaDetalleQueryService facturaDetalleQueryService;

    public FacturaDetalleResource(FacturaDetalleService facturaDetalleService, FacturaDetalleQueryService facturaDetalleQueryService) {
        this.facturaDetalleService = facturaDetalleService;
        this.facturaDetalleQueryService = facturaDetalleQueryService;
    }

    /**
     * {@code POST  /factura-detalles} : Create a new facturaDetalle.
     *
     * @param facturaDetalleDTO the facturaDetalleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new facturaDetalleDTO, or with status {@code 400 (Bad Request)} if the facturaDetalle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/factura-detalles")
    public ResponseEntity<FacturaDetalleDTO> createFacturaDetalle(@RequestBody FacturaDetalleDTO facturaDetalleDTO) throws URISyntaxException {
        log.debug("REST request to save FacturaDetalle : {}", facturaDetalleDTO);
        if (facturaDetalleDTO.getId() != null) {
            throw new BadRequestAlertException("A new facturaDetalle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FacturaDetalleDTO result = facturaDetalleService.save(facturaDetalleDTO);
        return ResponseEntity.created(new URI("/api/factura-detalles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /factura-detalles} : Updates an existing facturaDetalle.
     *
     * @param facturaDetalleDTO the facturaDetalleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated facturaDetalleDTO,
     * or with status {@code 400 (Bad Request)} if the facturaDetalleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the facturaDetalleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/factura-detalles")
    public ResponseEntity<FacturaDetalleDTO> updateFacturaDetalle(@RequestBody FacturaDetalleDTO facturaDetalleDTO) throws URISyntaxException {
        log.debug("REST request to update FacturaDetalle : {}", facturaDetalleDTO);
        if (facturaDetalleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        FacturaDetalleDTO result = facturaDetalleService.save(facturaDetalleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, facturaDetalleDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /factura-detalles} : get all the facturaDetalles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of facturaDetalles in body.
     */
    @GetMapping("/factura-detalles")
    public ResponseEntity<List<FacturaDetalleDTO>> getAllFacturaDetalles(FacturaDetalleCriteria criteria, Pageable pageable) {
        log.debug("REST request to get FacturaDetalles by criteria: {}", criteria);
        Page<FacturaDetalleDTO> page = facturaDetalleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /factura-detalles/count} : count all the facturaDetalles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/factura-detalles/count")
    public ResponseEntity<Long> countFacturaDetalles(FacturaDetalleCriteria criteria) {
        log.debug("REST request to count FacturaDetalles by criteria: {}", criteria);
        return ResponseEntity.ok().body(facturaDetalleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /factura-detalles/:id} : get the "id" facturaDetalle.
     *
     * @param id the id of the facturaDetalleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the facturaDetalleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/factura-detalles/{id}")
    public ResponseEntity<FacturaDetalleDTO> getFacturaDetalle(@PathVariable Long id) {
        log.debug("REST request to get FacturaDetalle : {}", id);
        Optional<FacturaDetalleDTO> facturaDetalleDTO = facturaDetalleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(facturaDetalleDTO);
    }

    /**
     * {@code DELETE  /factura-detalles/:id} : delete the "id" facturaDetalle.
     *
     * @param id the id of the facturaDetalleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/factura-detalles/{id}")
    public ResponseEntity<Void> deleteFacturaDetalle(@PathVariable Long id) {
        log.debug("REST request to delete FacturaDetalle : {}", id);
        facturaDetalleService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
