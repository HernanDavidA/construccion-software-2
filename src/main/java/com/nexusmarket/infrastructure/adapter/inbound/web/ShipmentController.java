package com.nexusmarket.infrastructure.adapter.inbound.web;

import com.nexusmarket.domain.model.Shipment;
import com.nexusmarket.domain.port.inbound.DispatchShipmentUseCase;
import com.nexusmarket.domain.port.inbound.MarkShipmentInTransitUseCase;
import com.nexusmarket.domain.port.inbound.PackShipmentUseCase;
import com.nexusmarket.domain.port.inbound.QueryShipmentUseCase;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final PackShipmentUseCase packShipment;
    private final DispatchShipmentUseCase dispatchShipment;
    private final MarkShipmentInTransitUseCase markInTransit;
    private final QueryShipmentUseCase queryShipment;
    private final CurrentUser currentUser;

    public ShipmentController(
            PackShipmentUseCase packShipment,
            DispatchShipmentUseCase dispatchShipment,
            MarkShipmentInTransitUseCase markInTransit,
            QueryShipmentUseCase queryShipment,
            CurrentUser currentUser) {
        this.packShipment = packShipment;
        this.dispatchShipment = dispatchShipment;
        this.markInTransit = markInTransit;
        this.queryShipment = queryShipment;
        this.currentUser = currentUser;
    }

    @PostMapping("/{id}/pack")
    Shipment pack(HttpServletRequest request, @PathVariable String id) {
        return packShipment.pack(currentUser.require(request), id);
    }

    @PostMapping("/{id}/dispatch")
    Shipment dispatch(HttpServletRequest request, @PathVariable String id) {
        return dispatchShipment.dispatch(currentUser.require(request), id);
    }

    @PostMapping("/{id}/transit")
    Shipment transit(HttpServletRequest request, @PathVariable String id) {
        return markInTransit.mark(currentUser.require(request), id);
    }

    @GetMapping("/order/{orderId}")
    Shipment byOrder(HttpServletRequest request, @PathVariable String orderId) {
        return queryShipment.getByOrder(currentUser.require(request), orderId);
    }
}
