import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class IPFinder {
    public static void main(String[] args) {
        new IPFinderDemo();  
    }
}

class IPFinderDemo extends JFrame {  
    private static final long serialVersionUID = 1L;
    private JTextField field;
    private JTextArea historyArea;
    private HashMap<String, String> ipHistory = new HashMap<>();
    private static final String HISTORY_FILE = "ipHistory.txt";  

    IPFinderDemo() { 
        loadHistory();

        setTitle("IP Finder Application");
        setLayout(null);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        getContentPane().setBackground(Color.LIGHT_GRAY);

        add(createLabel("IP Finder", 140, 30, 22));
        add(createLabel("Enter a valid website URL:", 85, 80, 14));

        field = new JTextField();
        field.setBounds(85, 110, 200, 25);
        add(field);

        add(createButton("Find IP", 85, 150, Color.DARK_GRAY, e -> findIP()));
        add(createButton("Clear", 175, 150, Color.GRAY, e -> field.setText("")));
        add(createButton("Exit", 140, 200, Color.RED, e -> System.exit(0)));

        historyArea = new JTextArea();
        historyArea.setBounds(50, 250, 300, 100);
        historyArea.setEditable(true);
        historyArea.setBackground(Color.LIGHT_GRAY);
        add(historyArea);

        setVisible(true);

        
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveHistory();
            }
        });
    }

    private JLabel createLabel(String text, int x, int y, int fontSize) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setBounds(x, y, 300, 30);
        return label;
    }

    private JButton createButton(String text, int x, int y, Color color, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 80, 30);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.addActionListener(actionListener);  
        return button;
    }

    private void findIP() {
        String url = field.getText().trim();
        if (url.isEmpty()) {
            showMessage("Please enter a URL!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ip = ipHistory.getOrDefault(url, getIPFromHost(url));
        if (ip != null) {
            ipHistory.put(url, ip);
            showMessage("The IP Address is: " + ip, "Success", JOptionPane.INFORMATION_MESSAGE);
            updateHistoryArea();
        }
    }

    private String getIPFromHost(String url) {
        try {
            return InetAddress.getByName(url).getHostAddress();
        } catch (UnknownHostException e) {
            showMessage("Oops! IP address not found. Please enter a valid URL.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void updateHistoryArea() {
        StringBuilder historyText = new StringBuilder("URL History:\n");
        ipHistory.forEach((url, ip) -> historyText.append(url).append(" -> ").append(ip).append("\n"));
        historyArea.setText(historyText.toString());
    }

    // Save the IP history to a file
    private void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (String url : ipHistory.keySet()) {
                writer.write(url + " -> " + ipHistory.get(url));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving history: " + e.getMessage());
        }
    }

    // Load the IP history from a file
    private void loadHistory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" -> ");
                if (parts.length == 2) {
                    ipHistory.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("No history file found or error loading it.");
        }
    }
}
