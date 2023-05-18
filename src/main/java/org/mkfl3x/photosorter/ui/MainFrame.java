package org.mkfl3x.photosorter.ui;

import org.mkfl3x.photosorter.logic.SortMode;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class MainFrame extends JFrame {
    private JTextField sourceFolderField; // TODO: use file chooser
    private JPanel contentPanel;
    private JTextField destinationFolderField; // TODO: use file chooser
    private JComboBox<String> mode; // TODO: add info
    private JButton sort;
    private JTextArea log;

    public MainFrame() {
        configureMainFrame();
        configureModes();
    }

    private void configureMainFrame(){
        setTitle("Photo-Sorter");
        setContentPane(contentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void configureModes(){
        Arrays.stream(SortMode.values()).forEach(s -> mode.addItem(s.getText()));
        mode.addActionListener(e -> {
            if (Objects.equals(mode.getSelectedItem(), SortMode.REPLACE.getText())){
                destinationFolderField.setEnabled(false);
                destinationFolderField.setBackground(Color.LIGHT_GRAY);
            }
        });
    }
}
