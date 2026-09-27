package com.nexusmarket.adapter.in.api;

import com.nexusmarket.application.catalog.port.in.AddProductVariantUseCase;
import com.nexusmarket.application.catalog.port.in.ChangeProductPublicationUseCase;
import com.nexusmarket.application.catalog.port.in.CreateMarketplaceWarehouseUseCase;
import com.nexusmarket.application.catalog.port.in.CreateSellerWarehouseUseCase;
import com.nexusmarket.application.catalog.port.in.PublishProductUseCase;
import com.nexusmarket.application.catalog.port.in.RegisterProductUseCase;
import com.nexusmarket.application.commerce.port.in.AddItemToCartUseCase;
import com.nexusmarket.application.commerce.port.in.CompleteDigitalOrderUseCase;
import com.nexusmarket.application.commerce.port.in.ConfirmOrderUseCase;
import com.nexusmarket.application.commerce.port.in.OpenCartUseCase;
import com.nexusmarket.application.commerce.port.in.RegisterOrderPaymentUseCase;
import com.nexusmarket.application.inventory.port.in.AdjustStockUseCase;
import com.nexusmarket.application.inventory.port.in.MarkInventoryDamagedUseCase;
import com.nexusmarket.application.inventory.port.in.OpenInventoryUseCase;
import com.nexusmarket.application.inventory.port.in.ReceiveStockUseCase;
import com.nexusmarket.application.inventory.port.in.ReserveStockUseCase;
import com.nexusmarket.application.logistics.port.in.ConfirmShipmentDeliveryUseCase;
import com.nexusmarket.application.logistics.port.in.CreateShipmentUseCase;
import com.nexusmarket.application.logistics.port.in.DispatchShipmentUseCase;
import com.nexusmarket.application.logistics.port.in.RequestReturnUseCase;
import com.nexusmarket.application.logistics.port.in.ResolveReturnAndRefundUseCase;
import com.nexusmarket.application.users.port.in.AddBuyerAddressUseCase;
import com.nexusmarket.application.users.port.in.ChangeBuyerCommercialStatusUseCase;
import com.nexusmarket.application.users.port.in.ChangeSellerStatusUseCase;
import com.nexusmarket.application.users.port.in.ChangeUserStatusUseCase;
import com.nexusmarket.application.users.port.in.OnboardSellerUseCase;
import com.nexusmarket.application.users.port.in.RegisterAdministratorUseCase;
import com.nexusmarket.application.users.port.in.RegisterBuyerUseCase;
import com.nexusmarket.application.users.port.in.RegisterLogisticsOperatorUseCase;
import com.nexusmarket.application.users.port.in.RegisterSupervisorUseCase;

/**
 * Driving adapter: the only inbound surface. Callers depend on use-case ports, not services.
 */
public class NexusMarketApi {

    private final RegisterBuyerUseCase registerBuyer;
    private final OnboardSellerUseCase onboardSeller;
    private final RegisterAdministratorUseCase registerAdministrator;
    private final RegisterLogisticsOperatorUseCase registerLogisticsOperator;
    private final RegisterSupervisorUseCase registerSupervisor;
    private final ChangeUserStatusUseCase changeUserStatus;
    private final ChangeBuyerCommercialStatusUseCase changeBuyerCommercialStatus;
    private final ChangeSellerStatusUseCase changeSellerStatus;
    private final AddBuyerAddressUseCase addBuyerAddress;

    private final CreateMarketplaceWarehouseUseCase createMarketplaceWarehouse;
    private final CreateSellerWarehouseUseCase createSellerWarehouse;
    private final RegisterProductUseCase registerProduct;
    private final PublishProductUseCase publishProduct;
    private final ChangeProductPublicationUseCase changeProductPublication;
    private final AddProductVariantUseCase addProductVariant;

    private final OpenInventoryUseCase openInventory;
    private final ReceiveStockUseCase receiveStock;
    private final ReserveStockUseCase reserveStock;
    private final AdjustStockUseCase adjustStock;
    private final MarkInventoryDamagedUseCase markInventoryDamaged;

    private final OpenCartUseCase openCart;
    private final AddItemToCartUseCase addItemToCart;
    private final ConfirmOrderUseCase confirmOrder;
    private final RegisterOrderPaymentUseCase registerOrderPayment;
    private final CompleteDigitalOrderUseCase completeDigitalOrder;

    private final CreateShipmentUseCase createShipment;
    private final DispatchShipmentUseCase dispatchShipment;
    private final ConfirmShipmentDeliveryUseCase confirmShipmentDelivery;
    private final RequestReturnUseCase requestReturn;
    private final ResolveReturnAndRefundUseCase resolveReturnAndRefund;

    public NexusMarketApi(RegisterBuyerUseCase registerBuyer,
                          OnboardSellerUseCase onboardSeller,
                          RegisterAdministratorUseCase registerAdministrator,
                          RegisterLogisticsOperatorUseCase registerLogisticsOperator,
                          RegisterSupervisorUseCase registerSupervisor,
                          ChangeUserStatusUseCase changeUserStatus,
                          ChangeBuyerCommercialStatusUseCase changeBuyerCommercialStatus,
                          ChangeSellerStatusUseCase changeSellerStatus,
                          AddBuyerAddressUseCase addBuyerAddress,
                          CreateMarketplaceWarehouseUseCase createMarketplaceWarehouse,
                          CreateSellerWarehouseUseCase createSellerWarehouse,
                          RegisterProductUseCase registerProduct,
                          PublishProductUseCase publishProduct,
                          ChangeProductPublicationUseCase changeProductPublication,
                          AddProductVariantUseCase addProductVariant,
                          OpenInventoryUseCase openInventory,
                          ReceiveStockUseCase receiveStock,
                          ReserveStockUseCase reserveStock,
                          AdjustStockUseCase adjustStock,
                          MarkInventoryDamagedUseCase markInventoryDamaged,
                          OpenCartUseCase openCart,
                          AddItemToCartUseCase addItemToCart,
                          ConfirmOrderUseCase confirmOrder,
                          RegisterOrderPaymentUseCase registerOrderPayment,
                          CompleteDigitalOrderUseCase completeDigitalOrder,
                          CreateShipmentUseCase createShipment,
                          DispatchShipmentUseCase dispatchShipment,
                          ConfirmShipmentDeliveryUseCase confirmShipmentDelivery,
                          RequestReturnUseCase requestReturn,
                          ResolveReturnAndRefundUseCase resolveReturnAndRefund) {
        this.registerBuyer = registerBuyer;
        this.onboardSeller = onboardSeller;
        this.registerAdministrator = registerAdministrator;
        this.registerLogisticsOperator = registerLogisticsOperator;
        this.registerSupervisor = registerSupervisor;
        this.changeUserStatus = changeUserStatus;
        this.changeBuyerCommercialStatus = changeBuyerCommercialStatus;
        this.changeSellerStatus = changeSellerStatus;
        this.addBuyerAddress = addBuyerAddress;
        this.createMarketplaceWarehouse = createMarketplaceWarehouse;
        this.createSellerWarehouse = createSellerWarehouse;
        this.registerProduct = registerProduct;
        this.publishProduct = publishProduct;
        this.changeProductPublication = changeProductPublication;
        this.addProductVariant = addProductVariant;
        this.openInventory = openInventory;
        this.receiveStock = receiveStock;
        this.reserveStock = reserveStock;
        this.adjustStock = adjustStock;
        this.markInventoryDamaged = markInventoryDamaged;
        this.openCart = openCart;
        this.addItemToCart = addItemToCart;
        this.confirmOrder = confirmOrder;
        this.registerOrderPayment = registerOrderPayment;
        this.completeDigitalOrder = completeDigitalOrder;
        this.createShipment = createShipment;
        this.dispatchShipment = dispatchShipment;
        this.confirmShipmentDelivery = confirmShipmentDelivery;
        this.requestReturn = requestReturn;
        this.resolveReturnAndRefund = resolveReturnAndRefund;
    }

    public RegisterBuyerUseCase registerBuyer() {
        return registerBuyer;
    }

    public OnboardSellerUseCase onboardSeller() {
        return onboardSeller;
    }

    public RegisterAdministratorUseCase registerAdministrator() {
        return registerAdministrator;
    }

    public RegisterLogisticsOperatorUseCase registerLogisticsOperator() {
        return registerLogisticsOperator;
    }

    public RegisterSupervisorUseCase registerSupervisor() {
        return registerSupervisor;
    }

    public ChangeUserStatusUseCase changeUserStatus() {
        return changeUserStatus;
    }

    public ChangeBuyerCommercialStatusUseCase changeBuyerCommercialStatus() {
        return changeBuyerCommercialStatus;
    }

    public ChangeSellerStatusUseCase changeSellerStatus() {
        return changeSellerStatus;
    }

    public AddBuyerAddressUseCase addBuyerAddress() {
        return addBuyerAddress;
    }

    public CreateMarketplaceWarehouseUseCase createMarketplaceWarehouse() {
        return createMarketplaceWarehouse;
    }

    public CreateSellerWarehouseUseCase createSellerWarehouse() {
        return createSellerWarehouse;
    }

    public RegisterProductUseCase registerProduct() {
        return registerProduct;
    }

    public PublishProductUseCase publishProduct() {
        return publishProduct;
    }

    public ChangeProductPublicationUseCase changeProductPublication() {
        return changeProductPublication;
    }

    public AddProductVariantUseCase addProductVariant() {
        return addProductVariant;
    }

    public OpenInventoryUseCase openInventory() {
        return openInventory;
    }

    public ReceiveStockUseCase receiveStock() {
        return receiveStock;
    }

    public ReserveStockUseCase reserveStock() {
        return reserveStock;
    }

    public AdjustStockUseCase adjustStock() {
        return adjustStock;
    }

    public MarkInventoryDamagedUseCase markInventoryDamaged() {
        return markInventoryDamaged;
    }

    public OpenCartUseCase openCart() {
        return openCart;
    }

    public AddItemToCartUseCase addItemToCart() {
        return addItemToCart;
    }

    public ConfirmOrderUseCase confirmOrder() {
        return confirmOrder;
    }

    public RegisterOrderPaymentUseCase registerOrderPayment() {
        return registerOrderPayment;
    }

    public CompleteDigitalOrderUseCase completeDigitalOrder() {
        return completeDigitalOrder;
    }

    public CreateShipmentUseCase createShipment() {
        return createShipment;
    }

    public DispatchShipmentUseCase dispatchShipment() {
        return dispatchShipment;
    }

    public ConfirmShipmentDeliveryUseCase confirmShipmentDelivery() {
        return confirmShipmentDelivery;
    }

    public RequestReturnUseCase requestReturn() {
        return requestReturn;
    }

    public ResolveReturnAndRefundUseCase resolveReturnAndRefund() {
        return resolveReturnAndRefund;
    }

    public int useCaseCount() {
        return 30;
    }
}
