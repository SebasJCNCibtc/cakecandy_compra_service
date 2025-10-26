package com.proyecto.cakecandy_compra_service.domain.repository;

import com.proyecto.cakecandy_compra_service.domain.model.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Integer> {
}