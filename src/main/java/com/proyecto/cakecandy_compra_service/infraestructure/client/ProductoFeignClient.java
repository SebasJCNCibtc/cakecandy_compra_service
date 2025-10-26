package com.proyecto.cakecandy_compra_service.infraestructure.client;

import com.proyecto.cakecandy_compra_service.infraestructure.dto.ProductoDto;
import com.proyecto.cakecandy_compra_service.infraestructure.dto.ProveedorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "producto-service")
public interface ProductoFeignClient {

    @PutMapping("/api/productos/{id}/add-stock")
    void addStock(@PathVariable("id") Integer id, @RequestParam("cantidad") Integer cantidad);
    @GetMapping("/api/productos/{id}")
    ProductoDto findProductoById(@PathVariable("id") Integer id);

    @GetMapping("/api/proveedores/{id}")
    ProveedorDto findProveedorById(@PathVariable("id") Integer id);
}