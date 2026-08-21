import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.TableRowSorter;
import java.util.Map;
import java.util.HashMap;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableCellRenderer;

import java.io.File;
import java.nio.file.Path;

public class FileWatcherGUI extends JFrame {

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable table;
    private JLabel createdLabel;
    private JLabel modifiedLabel;
    private JLabel deletedLabel;
    private JLabel totalLabel;
    private JLabel activeFileLabel;
    private Map<String, Integer> fileActivity = new HashMap<>();

    private int created = 0;
    private int modified = 0;
    private int deleted = 0;

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

        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
            ) {

                Component component = super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
                );

                if (isSelected) {
                    component.setBackground(table.getSelectionBackground());
                    component.setForeground(table.getSelectionForeground());
                    return component;
                }
            }
        }





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
        JButton exportButton = new JButton("Export CSV");
        JButton pauseButton = new JButton("Pause Monitoring");
        JButton clearHistoryButton = new JButton("Clear History");

        JCheckBox allBox = new JCheckBox("All", true);
        JCheckBox txtBox = new JCheckBox("TXT", true);
        JCheckBox pdfBox = new JCheckBox("PDF", true);
        JCheckBox javaBox = new JCheckBox("Java", true);
        JCheckBox otherBox = new JCheckBox("Other", true);
        
        JComboBox<String> eventFilter = new JComboBox<>(
            new String[] {
                "All",
                "ENTRY_CREATE",
                "ENTRY_MODIFY",
                "ENTRY_DELETE"
            }
        );

        ActionListener extensionListener = e -> filterTable(searchField, eventFilter, allBox, txtBox, pdfBox, javaBox, otherBox);

        allBox.addActionListener(extensionListener);
        txtBox.addActionListener(extensionListener);
        pdfBox.addActionListener(extensionListener);
        javaBox.addActionListener(extensionListener);
        otherBox.addActionListener(extensionListener);

        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setPreferredSize(new Dimension(90, 0));

        topPanel.add(new JLabel("Search Filename: "));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(clearButton);

        topPanel.add(new JLabel("Event Type:"));
        topPanel.add(eventFilter);

        topPanel.add(pauseButton);
        topPanel.add(statusLabel);

        bottomPanel.add(exportButton);
        add(bottomPanel, BorderLayout.SOUTH);

        sidePanel.add(new JLabel("Extensions"));
        sidePanel.add(allBox);
        sidePanel.add(txtBox);
        sidePanel.add(pdfBox);
        sidePanel.add(javaBox);
        sidePanel.add(otherBox);

        sidePanel.add(Box.createVerticalStrut(20));

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        createdLabel = new JLabel("Created: 0");
        modifiedLabel = new JLabel("Modified: 0");
        deletedLabel = new JLabel("Deleted: 0");
        totalLabel = new JLabel("Events: 0");
        activeFileLabel = new JLabel("<html>Most Active:<br>None</html>");

        statsPanel.add(createdLabel);
        statsPanel.add(modifiedLabel);
        statsPanel.add(deletedLabel);
        statsPanel.add(totalLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(activeFileLabel);
        statsPanel.add(clearHistoryButton);

        sidePanel.add(statsPanel);

        add(sidePanel, BorderLayout.WEST);

        searchButton.addActionListener(e ->
            filterTable(searchField, eventFilter, allBox, txtBox, pdfBox, javaBox, otherBox));

        searchField.addActionListener(e -> 
            filterTable(searchField, eventFilter, allBox, txtBox, pdfBox, javaBox, otherBox));

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

        exportButton.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("EventHistory.csv"));

            int result = chooser.showSaveDialog(this);

            if(result == JFileChooser.APPROVE_OPTION) {

                JavaFileWatcher.exportToCSV(chooser.getSelectedFile().toPath());

                JOptionPane.showMessageDialog(
                    this,
                    "Successfully exported Event History",
                    "EXPORT COMPLETE",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }

        });

        clearHistoryButton.addActionListener(e -> {

            int result = JOptionPane.showConfirmDialog(
                this,
                "Confirm clearing of history?",
                "Clear History",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {

                tableModel.setRowCount(0);

                fileActivity.clear();

                created = 0;
                modified = 0;
                deleted = 0;

                createdLabel.setText("Created: 0");
                modifiedLabel.setText("Modified: 0");
                deletedLabel.setText("Deleted: 0");
                totalLabel.setText("Total Events: 0");
                activeFileLabel.setText("<html>Most Active:<br>None</html>");

                JavaFileWatcher.clearEventHistory();

                System.out.println("--Event History cleared--");
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setVisible(true);
    }

    private void filterTable(JTextField searchField, JComboBox<String> eventFilter, JCheckBox allBox, JCheckBox txtBox, JCheckBox pdfBox, JCheckBox javaBox, JCheckBox otherBox)
    {
        RowFilter<DefaultTableModel, Object> filter = new RowFilter<>() {

            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {

                String file = entry.getStringValue(2).toLowerCase();
                String event = entry.getStringValue(1);
                String extension = entry.getStringValue(3).toLowerCase();
                String search = searchField.getText().toLowerCase();
                String selected = eventFilter.getSelectedItem().toString();
                boolean filenameMatch = file.contains(search);

                boolean eventMatch = selected.equals("All") || event.equals(selected) || event.startsWith(selected);

                boolean extensionMatch = false;

                if (allBox.isSelected()){
                    extensionMatch = true;
                }
                else if (extension.equals("txt")) {
                    extensionMatch = txtBox.isSelected();
                }
                else if (extension.equals("pdf")) {
                    extensionMatch = pdfBox.isSelected();
                }
                else if (extension.equals("java")) {
                    extensionMatch = javaBox.isSelected();
                }

                else {
                    extensionMatch = otherBox.isSelected();
                }

                return filenameMatch && eventMatch && extensionMatch;
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

            String type = event.getEventType();

            if(type.equals("ENTRY_CREATE")){
                created++;
            }

            else if(type.startsWith("ENTRY_MODIFY")){
                modified++;
            }

            else if(type.equals("ENTRY_DELETE")){
                deleted++;
            }

            createdLabel.setText("Created: " + created);
            modifiedLabel.setText("Modified: " + modified);
            deletedLabel.setText("Deleted: " + deleted);
            totalLabel.setText("Total Events: " + (created + modified + deleted));

            String fileName = event.getFileName();

            fileActivity.put(fileName, fileActivity.getOrDefault(fileName, 0) + 1);

            String busiest = "None";
            int highest = 0;

            for(Map.Entry<String, Integer> entry : fileActivity.entrySet()) {
                if (entry.getValue() > highest) {
                    highest = entry.getValue();
                    busiest = entry.getKey();
                }
            }

            activeFileLabel.setText(
                "<html>Most Active:<br>" + busiest + "<br>(" + highest + " events)</html>"
            );
        });
    }
}