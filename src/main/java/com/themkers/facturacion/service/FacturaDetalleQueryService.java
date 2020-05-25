package com.themkers.facturacion.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.themkers.facturacion.domain.FacturaDetalle;
import com.themkers.facturacion.domain.*; // for static metamodels
import com.themkers.facturacion.repository.FacturaDetalleRepository;
import com.themkers.facturacion.service.dto.FacturaDetalleCriteria;
import com.themkers.facturacion.service.dto.FacturaDetalleDTO;
import com.themkers.facturacion.service.mapper.FacturaDetalleMapper;

/**
 * Service for executing complex queries for {@link FacturaDetalle} entities in the database.
 * The main input is a {@link FacturaDetalleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FacturaDetalleDTO} or a {@link Page} of {@link FacturaDetalleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FacturaDetalleQueryService extends QueryService<FacturaDetalle> {

    private final Logger log = LoggerFactory.getLogger(FacturaDetalleQueryService.class);

    private final FacturaDetalleRepository facturaDetalleRepository;

    private final FacturaDetalleMapper facturaDetalleMapper;

    public FacturaDetalleQueryService(FacturaDetalleRepository facturaDetalleRepository, FacturaDetalleMapper facturaDetalleMapper) {
        this.facturaDetalleRepository = facturaDetalleRepository;
        this.facturaDetalleMapper = facturaDetalleMapper;
    }

    /**
     * Return a {@link List} of {@link FacturaDetalleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FacturaDetalleDTO> findByCriteria(FacturaDetalleCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<FacturaDetalle> specification = createSpecification(criteria);
        return facturaDetalleMapper.toDto(facturaDetalleRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FacturaDetalleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FacturaDetalleDTO> findByCriteria(FacturaDetalleCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<FacturaDetalle> specification = createSpecification(criteria);
        return facturaDetalleRepository.findAll(specification, page)
            .map(facturaDetalleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FacturaDetalleCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<FacturaDetalle> specification = createSpecification(criteria);
        return facturaDetalleRepository.count(specification);
    }

    /**
     * Function to convert {@link FacturaDetalleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FacturaDetalle> createSpecification(FacturaDetalleCriteria criteria) {
        Specification<FacturaDetalle> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), FacturaDetalle_.id));
            }
            if (criteria.getProductoId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getProductoId(), FacturaDetalle_.productoId));
            }
            if (criteria.getCantidad() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCantidad(), FacturaDetalle_.cantidad));
            }
            if (criteria.getPrecioUnitario() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrecioUnitario(), FacturaDetalle_.precioUnitario));
            }
        }
        return specification;
    }
}
