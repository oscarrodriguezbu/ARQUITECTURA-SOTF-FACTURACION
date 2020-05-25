package com.themkers.facturacion.service;

import com.themkers.facturacion.domain.FacturaDetalle;
import com.themkers.facturacion.repository.FacturaDetalleRepository;
import com.themkers.facturacion.service.dto.FacturaDetalleDTO;
import com.themkers.facturacion.service.mapper.FacturaDetalleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link FacturaDetalle}.
 */
@Service
@Transactional
public class FacturaDetalleService {

    private final Logger log = LoggerFactory.getLogger(FacturaDetalleService.class);

    private final FacturaDetalleRepository facturaDetalleRepository;

    private final FacturaDetalleMapper facturaDetalleMapper;

    public FacturaDetalleService(FacturaDetalleRepository facturaDetalleRepository, FacturaDetalleMapper facturaDetalleMapper) {
        this.facturaDetalleRepository = facturaDetalleRepository;
        this.facturaDetalleMapper = facturaDetalleMapper;
    }

    /**
     * Save a facturaDetalle.
     *
     * @param facturaDetalleDTO the entity to save.
     * @return the persisted entity.
     */
    public FacturaDetalleDTO save(FacturaDetalleDTO facturaDetalleDTO) {
        log.debug("Request to save FacturaDetalle : {}", facturaDetalleDTO);
        FacturaDetalle facturaDetalle = facturaDetalleMapper.toEntity(facturaDetalleDTO);
        facturaDetalle = facturaDetalleRepository.save(facturaDetalle);
        return facturaDetalleMapper.toDto(facturaDetalle);
    }

    /**
     * Get all the facturaDetalles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FacturaDetalleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FacturaDetalles");
        return facturaDetalleRepository.findAll(pageable)
            .map(facturaDetalleMapper::toDto);
    }

    /**
     * Get one facturaDetalle by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FacturaDetalleDTO> findOne(Long id) {
        log.debug("Request to get FacturaDetalle : {}", id);
        return facturaDetalleRepository.findById(id)
            .map(facturaDetalleMapper::toDto);
    }

    /**
     * Delete the facturaDetalle by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete FacturaDetalle : {}", id);
        facturaDetalleRepository.deleteById(id);
    }
}
