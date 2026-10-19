package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.command.AdjustStock;
import com.nexusmarket.domain.command.ReceiveStock;
import com.nexusmarket.domain.model.Inventory;
import com.nexusmarket.domain.model.InventoryCondition;
import com.nexusmarket.domain.model.InventoryMovement;
import com.nexusmarket.domain.port.inbound.AdjustStockUseCase;
import com.nexusmarket.domain.port.inbound.MarkInventoryConditionUseCase;
import com.nexusmarket.domain.port.inbound.QueryInventoryUseCase;
import com.nexusmarket.domain.port.inbound.ReceiveStockUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final ReceiveStockUseCase receiveStock;
    private final AdjustStockUseCase adjustStock;
    private final MarkInventoryConditionUseCase markCondition;
    private final QueryInventoryUseCase queryInventory;
    private final CurrentUser currentUser;

    public InventoryController(
            ReceiveStockUseCase receiveStock,
            AdjustStockUseCase adjustStock,
            MarkInventoryConditionUseCase markCondition,
            QueryInventoryUseCase queryInventory,
            CurrentUser currentUser) {
        this.receiveStock = receiveStock;
        this.adjustStock = adjustStock;
        this.markCondition = markCondition;
        this.queryInventory = queryInventory;
        this.currentUser = currentUser;
    }

    @PostMapping("/receipts")
    Inventory receive(HttpServletRequest request, @RequestBody ReceiveStock command) {
        return receiveStock.receive(currentUser.require(request), command);
    }

    @PostMapping("/adjustments")
    Inventory adjust(HttpServletRequest request, @RequestBody AdjustStock command) {
        return adjustStock.adjust(currentUser.require(request), command);
    }

    @PostMapping("/{id}/condition")
    Inventory mark(HttpServletRequest request, @PathVariable String id, @RequestParam InventoryCondition condition) {
        return markCondition.mark(currentUser.require(request), id, condition);
    }

    @GetMapping
    List<Inventory> list(HttpServletRequest request) {
        return queryInventory.list(currentUser.require(request));
    }

    @GetMapping("/{id}/movements")
    List<InventoryMovement> movements(HttpServletRequest request, @PathVariable String id) {
        return queryInventory.movements(currentUser.require(request), id);
    }
}
