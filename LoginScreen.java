package stockTrackerProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class LoginScreen extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private final String credentialsFile = "username.csv";

    public LoginScreen() {
        setTitle("Stock Tracker - Login/Register");
        setSize(450, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel choicePanel = createChoicePanel();
        JPanel loginPanel = createAuthPanel("Login");
        JPanel registerPanel = createAuthPanel("Register");

        mainPanel.add(choicePanel, "CHOICE");
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");

        add(mainPanel);
        cardLayout.show(mainPanel, "CHOICE");

        setVisible(true);
    }

    private JPanel createChoicePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome to Stock Market Tracker", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        loginBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.add(welcomeLabel);
        panel.add(loginBtn);
        panel.add(registerBtn);

        return panel;
    }

    private JPanel createAuthPanel(String type) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        JButton actionBtn = new JButton(type);
        JButton backBtn = new JButton("Back");

        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        actionBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password are required.");
                return;
            }

            if (type.equals("Login")) {
                loginUser(username, password);
            } else {
                registerUser(username, password);
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "CHOICE"));

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(backBtn);
        panel.add(actionBtn);

        return panel;
    }

    private void loginUser(String username, String password) {
        try (Scanner scanner = new Scanner(new File(credentialsFile))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    JOptionPane.showMessageDialog(this, "Welcome back, " + username + "!\nLogin successful! ");
                    dispose();
                    new StockDashboard(username);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid credentials! Please try again.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading user file.");
        }
    }

    private void registerUser(String username, String password) {
        try (Scanner scanner = new Scanner(new File(credentialsFile))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 2 && parts[0].equals(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists.");
                    return;
                }
            }
        } catch (IOException ignored) {}

        try (FileWriter fw = new FileWriter(credentialsFile, true)) {
            fw.write(username + "," + password + "\n");
            JOptionPane.showMessageDialog(this, "Welcome, " + username + "!\nRegistered successfully!");
            dispose();
            new StockDashboard(username);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving user.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
