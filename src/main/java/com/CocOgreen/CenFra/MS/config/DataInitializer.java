package com.CocOgreen.CenFra.MS.config;

import com.CocOgreen.CenFra.MS.entity.Role;
import com.CocOgreen.CenFra.MS.entity.Store;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.entity.Category;
import com.CocOgreen.CenFra.MS.entity.Product;
import com.CocOgreen.CenFra.MS.enums.ProductStatus;
import com.CocOgreen.CenFra.MS.enums.RoleName;
import com.CocOgreen.CenFra.MS.repository.CategoryRepository;
import com.CocOgreen.CenFra.MS.repository.ProductRepository;
import com.CocOgreen.CenFra.MS.repository.RoleRepository;
import com.CocOgreen.CenFra.MS.repository.StoreRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run( String... args) {

        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByRoleName(roleName)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName(roleName);
                        return roleRepository.save(role);
                    });
        }

        createUser("admin", RoleName.ADMIN);
        createUser("manager", RoleName.MANAGER);
        createUser("coordinator", RoleName.SUPPLY_COORDINATOR);
        createUser("kitchen", RoleName.CENTRAL_KITCHEN_STAFF);
        createUser("store", RoleName.FRANCHISE_STORE_STAFF);
        createHardcodedStore();
        createHardcodedProducts();

        System.out.println("Data seeded");
    }

    private void createUser(String username, RoleName roleName) {

        if (userRepository.findByUserName(username).isPresent()) return;

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow();

        User user = new User();
        user.setUserName(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setIsActive(true);
        user.setRole(role);

        userRepository.save(user);
    }

    private void createHardcodedStore() {
        if (storeRepository.findByStoreName("Store Demo").isPresent()) {
            return;
        }

        User manager = userRepository.findByUserName("store")
                .orElseThrow(() -> new RuntimeException("store manager user not found"));

        Store store = new Store();
        store.setStoreName("Store Demo");
        store.setAddress("123 Demo Street");
        store.setPhone("0900000000");
        store.setIsActive(true);
        store.setManager(manager);
        storeRepository.save(store);
    }

    private void createHardcodedProducts() {
        Category meat = findOrCreateCategory("Meat");
        Category vegetable = findOrCreateCategory("Vegetables");

        createProductIfAbsent(
                "Chicken Breast",
                "kg",
                "https://via.placeholder.com/150",
                "Fresh chicken breast",
                ProductStatus.ACTIVE,
                meat
        );
        createProductIfAbsent(
                "Pork Belly",
                "kg",
                "https://via.placeholder.com/150",
                "Pork belly for braised dishes",
                ProductStatus.ACTIVE,
                meat
        );
        createProductIfAbsent(
                "Carrot",
                "kg",
                "https://via.placeholder.com/150",
                "Fresh carrot",
                ProductStatus.ACTIVE,
                vegetable
        );
    }

    private Category findOrCreateCategory(String categoryName) {
        return categoryRepository.findAll().stream()
                .filter(c -> categoryName.equalsIgnoreCase(c.getCategoryName()))
                .findFirst()
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setCategoryName(categoryName);
                    return categoryRepository.save(category);
                });
    }

    private void createProductIfAbsent(String productName,
                                       String unit,
                                       String imageUrl,
                                       String description,
                                       ProductStatus status,
                                       Category category) {
        boolean exists = productRepository.findAll().stream()
                .anyMatch(p -> productName.equalsIgnoreCase(p.getProductName()));
        if (exists) {
            return;
        }

        Product product = new Product();
        product.setProductName(productName);
        product.setUnit(unit);
        product.setImageUrl(imageUrl);
        product.setDescription(description);
        product.setStatus(status);
        product.setCategory(category);
        productRepository.save(product);
    }
}
