package org.mkfl3x.photosorter.ui;

import org.mkfl3x.photosorter.app.DirectoryException;
import org.mkfl3x.photosorter.app.PhotoSorter;
import org.mkfl3x.photosorter.app.SortMode;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.Arrays;

public class MainFrame extends JFrame {

    // TODO:
    //  - add info popup for mode combobox
    //  - use file chooser for source and destination fields

    private final PhotoSorter photoSorter = new PhotoSorter();

    private JTextField sourceFolderField;

    private JPanel contentPanel;

    private JTextField destinationFolderField;

    private JComboBox<String> mode;

    private JButton sort;

    private JTextArea log;

    private final DefaultCaret logCaret = (DefaultCaret) log.getCaret();

    private JCheckBox sortByYearsCheckbox; // TODO: use it

    public MainFrame() {
        configureModes();
        configureSortButton();
        configureMainFrame();
    }

    private void configureMainFrame() {
        setTitle("Photo-Sorter"); // TODO: move to properties
        setContentPane(contentPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500); // TODO: move to properties
        setLocationRelativeTo(null);
        setVisible(true);

        sortByYearsCheckbox.setEnabled(false); // TODO: remove when implemented
        logCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    private void configureModes() {
        Arrays.stream(SortMode.values()).forEach(s -> mode.addItem(s.getText()));
        mode.addActionListener(e -> switchDestinationField(!getSelectedMode().equals(SortMode.REPLACE)));
    }

    private void configureSortButton() {
        sort.addActionListener(e ->
                {
                    log.setText(""); // clear log before new sorting
                    try {
                        photoSorter.sortFiles(getSelectedMode(), sourceFolderField.getText(), destinationFolderField.getText(), log);
                    } catch (DirectoryException exception) {
                        String message = exception.getClass().getSimpleName() + "\n" + exception.getMessage();
                        JOptionPane.showMessageDialog(null, message, "Directory error", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception exception) {
                        String message = exception.getClass().getSimpleName() + "\n" + exception.getMessage();
                        JOptionPane.showMessageDialog(null, message, "Unknown error", JOptionPane.ERROR_MESSAGE);
                    }
                }
        );
    }

    private void switchDestinationField(boolean enable) {
        destinationFolderField.setEnabled(enable);
        if (enable)
            destinationFolderField.setBackground(Color.WHITE);
        else
            destinationFolderField.setBackground(Color.LIGHT_GRAY);
    }

    private SortMode getSelectedMode() {
        return photoSorter.getModeByText(String.valueOf(mode.getSelectedItem()));
    }
}
