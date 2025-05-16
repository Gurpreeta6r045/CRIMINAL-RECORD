package DbmsProject;

public class criminalManagement {
    import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CriminalManagement extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField firstNameField, lastNameField, dobField, addressField;
    private JButton addButton, updateButton, deleteButton;

    public CriminalManagement() {
        setTitle("Criminal Record Management");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel for form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        dobField = new JTextField();
        formPanel.add(dobField);

        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        formPanel.add(buttonPanel);
        add(formPanel, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "DOB", "Address"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadTableData();

        // Button listeners
        addButton.addActionListener(e -> addCriminal());
        updateButton.addActionListener(e -> updateCriminal());
        deleteButton.addActionListener(e -> deleteCriminal());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selected = table.getSelectedRow();
                firstNameField.setText(model.getValueAt(selected, 1).toString());
                lastNameField.setText(model.getValueAt(selected, 2).toString());
                dobField.setText(model.getValueAt(selected, 3).toString());
                addressField.setText(model.getValueAt(selected, 4).toString());
            }
        });
    }

    private void loadTableData() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/criminal_db", "root", "your_password")) {
            model.setRowCount(0);
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM criminal_record");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("FirstName"),
                    rs.getString("LastName"),
                    rs.getDate("DateOfBirth"),
                    rs.getString("Address")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addCriminal() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/criminal_db", "root", "your_password")) {
            String sql = "INSERT INTO criminal_record (FirstName, LastName, DateOfBirth, Address) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, firstNameField.getText());
            pst.setString(2, lastNameField.getText());
            pst.setDate(3, Date.valueOf(dobField.getText()));
            pst.setString(4, addressField.getText());
            pst.executeUpdate();
            loadTableData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateCriminal() {
        int selected = table.getSelectedRow();
        if (selected >= 0) {
            int id = (int) model.getValueAt(selected, 0);
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/criminal_db", "root", "your_password")) {
                String sql = "UPDATE criminal_record SET FirstName=?, LastName=?, DateOfBirth=?, Address=? WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, firstNameField.getText());
                pst.setString(2, lastNameField.getText());
                pst.setDate(3, Date.valueOf(dobField.getText()));
                pst.setString(4, addressField.getText());
                pst.setInt(5, id);
                pst.executeUpdate();
                loadTableData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteCriminal() {
        int selected = table.getSelectedRow();
        if (selected >= 0) {
            int id = (int) model.getValueAt(selected, 0);
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/criminal_db", "root", "your_password")) {
                String sql = "DELETE FROM criminal_record WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                loadTableData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CriminalManagement().setVisible(true));
    }
}

}
