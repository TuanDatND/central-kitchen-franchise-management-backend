package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.entity.Category;
import com.CocOgreen.CenFra.MS.entity.ExportItem;
import com.CocOgreen.CenFra.MS.entity.ExportNote;
import com.CocOgreen.CenFra.MS.entity.InventoryReceipt;
import com.CocOgreen.CenFra.MS.entity.InventoryTransaction;
import com.CocOgreen.CenFra.MS.entity.ManufacturingOrder;
import com.CocOgreen.CenFra.MS.entity.OrderDetail;
import com.CocOgreen.CenFra.MS.entity.Product;
import com.CocOgreen.CenFra.MS.entity.ProductBatch;
import com.CocOgreen.CenFra.MS.entity.ReceiptItem;
import com.CocOgreen.CenFra.MS.entity.RefreshToken;
import com.CocOgreen.CenFra.MS.entity.Role;
import com.CocOgreen.CenFra.MS.entity.Store;
import com.CocOgreen.CenFra.MS.entity.StoreOrder;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.enums.BatchStatus;
import com.CocOgreen.CenFra.MS.enums.CategoryStatus;
import com.CocOgreen.CenFra.MS.enums.ExportStatus;
import com.CocOgreen.CenFra.MS.enums.ManuOrderStatus;
import com.CocOgreen.CenFra.MS.enums.ProductStatus;
import com.CocOgreen.CenFra.MS.enums.ReceiptStatus;
import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.enums.StoreOrderStatus;
import com.CocOgreen.CenFra.MS.enums.TransactionType;
import com.CocOgreen.CenFra.MS.repository.CategoryRepository;
import com.CocOgreen.CenFra.MS.repository.ExportNoteRepositoty;
import com.CocOgreen.CenFra.MS.repository.InventoryReceiptRepository;
import com.CocOgreen.CenFra.MS.repository.InventoryTransactionRepository;
import com.CocOgreen.CenFra.MS.repository.ManufacturingOrderRepository;
import com.CocOgreen.CenFra.MS.repository.ProductBatchRepository;
import com.CocOgreen.CenFra.MS.repository.ProductRepository;
import com.CocOgreen.CenFra.MS.repository.RefreshTokenRepository;
import com.CocOgreen.CenFra.MS.repository.RoleRepository;
import com.CocOgreen.CenFra.MS.repository.StoreOrderRepository;
import com.CocOgreen.CenFra.MS.repository.StoreRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DevelopmentDataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ManufacturingOrderRepository manufacturingOrderRepository;
    private final ProductBatchRepository productBatchRepository;
    private final InventoryReceiptRepository inventoryReceiptRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final ExportNoteRepositoty exportNoteRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedAllTables();
        log.info("Da seed du lieu test local. Tai khoan mau: admin/admin123, supply/supply123, manager/manager123, kitchen/kitchen123, staff.hn.01/staff123");
    }

    private void seedAllTables() {
        Role adminRole = getOrCreateRole(RoleName.ADMIN);
        Role managerRole = getOrCreateRole(RoleName.MANAGER);
        Role supplyRole = getOrCreateRole(RoleName.SUPPLY_COORDINATOR);
        Role kitchenRole = getOrCreateRole(RoleName.CENTRAL_KITCHEN_STAFF);
        Role storeStaffRole = getOrCreateRole(RoleName.FRANCHISE_STORE_STAFF);

        Store storeHaNoi = getOrCreateStore("Cửa hàng Hà Nội", "123 Trần Duy Hưng, Hà Nội", "0901000001", true);
        Store storeSaiGon = getOrCreateStore("Cửa hàng Sài Gòn", "456 Nguyễn Huệ, TP.HCM", "0901000002", true);

        User admin = getOrCreateUser("admin", "admin123", "Quản trị hệ thống", "admin@cenfra.local", adminRole, null);
        User manager = getOrCreateUser("manager", "manager123", "Quản lý hệ thống", "manager@cenfra.local", managerRole, null);
        User supply = getOrCreateUser("supply", "supply123", "Điều phối kho", "supply@cenfra.local", supplyRole, null);
        User kitchen = getOrCreateUser("kitchen", "kitchen123", "Nhân viên bếp trung tâm", "kitchen@cenfra.local", kitchenRole, null);
        User staffHaNoi01 = getOrCreateUser("staff.hn.01", "staff123", "Nhân viên HN 01", "staff.hn.01@cenfra.local", storeStaffRole, storeHaNoi);
        getOrCreateUser("staff.hn.02", "staff123", "Nhân viên HN 02", "staff.hn.02@cenfra.local", storeStaffRole, storeHaNoi);
        getOrCreateUser("staff.sg.01", "staff123", "Nhân viên SG 01", "staff.sg.01@cenfra.local", storeStaffRole, storeSaiGon);

        Category drinks = getOrCreateCategory("Đồ uống", CategoryStatus.ACTIVE);
        Category toppings = getOrCreateCategory("Topping", CategoryStatus.ACTIVE);

        Product traSua = getOrCreateProduct("Trà sữa trân châu", "ly", "Đồ uống bán chạy", ProductStatus.ACTIVE, drinks);
        Product matcha = getOrCreateProduct("Matcha latte", "ly", "Đồ uống matcha", ProductStatus.ACTIVE, drinks);
        Product tranChau = getOrCreateProduct("Trân châu đen", "kg", "Topping dùng chung", ProductStatus.ACTIVE, toppings);

        ManufacturingOrder manuTraSua = manufacturingOrderRepository.findByOrderCode("MO-202603-001")
                .orElseGet(() -> manufacturingOrderRepository.save(manufacturingOrder(
                        "MO-202603-001",
                        Instant.now().minusSeconds(172800),
                        Instant.now().minusSeconds(86400),
                        ManuOrderStatus.COMPLETED,
                        kitchen,
                        traSua,
                        300
                )));

        ManufacturingOrder manuMatcha = manufacturingOrderRepository.findByOrderCode("MO-202603-002")
                .orElseGet(() -> manufacturingOrderRepository.save(manufacturingOrder(
                        "MO-202603-002",
                        Instant.now().minusSeconds(86400),
                        Instant.now().plusSeconds(86400),
                        ManuOrderStatus.COOKING,
                        kitchen,
                        matcha,
                        200
                )));

        ProductBatch batchTraSuaA = getOrCreateBatch("BATCH-TS-001", traSua, manuTraSua, 180, 120, LocalDate.now().minusDays(2), LocalDate.now().plusDays(5), BatchStatus.AVAILABLE);
        ProductBatch batchTraSuaB = getOrCreateBatch("BATCH-TS-002", traSua, manuTraSua, 120, 120, LocalDate.now().minusDays(1), LocalDate.now().plusDays(6), BatchStatus.AVAILABLE);
        ProductBatch batchMatcha = getOrCreateBatch("BATCH-MA-001", matcha, manuMatcha, 200, 170, LocalDate.now().minusDays(1), LocalDate.now().plusDays(7), BatchStatus.AVAILABLE);
        ProductBatch batchTranChau = getOrCreateBatch("BATCH-TC-001", tranChau, null, 100, 95, LocalDate.now().minusDays(3), LocalDate.now().plusDays(20), BatchStatus.AVAILABLE);

        InventoryReceipt receipt = inventoryReceiptRepository.findByReceiptCode("IR-202603-001")
                .orElseGet(() -> inventoryReceiptRepository.save(inventoryReceipt("IR-202603-001", Instant.now().minusSeconds(90000), ReceiptStatus.COMPLETED, supply)));

        if (receipt.getReceiptItems() == null || receipt.getReceiptItems().isEmpty()) {
            receipt.setReceiptItems(new ArrayList<>(List.of(
                    receiptItem(receipt, batchTraSuaA, 180),
                    receiptItem(receipt, batchTraSuaB, 120),
                    receiptItem(receipt, batchMatcha, 200),
                    receiptItem(receipt, batchTranChau, 100)
            )));
            inventoryReceiptRepository.save(receipt);
        }

        StoreOrder pendingOrder = storeOrderRepository.findByOrderCode("SO-202603-001").orElseGet(() -> {
            StoreOrder order = storeOrder("SO-202603-001", storeHaNoi, Date.from(Instant.now().plusSeconds(86400)), StoreOrderStatus.PENDING);
            order.addOrderDetail(orderDetail(order, traSua, 20));
            order.addOrderDetail(orderDetail(order, matcha, 10));
            return storeOrderRepository.save(order);
        });

        StoreOrder approvedOrder = storeOrderRepository.findByOrderCode("SO-202603-002").orElseGet(() -> {
            StoreOrder order = storeOrder("SO-202603-002", storeSaiGon, Date.from(Instant.now().plusSeconds(172800)), StoreOrderStatus.APPROVED);
            order.addOrderDetail(orderDetail(order, traSua, 30));
            order.addOrderDetail(orderDetail(order, tranChau, 5));
            return storeOrderRepository.save(order);
        });

        storeOrderRepository.findByOrderCode("SO-202603-003").orElseGet(() -> {
            StoreOrder order = storeOrder("SO-202603-003", storeHaNoi, Date.from(Instant.now().plusSeconds(259200)), StoreOrderStatus.CANCELLED);
            order.addOrderDetail(orderDetail(order, matcha, 8));
            return storeOrderRepository.save(order);
        });

        ExportNote exportNote = exportNoteRepository.findByExportCode("EX-202603-001").orElseGet(() -> {
            ExportNote note = exportNote("EX-202603-001", approvedOrder, supply, ExportStatus.SHIPPED);
            note.setItems(new ArrayList<>(List.of(
                    exportItem(note, batchTraSuaA, 30),
                    exportItem(note, batchTranChau, 5)
            )));
            return exportNoteRepository.save(note);
        });

        if (inventoryTransactionRepository.count() == 0) {
            inventoryTransactionRepository.save(transaction(batchTraSuaA, TransactionType.IMPORT, 180, receipt.getReceiptCode(), "Nhập kho lô trà sữa 1"));
            inventoryTransactionRepository.save(transaction(batchTraSuaB, TransactionType.IMPORT, 120, receipt.getReceiptCode(), "Nhập kho lô trà sữa 2"));
            inventoryTransactionRepository.save(transaction(batchMatcha, TransactionType.IMPORT, 200, receipt.getReceiptCode(), "Nhập kho lô matcha"));
            inventoryTransactionRepository.save(transaction(batchTranChau, TransactionType.IMPORT, 100, receipt.getReceiptCode(), "Nhập kho lô trân châu"));
            inventoryTransactionRepository.save(transaction(batchTraSuaA, TransactionType.EXPORT, -30, exportNote.getExportCode(), "Xuất cho đơn SO-202603-002"));
            inventoryTransactionRepository.save(transaction(batchTranChau, TransactionType.EXPORT, -5, exportNote.getExportCode(), "Xuất topping cho đơn SO-202603-002"));
        }

        if (refreshTokenRepository.count() == 0) {
            refreshTokenRepository.save(refreshToken(admin,
                    "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
                    Instant.now().plusSeconds(604800),
                    false));
            refreshTokenRepository.save(refreshToken(staffHaNoi01,
                    "abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789",
                    Instant.now().plusSeconds(259200),
                    true));
        }
    }

    private Role getOrCreateRole(RoleName roleName) {
        return roleRepository.findByRoleName(roleName).orElseGet(() -> roleRepository.save(role(roleName)));
    }

    private Store getOrCreateStore(String name, String address, String phone, Boolean isActive) {
        Store store = storeRepository.findByStoreName(name).orElseGet(Store::new);
        store.setStoreName(name);
        store.setAddress(address);
        store.setPhone(phone);
        store.setIsActive(isActive);
        return storeRepository.save(store);
    }

    private User getOrCreateUser(String username, String rawPassword, String fullName, String email, Role role, Store store) {
        User user = userRepository.findByUserName(username).orElseGet(User::new);
        user.setUserName(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setStore(store);
        user.setIsActive(true);
        if (user.getUserId() == null) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        return userRepository.save(user);
    }

    private Category getOrCreateCategory(String name, CategoryStatus status) {
        Category category = categoryRepository.findAll().stream()
                .filter(item -> name.equals(item.getCategoryName()))
                .findFirst()
                .orElseGet(Category::new);
        category.setCategoryName(name);
        category.setStatus(status);
        return categoryRepository.save(category);
    }

    private Product getOrCreateProduct(String name, String unit, String description, ProductStatus status, Category category) {
        Product product = productRepository.findAll().stream()
                .filter(item -> name.equals(item.getProductName()))
                .findFirst()
                .orElseGet(Product::new);
        product.setProductName(name);
        product.setUnit(unit);
        product.setDescription(description);
        product.setImageUrl("https://example.com/" + Math.abs(name.hashCode()));
        product.setStatus(status);
        product.setCategory(category);
        return productRepository.save(product);
    }

    private ProductBatch getOrCreateBatch(String code, Product product, ManufacturingOrder manufacturingOrder,
                                          Integer initialQuantity, Integer currentQuantity,
                                          LocalDate manufacturingDate, LocalDate expiryDate, BatchStatus status) {
        return productBatchRepository.findByBatchCode(code).orElseGet(() -> productBatchRepository.save(productBatch(
                code, product, manufacturingOrder, initialQuantity, currentQuantity, manufacturingDate, expiryDate, status
        )));
    }

    private Role role(RoleName roleName) {
        Role role = new Role();
        role.setRoleName(roleName);
        return role;
    }

    private ManufacturingOrder manufacturingOrder(String orderCode, Instant startDate, Instant endDate,
                                                  ManuOrderStatus status, User createdBy, Product product,
                                                  Integer quantityPlanned) {
        ManufacturingOrder order = new ManufacturingOrder();
        order.setOrderCode(orderCode);
        order.setStartDate(startDate);
        order.setEndDate(endDate);
        order.setStatus(status);
        order.setCreatedBy(createdBy);
        order.setProduct(product);
        order.setQuantityPlanned(quantityPlanned);
        return order;
    }

    private ProductBatch productBatch(String batchCode, Product product, ManufacturingOrder manufacturingOrder,
                                      Integer initialQuantity, Integer currentQuantity,
                                      LocalDate manufacturingDate, LocalDate expiryDate, BatchStatus status) {
        ProductBatch batch = new ProductBatch();
        batch.setBatchCode(batchCode);
        batch.setProduct(product);
        batch.setManufacturingOrder(manufacturingOrder);
        batch.setInitialQuantity(initialQuantity);
        batch.setCurrentQuantity(currentQuantity);
        batch.setManufacturingDate(manufacturingDate);
        batch.setExpiryDate(expiryDate);
        batch.setStatus(status);
        return batch;
    }

    private InventoryReceipt inventoryReceipt(String code, Instant receiptDate, ReceiptStatus status, User createdBy) {
        InventoryReceipt receipt = new InventoryReceipt();
        receipt.setReceiptCode(code);
        receipt.setReceiptDate(receiptDate);
        receipt.setStatus(status);
        receipt.setCreatedBy(createdBy);
        return receipt;
    }

    private ReceiptItem receiptItem(InventoryReceipt receipt, ProductBatch batch, Integer quantity) {
        ReceiptItem item = new ReceiptItem();
        item.setInventoryReceipt(receipt);
        item.setProductBatch(batch);
        item.setQuantity(quantity);
        return item;
    }

    private StoreOrder storeOrder(String code, Store store, Date deliveryDate, StoreOrderStatus status) {
        StoreOrder order = new StoreOrder();
        order.setOrderCode(code);
        order.setStore(store);
        order.setOrderDate(new Date());
        order.setDeliveryDate(deliveryDate);
        order.setStatus(status);
        order.setOrderDetails(new ArrayList<>());
        return order;
    }

    private OrderDetail orderDetail(StoreOrder storeOrder, Product product, Integer quantity) {
        OrderDetail detail = new OrderDetail();
        detail.setStoreOrder(storeOrder);
        detail.setProduct(product);
        detail.setQuantity(quantity);
        return detail;
    }

    private ExportNote exportNote(String code, StoreOrder order, User createdBy, ExportStatus status) {
        ExportNote note = new ExportNote();
        note.setExportCode(code);
        note.setStoreOrder(order);
        note.setCreatedBy(createdBy);
        note.setExportDate(OffsetDateTime.now(ZoneOffset.UTC));
        note.setStatus(status);
        return note;
    }

    private ExportItem exportItem(ExportNote note, ProductBatch batch, Integer quantity) {
        ExportItem item = new ExportItem();
        item.setExportNote(note);
        item.setProductBatch(batch);
        item.setQuantity(quantity);
        return item;
    }

    private InventoryTransaction transaction(ProductBatch batch, TransactionType type, Integer quantity, String referenceCode, String note) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductBatch(batch);
        transaction.setTransactionType(type);
        transaction.setQuantity(quantity);
        transaction.setReferenceCode(referenceCode);
        transaction.setTransactionDate(OffsetDateTime.now(ZoneOffset.UTC));
        transaction.setNote(note);
        return transaction;
    }

    private RefreshToken refreshToken(User user, String tokenHash, Instant expiresAt, boolean revoked) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setRevoked(revoked);
        return refreshToken;
    }
}
