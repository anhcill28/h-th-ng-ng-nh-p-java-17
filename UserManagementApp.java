import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class UserManagementApp extends JFrame {
    private final LoginService loginService;
    private final UserActionService userActionService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JCheckBox showPasswordCheckBox;
    private JButton logoutButton;
    private JButton deleteUserButton;
    private JLabel statusLabel; // Thêm nhãn trạng thái đăng nhập
    private UserData loggedInUser;

    public UserManagementApp() {
        loginService = new LoginService();
        userActionService = new UserActionService();

        setTitle("User Management System");
        setSize(600, 600); // Đặt kích thước khởi tạo lớn hơn
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("User Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Tăng kích thước font
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.setBackground(new Color(50, 120, 220));
        add(titlePanel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout()); // Sử dụng GridBagLayout
        mainPanel.setBackground(new Color(230, 240, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Thêm khoảng cách giữa các thành phần

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(createStyledLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(createStyledLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);

        // Show Password Checkbox
        gbc.gridx = 1;
        gbc.gridy = 2;
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBackground(new Color(230, 240, 255));
        showPasswordCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                passwordField.setEchoChar(e.getStateChange() == ItemEvent.SELECTED ? (char) 0 : '*');
            }
        });
        mainPanel.add(showPasswordCheckBox, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(createStyledLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(15);
        mainPanel.add(emailField, gbc);

        // Role Selection
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(createStyledLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"Regular User"}); // Mặc định là Regular User
        mainPanel.add(roleComboBox, gbc);

        // Status Label
        gbc.gridx = 0;
        gbc.gridy = 5;
        statusLabel = new JLabel("Status: Not logged in");
        mainPanel.add(statusLabel, gbc);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buttonPanel, BorderLayout.SOUTH);

        // Login Button
        JButton loginButton = createStyledButton("Login", Color.GREEN.darker(), Color.BLACK);
        loginButton.addActionListener(new LoginButtonAction());
        buttonPanel.add(loginButton);

        // Register Button
        JButton registerButton = createStyledButton("Register", new Color(0, 153, 204), Color.BLACK);
        registerButton.addActionListener(new RegisterButtonAction());
        buttonPanel.add(registerButton);

        // Logout Button
        logoutButton = createStyledButton("Logout", Color.RED, Color.BLACK);
        logoutButton.addActionListener(new LogoutButtonAction());
        logoutButton.setEnabled(false);
        buttonPanel.add(logoutButton);

        // Delete User Button
        deleteUserButton = createStyledButton("Delete User", Color.ORANGE, Color.BLACK);
        deleteUserButton.addActionListener(new DeleteUserButtonAction());
        deleteUserButton.setEnabled(false);
        buttonPanel.add(deleteUserButton);
    }

    // Method to create styled JLabel
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16)); // Tăng kích thước font
        label.setForeground(new Color(50, 50, 150));
        return label;
    }

    // Method to create styled JButton
    private JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Tăng kích thước font
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return button;
    }

    private class LoginButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username and password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                loggedInUser = loginService.login(username, password);
                JOptionPane.showMessageDialog(null, "Login successful as " + loggedInUser.username());
                userActionService.handleUserAccess(loggedInUser);
                showUserInfo();
                toggleUserManagementButtons(true);
                statusLabel.setText("Status: Logged in as " + loggedInUser.username());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Status: Login failed");
            }
        }
    }

    private class RegisterButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String selectedRole = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User userType;
            if (loggedInUser != null && loggedInUser.userType() instanceof Admin) {
                // Admin có quyền chọn vai trò
                switch (selectedRole) {
                    case "Admin" -> userType = new Admin(username, email, password);
                    case "Moderator" -> userType = new Moderator(username, email, password);
                    case "Regular User" -> userType = new RegularUser(username, email, password);
                    default -> throw new IllegalArgumentException("Invalid role selection");
                }
            } else {
                // Người dùng không phải Admin chỉ có thể đăng ký là Regular User
                userType = new RegularUser(username, email, password);
            }

            try {
                loginService.register(username, email, password, userType);
                JOptionPane.showMessageDialog(null, "User " + username + " registered successfully.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class LogoutButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearFields();
            loggedInUser = null;
            toggleUserManagementButtons(false);
            statusLabel.setText("Status: Logged out");
            JOptionPane.showMessageDialog(null, "Logged out successfully.");

            // Reset role selection to Regular User on logout
            roleComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Regular User"}));
        }
    }

    private class DeleteUserButtonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (loggedInUser.userType() instanceof Admin || loggedInUser.userType() instanceof Moderator) {
                String usernameToDelete = JOptionPane.showInputDialog("Enter the username to delete:");
                if (usernameToDelete != null && !usernameToDelete.isEmpty()) {
                    userActionService.deleteUser(usernameToDelete);
                    JOptionPane.showMessageDialog(null, "User " + usernameToDelete + " deleted successfully.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "You do not have permission to delete users.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        roleComboBox.setSelectedIndex(0); // Đặt lại về Regular User
    }

    private void toggleUserManagementButtons(boolean isEnabled) {
        logoutButton.setEnabled(isEnabled);
        if (loggedInUser != null) {
            boolean isAdminOrModerator = loggedInUser.userType() instanceof Admin || loggedInUser.userType() instanceof Moderator;
            deleteUserButton.setEnabled(isEnabled && isAdminOrModerator);
        }
    }

    private void showUserInfo() {
        // Hiển thị thông tin người dùng đã đăng nhập
        usernameField.setText(loggedInUser.username());
        emailField.setText(loggedInUser.email());

        // Cập nhật roleComboBox nếu là Admin
        if (loggedInUser.userType() instanceof Admin) {
            roleComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Admin", "Moderator", "Regular User"}));
        } else {
            roleComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Regular User"}));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserManagementApp app = new UserManagementApp();
            app.setVisible(true);
        });
    }
}
