package com.nexusmarket.bootstrap;

import com.nexusmarket.adapter.in.api.NexusMarketApi;
import com.nexusmarket.adapter.out.persistence.memory.InMemoryCartRepository;
import com.nexusmarket.adapter.out.persistence.memory.InMemoryInventoryRepository;
import com.nexusmarket.adapter.out.persistence.memory.InMemoryOrderRepository;
import com.nexusmarket.adapter.out.persistence.memory.InMemoryProductRepository;
import com.nexusmarket.adapter.out.persistence.memory.InMemoryReturnRequestRepository;
import com.nexusmarket.adapter.out.persistence.memory.InMemoryShipmentRepository;
import com.nexusmarket.adapter.out.persistence.memory.InMemoryUserRepository;
import com.nexusmarket.adapter.out.persistence.memory.InMemoryWarehouseRepository;
import com.nexusmarket.application.catalog.service.AddProductVariantService;
import com.nexusmarket.application.catalog.service.ChangeProductPublicationService;
import com.nexusmarket.application.catalog.service.CreateMarketplaceWarehouseService;
import com.nexusmarket.application.catalog.service.CreateSellerWarehouseService;
import com.nexusmarket.application.catalog.service.PublishProductService;
import com.nexusmarket.application.catalog.service.RegisterProductService;
import com.nexusmarket.application.commerce.service.AddItemToCartService;
import com.nexusmarket.application.commerce.service.CompleteDigitalOrderService;
import com.nexusmarket.application.commerce.service.ConfirmOrderService;
import com.nexusmarket.application.commerce.service.OpenCartService;
import com.nexusmarket.application.commerce.service.RegisterOrderPaymentService;
import com.nexusmarket.application.inventory.service.AdjustStockService;
import com.nexusmarket.application.inventory.service.MarkInventoryDamagedService;
import com.nexusmarket.application.inventory.service.OpenInventoryService;
import com.nexusmarket.application.inventory.service.ReceiveStockService;
import com.nexusmarket.application.inventory.service.ReserveStockService;
import com.nexusmarket.application.logistics.service.ConfirmShipmentDeliveryService;
import com.nexusmarket.application.logistics.service.CreateShipmentService;
import com.nexusmarket.application.logistics.service.DispatchShipmentService;
import com.nexusmarket.application.logistics.service.RequestReturnService;
import com.nexusmarket.application.logistics.service.ResolveReturnAndRefundService;
import com.nexusmarket.application.users.service.AddBuyerAddressService;
import com.nexusmarket.application.users.service.ChangeBuyerCommercialStatusService;
import com.nexusmarket.application.users.service.ChangeSellerStatusService;
import com.nexusmarket.application.users.service.ChangeUserStatusService;
import com.nexusmarket.application.users.service.OnboardSellerService;
import com.nexusmarket.application.users.service.RegisterAdministratorService;
import com.nexusmarket.application.users.service.RegisterBuyerService;
import com.nexusmarket.application.users.service.RegisterLogisticsOperatorService;
import com.nexusmarket.application.users.service.RegisterSupervisorService;

/**
 * Composition root: wires outbound adapters to services and exposes the inbound API.
 */
public final class NexusMarketHexagon {

    private final NexusMarketApi api;

    private NexusMarketHexagon(NexusMarketApi api) {
        this.api = api;
    }

    public static NexusMarketHexagon withInMemoryAdapters() {
        InMemoryUserRepository users = new InMemoryUserRepository();
        InMemoryWarehouseRepository warehouses = new InMemoryWarehouseRepository();
        InMemoryProductRepository products = new InMemoryProductRepository();
        InMemoryInventoryRepository inventories = new InMemoryInventoryRepository();
        InMemoryCartRepository carts = new InMemoryCartRepository();
        InMemoryOrderRepository orders = new InMemoryOrderRepository();
        InMemoryShipmentRepository shipments = new InMemoryShipmentRepository();
        InMemoryReturnRequestRepository returns = new InMemoryReturnRequestRepository();

        NexusMarketApi api = new NexusMarketApi(
                new RegisterBuyerService(users),
                new OnboardSellerService(users),
                new RegisterAdministratorService(users),
                new RegisterLogisticsOperatorService(users),
                new RegisterSupervisorService(users),
                new ChangeUserStatusService(users),
                new ChangeBuyerCommercialStatusService(users),
                new ChangeSellerStatusService(users),
                new AddBuyerAddressService(users),
                new CreateMarketplaceWarehouseService(warehouses),
                new CreateSellerWarehouseService(warehouses, users),
                new RegisterProductService(products, users),
                new PublishProductService(products),
                new ChangeProductPublicationService(products),
                new AddProductVariantService(products),
                new OpenInventoryService(inventories, products, warehouses, users),
                new ReceiveStockService(inventories, users),
                new ReserveStockService(inventories, users),
                new AdjustStockService(inventories, users),
                new MarkInventoryDamagedService(inventories, users),
                new OpenCartService(carts, users),
                new AddItemToCartService(carts, products, inventories),
                new ConfirmOrderService(carts, orders, inventories),
                new RegisterOrderPaymentService(orders),
                new CompleteDigitalOrderService(orders),
                new CreateShipmentService(shipments, orders, users),
                new DispatchShipmentService(shipments, orders),
                new ConfirmShipmentDeliveryService(shipments, orders),
                new RequestReturnService(returns, orders, users),
                new ResolveReturnAndRefundService(returns, orders, inventories, users)
        );
        return new NexusMarketHexagon(api);
    }

    public NexusMarketApi api() {
        return api;
    }
}
