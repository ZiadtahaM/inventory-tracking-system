package com.mycompany.inventorymanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class InventoryManagementSystem {
    private JTextArea outputArea = new JTextArea(10, 50);

    // =============================================================================================
    // SINGLETON PATTERN: StockManagement
    // =============================================================================================
    static class StockManagement {
        private static final StockManagement INSTANCE = new StockManagement();
        private final Map<String, Integer> stock = new HashMap<>();

        private StockManagement() {}

        public static StockManagement getInstance() {
            return INSTANCE;
        }

        public void updateStock(String productId, int quantity) {
            stock.put(productId, stock.getOrDefault(productId, 0) + quantity);
        }

        public int getStockLevel(String productId) {
            return stock.getOrDefault(productId, 0);
        }

        @Override
        public String toString() {
            return stock.toString();
        }
    }

    // =============================================================================================
    // SINGLETON PATTERN: ReportingModule
    // =============================================================================================
    static class ReportingModule {
        private static final ReportingModule INSTANCE = new ReportingModule();
        private final List<String> salesRecords = new ArrayList<>();

        private ReportingModule() {}

        public static ReportingModule getInstance() {
            return INSTANCE;
        }

        public String generateSalesReport() {
            return "Sales Report: " + String.join(", ", salesRecords);
        }

        public String generateInventoryReport() {
            return "Inventory Report: " + StockManagement.getInstance().toString();
        }

        public void recordSale(String productId, int quantity) {
            salesRecords.add(productId + ":" + quantity);
        }
    }

    // =============================================================================================
    // FACTORY PATTERN: ProductFactory
    // =============================================================================================
    static class ProductFactory {
        public static Product createProduct(String category, String id, String name, double price) {
            return switch (category.toLowerCase()) {
                case "electronics" -> new Electronics(id, name, price);
                case "furniture" -> new Furniture(id, name, price);
                case "groceries" -> new Groceries(id, name, price);
                default -> throw new IllegalArgumentException("Unknown category");
            };
        }
    }

    // =============================================================================================
    // FACTORY PATTERN: SupplierFactory
    // =============================================================================================
    static class SupplierFactory {
        public static Supplier createSupplier(String type, String name, String contact) {
            return switch (type.toLowerCase()) {
                case "local" -> new LocalSupplier(name, contact);
                case "international" -> new InternationalSupplier(name, contact);
                default -> throw new IllegalArgumentException("Unknown supplier type");
            };
        }
    }

    // =============================================================================================
    // PROXY PATTERN: StockUpdateProxy
    // =============================================================================================
    static class StockUpdateProxy {
        public static boolean updateStock(String productId, int quantity, String user, String password) {
            if ("admin".equals(user) && "admin123".equals(password)) {
                StockManagement.getInstance().updateStock(productId, quantity);
                return true;
            }
            return false;
        }
    }

    // =============================================================================================
    // ADAPTER PATTERN: SupplierAdapter
    // =============================================================================================
    static class SupplierAdapter implements Supplier {
        private final ExternalSupplierAPI externalSupplier;

        public SupplierAdapter(ExternalSupplierAPI externalSupplier) {
            this.externalSupplier = externalSupplier;
        }

        @Override
        public String getName() {
            return externalSupplier.getSupplierName();
        }

        @Override
        public String getContact() {
            return externalSupplier.getSupplierEmail();
        }
    }

    // =============================================================================================
    // PROTOTYPE PATTERN: Product
    // =============================================================================================
    static abstract class Product implements Cloneable {
        protected String productId;
        protected String name;
        protected double price;

        public Product(String productId, String name, double price) {
            this.productId = productId;
            this.name = name;
            this.price = price;
        }

        @Override
        public Product clone() {
            try {
                return (Product) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            }
    }

    // =============================================================================================
    // CONCRETE PRODUCT CLASSES
    // =============================================================================================
    static class Electronics extends Product {
        public Electronics(String productId, String name, double price) {
            super(productId, name, price);
        }
    }

    static class Furniture extends Product {
        public Furniture(String productId, String name, double price) {
            super(productId, name, price);
        }
    }

    static class Groceries extends Product {
        public Groceries(String productId, String name, double price) {
            super(productId, name, price);
        }
    }

    // =============================================================================================
    // SUPPLIER INTERFACE AND IMPLEMENTATIONS
    // =============================================================================================
    interface Supplier {
        String getName();
        String getContact();
    }

    static class LocalSupplier implements Supplier {
        private final String name;
        private final String contact;

        public LocalSupplier(String name, String contact) {
            this.name = name;
            this.contact = contact;
        }

        @Override
        public String getName() { return name; }
        @Override
        public String getContact() { return contact; }
    }

    static class InternationalSupplier implements Supplier {
        private final String name;
        private final String contact;

        public InternationalSupplier(String name, String contact) {
            this.name = name;
            this.contact = contact;
        }

        @Override
        public String getName() { return name; }
        @Override
        public String getContact() { return contact; }
    }

    // =============================================================================================
    // EXTERNAL SUPPLIER API (SIMULATED)
    // =============================================================================================
    static class ExternalSupplierAPI {
        public String getSupplierName() { return "Global Corp"; }
        public String getSupplierEmail() { return "contact@globalcorp.com"; }
    }

    // =============================================================================================
    // GUI SETUP (SWING)
    // =============================================================================================
    public InventoryManagementSystem() {
        JFrame frame = new JFrame("Inventory Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Products", createProductPanel());
        tabbedPane.addTab("Suppliers", createSupplierPanel());
        tabbedPane.addTab("Stock", createStockPanel());
        tabbedPane.addTab("Reports", createReportPanel());

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Electronics", "Furniture", "Groceries"});

        JTextField cloneIdField = new JTextField();
        JButton cloneButton = new JButton("Clone Product");
        cloneButton.addActionListener(e -> outputArea.append("Cloned product: " + cloneIdField.getText() + "\n"));

        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(e -> {
            try {
                Product product = ProductFactory.createProduct(
                    (String) categoryBox.getSelectedItem(),
                    idField.getText(),
                    nameField.getText(),
                    Double.parseDouble(priceField.getText())
                );
                outputArea.append("Added: " + product.name + "\n");
            } catch (NumberFormatException ex) {
                outputArea.append("Invalid price format\n");
            }
        });

        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryBox);
        inputPanel.add(new JLabel("Clone Product ID:"));
        inputPanel.add(cloneIdField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cloneButton);

        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Local", "International"});

        JButton addButton = new JButton("Add Supplier");
        addButton.addActionListener(e -> {
            Supplier supplier = SupplierFactory.createSupplier(
                (String) typeBox.getSelectedItem(),
                nameField.getText(),
                contactField.getText()
            );
            outputArea.append("Added supplier: " + supplier.getName() + "\n");
        });

        JButton integrateButton = new JButton("Integrate External Supplier");
        integrateButton.addActionListener(e -> {
            SupplierAdapter adapter = new SupplierAdapter(new ExternalSupplierAPI());
            outputArea.append("Integrated: " + adapter.getName() + "\n");
        });

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Contact:"));
        inputPanel.add(contactField);
        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(typeBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(integrateButton);

        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField productIdField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField userField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton updateButton = new JButton("Update Stock");
        updateButton.addActionListener(e -> {
            try {
                boolean success = StockUpdateProxy.updateStock(
                    productIdField.getText(),
                    Integer.parseInt(quantityField.getText()),
                    userField.getText(),
                    new String(passwordField.getPassword())
                );
                outputArea.append(success ? "Stock updated\n" : "Access denied\n");
            } catch (NumberFormatException ex) {
                outputArea.append("Invalid quantity format\n");
            }
        });

        inputPanel.add(new JLabel("Product ID:"));
        inputPanel.add(productIdField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("User:"));
        inputPanel.add(userField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);

        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton salesReportButton = new JButton("Generate Sales Report");
        salesReportButton.addActionListener(e -> outputArea.setText(ReportingModule.getInstance().generateSalesReport()));

        JButton inventoryReportButton = new JButton("Generate Inventory Report");
        inventoryReportButton.addActionListener(e -> outputArea.setText(ReportingModule.getInstance().generateInventoryReport()));

        buttonPanel.add(salesReportButton);
        buttonPanel.add(inventoryReportButton);

        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryManagementSystem());
    }
}