package org.mkfl3x.photosorter.ui;

import org.mkfl3x.photosorter.app.PhotoSorter;
import org.mkfl3x.photosorter.app.SortMode;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class MainFrame extends JFrame {

    private final PhotoSorter photoSorter = new PhotoSorter();

    private JTextField sourceFolderField; // TODO: use file chooser

    private JPanel contentPanel;

    private JTextField destinationFolderField; // TODO: use file chooser

    private JComboBox<String> mode; // TODO: add info

    private JButton sort;

    private JTextArea log;

    public MainFrame() {

        // DEBUG
        sourceFolderField.setText("/Users/Mikhail.Kuznetsov/Desktop/test-source");
        destinationFolderField.setText("/Users/Mikhail.Kuznetsov/Desktop/test-destination");
        // DEBUG

        configureModes();
        configureSortButton();
        configureMainFrame();
    }

    private void configureMainFrame() {
        setTitle("Photo-Sorter");
        setContentPane(contentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void configureModes() {
        Arrays.stream(SortMode.values()).forEach(s -> mode.addItem(s.getText()));
        mode.addActionListener(e -> {
            if (photoSorter.getModeByText(mode.getSelectedItem().toString()).equals(SortMode.REPLACE)) {
                destinationFolderField.setEnabled(false);
                destinationFolderField.setBackground(Color.LIGHT_GRAY);
            }
        });
    }

    private void configureSortButton() {
        sort.addActionListener(e ->
                {
                    try {
                        photoSorter.sortFiles(
                                photoSorter.getModeByText(mode.getSelectedItem().toString()),
                                sourceFolderField.getText(),
                                destinationFolderField.getText(),
                                log
                        );
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );
    }
}
