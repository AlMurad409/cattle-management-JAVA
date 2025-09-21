import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrameIntroo extends JFrame {

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(50, 120, 70); // Dark green
    private static final Color SECONDARY_COLOR = new Color(240, 245, 240); // Very light green
    private static final Color ACCENT_COLOR = new Color(80, 160, 100); // Medium green
    private static final Color TEXT_COLOR = new Color(60, 60, 60); // Dark gray

    // UI Components
    private JLabel titleLabel;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JButton historyButton;

    // Cow Section
    private JPanel cowSectionPanel;
    private JPanel cowListPanel;
    private LinkedHashMap<String, Double> cowData;
    private Map<String, JCheckBox> cowCheckBoxes;
    private Map<String, JSpinner> cowQuantitySpinners;

    // Goat Section
    private JPanel goatSectionPanel;
    private JPanel goatListPanel;
    private LinkedHashMap<String, Double> goatData;
    private Map<String, JCheckBox> goatCheckBoxes;
    private Map<String, JSpinner> goatQuantitySpinners;

    // Delivery Section
    private JPanel deliverySectionPanel;
    private JTextField deliveryDateTF;
    private JTextArea deliveryAddressTA;

    // Bill Section
    private JTextArea billTextArea;

    // Fonts
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 22);
    private Font primaryFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font monoFont = new Font("Consolas", Font.PLAIN, 13);

    public FrameIntroo() {
        super("Cattle Management");
        setupFrame();
        initializeData();
        createUI();
    }

    private void setupFrame() {
        this.setSize(1200, 800);
        this.setMinimumSize(new Dimension(1000, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(10, 10));
        this.getContentPane().setBackground(Color.WHITE);
    }

    private void createUI() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        titleLabel = new JLabel("Cattle Management", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(TEXT_COLOR);
        
        historyButton = createStyledButton("Bill History");
        historyButton.addActionListener(e -> showBillHistory());
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(historyButton, BorderLayout.EAST);
        this.add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel with scroll
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create a scroll pane for the main panel
        scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane, BorderLayout.CENTER);

        // Create selection panels in a horizontal row
        JPanel selectionRow = new JPanel(new GridLayout(1, 3, 15, 0));
        selectionRow.setBackground(Color.WHITE);
        selectionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        // Cow section
        cowCheckBoxes = new LinkedHashMap<>();
        cowQuantitySpinners = new LinkedHashMap<>();
        cowSectionPanel = createSelectionSection("Cow List", cowData, cowCheckBoxes, cowQuantitySpinners);
        selectionRow.add(cowSectionPanel);

        // Goat section
        goatCheckBoxes = new LinkedHashMap<>();
        goatQuantitySpinners = new LinkedHashMap<>();
        goatSectionPanel = createSelectionSection("Goat List", goatData, goatCheckBoxes, goatQuantitySpinners);
        selectionRow.add(goatSectionPanel);

        // Delivery section
        deliverySectionPanel = createDeliverySection();
        selectionRow.add(deliverySectionPanel);

        mainPanel.add(selectionRow);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Bill section at the bottom
        createBillSection();
        
        generateBill();
    }

    private JPanel createSelectionSection(String title, Map<String, Double> itemData, 
                                        Map<String, JCheckBox> checkBoxes, 
                                        Map<String, JSpinner> quantitySpinners) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(createSectionBorder(title));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        for (Map.Entry<String, Double> entry : itemData.entrySet()) {
            JPanel itemPanel = new JPanel(new BorderLayout(5, 0));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            // Quantity spinner on left
            JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            spinnerPanel.setBackground(Color.WHITE);
            
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
            spinner.setFont(primaryFont);
            ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(2);
            spinner.addChangeListener(e -> generateBill());
            quantitySpinners.put(entry.getKey(), spinner);
            spinnerPanel.add(spinner);

            JLabel qtyLabel = new JLabel("x");
            qtyLabel.setFont(primaryFont);
            spinnerPanel.add(qtyLabel);

            itemPanel.add(spinnerPanel, BorderLayout.WEST);

            // Checkbox in center
            JCheckBox checkBox = new JCheckBox(entry.getKey() + " - $" + String.format("%.2f", entry.getValue()));
            checkBox.setFont(primaryFont);
            checkBox.setBackground(Color.WHITE);
            checkBox.addItemListener(e -> generateBill());
            checkBoxes.put(entry.getKey(), checkBox);
            itemPanel.add(checkBox, BorderLayout.CENTER);

            listPanel.add(itemPanel);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(300, 300));
        
        sectionPanel.add(scrollPane);
        return sectionPanel;
    }

    private JPanel createDeliverySection() {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(createSectionBorder("Delivery Options"));

        // Delivery date
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        datePanel.setBackground(Color.WHITE);
        JLabel dateLabel = new JLabel("Delivery Date (DD-MM-YYYY):");
        dateLabel.setFont(primaryFont);
        datePanel.add(dateLabel);
        deliveryDateTF = new JTextField(12);
        styleTextField(deliveryDateTF);
        deliveryDateTF.addActionListener(e -> generateBill());
        datePanel.add(deliveryDateTF);
        sectionPanel.add(datePanel);

        // Delivery address
        JPanel addressPanel = new JPanel(new BorderLayout(5, 5));
        addressPanel.setBackground(Color.WHITE);
        JLabel addressLabel = new JLabel("Delivery Address:");
        addressLabel.setFont(primaryFont);
        addressPanel.add(addressLabel, BorderLayout.NORTH);
        
        deliveryAddressTA = new JTextArea(5, 20);
        styleTextArea(deliveryAddressTA);
        addressPanel.add(new JScrollPane(deliveryAddressTA), BorderLayout.CENTER);
        sectionPanel.add(addressPanel);

        return sectionPanel;
    }

    private void createBillSection() {
        JPanel billContainerPanel = new JPanel(new BorderLayout(10, 10));
        billContainerPanel.setBackground(Color.WHITE);
        billContainerPanel.setBorder(createSectionBorder("Generated Bill / Receipt"));
        billContainerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JButton generateBillButton = createStyledButton("Generate/Update Bill");
        generateBillButton.addActionListener(e -> {
            generateBill();
            saveBillToFile();
        });
        billContainerPanel.add(generateBillButton, BorderLayout.NORTH);

        billTextArea = new JTextArea(10, 80);
        billTextArea.setEditable(false);
        billTextArea.setFont(monoFont);
        billTextArea.setBackground(new Color(250, 250, 250));
        billTextArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(billTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        billContainerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(billContainerPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(boldFont);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
            new EmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleTextField(JTextField field) {
        field.setFont(primaryFont);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleTextArea(JTextArea area) {
        area.setFont(primaryFont);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private Border createSectionBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                title, 
                0, 0, boldFont, TEXT_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        );
    }

    private void initializeData() {
        cowData = new LinkedHashMap<>();
        cowData.put("Deshi Cow", 120000.00);
        cowData.put("Imported Cow", 200000.00);
        cowData.put("Angus Beef Cow", 355000.00);
        cowData.put("Brahman Cow", 230000.00);

        goatData = new LinkedHashMap<>();
        goatData.put("Deshi Goat", 30000.00);
        goatData.put("Mixed breed Goat", 25000.00);
        goatData.put("Alpine Goat", 28000.00);
        goatData.put("Imported Goat", 20000.00);
    }

    private void generateBill() {
        StringBuilder billContent = new StringBuilder();
        double totalAmount = 0.0;

        billContent.append("====== CATTLE MANAGEMENT BILL ======\n\n");
        billContent.append("------ SELECTED ITEMS ------\n");
        boolean itemSelected = false;

        // Process cows
        for (Map.Entry<String, JCheckBox> entry : cowCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                String itemName = entry.getKey();
                double price = cowData.get(itemName);
                int quantity = (Integer) cowQuantitySpinners.get(itemName).getValue();
                double itemTotal = price * quantity;
                billContent.append(String.format("%-25s %2d x $%-8.2f = $%.2f\n", 
                    itemName, quantity, price, itemTotal));
                totalAmount += itemTotal;
                itemSelected = true;
            }
        }

        // Process goats
        for (Map.Entry<String, JCheckBox> entry : goatCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                String itemName = entry.getKey();
                double price = goatData.get(itemName);
                int quantity = (Integer) goatQuantitySpinners.get(itemName).getValue();
                double itemTotal = price * quantity;
                billContent.append(String.format("%-25s %2d x $%-8.2f = $%.2f\n", 
                    itemName, quantity, price, itemTotal));
                totalAmount += itemTotal;
                itemSelected = true;
            }
        }

        if (!itemSelected) {
            billContent.append("No items selected.\n");
        }

        billContent.append("\n--- DELIVERY DETAILS ---\n");
        String deliveryDate = deliveryDateTF.getText().trim();
        billContent.append(deliveryDate.isEmpty() ? 
            "Delivery Date: Not Specified\n" : 
            "Delivery Date: " + deliveryDate + "\n");
        
        String deliveryAddress = deliveryAddressTA.getText().trim();
        billContent.append(deliveryAddress.isEmpty() ? 
            "Delivery Address: Not Specified\n" : 
            "Delivery Address: " + deliveryAddress + "\n");

        billContent.append("\n----------------------------------------\n");
        billContent.append(String.format("%-30s $%.2f\n", "TOTAL AMOUNT:", totalAmount));
        billContent.append("----------------------------------------\n");
        billContent.append("\nThank you for your order!\n");

        billTextArea.setText(billContent.toString());
        billTextArea.setCaretPosition(0);
    }

    private void saveBillToFile() {
        try {
            // Create bills directory if it doesn't exist
            File billsDir = new File("bills");
            if (!billsDir.exists()) {
                billsDir.mkdir();
            }

            // Generate filename with timestamp
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
            String fileName = "bills/bill_" + timeStamp + ".txt";
            
            // Write bill content to file
            try (PrintWriter out = new PrintWriter(fileName)) {
                out.println(billTextArea.getText());
            }
            
            JOptionPane.showMessageDialog(this, "Bill saved to " + fileName, "Bill Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBillHistory() {
        File billsDir = new File("bills");
        if (!billsDir.exists() || billsDir.listFiles() == null || billsDir.listFiles().length == 0) {
            JOptionPane.showMessageDialog(this, "No bill history found", "Bill History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create dialog to display bill history
        JDialog historyDialog = new JDialog(this, "Bill History", true);
        historyDialog.setSize(600, 400);
        historyDialog.setLayout(new BorderLayout());
        historyDialog.getContentPane().setBackground(Color.WHITE);

        // Create list model with bill filenames
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (File file : billsDir.listFiles()) {
            if (file.isFile() && file.getName().startsWith("bill_")) {
                listModel.addElement(file.getName());
            }
        }

        JList<String> billList = new JList<>(listModel);
        billList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billList.setBackground(Color.WHITE);
        billList.setFont(primaryFont);
        JScrollPane listScrollPane = new JScrollPane(billList);
        listScrollPane.setBorder(BorderFactory.createEmptyBorder());
        historyDialog.add(listScrollPane, BorderLayout.CENTER);

        // Add view button
        JButton viewButton = createStyledButton("View Selected Bill");
        viewButton.addActionListener(e -> {
            String selectedFile = billList.getSelectedValue();
            if (selectedFile != null) {
                displayBillContent(new File("bills/" + selectedFile));
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewButton);
        historyDialog.add(buttonPanel, BorderLayout.SOUTH);

        historyDialog.setLocationRelativeTo(this);
        historyDialog.setVisible(true);
    }

    private void displayBillContent(File billFile) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(billFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            JTextArea contentArea = new JTextArea(content.toString());
            contentArea.setEditable(false);
            contentArea.setFont(monoFont);
            contentArea.setBackground(new Color(250, 250, 250));

            JScrollPane scrollPane = new JScrollPane(contentArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            JOptionPane.showMessageDialog(this, scrollPane, "Bill: " + billFile.getName(), JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FrameIntroo frame = new FrameIntroo();
            frame.setVisible(true);
        });
    }
}