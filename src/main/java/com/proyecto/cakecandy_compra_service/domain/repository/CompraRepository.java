package com.proyecto.cakecandy_compra_service.domain.repository;

import com.proyecto.cakecandy_compra_service.domain.model.Compra;
import com.proyecto.cakecandy_compra_service.domain.model.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {
    List<Compra> findByEstadoPago(EstadoPago estado);

    List<Compra> findByEstadoPagoAndFechaVencimientoBefore(EstadoPago estado, LocalDate fecha);
}