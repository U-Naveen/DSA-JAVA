

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

// AVL Tree implementation for portfolio management
class StockAVLTree {
    class Node {
        String stockName;
        int quantity;
        double averagePrice;
        int height;
        Node left, right;
        Node(String stockName, int quantity, double averagePrice) {
            this.stockName = stockName;
            this.quantity = quantity;
            this.averagePrice = averagePrice;
            this.height = 1;
        }
    }
    Node root;

    // Utility functions to get height and balance
    int height(Node N) {
        return (N == null) ? 0 : N.height;
    }

    int getBalance(Node N) {
        return (N == null) ? 0 : height(N.left) - height(N.right);
    }

    // Right rotate subtree rooted with y
    Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        return x;
    }

    // Left rotate subtree rooted with x
    Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        return y;
    }

    // Insert (or update) a stock in the AVL tree.
    Node insert(Node node, String stockName, int quantity, double price) {
        if (node == null)
            return new Node(stockName, quantity, price);

        int cmp = stockName.compareTo(node.stockName);
        if (cmp < 0) {
            node.left = insert(node.left, stockName, quantity, price);
        } else if (cmp > 0) {
            node.right = insert(node.right, stockName, quantity, price);
        } else {
            // Stock exists; update quantity and recalc average price.
            double totalCost = node.averagePrice * node.quantity + price * quantity;
            node.quantity += quantity;
            node.averagePrice = totalCost / node.quantity;
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // Balancing the tree (LL, RR, LR, RL cases)
        if (balance > 1 && stockName.compareTo(node.left.stockName) < 0)
            return rightRotate(node);
        if (balance < -1 && stockName.compareTo(node.right.stockName) > 0)
            return leftRotate(node);
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

    // Delete (or reduce quantity of) a stock when selling.
    Node delete(Node node, String stockName, int quantity) {
        if (node == null)
            return node;

        int cmp = stockName.compareTo(node.stockName);
        if (cmp < 0)
            node.left = delete(node.left, stockName, quantity);
        else if (cmp > 0)
            node.right = delete(node.right, stockName, quantity);
        else {
            // Found the stock
            if (node.quantity > quantity) {
                node.quantity -= quantity;
                return node;
            } else if (node.quantity == quantity) {
                // Remove node
                if (node.left == null || node.right == null) {
                    Node temp = (node.left != null) ? node.left : node.right;
                    node = (temp == null) ? null : temp;
                } else {
                    Node temp = minValueNode(node.right);
                    node.stockName = temp.stockName;
                    node.quantity = temp.quantity;
                    node.averagePrice = temp.averagePrice;
                    node.right = delete(node.right, temp.stockName, temp.quantity);
                }
            } else {
                System.out.println("Not enough stock to sell.");
                return node;
            }
        }
        if (node == null)
            return node;

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        int balance = getBalance(node);

        // Rebalance the tree if needed.
        if (balance > 1 && getBalance(node.left) >= 0)
            return rightRotate(node);
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && getBalance(node.right) <= 0)
            return leftRotate(node);
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    // Search for a stock node by name.
    Node search(Node node, String stockName) {
        if (node == null || node.stockName.equals(stockName))
            return node;
        int cmp = stockName.compareTo(node.stockName);
        return (cmp < 0) ? search(node.left, stockName) : search(node.right, stockName);
    }

    // Inorder traversal to display portfolio.
    void inOrder(Node node) {
        if (node != null) {
            inOrder(node.left);
            System.out.println("Stock: " + node.stockName + " | Quantity: " + node.quantity +
                               " | Avg Price: " + node.averagePrice);
            inOrder(node.right);
        }
    }

    // Public methods
    public void insert(String stockName, int quantity, double price) {
        root = insert(root, stockName, quantity, price);
    }

    public void delete(String stockName, int quantity) {
        root = delete(root, stockName, quantity);
    }

    public Node search(String stockName) {
        return search(root, stockName);
    }

    public void displayPortfolio() {
        System.out.println("\nYour Portfolio:");
        inOrder(root);
    }

    // Export current portfolio to a CSV file.
    public void exportToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Stock Name,Quantity,Average Price");
            exportToCSV(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportToCSV(Node node, PrintWriter writer) {
        if (node != null) {
            exportToCSV(node.left, writer);
            writer.println(node.stockName + "," + node.quantity + "," + node.averagePrice);
            exportToCSV(node.right, writer);
        }
    }
}

// Heap implementation for tracking trading performance (using a max heap).
class PerformanceHeap {
    class TradePerformance {
        String stockName;
        int quantity;
        double profit;  // profit = (sell price - avg buy price) * quantity

        TradePerformance(String stockName, int quantity, double profit) {
            this.stockName = stockName;
            this.quantity = quantity;
            this.profit = profit;
        }
    }

    List<TradePerformance> heap;

    public PerformanceHeap() {
        heap = new ArrayList<>();
    }

    // Helper functions to get parent/children indices.
    private int parent(int i) { return (i - 1) / 2; }
    private int leftChild(int i) { return 2 * i + 1; }
    private int rightChild(int i) { return 2 * i + 2; }

    // Insert a new trade performance record.
    public void insert(String stockName, int quantity, double profit) {
        TradePerformance tp = new TradePerformance(stockName, quantity, profit);
        heap.add(tp);
        int index = heap.size() - 1;
        // Bubble up to maintain max heap (largest profit on top)
        while (index != 0 && heap.get(parent(index)).profit < heap.get(index).profit) {
            Collections.swap(heap, index, parent(index));
            index = parent(index);
        }
    }

    // Extract the trade with the maximum profit.
    public TradePerformance extractMax() {
        if (heap.size() == 0) return null;
        TradePerformance max = heap.get(0);
        TradePerformance last = heap.remove(heap.size() - 1);
        if (heap.size() > 0) {
            heap.set(0, last);
            heapify(0);
        }
        return max;
    }

    // Heapify from a given index.
    private void heapify(int i) {
        int left = leftChild(i);
        int right = rightChild(i);
        int largest = i;
        if (left < heap.size() && heap.get(left).profit > heap.get(largest).profit)
            largest = left;
        if (right < heap.size() && heap.get(right).profit > heap.get(largest).profit)
            largest = right;
        if (largest != i) {
            Collections.swap(heap, i, largest);
            heapify(largest);
        }
    }

    // Display all trade performance records.
    public void displayHeap() {
        System.out.println("\nTrading Performance (Top trades):");
        for (TradePerformance tp : heap) {
            System.out.println("Stock: " + tp.stockName + " | Quantity: " + tp.quantity +
                               " | Profit: " + tp.profit);
        }
    }
}

public class User {
    Scanner in = new Scanner(System.in);
    String userFile = "";
    String portfolioFile = "";
    String userFilename = "username.csv";

    // Instantiate the AVL tree and performance heap.
    StockAVLTree portfolioTree = new StockAVLTree();
    PerformanceHeap performanceHeap = new PerformanceHeap();

    // Registration method (creates a user and an empty portfolio file)
    public void register() {
        int n = 0;
        while (n == 0) {
            System.out.println("Enter user name: ");
            String name = in.nextLine();

            try (BufferedReader br = new BufferedReader(new FileReader(userFilename))) {
                String line;
                boolean userExists = false;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values[0].equals(name)) {
                        System.out.println("User name already exists. Please enter a different user name.");
                        userExists = true;
                        break;
                    }
                }
                if (!userExists) {
                    System.out.println("Enter password: ");
                    String password = in.nextLine();

                    try (FileWriter writer = new FileWriter(userFilename, true)) {
                        writer.append(name).append(",").append(password).append("\n");
                        writer.flush();
                        System.out.println("User registered successfully!");
                        n = 1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Create a portfolio file for the user
                    portfolioFile = name + "_portfolio.csv";
                    try (FileWriter writer = new FileWriter(portfolioFile)) {
                        writer.append("Stock Name,Quantity,Average Price\n");
                        writer.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    // Initialize user-specific transaction log file
    // Initialize user-specific transaction log file
    public void initializeTransactionLog(String username) {
    String transactionFile = username + "_transaction_history.csv";
    File file = new File(transactionFile);
    if (!file.exists()) {
        try (FileWriter writer = new FileWriter(transactionFile)) {
            writer.append("Transaction Type,Stock Name,Quantity,Price,Date\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Log transaction to user-specific CSV file
    public void logTransaction(String username, String type, String stockName, int quantity, double price) {
    String transactionFile = username + "_transaction_history.csv";
    try (FileWriter writer = new FileWriter(transactionFile, true)) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        writer.append(type).append(",")
              .append(stockName).append(",")
              .append(String.valueOf(quantity)).append(",")
              .append(String.valueOf(price)).append(",")
              .append(date).append("\n");
        writer.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    // Login method loads the user's portfolio into the AVL tree and starts the session.
    public void login() {
        System.out.println("Enter user name: ");
        String name = in.nextLine();
        System.out.println("Enter password: ");
        String password = in.nextLine();
        boolean loggedIn = false;
    
        try (BufferedReader br = new BufferedReader(new FileReader(userFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(name) && values[1].equals(password)) {
                    System.out.println("Login successful!");
                    loggedIn = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        if (loggedIn) {
            // Set user-specific file names.
            userFile = name + ".csv"; // Can be used for transaction history if needed.
            portfolioFile = name + "_portfolio.csv";
            initializeTransactionLog(name); // Initialize user-specific transaction log
            loadPortfolio();
            manageStocks();
        } else {
            System.out.println("Invalid user name or password. Please try again.");
        }
    }
    

    // Load portfolio from CSV file into the AVL tree.
    public void loadPortfolio() {
        File file = new File(portfolioFile);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(portfolioFile)) {
                writer.append("Stock Name,Quantity,Average Price\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(portfolioFile))) {
                String header = br.readLine(); // Skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length >= 3) {
                        String stockName = values[0];
                        int quantity = Integer.parseInt(values[1]);
                        double avgPrice = Double.parseDouble(values[2]);
                        portfolioTree.insert(stockName, quantity, avgPrice);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Display transaction history for the logged-in user.
public void displayTransactionHistory(String username) {
    String transactionFile = username + "_transaction_history.csv";
    File file = new File(transactionFile);
    if (!file.exists()) {
        System.out.println("No transaction history found for user: " + username);
        return;
    }
    try (BufferedReader br = new BufferedReader(new FileReader(transactionFile))) {
        String header = br.readLine(); // Read header
        String line;
        System.out.println("\nTransaction History:");
        System.out.println(header);
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    // Main menu after login.
    // Main menu after login.
    public void manageStocks() {
    int choice;
    do {
        System.out.println("\nStock Management:");
        System.out.println("1. View Portfolio");
        System.out.println("2. Buy Stock");
        System.out.println("3. Sell Stock");
        System.out.println("4. View Trading Performance");
        System.out.println("5. View Transaction History"); // New option
        System.out.println("6. Logout");
        System.out.print("Enter choice: ");
        choice = in.nextInt();
        in.nextLine(); // consume newline

        switch(choice) {
            case 1:
                portfolioTree.displayPortfolio();
                break;
            case 2:
                buyStock();
                break;
            case 3:
                sellStock();
                break;
            case 4:
                performanceHeap.displayHeap();
                break;
            case 5: // New case for viewing transaction history
                displayTransactionHistory(userFile.split("\\.")[0]);
                break;
            case 6:
                System.out.println("Logging out...");
                // Save the portfolio before logging out.
                portfolioTree.exportToCSV(portfolioFile);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    } while(choice != 6);
}


    // Buy stock: update the AVL tree and then export the portfolio.
    // Buy stock: update the AVL tree and then export the portfolio.
public void buyStock() {
    System.out.print("Enter stock name: ");
    String stockName = in.nextLine();
    System.out.print("Enter quantity to buy: ");
    int quantity = in.nextInt();
    System.out.print("Enter price per stock: ");
    double price = in.nextDouble();
    in.nextLine(); // consume newline

    portfolioTree.insert(stockName, quantity, price);
    System.out.println("Stock purchased and portfolio updated successfully!");
    portfolioTree.exportToCSV(portfolioFile);

    // Log transaction
    logTransaction(userFile.split("\\.")[0], "BUY", stockName, quantity, price);
}

// Sell stock: update the AVL tree, compute profit, and update the performance heap.
public void sellStock() {
    System.out.print("Enter stock name to sell: ");
    String stockName = in.nextLine();
    System.out.print("Enter quantity to sell: ");
    int sellQuantity = in.nextInt();
    System.out.print("Enter selling price per stock: ");
    double sellPrice = in.nextDouble();
    in.nextLine(); // consume newline

    StockAVLTree.Node node = portfolioTree.search(stockName);
    if (node == null) {
        System.out.println("Stock not found in your portfolio.");
        return;
    }
    if (node.quantity < sellQuantity) {
        System.out.println("Not enough stock to sell.");
        return;
    }

    // Compute trade profit: (sell price - average price) * quantity sold.
    double profit = (sellPrice - node.averagePrice) * sellQuantity;
    portfolioTree.delete(stockName, sellQuantity);
    System.out.println("Stock sold successfully!");

    // Record performance in the heap.
    performanceHeap.insert(stockName, sellQuantity, profit);
    portfolioTree.exportToCSV(portfolioFile);

    // Log transaction
    logTransaction(userFile.split("\\.")[0], "SELL", stockName, sellQuantity, sellPrice);
}


    public static void main(String[] args) {
        User user = new User();
        int choice;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("\nStock Market System:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch(choice) {
                case 1:
                    user.register();
                    break;
                case 2:
                    user.login();
                    break;
                case 3:
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while(choice != 3);
    }
}

