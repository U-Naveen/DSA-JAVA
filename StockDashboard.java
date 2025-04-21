package stockTrackerProject;

import java.util.*;
import java.util.List;
import java.io.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class StockNode {
    String stockName;
    int quantity;
    double averagePrice;
    int height;
    StockNode left, right;

    StockNode(String stockName, int quantity, double averagePrice) {
        this.stockName = stockName;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
        this.height = 1;
    }
}

class StockAVLTree {
    StockNode root;

    int height(StockNode node) {
        return (node == null) ? 0 : node.height;
    }

    int getBalance(StockNode node) {
        return (node == null) ? 0 : height(node.left) - height(node.right);
    }

    StockNode rightRotate(StockNode y) {
        StockNode x = y.left;
        StockNode T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return x;
    }

    StockNode leftRotate(StockNode x) {
        StockNode y = x.right;
        StockNode T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        return y;
    }

    StockNode insert(StockNode node, String stockName, int quantity, double price) {
        if (node == null) return new StockNode(stockName, quantity, price);
        int cmp = stockName.compareTo(node.stockName);
        if (cmp < 0) node.left = insert(node.left, stockName, quantity, price);
        else if (cmp > 0) node.right = insert(node.right, stockName, quantity, price);
        else {
            double totalCost = node.averagePrice * odne.quantity + price * quantity;
            node.quantity += quantity;
            node.averagePrice = totalCost / node.quantity;
            return node;
        }
        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);
        if (balance > 1 && stockName.compareTo(node.left.stockName) < 0) return rightRotate(node);
        if (balance < -1 && stockName.compareTo(node.right.stockName) > 0) return leftRotate(node);
        if (balance > 1 && stockName.compareTo(node.left.stockName) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && stockName.compareTo(node.right.stockName) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    StockNode delete(StockNode node, String stockName, int quantity) {
        if (node == null) return null;
        int cmp = stockName.compareTo(node.stockName);
        if (cmp < 0) node.left = delete(node.left, stockName, quantity);
        else if (cmp > 0) node.right = delete(node.right, stockName, quantity);
        else {
            if (node.quantity > quantity) {
                node.quantity -= quantity;
                return node;
            } else if (node.quantity == quantity) {
                if (node.left == null || node.right == null) {
                    StockNode temp = (node.left != null) ? node.left : node.right;
                    node = (temp == null) ? null : temp;
                } else {
                    StockNode temp = minValueNode(node.right);
                    node.stockName = temp.stockName;
                    node.quantity = temp.quantity;
                    node.averagePrice = temp.averagePrice;
                    node.right = delete(node.right, temp.stockName, temp.quantity);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Not enough stock to sell.");
                return node;
            }
        }
        if (node == null) return null;
        node.height = Math.max(height(node.left), height(node.right)) + 1;
        int balance = getBalance(node);
        if (balance > 1 && getBalance(node.left) >= 0) return rightRotate(node);
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && getBalance(node.right) <= 0) return leftRotate(node);
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    StockNode minValueNode(StockNode node) {
        StockNode current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    StockNode search(StockNode node, String stockName) {
        if (node == null || node.stockName.equals(stockName)) return node;
        int cmp = stockName.compareTo(node.stockName);
        return (cmp < 0) ? search(node.left, stockName) : search(node.right, stockName);
    }

    void inOrder(StockNode node, StringBuilder sb) {
        if (node != null) {
            inOrder(node.left, sb);
            sb.append("Stock: ").append(node.stockName)
              .append(" | Quantity: ").append(node.quantity)
              .append(" | Avg Price: ").append(node.averagePrice).append("\n");
            inOrder(node.right, sb);
        }
    }

    public void insert(String stockName, int quantity, double price) {
        root = insert(root, stockName, quantity, price);
    }

    public void delete(String stockName, int quantity) {
        root = delete(root, stockName, quantity);
    }

    public StockNode search(String stockName) {
        return search(root, stockName);
    }

    public String getPortfolioString() {
        StringBuilder sb = new StringBuilder();
        inOrder(root, sb);
        return sb.toString();
    }

    public void exportToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Stock Name,Quantity,Average Price");
            exportToCSV(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportToCSV(StockNode node, PrintWriter writer) {
        if (node != null) {
            exportToCSV(node.left, writer);
            writer.println(node.stockName + "," + node.quantity + "," + node.averagePrice);
            exportToCSV(node.right, writer);
        }
    }
}

class Trade {
    String stockName;
    int quantity;
    double profit;

    Trade(String stockName, int quantity, double profit) {
        this.stockName = stockName;
        this.quantity = quantity;
        this.profit = profit;
    }

    public String getStockName() {
        return stockName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getProfit() {
        return profit;
    }
}

class PerformanceHeap {
    private final List<Trade> heap = new ArrayList<>();

    public void insert(String stockName, int quantity, double profit) {
        Trade trade = new Trade(stockName, quantity, profit);
        heap.add(trade);
        int i = heap.size() - 1;
        while (i > 0 && heap.get(parent(i)).profit < heap.get(i).profit) {
            Collections.swap(heap, i, parent(i));
            i = parent(i);
        }
    }

    public List<Trade> getSortedTrades() {
        List<Trade> sorted = new ArrayList<>(heap);
        sorted.sort((a, b) -> Double.compare(b.profit, a.profit));
        return sorted;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }
}

public class StockDashboard extends JFrame {
    private final StockAVLTree portfolioTree = new StockAVLTree();
    private final PerformanceHeap performanceHeap = new PerformanceHeap();
    private final JTextArea outputArea = new JTextArea();
    private final JTextField stockField = new JTextField();
    private final JTextField quantityField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final String username;
    private final String portfolioFile;

    public StockDashboard(String username) {
        this.username = username;
        this.portfolioFile = username + "_portfolio.csv";
        loadPortfolioFromCSV();

        setTitle("Stock Dashboard - Welcome " + username);
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Stock:"));
        inputPanel.add(stockField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);

        JButton buyBtn = new JButton("Buy");
        JButton sellBtn = new JButton("Sell");
        inputPanel.add(buyBtn);
        inputPanel.add(sellBtn);
        add(inputPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton viewPortfolioBtn = new JButton("View Portfolio");
        JButton viewHeapBtn = new JButton("Top Trades");
        JButton logoutBtn = new JButton("Logout");
        JButton exportCSVBtn = new JButton("Export Portfolio to CSV");
        JButton viewHistoryBtn = new JButton("View Transaction History");


        bottomPanel.add(viewPortfolioBtn);
        bottomPanel.add(viewHeapBtn);
        bottomPanel.add(viewHistoryBtn); 
        bottomPanel.add(exportCSVBtn);
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        buyBtn.addActionListener(e -> buyStock());
        sellBtn.addActionListener(e -> sellStock());
        viewPortfolioBtn.addActionListener(e -> outputArea.setText(portfolioTree.getPortfolioString()));
        viewHeapBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (Trade t : performanceHeap.getSortedTrades()) {
                sb.append("Stock: ").append(t.getStockName())
                  .append(" | Qty: ").append(t.getQuantity())
                  .append(" | Profit: ").append(t.getProfit()).append("\n");
            }
            outputArea.setText(sb.toString());
        });
        exportCSVBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                portfolioTree.exportToCSV(file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Portfolio exported to " + file.getName());
            }
        });
        viewHistoryBtn.addActionListener(e -> {
            String filename = username + "_transaction_history.csv";
            StringBuilder sb = new StringBuilder("Transaction History:\n");
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException ex) {
                sb.append("No transaction history found.");
            }
            outputArea.setText(sb.toString());
        });

        logoutBtn.addActionListener(e -> {
            portfolioTree.exportToCSV(portfolioFile);
            dispose();
            new LoginScreen();
        });

        setVisible(true);
    }

    private void buyStock() {
        try {
            String stock = stockField.getText().trim();
            int qty = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            portfolioTree.insert(stock, qty, price);
            logTransaction("BUY", stock, qty, price);
            JOptionPane.showMessageDialog(this, "Stock bought successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

    private void sellStock() {
        try {
            String stock = stockField.getText().trim();
            int qty = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            StockNode node = portfolioTree.search(stock);
            if (node == null || node.quantity < qty) {
                JOptionPane.showMessageDialog(this, "Not enough stock to sell.");
                return;
            }
            double profit = (price - node.averagePrice) * qty;
            portfolioTree.delete(stock, qty);
            performanceHeap.insert(stock, qty, profit);
            logTransaction("SELL", stock, qty, price);
            JOptionPane.showMessageDialog(this, "Stock sold successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

    private void logTransaction(String type, String stock, int qty, double price) {
        String filename = username + "_transaction_history.csv";
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        try (FileWriter fw = new FileWriter(filename, true)) {
            fw.write(type + "," + stock + "," + qty + "," + price + "," + time + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPortfolioFromCSV() {
        File file = new File(portfolioFile);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String stock = parts[0];
                    int qty = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);
                    portfolioTree.insert(stock, qty, price);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}