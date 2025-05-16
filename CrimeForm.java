package DbmsProject;

public class crimeform {
    import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CrimeForm extends JFrame {
    private JComboBox<String> criminalDropdown, statusDropdown;
    private JTextField dateField, typeField;
    private JTextArea caseNotesArea;
    private JButton saveButton;

    public CrimeForm() {
        setTitle("Crime Entry");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 5, 5));

        add(new JLabel("Criminal:"));
        criminalDropdown = new JComboBox<>();
        loadCriminals();
        add(criminalDropdown);

        add(new JLabel("Crime Type:"));
        typeField = new JTextField();
        add(typeField);

        add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField();
        add(dateField);

        add(new JLabel("Status:"));
        statusDropdown = new JComboBox<>(new String[]{"open", "closed", "under investigation"});
        add(statusDropdown);

        add(new JLabel("Case Notes:"));
        caseNotesArea = new JTextArea(3, 20);
        add(new JScrollPane(caseNotesArea));

        saveButton = new JButton("Save");
        add(new JLabel()); // Spacer
        add(saveButton);

        saveButton.addActionListener(e -> saveCrime());
    }

    private void loadCriminals() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/criminal_db", "root", "your_password")) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT id, FirstName, LastName FROM criminal_record");
            while (rs.next()) {
                String item = rs.getInt("id") + " - " + rs.getString("FirstName") + " " + rs.getString("LastName");
                criminalDropdown.addItem(item);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveCrime() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/criminal_db", "root", "your_password")) {
            String selected = (String) criminalDropdown.getSelectedItem();
            int criminalId = Integer.parseInt(selected.split(" - ")[0]);
            String sql = "INSERT INTO crime_record (criminal_id, date, type, status, case_notes) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, criminalId);
            pst.setDate(2, Date.valueOf(dateField.getText()));
            pst.setString(3, typeField.getText());
            pst.setString(4, (String) statusDropdown.getSelectedItem());
            pst.setString(5, caseNotesArea.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Crime record saved successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving crime record.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CrimeForm().setVisible(true));
    }
}

}
