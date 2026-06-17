import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;


/**
 * Presentation Layer.
 * Manages user interaction via the terminal, including menus, input handling, and basic validation.
 */


public class InventoryUI {
    private ServiceController service;
    private Scanner scanner;
    private boolean isDataLoaded = false;
    public InventoryUI(ServiceController serviceController) {
        this.service = serviceController;
        scanner = new Scanner(System.in);
    }

    /**
     * Guides the user through initializing system data.
     * Continues to prompt the user until a valid input ('y' or 'n') is provided.
     */
    private Boolean enterPassword(){
        String adminPass="system_admin";
        for (int i=1;i<=5;i++) {
            System.out.print("Please enter your admin password (attempt" +i+"): ");
            String input=scanner.next();
            if(input.equals(adminPass)){return true;}
            System.out.println("Wrong password, Please try again");
        }return false;
    }
    public void initialData(){
        while(true){
        System.out.print("Enter y/n if you want to initial Data:");
        String input=scanner.next();
        if(input.equalsIgnoreCase("y")){
            DomainController domainController = new DomainController();
            DataInitializer.initialize(domainController);
            this.service = new ServiceController(domainController);
            isDataLoaded = true;
            return;
        }
        else if(input.equalsIgnoreCase("n")){
            return;
        }
        else{
            System.out.println("Wrong input, please try again");
        }}

    }
    private void LoadData(){
        if (!isDataLoaded){
            DomainController domainController = new DomainController();
            DataInitializer.initialize(domainController);
            this.service = new ServiceController(domainController);
            System.out.println("Data Loaded");
            isDataLoaded = true;}
        else if (isDataLoaded){
            System.out.print("Data already Loaded");
        }
    }

    public void start() {
        if (enterPassword()) {
            initialData();
            ShowMenu();

        }
        else {
            System.out.println("access denied. system locked");
        }

    }
    /**
     * Displays the main system menu and routes user choices to the appropriate sub-menus.
     */
    private void ShowMenu(){
        int choice = -1;
        while(choice!=11){
            System.out.println("\n========================================");
            System.out.println("       Inventory System - Main Menu       ");
            System.out.println("========================================");
            System.out.println("1. Add New Item");
            System.out.println("2. Update Inventory");
            System.out.println("3. Inventory Check");
            System.out.println("4. Create Inventory Report");
            System.out.println("5. Create Periodic Report For Damaged Items");
            System.out.println("6. Suppliers");
            System.out.println("7. Store Sales");
            System.out.println("8. Show System Alerts");
            System.out.println("9. Load Data");
            System.out.println("10. Categories");
            System.out.println("11. Exit the System");
            System.out.println("========================================");
            System.out.println("Please Enter Your Choice: ");

            try {
                choice = scanner.nextInt();
            } catch (Exception e){
                System.out.println("Wrong input, please try again");
                scanner.nextLine(); // cleans the scanner
                continue;
            }
            switch (choice){
                case 1: addProduct(); break;
                case 2: updateInventorySubMenu(); break;
                case 3: checkInventorySubMenu(); break;
                case 4: createInventoryReportSubMenu(); break;
                case 5: createPeriodicReport(); break;
                case 6: suppliersSubMenu(); break;
                case 7: salesSubMenu(); break;
                case 8: SystemAlerts(); break;
                case 9: LoadData(); break;
                case 10: categoriesSubMenu();break;
                case 11: System.out.println("Exit");break;
                default:System.out.println("Wrong input, please try again");
            }
        }
    }

    /**
     * Adds a new product to the inventory by gathering details from the user.
     * Includes exception handling for date formats and numeric inputs to prevent system crashes.
     */
    private void addProduct(){
        try{
        scanner.nextLine();
        System.out.println("Enter Product Name: ");
        String name = scanner.nextLine();
        System.out.println("Enter Brand (e.g.,: Tnuva, Osem): ");
        String brand = scanner.nextLine();
        System.out.println("Enter Category (e.g., Dairy, Cleaning): ");
        String category = scanner.nextLine();
        System.out.println("Enter subCategory (e.g., Milk, Shampoo): ");
        String subCategory = scanner.nextLine();
        System.out.println("Enter Size/Weight (e.g., 1L, 500g): ");
        String size = scanner.nextLine();
        System.out.print("Enter Aisle: ");
        String aisle = scanner.nextLine();
        System.out.print("Enter Shelf: ");
        String shelf = scanner.nextLine();
        System.out.print("Initial Quantity for Store: ");
        int shopQty = scanner.nextInt();
        System.out.print("Initial Quantity for Warehouse: ");
        int warQty = scanner.nextInt();
        System.out.print("Demand Rank (1-10): ");
        int rank = scanner.nextInt();
        System.out.print("Delivery Time from supplier (times: 1-7): ");
        int deliveryTime = scanner.nextInt();
        System.out.println("Enter Damaged amount: ");
        int damagedAmount = scanner.nextInt();
        System.out.print("Cost Price from Supplier: ");
        double cost = scanner.nextDouble();
        System.out.print("Enter Supplier ID: ");
        String sId = scanner.next().toUpperCase();
        scanner.nextLine();
        System.out.print("Enter Expiration Date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        LocalDate expDate = LocalDate.parse(dateStr);

        boolean success = service.productService.addProductBatch(name, brand, category, subCategory, size, shopQty, warQty, rank, deliveryTime, cost, expDate,damagedAmount, sId, aisle, shelf);
        if (success){System.out.println("\nProduct '" + name + "' was successfully added!");}}
        catch (DateTimeParseException e){
            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
        }
        catch (Exception e){
            System.out.println("Error: Invalid input. Please make sure to enter numbers where required.");
            if (scanner.hasNextLine()) scanner.nextLine();
        }

    }
    private void updateInventorySubMenu() {
        String id = "";
        Product p =  null;
        while(p==null){
            System.out.println("Please enter the Product ID OR 0 to return to main menu");
            id =scanner.next().toUpperCase();
            if (id.equals("0")) return;
            p= service.productService.getProductByID(id);
            if (p==null){
                System.out.println("Product not found in system, please try again");}
        }
        int subChoice = -1;
        while (subChoice!=4){
            System.out.println("Please choose from the following options");
            System.out.println("1. Update Current Amount");
            System.out.println("2. Update Damaged Products Amount");
            System.out.println("3. Update Product Status");
            System.out.println("4. Back To Main Menu");
            subChoice = scanner.nextInt();

            switch (subChoice){
                case 1: updateQty(id);  break;
                case 2: updateDamagedQty(id); break;
                case 3: updateStatus(id); break;
                case 4: System.out.println("Back To Main Menu"); break;
                default:System.out.println("Wrong input, please try again");
            }
        }
    }
    private void updateQty(String id){
        System.out.println("Please enter new amount in store");
        int store = scanner.nextInt();
        System.out.println("Please enter new quantity in warehouse");
        int warehouse = scanner.nextInt();
        boolean success = service.productService.updateProductQuantity(id, store, warehouse);
        if  (success){
            System.out.println("New Quantity Updated Successfully");
        } else {
            System.out.println("New Quantity Updated Failed"); }
    }
    private void updateDamagedQty(String id){
        System.out.println("Please enter amount of damaged products");
        int damaged = scanner.nextInt();
        boolean success = service.productService.updateDamaged(id, damaged);
        if  (success){
            System.out.println("Damaged Quantity Added Successfully");
        }
        else {
            System.out.println("Damaged Quantity Added Failed");
        }
    }
    private void updateStatus(String id) {
        System.out.println("Please enter new status: true for Active, false for Inactive");
        boolean status = scanner.nextBoolean();
        boolean success = service.productService.updateProductActiveStatus(id, status);
        if (success) {
            System.out.println("Product Status Updated Successfully");
        } else {
            System.out.println("Product Status Updated Failed");
        }
    }

    private void checkInventorySubMenu() {
        int subChoice = -1;
        while (subChoice != 4) {
            System.out.println("Please choose from the following options");
            System.out.println("1. Check Inventory By Product ID");
            System.out.println("2. Check Inventory By Sub Category");
            System.out.println("3. Check Inventory By Category");
            System.out.println("4. Back To Main Menu");

            try {
                subChoice = scanner.nextInt();
                scanner.nextLine();

                switch (subChoice) {
                    case 1: checkByID(); break;
                    case 2: checkBySubCat();break;
                    case 3: checkByCat();break;
                    case 4: System.out.println("Back To Main Menu"); break;
                    default: System.out.println("Wrong input, please try again");
                }
            } catch (Exception e) {
                System.out.println("Wrong input, please try again");
                scanner.nextLine();
            }
        }
    }
    private void checkByID() {
        System.out.println("Please enter the Product ID");
        String id = scanner.nextLine().toUpperCase();
        Product p = service.productService.getProductByID(id);
        if (p==null){
            System.out.println("Product not found in system, please try again");
        } else {
            p.displayProduct(p, true); } // לחשוב איך אנחנו רוצים שהיא תראה, גם צריכה להיות ממומשת פה
    }
    private void checkBySubCat() {
        System.out.println("Please enter the Sub Category");
        String subCategory = scanner.nextLine();
        List<Product> result = service.productService.getProductBySubCat(subCategory);
        if  (result.isEmpty()){
            System.out.println("Sub Category not found in system, please try again");
        } else {
            for (int i=0; i<result.size(); i++) {
                Product p = result.get(i);
                p.displayProduct(p, i==0);
            }
        }
    }

    private void checkByCat() {
        System.out.println("Please enter Category");
        String Category =  scanner.nextLine();
        List<Product> result =  service.productService.getProductByCat(Category);
        if  (result.isEmpty()) {
            System.out.println("Category not found in system, please try again");
        }  else {
            for (int i=0; i<result.size(); i++) {
                Product p = result.get(i);
                p.displayProduct(p, i == 0);
            } }
    }


    public void createInventoryReportSubMenu() {
        int subChoice = -1;

        while (subChoice != 5) {
            System.out.println("Please choose from the following options");
            System.out.println("1. Create Inventory Report By Product ID");
            System.out.println("2. Create Inventory Report By Sub Category");
            System.out.println("3. Create Inventory Report By Category");
            System.out.println("4. Create Inventory Report By Sub-Sub-Category (Size)");
            System.out.println("5. Back To Main Menu");

            try {
                subChoice = scanner.nextInt();

                switch (subChoice) {
                    case 1: reportByID(); break;
                    case 2: reportBySubCat(); break;
                    case 3: reportByCategory(); break;
                    case 4: reportBySubSubCategory(); break;
                    case 5: System.out.println("Back To Main Menu"); break;
                    default: System.out.println("Wrong input, please try again");
                }
            } catch (Exception e) {
                System.out.println("Wrong input, please try again");
                scanner.nextLine();
            }
        }
    }

    public void reportByID() {
        System.out.println("Please enter Product ID");
        String id = scanner.next().toUpperCase();
        Product p = service.reportService.getProductForReport(id);
        if (p!=null){
            System.out.println("\n" + "=".repeat(130));
            System.out.println("                                 INVENTORY STATUS REPORT - " + java.time.LocalDate.now());
            p.displayProduct(p, true);
            System.out.println("=".repeat(130));
        }
    }

    public void reportBySubCat() {
        System.out.println("Please enter Sub Category");
        scanner.nextLine();
        String subCategory = scanner.next();
        List<Product> result = service.reportService.getSubCategoryReport(subCategory);
        if (result.isEmpty()) {
            System.out.println("No products found for sub-category");
        } else {
            System.out.println("\n" + "=".repeat(130));
            System.out.println("                                 INVENTORY STATUS REPORT - " + java.time.LocalDate.now());
            System.out.println("=".repeat(130));

            for (int i = 0; i < result.size(); i++) {
                Product p = result.get(i);
                p.displayProduct(p, i == 0);
            }
            System.out.println("=".repeat(130));
        }
    }

    public void reportByCategory() {
        System.out.println("Please enter Category");
        String Category = scanner.next();
        List<Product> result = service.reportService.getCategoryReport(Category);
        if (result.isEmpty()) {
            System.out.println("No products found for sub-category");
        } else {
            System.out.println("\n" + "=".repeat(130));
            System.out.println("                                 INVENTORY STATUS REPORT - " + java.time.LocalDate.now());
            System.out.println("=".repeat(130));

            for (int i = 0; i < result.size(); i++) {
                Product p = result.get(i);
                p.displayProduct(p, i == 0);
            }
            System.out.println("=".repeat(130));
        }
    }

    public void reportBySubSubCategory() {
        System.out.println("Please enter Sub-Sub-Category (Size)");
        String size = scanner.next();

        List<Product> result = service.reportService.getSubSubCategoryReport(size);

        if (result == null || result.isEmpty()) {
            System.out.println("No products found for sub-sub-category (size)");
        } else {
            System.out.println("\n" + "=".repeat(130));
            System.out.println("                                 INVENTORY STATUS REPORT BY SIZE - " + LocalDate.now());
            System.out.println("=".repeat(130));

            for (int i = 0; i < result.size(); i++) {
                Product product = result.get(i);
                product.displayProduct(product, i == 0);
            }

            System.out.println("=".repeat(130));
        }
    }
    private void createPeriodicReport() {
        try {
            System.out.println("Please enter start date (YYYY-MM-DD): ");
            String start = scanner.next();
            LocalDate startDate = LocalDate.parse(start);

            System.out.println("Please enter end date (YYYY-MM-DD): ");
            String end = scanner.next();
            LocalDate endDate = LocalDate.parse(end);
            List<Product> report = service.reportService.getDamagedProductsReport(startDate, endDate);
            if (report.isEmpty()) {
                System.out.println("No damaged products found for this period.");
            } else  {
                System.out.println("\n--- DAMAGED PRODUCTS REPORT [" + start + " to " + end + "] ---");
                for (int i = 0; i < report.size(); i++) {
                    report.get(i).displayProduct(report.get(i), i == 0);
                }
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD (e.g., 2024-05-20).");
        }
    }

    public void suppliersSubMenu() {
        int subChoice = -1;
        while (subChoice != 4) {
            System.out.println("Please choose from the following options");
            System.out.println("1. Add Supplier");
            System.out.println("2. Update Supplier Discount");
            System.out.println("3. Show Suppliers");
            System.out.println("4. Back To Main Menu");

            try {
                subChoice = scanner.nextInt();
                switch (subChoice) {
                    case 1: addSupplier();  break;
                    case 2: updateSupDiscount();  break;
                    case 3: showSuppliers(); break; // todo
                    case 4: System.out.println("Back To Main Menu"); break;
                    default: System.out.println("Wrong input, please try again");
                }
            } catch (Exception e) {
                System.out.println("Wrong input, please try again");
                scanner.nextLine();
            }
        }
    }
    private  void addSupplier() {
        System.out.println("Please enter Supplier Name:");
        String name = scanner.next();
        System.out.println("Please enter Supplier ID:");
        String id = scanner.next();
        System.out.println("Please enter Supplier Discount Rate: (e.g. 10,5.5,17.0)");
        double rate = scanner.nextDouble();
        boolean success = service.supplierService.addNewSup(name, id, rate);
        if (success) {
            System.out.println("Supplier ID: " + id + " added successfully");
        } else {
            System.out.println("Failed to add supplier.");
        }
    }
    private void updateSupDiscount() {
        System.out.println("Please enter Supplier ID:");
        String id = scanner.next();
        System.out.println("Please enter Supplier Discount Rate:");
        double rate = scanner.nextDouble();
        boolean success = service.supplierService.updateSupDiscountRate(id, rate);
        if (success) {
        System.out.println("Supplier discount updated successfully");}
        else {
            System.out.println("Failed to update supplier discount.");
        }
    }
    private void showSuppliers(){
        List<Supplier> list = service.supplierService.getAllSuppliers();
        if (list.isEmpty()) {
            System.out.println("No suppliers found in the system.");
        } else {
            System.out.println("\n--- SUPPLIERS LIST ---");
            for (int i = 0; i < list.size(); i++) {
                list.get(i).displaySupplier(i == 0);
            }
            System.out.println("=".repeat(55));
    }
    }

    private void SystemAlerts() {
        List<Product> lowStock = service.reportService.getSystemAlerts();
        System.out.println("\nSYSTEM NOTIFICATIONS - LOW STOCK");
        if (lowStock.isEmpty()) {
            System.out.println("All items are well-stocked. No alerts.");
        } else {
            System.out.printf("%-10s | %-15s | %-8s | %-8s | %-10s\n","ID", "Name", "Stock", "Limit", "Location");
            System.out.println("-".repeat(70));
            for (Product p : lowStock) {
                System.out.printf("%-10s | %-15.15s | %-8d | %-8d | %-10s\n", p.getProductID(), p.getProductName(),
                        p.getTotalQuantity(), p.getMin_limit(), p.getAisle() + "-" + p.getShelf());
            }

            System.out.println("-".repeat(70));
            System.out.println("Total alerts found: " + lowStock.size());
        }
    }

    private void salesSubMenu(){
        int choice = -1;
        while (choice != 3) {
            System.out.println("1. Add New Sale");
            System.out.println("2. Display All Active Sales");
            System.out.println("3. Back to Main Menu");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        addNewSale();
                        break;
                    case 2: displayAllStoreSales();
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("Wrong input.");
                }
            } catch (Exception e) {
                System.out.println("Wrong input, please try again");
                scanner.nextLine();
            }
        }
        }
    private void displayAllStoreSales() {
        List<Sale> allSales = service.saleService.getAllSales();
        if (allSales.isEmpty()) {
            System.out.println("No active sales found in the system.");
        } else {
            System.out.println("\n--- STORE SALES REPORT ---");
            for (int i = 0; i < allSales.size(); i++) {
                allSales.get(i).displaySale(i == 0);
            }
            System.out.println("=".repeat(85));
    }
    }
    private void addNewSale(){
        try {
            System.out.print("Enter Sale ID: ");
            String id = scanner.next();
            scanner.nextLine();
            System.out.print("Enter Category: ");
            String category = scanner.nextLine();
            System.out.print("Enter Sub-Category: ");
            String subCat = scanner.nextLine();
            System.out.print("Enter Discount Rate: ");
            double rate = scanner.nextDouble();
            System.out.print("Enter Start Date (YYYY-MM-DD): ");
            LocalDate start = LocalDate.parse(scanner.next());
            System.out.print("Enter End Date (YYYY-MM-DD): ");
            LocalDate end = LocalDate.parse(scanner.next());
            boolean success = service.saleService.addNewSale(id, category, subCat, rate, start, end);
            if (success) {
                System.out.println("Sale was added and applied successfully!");
            } else {
                System.out.println("Failed to add sale.");
            }
    }catch (DateTimeParseException e){
        System.out.println("Invalid date format.");}
    catch (Exception e){
            System.out.println("Wrong input");
    }
        if (scanner.hasNextLine()) {scanner.nextLine();}

    }
    private void categoriesSubMenu() {

        int choice = -1;

        while (choice != 3) {

            System.out.println("\n===== Category Management =====");
            System.out.println("1. Add Category");
            System.out.println("2. Show All Categories");
            System.out.println("3. Back To Main Menu");
            System.out.println("Please Enter Your Choice:");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1: addCategory();break;
                    case 2: showAllCategories();break;
                    case 3: System.out.println("Back To Main Menu");break;
                    default: System.out.println("Wrong input, please try again");
                }

            } catch (Exception e) {
                System.out.println("Wrong input, please try again");
                scanner.nextLine();
            }
        }
    }
    private void addCategory() {
        System.out.println("Enter Category:");
        String categoryName = scanner.nextLine();
        System.out.println("Enter Sub-Category:");
        String subCategoryName = scanner.nextLine();
        System.out.println("Enter Sub-Sub-Category:");
        String subSubCategoryName = scanner.nextLine();
        boolean success = service.categoryService.addCategory(categoryName, subCategoryName, subSubCategoryName);
        if (success) {
            System.out.println("Category added successfully.");
        } else {
            System.out.println("Category was not added.");
        }
    }
    private void showAllCategories() {
        List<Category> categories = service.categoryService.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("No categories found in the system.");
            return;}
        System.out.println("\n===== Categories =====");
        for (Category category : categories) {System.out.println(category);}
    }

}
