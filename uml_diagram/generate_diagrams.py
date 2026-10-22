import urllib.request
from PIL import Image
import io
import os
import numpy as np
import shutil

# ─────────────────────────────────────────────────────────────────────────────
# 1. STAGE 1 ARCHITECTURE (PARTS 1 - 5 + SYSTEM CORE & DATABASE)
# ─────────────────────────────────────────────────────────────────────────────
stage1_puml = """@startuml
scale max 2400 width
skinparam dpi 130
skinparam defaultFontName Arial
skinparam classFontSize 11
skinparam classAttributeFontSize 10
skinparam packageFontSize 13
skinparam packageStyle rectangle
skinparam roundcorner 4
skinparam shadowing false

skinparam package {
    BackgroundColor #FAFAF8
    BorderColor #222222
    BorderThickness 1.3
}

skinparam class {
    BackgroundColor #FFFFFF
    ArrowColor #222222
    BorderColor #222222
    BorderThickness 1.1
}

' ═══════════════════════════════════════════════════════════
' ROW 1: CORE SERVICES, SINGLETON, OBSERVER & PROXY
' ═══════════════════════════════════════════════════════════
package "System Core & Database" as p_core {
    class QuickBiteServer {
        + main(args : String[]) : void
        + OrderApiHandler (/api/stage1)
    }

    class Database {
        - {static} DB_URL : String
        - {static} connection : Connection
        + {static} getConnection() : Connection
    }

    class "Database.OrderRepository" as OrderRepo {
        + save(order, region, channel, status) : void
        + findAllAsText() : List<String>
    }
    Database +-- OrderRepo
}

package "part1_singleton" as p1 {
    class Order {
        - customerName : String
        - amount : double
        - tax : double
        - couponCode : String
        - paymentProcessor : PaymentProcessor
        + getCustomerName() : String
        + getAmount() : double
        + getTax() : double
        + setTax(tax : double) : void
        + getCouponCode() : String
        + getTotalPayable() : double
    }

    class QuickBiteSystem <<Singleton>> {
        - {static} instance : QuickBiteSystem
        - QuickBiteSystem()
        + {static} getInstance() : QuickBiteSystem
        + processOrder(order, regionFactory, channel) : void
    }
    Order "*" <-- QuickBiteSystem : coordinates
}

package "part5_observer_proxy" as p5 {
    interface OrderService {
        + placeOrder(order, regionFactory, channel) : void
    }

    class OrderServiceProxy implements OrderService {
        - realOrderService : RealOrderService
        - username : String
        - password : String
        + authenticate() : boolean
        + placeOrder(order, regionFactory, channel) : void
    }

    class RealOrderService implements OrderService {
        - tracker : DeliveryTracker
        + placeOrder(order, regionFactory, channel) : void
    }

    enum DeliveryStatus {
        PLACED
        OUT_FOR_DELIVERY
        DELIVERED
    }

    class DeliveryTracker {
        - order : Order
        - observers : List<DeliveryObserver>
        - status : DeliveryStatus
        + addObserver(observer : DeliveryObserver) : void
        + removeObserver(observer : DeliveryObserver) : void
        + setStatus(status : DeliveryStatus) : void
        - notifyObservers() : void
    }

    interface DeliveryObserver {
        + update(order : Order, status : DeliveryStatus) : void
    }

    class RestaurantDashboard implements DeliveryObserver {
        + update(order : Order, status : DeliveryStatus) : void
    }
    class PartnerAppNotifier implements DeliveryObserver {
        + update(order : Order, status : DeliveryStatus) : void
    }
    class CustomerAppNotifier implements DeliveryObserver {
        + update(order : Order, status : DeliveryStatus) : void
    }

    OrderServiceProxy o--> "1" RealOrderService : delegates to
    RealOrderService ..> DeliveryTracker : updates
    DeliveryTracker o--> "*" DeliveryObserver : notifies
}

' ═══════════════════════════════════════════════════════════
' ROW 2: ABSTRACT FACTORY, CHAIN OF RESPONSIBILITY, BRIDGE
' ═══════════════════════════════════════════════════════════
package "part2_abstract_factory" as p2 {
    interface RegionFactory {
        + createTaxCalculator() : TaxCalculator
        + createPaymentProcessor() : PaymentProcessor
    }

    class IndiaRegionFactory implements RegionFactory {
        + createTaxCalculator() : TaxCalculator
        + createPaymentProcessor() : PaymentProcessor
    }

    class USRegionFactory implements RegionFactory {
        + createTaxCalculator() : TaxCalculator
        + createPaymentProcessor() : PaymentProcessor
    }

    interface TaxCalculator {
        + calculateTax(amount : double) : double
    }
    class GSTCalculator implements TaxCalculator {
        + calculateTax(amount : double) : double
    }
    class SalesTaxCalculator implements TaxCalculator {
        + calculateTax(amount : double) : double
    }

    interface PaymentProcessor {
        + charge(amount : double) : boolean
    }
    class UPIProcessor implements PaymentProcessor {
        + charge(amount : double) : boolean
    }
    class CardProcessor implements PaymentProcessor {
        + charge(amount : double) : boolean
    }

    IndiaRegionFactory ..> GSTCalculator : creates
    IndiaRegionFactory ..> UPIProcessor : creates
    USRegionFactory ..> SalesTaxCalculator : creates
    USRegionFactory ..> CardProcessor : creates
}

package "part3_chain_of_responsibility" as p3 {
    abstract class OrderHandler {
        # next : OrderHandler
        + setNext(next : OrderHandler) : OrderHandler
        + handle(order : Order) : boolean
    }

    class CouponValidationHandler extends OrderHandler {
        + handle(order : Order) : boolean
    }
    class FraudCheckHandler extends OrderHandler {
        + handle(order : Order) : boolean
    }
    class PaymentGatewayHandler extends OrderHandler {
        + handle(order : Order) : boolean
    }

    OrderHandler "0..1" o--> "1" OrderHandler : next
}

package "part4_bridge" as p4 {
    abstract class Notification {
        # channel : NotificationChannel
        + Notification(channel : NotificationChannel)
        + {abstract} send(customerName : String) : void
    }

    class OrderConfirmedNotification extends Notification {
        + send(customerName : String) : void
    }
    class OutForDeliveryNotification extends Notification {
        + send(customerName : String) : void
    }

    interface NotificationChannel {
        + send(message : String) : void
    }

    class PushChannel implements NotificationChannel {
        + send(message : String) : void
    }
    class SMSChannel implements NotificationChannel {
        + send(message : String) : void
    }
    class EmailChannel implements NotificationChannel {
        + send(message : String) : void
    }

    Notification o--> "1" NotificationChannel : bridges to
}

' ═══════════════════════════════════════════════════════════
' INVOCATION & DELEGATION ARROWS
' ═══════════════════════════════════════════════════════════
QuickBiteServer ..> OrderServiceProxy : invokes (/api/stage1)
RealOrderService ..> QuickBiteSystem : processes via
RealOrderService ..> OrderRepo : persists audit
QuickBiteSystem -down-> p2 : requests tax/payment
QuickBiteSystem -down-> p3 : runs filter chain
QuickBiteSystem -down-> p4 : dispatches notifications

' ═══════════════════════════════════════════════════════════
' STRICT 2-ROW GRID CONSTRAINTS (CLASS-LEVEL)
' ═══════════════════════════════════════════════════════════
QuickBiteServer -[hidden]right-> QuickBiteSystem
QuickBiteSystem -[hidden]right-> OrderServiceProxy

OrderRepo -[hidden]down-> RegionFactory
Order -[hidden]down-> OrderHandler
DeliveryObserver -[hidden]down-> Notification

RegionFactory -[hidden]right-> OrderHandler
OrderHandler -[hidden]right-> Notification

@enduml
"""

# ─────────────────────────────────────────────────────────────────────────────
# 2. STAGE 2 ARCHITECTURE (PARTS 6 - 9 + SERVER)
# ─────────────────────────────────────────────────────────────────────────────
stage2_puml = """@startuml
scale max 2400 width
skinparam dpi 130
skinparam defaultFontName Arial
skinparam classFontSize 11
skinparam classAttributeFontSize 10
skinparam packageFontSize 13
skinparam packageStyle rectangle
skinparam roundcorner 4
skinparam shadowing false

skinparam package {
    BackgroundColor #FAFAF8
    BorderColor #222222
    BorderThickness 1.3
}

skinparam class {
    BackgroundColor #FFFFFF
    ArrowColor #222222
    BorderColor #222222
    BorderThickness 1.1
}

class QuickBiteServer {
    + main(args : String[]) : void
    + Phase2ApiHandler (/api/stage2)
}

' ═══════════════════════════════════════════════════════════
' ROW 1: BUILDER & PROTOTYPE | TEMPLATE METHOD WORKFLOW
' ═══════════════════════════════════════════════════════════
package "part6_builder_prototype" as p6 {
    class CustomOrder {
        - customerName : String
        - baseDish : String
        - size : String
        - toppings : List<String>
        - sideDrink : String
        - spiceLevel : String
        - specialInstructions : String
        - basePrice : double
        + calculateSubtotal() : double
    }

    class CustomOrderBuilder {
        + setCustomerName(name : String) : CustomOrderBuilder
        + setBaseDish(dish, price) : CustomOrderBuilder
        + setSize(size : String) : CustomOrderBuilder
        + addTopping(topping : String) : CustomOrderBuilder
        + setSideDrink(drink : String) : CustomOrderBuilder
        + setSpiceLevel(spice : String) : CustomOrderBuilder
        + setSpecialInstructions(notes : String) : CustomOrderBuilder
        + build() : CustomOrder
    }

    class MealDirector {
        + constructBurgerCombo(customer : String) : CustomOrder
        + constructFitnessMeal(customer : String) : CustomOrder
        + constructPartyFeast(customer : String) : CustomOrder
    }

    interface OrderPrototype {
        + cloneOrder() : OrderPrototype
    }

    class ReorderableMeal implements OrderPrototype {
        - originalOrderId : String
        - customerName : String
        - mealName : String
        - price : double
        - addons : List<String>
        - deliveryAddress : String
        + ReorderableMeal(target : ReorderableMeal)
        + cloneOrder() : ReorderableMeal
        + addAddon(addon : String) : void
    }

    class OrderRegistry {
        - prototypes : Map<String, ReorderableMeal>
        + registerPrototype(key, meal) : void
        + getClone(key) : ReorderableMeal
    }

    MealDirector ..> CustomOrderBuilder : drives
    CustomOrderBuilder ..> CustomOrder : builds
    OrderRegistry o--> "*" ReorderableMeal : manages archetypes
}

package "part7_template_method" as p7 {
    abstract class OrderFulfillmentWorkflow {
        + fulfillOrder(orderId, customer, item, amount) : void
        + {abstract} getWorkflowName() : String
        # verifyOrder(orderId, customer, amount) : void
        # prepareFood(item) : void
        # qualityCheck() : void
        # {abstract} packageOrder() : void
        # {abstract} handoverOrder() : void
        # sendCompletionAlert(customer) : void
    }

    class DeliveryFulfillmentWorkflow extends OrderFulfillmentWorkflow {
        + getWorkflowName() : String
        # packageOrder() : void
        # handoverOrder() : void
    }

    class TakeawayFulfillmentWorkflow extends OrderFulfillmentWorkflow {
        + getWorkflowName() : String
        # packageOrder() : void
        # handoverOrder() : void
    }

    class DineInFulfillmentWorkflow extends OrderFulfillmentWorkflow {
        + getWorkflowName() : String
        # packageOrder() : void
        # handoverOrder() : void
    }
}

' ═══════════════════════════════════════════════════════════
' ROW 2: STRATEGY PRICING | MEMENTO TIME-TRAVEL
' ═══════════════════════════════════════════════════════════
package "part8_strategy" as p8 {
    interface DiscountStrategy {
        + applyDiscount(subtotal : double) : double
        + getStrategyName() : String
    }

    class PercentageDiscountStrategy implements DiscountStrategy {
        - percentage : double
        + applyDiscount(subtotal : double) : double
        + getStrategyName() : String
    }

    class FlatDiscountStrategy implements DiscountStrategy {
        - flatAmount : double
        + applyDiscount(subtotal : double) : double
        + getStrategyName() : String
    }

    class LoyaltyTierStrategy implements DiscountStrategy {
        - tier : String
        + applyDiscount(subtotal : double) : double
        + getStrategyName() : String
    }

    class NoDiscountStrategy implements DiscountStrategy {
        + applyDiscount(subtotal : double) : double
        + getStrategyName() : String
    }

    class PricingContext {
        - strategy : DiscountStrategy
        + setStrategy(strategy : DiscountStrategy) : void
        + calculateFinalPrice(subtotal : double) : double
    }

    PricingContext o--> "1" DiscountStrategy : executes
}

package "part9_memento" as p9 {
    class OrderMemento {
        - cartItems : List<String>
        - subtotal : double
        - timestamp : String
        + getCartItems() : List<String>
        + getSubtotal() : double
        + getTimestamp() : String
    }

    class CartOriginator {
        - items : List<String>
        - subtotal : double
        + addItem(name : String, price : double) : void
        + saveStateToMemento() : OrderMemento
        + restoreStateFromMemento(memento : OrderMemento) : void
        + getItems() : List<String>
        + getSubtotal() : double
    }

    class CartCaretaker {
        - undoStack : Deque<OrderMemento>
        - redoStack : Deque<OrderMemento>
        + saveState(originator : CartOriginator) : void
        + undo(originator : CartOriginator) : boolean
        + redo(originator : CartOriginator) : boolean
    }

    CartOriginator ..> OrderMemento : creates / restores
    CartCaretaker o--> "*" OrderMemento : stacks
    CartCaretaker ..> CartOriginator : operates on
}

' ═══════════════════════════════════════════════════════════
' SERVER DISPATCH & INTER-PACKAGE RELATIONS
' ═══════════════════════════════════════════════════════════
QuickBiteServer ..> MealDirector : constructs combo (/api/stage2)
QuickBiteServer ..> OrderRegistry : clones archetype
QuickBiteServer ..> OrderFulfillmentWorkflow : executes workflow
QuickBiteServer ..> PricingContext : calculates pricing
QuickBiteServer ..> CartCaretaker : manages undo/redo

p6 ..> p8 : calculates discounted pricing
p6 ..> p9 : populates cart state

' ═══════════════════════════════════════════════════════════
' STRICT 2x2 GRID CONSTRAINTS (CLASS-LEVEL)
' ═══════════════════════════════════════════════════════════
QuickBiteServer -[hidden]down-> MealDirector
CustomOrderBuilder -[hidden]right-> OrderFulfillmentWorkflow

CustomOrder -[hidden]down-> PricingContext
DineInFulfillmentWorkflow -[hidden]down-> CartCaretaker

DiscountStrategy -[hidden]right-> CartOriginator

@enduml
"""

# ─────────────────────────────────────────────────────────────────────────────
# 3. COMPLETE MASTER ARCHITECTURE (PARTS 1 - 9 + CORE)
# ─────────────────────────────────────────────────────────────────────────────
master_puml = """@startuml
scale max 2400 width
skinparam dpi 120
skinparam defaultFontName Arial
skinparam classFontSize 10
skinparam classAttributeFontSize 9
skinparam packageFontSize 12
skinparam packageStyle rectangle
skinparam roundcorner 4
skinparam shadowing false

skinparam package {
    BackgroundColor #FAFAF8
    BorderColor #222222
    BorderThickness 1.2
}

skinparam class {
    BackgroundColor #FFFFFF
    ArrowColor #222222
    BorderColor #222222
    BorderThickness 1.1
}

' ═══════════════════════════════════════════════════════════
' ROW 1: CORE SERVICES, SINGLETON & SECURITY GATEWAYS
' ═══════════════════════════════════════════════════════════
package "System Core & Database" as p_core {
    class QuickBiteServer {
        + main(args : String[]) : void
        + StaticHandler (public/*)
        + OrderApiHandler (/api/stage1)
        + Phase2ApiHandler (/api/stage2)
    }

    class Database {
        - {static} DB_URL : String
        - {static} connection : Connection
        + {static} getConnection() : Connection
    }

    class "Database.OrderRepository" as OrderRepo {
        + save(order, region, channel, status) : void
        + findAllAsText() : List<String>
    }
    Database +-- OrderRepo
}

package "part1_singleton" as p1 {
    class Order {
        - customerName : String
        - amount : double
        - tax : double
        - couponCode : String
        - paymentProcessor : PaymentProcessor
        + getTotalPayable() : double
    }

    class QuickBiteSystem <<Singleton>> {
        - {static} instance : QuickBiteSystem
        - QuickBiteSystem()
        + {static} getInstance() : QuickBiteSystem
        + processOrder(order, regionFactory, channel) : void
    }
    Order "*" <-- QuickBiteSystem : coordinates
}

package "part5_observer_proxy" as p5 {
    interface OrderService {
        + placeOrder(order, regionFactory, channel) : void
    }

    class OrderServiceProxy implements OrderService {
        - realOrderService : RealOrderService
        + authenticate() : boolean
        + placeOrder(order, regionFactory, channel) : void
    }

    class RealOrderService implements OrderService {
        - tracker : DeliveryTracker
        + placeOrder(order, regionFactory, channel) : void
    }

    class DeliveryTracker {
        - observers : List<DeliveryObserver>
        + addObserver(observer : DeliveryObserver) : void
        + setStatus(status : DeliveryStatus) : void
    }

    interface DeliveryObserver {
        + update(order : Order, status : DeliveryStatus) : void
    }

    class RestaurantDashboard implements DeliveryObserver
    class PartnerAppNotifier implements DeliveryObserver
    class CustomerAppNotifier implements DeliveryObserver

    OrderServiceProxy o--> "1" RealOrderService
    RealOrderService ..> DeliveryTracker
    DeliveryTracker o--> "*" DeliveryObserver : notifies
}

' ═══════════════════════════════════════════════════════════
' ROW 2: STAGE 1 VALIDATION, REGIONAL TAX & DISPATCH
' ═══════════════════════════════════════════════════════════
package "part2_abstract_factory" as p2 {
    interface RegionFactory {
        + createTaxCalculator() : TaxCalculator
        + createPaymentProcessor() : PaymentProcessor
    }
    class IndiaRegionFactory implements RegionFactory
    class USRegionFactory implements RegionFactory

    interface TaxCalculator {
        + calculateTax(amount : double) : double
    }
    class GSTCalculator implements TaxCalculator
    class SalesTaxCalculator implements TaxCalculator

    interface PaymentProcessor {
        + charge(amount : double) : boolean
    }
    class UPIProcessor implements PaymentProcessor
    class CardProcessor implements PaymentProcessor

    IndiaRegionFactory ..> GSTCalculator : creates
    IndiaRegionFactory ..> UPIProcessor : creates
    USRegionFactory ..> SalesTaxCalculator : creates
    USRegionFactory ..> CardProcessor : creates
}

package "part3_chain_of_responsibility" as p3 {
    abstract class OrderHandler {
        # next : OrderHandler
        + setNext(next : OrderHandler) : OrderHandler
        + handle(order : Order) : boolean
    }
    class CouponValidationHandler extends OrderHandler
    class FraudCheckHandler extends OrderHandler
    class PaymentGatewayHandler extends OrderHandler

    OrderHandler "0..1" o--> "1" OrderHandler : next
}

package "part4_bridge" as p4 {
    abstract class Notification {
        # channel : NotificationChannel
        + {abstract} send(customerName : String) : void
    }
    class OrderConfirmedNotification extends Notification
    class OutForDeliveryNotification extends Notification

    interface NotificationChannel {
        + send(message : String) : void
    }
    class PushChannel implements NotificationChannel
    class SMSChannel implements NotificationChannel
    class EmailChannel implements NotificationChannel

    Notification o--> "1" NotificationChannel : bridges to
}

' ═══════════════════════════════════════════════════════════
' ROW 3: STAGE 2 FULFILLMENT, PRICING & MEMENTO
' ═══════════════════════════════════════════════════════════
package "part6_builder_prototype" as p6 {
    class CustomOrder {
        - customerName : String
        - baseDish : String
        - size : String
        - toppings : List<String>
        - basePrice : double
        + calculateSubtotal() : double
    }
    class CustomOrderBuilder {
        + setCustomerName(name) : CustomOrderBuilder
        + setBaseDish(dish, price) : CustomOrderBuilder
        + build() : CustomOrder
    }
    class MealDirector {
        + constructBurgerCombo(customer) : CustomOrder
        + constructFitnessMeal(customer) : CustomOrder
    }
    interface OrderPrototype {
        + cloneOrder() : OrderPrototype
    }
    class ReorderableMeal implements OrderPrototype {
        - originalOrderId : String
        - mealName : String
        - price : double
        + cloneOrder() : ReorderableMeal
    }
    class OrderRegistry {
        + registerPrototype(key, meal) : void
        + getClone(key) : ReorderableMeal
    }

    MealDirector ..> CustomOrderBuilder
    CustomOrderBuilder ..> CustomOrder
    OrderRegistry o--> "*" ReorderableMeal
}

package "part7_template_method" as p7 {
    abstract class OrderFulfillmentWorkflow {
        + fulfillOrder(orderId, customer, item, amount) : void
        + {abstract} getWorkflowName() : String
        # verifyOrder() : void
        # prepareFood() : void
        # qualityCheck() : void
        # {abstract} packageOrder() : void
        # {abstract} handoverOrder() : void
        # sendCompletionAlert() : void
    }
    class DeliveryFulfillmentWorkflow extends OrderFulfillmentWorkflow
    class TakeawayFulfillmentWorkflow extends OrderFulfillmentWorkflow
    class DineInFulfillmentWorkflow extends OrderFulfillmentWorkflow
}

package "part8_strategy" as p8 {
    interface DiscountStrategy {
        + applyDiscount(subtotal : double) : double
        + getStrategyName() : String
    }
    class PercentageDiscountStrategy implements DiscountStrategy
    class FlatDiscountStrategy implements DiscountStrategy
    class LoyaltyTierStrategy implements DiscountStrategy
    class NoDiscountStrategy implements DiscountStrategy

    class PricingContext {
        - strategy : DiscountStrategy
        + setStrategy(strategy : DiscountStrategy) : void
        + calculateFinalPrice(subtotal : double) : double
    }
    PricingContext o--> "1" DiscountStrategy
}

package "part9_memento" as p9 {
    class OrderMemento {
        - cartItems : List<String>
        - subtotal : double
        - timestamp : String
        + getCartItems() : List<String>
        + getSubtotal() : double
    }
    class CartOriginator {
        - items : List<String>
        - subtotal : double
        + addItem(name, price) : void
        + saveStateToMemento() : OrderMemento
        + restoreStateFromMemento(memento : OrderMemento) : void
    }
    class CartCaretaker {
        - undoStack : Deque<OrderMemento>
        - redoStack : Deque<OrderMemento>
        + saveState(originator : CartOriginator) : void
        + undo(originator : CartOriginator) : boolean
        + redo(originator : CartOriginator) : boolean
    }
    CartOriginator ..> OrderMemento
    CartCaretaker o--> "*" OrderMemento
}

' ═══════════════════════════════════════════════════════════
' ORCHESTRATION LINKS
' ═══════════════════════════════════════════════════════════
QuickBiteServer ..> OrderServiceProxy : Stage 1
QuickBiteServer ..> MealDirector : Stage 2
QuickBiteServer ..> OrderFulfillmentWorkflow : Stage 2
QuickBiteServer ..> PricingContext : Stage 2
QuickBiteServer ..> CartCaretaker : Stage 2
RealOrderService ..> QuickBiteSystem : processes
RealOrderService ..> OrderRepo : persists
QuickBiteSystem -down-> p2 : taxes & payments
QuickBiteSystem -down-> p3 : chains
QuickBiteSystem -down-> p4 : alerts

' ═══════════════════════════════════════════════════════════
' 3-TIER GRID CONSTRAINTS (CLASS-LEVEL)
' ═══════════════════════════════════════════════════════════
QuickBiteServer -[hidden]right-> QuickBiteSystem
QuickBiteSystem -[hidden]right-> OrderServiceProxy

OrderRepo -[hidden]down-> RegionFactory
Order -[hidden]down-> OrderHandler
DeliveryTracker -[hidden]down-> Notification

RegionFactory -[hidden]right-> OrderHandler
OrderHandler -[hidden]right-> Notification

TaxCalculator -[hidden]down-> MealDirector
OrderHandler -[hidden]down-> OrderFulfillmentWorkflow
Notification -[hidden]down-> PricingContext

CustomOrder -[hidden]right-> OrderFulfillmentWorkflow
OrderFulfillmentWorkflow -[hidden]right-> PricingContext
PricingContext -[hidden]right-> CartCaretaker

@enduml
"""

def render_diagram(name, puml_code, out_file):
    print(f"=== Rendering {name} to {out_file} ===")
    req = urllib.request.Request(
        "https://kroki.io/plantuml/png",
        data=puml_code.encode("utf-8"),
        headers={
            "Content-Type": "text/plain; charset=utf-8",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
        }
    )
    with urllib.request.urlopen(req, timeout=45) as resp:
        data = resp.read()
    
    im = Image.open(io.BytesIO(data))
    arr = np.array(im.convert("RGB"))
    right_col = arr[:, -5:, :]
    bottom_row = arr[-5:, :, :]
    is_right_cut = bool((right_col < 250).sum() > 50)
    is_bottom_cut = bool((bottom_row < 250).sum() > 50)
    
    print(f"-> {name} Size: {im.size}, Right Cut: {is_right_cut}, Bottom Cut: {is_bottom_cut}, Bytes: {len(data)}")
    
    with open(out_file, "wb") as f:
        f.write(data)
    
    puml_file = out_file.rsplit(".", 1)[0] + ".puml"
    with open(puml_file, "w", encoding="utf-8") as f:
        f.write(puml_code)
    print(f"-> Saved {out_file} and {puml_file}\n")
    return im.size, is_right_cut, is_bottom_cut

if __name__ == "__main__":
    os.makedirs("uml_diagram", exist_ok=True)
    render_diagram("Stage 1 Architecture (Parts 1-5 + Core)", stage1_puml, "uml_diagram/stage1_uml.png")
    render_diagram("Stage 2 Architecture (Parts 6-9 + Core)", stage2_puml, "uml_diagram/stage2_uml.png")
    render_diagram("Master Unified Architecture (Parts 1-9)", master_puml, "uml_diagram/UML_DIAGRAM.png")
    
    # Copy to artifacts directory for inspection
    artifact_dir = r"C:\Users\yashs\.gemini\antigravity-ide\brain\e1fe1027-5541-40c3-95b2-a33458413a44"
    for f in ["stage1_uml.png", "stage2_uml.png", "UML_DIAGRAM.png"]:
        src = os.path.join("uml_diagram", f)
        dst = os.path.join(artifact_dir, f)
        shutil.copy2(src, dst)
    print("All 3 files generated and copied successfully.")
