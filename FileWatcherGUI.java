import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.TableRowSorter;

public class FileWatcherGUI extends JFrame {

    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

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
        tableModel.addColumn("Time");
        tableModel.addColumn("Relative Path");
        tableModel.addColumn("Size");

        JTable table = new JTable(tableModel);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        java.awt.Font currentFont = table.getTableHeader().getFont();
        table.getTableHeader().setFont(currentFont.deriveFont(java.awt.Font.BOLD));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        JButton pauseButton = new JButton("Pause Monitoring");

        JPanel topPanel = new JPanel();

        topPanel.add(new JLabel("Search Filename: "));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(clearButton);
        topPanel.add(pauseButton);

        searchButton.addActionListener(e -> {
            performSearch(searchField);
        });

        searchField.addActionListener(e -> {
            performSearch(searchField);
        });

        clearButton.addActionListener(e -> {

            searchField.setText("");
            sorter.setRowFilter(null);
        });

        pauseButton.addActionListener(e -> {
            if (JavaFileWatcher.isPaused()) {
                JavaFileWatcher.setPaused(false);
                pauseButton.setText("Pause Monitoring");
            }
            else{
                JavaFileWatcher.setPaused(true);
                pauseButton.setText("Resume Monitoring");
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setVisible(true);
    }

    private void performSearch(JTextField searchField) 
    {
        String text = searchField.getText();

        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
            } else 
            {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 2));
            }
    }

    public void addEvent(EventFormatter event)
    {
        
        SwingUtilities.invokeLater(() -> {

            tableModel.addRow(new Object[] {
                event.getID(),
                event.getEventType(),
                event.getFileName(),
                event.getEventTime(),
                event.getRelativePath(),
                event.getFileSize() + " bytes"
            });
        });
    }
}