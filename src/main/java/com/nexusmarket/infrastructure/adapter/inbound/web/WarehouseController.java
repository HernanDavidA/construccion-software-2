package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.command.RegisterWarehouse;
import com.nexusmarket.domain.model.Warehouse;
import com.nexusmarket.domain.port.inbound.QueryWarehousesUseCase;
import com.nexusmarket.domain.port.inbound.RegisterWarehouseUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final RegisterWarehouseUseCase registerWarehouse;
    private final QueryWarehousesUseCase queryWarehouses;
    private final CurrentUser currentUser;

    public WarehouseController(RegisterWarehouseUseCase registerWarehouse, QueryWarehousesUseCase queryWarehouses, CurrentUser currentUser) {
        this.registerWarehouse = registerWarehouse;
        this.queryWarehouses = queryWarehouses;
        this.currentUser = currentUser;
    }

    @PostMapping
    Warehouse register(HttpServletRequest request, @RequestBody RegisterWarehouse command) {
        return registerWarehouse.register(currentUser.require(request), command);
    }

    @GetMapping("/{id}")
    Warehouse get(HttpServletRequest request, @PathVariable String id) {
        return queryWarehouses.get(currentUser.require(request), id);
    }

    @GetMapping
    List<Warehouse> list(HttpServletRequest request) {
        return queryWarehouses.list(currentUser.require(request));
    }
}
