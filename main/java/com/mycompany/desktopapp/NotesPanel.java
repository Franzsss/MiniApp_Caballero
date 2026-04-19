package com.mycompany.desktopapp;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotesPanel extends JPanel {

    private static class Note {
        String title;
        String content;
        String created;

        Note(String title) {
            this.title = title;
            this.content = "";
            this.created = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    private final List<Note> notes = new ArrayList<>();
    private int selectedIndex = -1;
    private boolean ignoreListSelection = false;

    private DefaultListModel<String> listModel;
    private JList<String> noteList;
    private JTextArea textArea;
    private JLabel statusBar;
    private JButton btnNew, btnDelete, btnRename, btnSaveFile, btnLoadFile;

    public NotesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();
        addSampleNote();
    }

    private void buildUI() {

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(150, 0));
        leftPanel.setBackground(new Color(245, 245, 250));

        JLabel listTitle = new JLabel("My Notes");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        listTitle.setForeground(new Color(60, 60, 80));
        listTitle.setBorder(new EmptyBorder(0, 4, 6, 0));

        listModel = new DefaultListModel<>();
        noteList  = new JList<>(listModel);
        noteList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteList.setBackground(Color.WHITE);
        noteList.setForeground(new Color(40, 40, 60));
        noteList.setSelectionBackground(new Color(100, 140, 230));
        noteList.setSelectionForeground(Color.WHITE);
        noteList.setBorder(null);

        noteList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !ignoreListSelection) {
                onNoteSelected(noteList.getSelectedIndex());
            }
        });

        JScrollPane listScroll = new JScrollPane(noteList);
        listScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 220)));

        JPanel listBtns = new JPanel(new GridLayout(1, 2, 5, 0));
        listBtns.setBackground(new Color(245, 245, 250));
        btnNew    = smallBtn("+ New",  new Color(80, 140, 230));
        btnDelete = smallBtn("Delete", new Color(220, 80, 80));
        listBtns.add(btnNew);
        listBtns.add(btnDelete);

        btnNew.addActionListener(e -> onNewNote());
        btnDelete.addActionListener(e -> onDeleteNote());

        leftPanel.add(listTitle,  BorderLayout.NORTH);
        leftPanel.add(listScroll, BorderLayout.CENTER);
        leftPanel.add(listBtns,   BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setBackground(new Color(245, 245, 250));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        toolbar.setBackground(new Color(245, 245, 250));

        btnRename   = smallBtn("Rename",        new Color(100, 100, 130));
        btnSaveFile = smallBtn("Save to File",  new Color(50, 160, 100));
        btnLoadFile = smallBtn("Load File",     new Color(50, 130, 190));

        btnRename.addActionListener(e -> onRenameNote());
        btnSaveFile.addActionListener(e -> onSaveToFile());
        btnLoadFile.addActionListener(e -> onLoadFromFile());

        toolbar.add(btnRename);
        toolbar.add(btnSaveFile);
        toolbar.add(btnLoadFile);

        textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(new Color(30, 30, 50));
        textArea.setCaretColor(new Color(80, 140, 230));
        textArea.setBorder(new EmptyBorder(12, 14, 12, 14));

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { onTextChanged(); }
            public void removeUpdate(DocumentEvent e)  { onTextChanged(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        JScrollPane textScroll = new JScrollPane(textArea);
        textScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 220)));

        statusBar = new JLabel("Select or create a note");
        statusBar.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusBar.setForeground(new Color(130, 130, 150));
        statusBar.setBorder(new EmptyBorder(2, 4, 0, 0));

        rightPanel.add(toolbar,    BorderLayout.NORTH);
        rightPanel.add(textScroll, BorderLayout.CENTER);
        rightPanel.add(statusBar,  BorderLayout.SOUTH);

        add(leftPanel,  BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void onNewNote() {
        String title = JOptionPane.showInputDialog(this,
            "Enter note title:", "New Note", JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.trim().isEmpty()) return;
        Note note = new Note(title.trim());
        notes.add(note);
        listModel.addElement(note.title);
        selectNote(notes.size() - 1);
        textArea.requestFocusInWindow();
    }

    private void onDeleteNote() {
        if (selectedIndex < 0) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete \"" + notes.get(selectedIndex).title + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        notes.remove(selectedIndex);
        listModel.remove(selectedIndex);
        selectedIndex = -1;
        textArea.setText("");
        statusBar.setText("Note deleted.");
        if (!notes.isEmpty()) selectNote(Math.min(selectedIndex, notes.size() - 1));
    }

    private void onRenameNote() {
        if (selectedIndex < 0) return;
        String newTitle = (String) JOptionPane.showInputDialog(this,
            "New title:", "Rename Note", JOptionPane.PLAIN_MESSAGE,
            null, null, notes.get(selectedIndex).title);
        if (newTitle == null || newTitle.trim().isEmpty()) return;
        notes.get(selectedIndex).title = newTitle.trim();
        ignoreListSelection = true;
        listModel.set(selectedIndex, newTitle.trim());
        ignoreListSelection = false;
    }

    private void onNoteSelected(int index) {
        if (index < 0 || index >= notes.size()) return;
        if (selectedIndex >= 0 && selectedIndex < notes.size()) {
            notes.get(selectedIndex).content = textArea.getText();
        }
        selectedIndex = index;
        textArea.setText(notes.get(index).content);
        textArea.setCaretPosition(0);
        updateStatus();
    }

    private void onTextChanged() {
        if (selectedIndex >= 0 && selectedIndex < notes.size()) {
            notes.get(selectedIndex).content = textArea.getText();
            updateStatus();
        }
    }

    private void onSaveToFile() {
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a note first.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(notes.get(selectedIndex).title + ".txt"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(f))) {
            w.write("=== " + notes.get(selectedIndex).title + " ===");
            w.newLine();
            w.write("Created: " + notes.get(selectedIndex).created);
            w.newLine(); w.newLine();
            w.write(textArea.getText());
            statusBar.setText("Saved to: " + f.getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
    }

    private void onLoadFromFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append("\n");
            String title = f.getName().replaceAll("\\.txt$", "");
            Note note = new Note(title);
            note.content = sb.toString();
            notes.add(note);
            listModel.addElement(note.title);
            selectNote(notes.size() - 1);
            statusBar.setText("Loaded: " + f.getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
        }
    }

    private void selectNote(int index) {
        ignoreListSelection = true;
        noteList.setSelectedIndex(index);
        ignoreListSelection = false;
        onNoteSelected(index);
    }

    private void updateStatus() {
        String text = textArea.getText();
        int chars = text.length();
        int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        statusBar.setText(words + " words  |  " + chars + " characters");
    }

    private void addSampleNote() {
        Note n = new Note("Welcome");
        n.content = "Welcome to MiniApp Notes!\n\n"
            + "- Click '+ New' to create a note\n"
            + "- Click 'Delete' to remove a note\n"
            + "- Click 'Rename' to rename it\n"
            + "- Use 'Save to File' to export as .txt\n"
            + "- Use 'Load File' to import a .txt file\n\n"
            + "Start typing here...";
        notes.add(n);
        listModel.addElement(n.title);
        selectNote(0);
    }

    private JButton smallBtn(String label, Color bg) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(5, 10, 5, 10));
        return btn;
    }
}