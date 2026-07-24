import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.TableRowSorter;

public class FileWatcherGUI extends JFrame {

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable table;

    public FileWatcherGUI()
    {
        setTitle("Java File Handling Service");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();

        tableModel.addColumn("ID");
        tableModel.addColumn("Event");
        tableModel.addColumn("File");
        tableModel.addColumn("Extension");
        tableModel.addColumn("Time");
        tableModel.addColumn("Relative Path");
        tableModel.addColumn("Size");

        table = new JTable(tableModel);
        JLabel statusLabel = new JLabel("Status: Monitoring");
        statusLabel.setForeground(Color.GREEN.darker());

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        table.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();

                    if (row != -1) {

                        showEventDetails(row);
                    }
                }
            }
        });

        java.awt.Font currentFont = table.getTableHeader().getFont();
        table.getTableHeader().setFont(currentFont.deriveFont(java.awt.Font.BOLD));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        JButton pauseButton = new JButton("Pause Monitoring");

        JComboBox<String> eventFilter = new JComboBox<>(
            new String[] {
                "All",
                "ENTRY_CREATE",
                "ENTRY_MODIFY",
                "ENTRY_DELETE"
            }
        );

        JPanel topPanel = new JPanel();

        topPanel.add(new JLabel("Search Filename: "));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(clearButton);

        topPanel.add(new JLabel("Event Type:"));
        topPanel.add(eventFilter);

        topPanel.add(pauseButton);
        topPanel.add(statusLabel);

        searchButton.addActionListener(e -> {
            filterTable(searchField, eventFilter);
        });

        searchField.addActionListener(e -> {
            filterTable(searchField, eventFilter);
        });

        clearButton.addActionListener(e -> {

            searchField.setText("");
            sorter.setRowFilter(null);
        });

        pauseButton.addActionListener(e -> {
            if (JavaFileWatcher.isPaused()) {

                JavaFileWatcher.setPaused(false);
                pauseButton.setText("Pause Monitoring");
                statusLabel.setText("Status: Monitoring");
                pauseButton.setForeground(Color.GREEN.darker());
            }
            else {
                JavaFileWatcher.setPaused(true);
                pauseButton.setText("Resume Monitoring");
                statusLabel.setText("Status: Paused");
                pauseButton.setForeground(Color.RED);
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setVisible(true);
    }

    private void filterTable(JTextField searchField, JComboBox<String> eventFilter)
    {
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<>() {

            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {

                String file = entry.getStringValue(2).toLowerCase();
                String event = entry.getStringValue(1);
                String search = searchField.getText().toLowerCase();
                String selected = eventFilter.getSelectedItem().toString();
                boolean filenameMatch = file.contains(search);

                boolean eventMatch = selected.equals("All") || event.equals(selected) || event.startsWith(selected);

                return filenameMatch && eventMatch;
            }
        };

        sorter.setRowFilter(filter);
    }

    private void showEventDetails(int row) {
        
        row = table.convertRowIndexToModel(row);

        String message = 
                   "Event ID: " + tableModel.getValueAt(row,0) + "\n\n" +
                   "Event Type: " + tableModel.getValueAt(row, 1) + "\n\n" +
                   "File Name: " + tableModel.getValueAt(row, 2) + "\n\n" +
                   "Extension: " + tableModel.getValueAt(row, 3) + "\n\n" +
                   "Time: " + tableModel.getValueAt(row, 4) + "\n" +
                   "Relative Path: " + tableModel.getValueAt(row, 5) + "\n" +
                   "Size: " + tableModel.getValueAt(row, 6);

        JOptionPane.showMessageDialog(this, message, "Event Details", JOptionPane.INFORMATION_MESSAGE);
    }

    public void addEvent(EventFormatter event)
    {
        
        SwingUtilities.invokeLater(() -> {

            tableModel.addRow(new Object[] {
                event.getID(),
                event.getEventType(),
                event.getFileName(),
                event.getFileExtension(),
                event.getEventTime(),
                event.getRelativePath(),
                event.getFileSize() + " bytes"
            });
        });
    }
}