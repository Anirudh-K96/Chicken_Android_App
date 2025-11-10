# Indian Chicken Center

Android app built with Kotlin, Jetpack Compose, and Room to help a chicken distribution business manage customers, orders, payments, procurement inventory, and a simple daily delivery route.

## Features
- **Customers** – add shops with optional lat/long, search, and see live balances (orders minus payments).
- **Orders** – capture per-customer orders with validation, show daily procurement & remaining inventory, and quick stats.
- **Payments** – record cash/UPI/other payments per customer and watch balances update instantly.
- **Routing MVP** – one-tap “Plan route” lists base → Bengaluru procurement → customers (nearest-neighbor/Haversine) when locations are available.
- **Procurement tracking** – default 5,000 kg per day with live remaining kg as orders are created.
- Material 3 UI polish: top app bar, cards, FABs, empty states, snackbar validation.

## Architecture
- **UI**: Jetpack Compose + Navigation (customers, orders, payments tabs). ViewModels expose `StateFlow`s to composables.
- **Persistence**: Room database (KSP) with entities for customers, orders, payments, procurements; shared `AppDatabase` singleton.
- **Domain helpers**: Repositories per entity, `FinanceUtils` for totals, `RoutePlanner` for naive routing, `DateUtils` for start-of-day logic.
- **State management**: MVVM with Coroutines/StateFlow; inventories & routes combine DAO flows.

## Getting Started
```bash
./gradlew assembleDebug
```
The project targets SDK 34 / minSdk 24 and uses JDK 11.

### Run Tests
```bash
./gradlew test
```
Includes converter + finance utility unit tests.

## Next Ideas
1. Integrate Hilt for dependency injection instead of manual factories.
2. Add customer detail screen with payment/order history, plus export/sharing.
3. Expand routing to honor delivery windows and integrate Maps/Directions APIs when keys are available.
