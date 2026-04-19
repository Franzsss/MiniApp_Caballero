/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.desktopapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class CalculatorPanel extends JPanel {

    private JTextField display;
    private JLabel historyLabel;

    private double firstOperand = 0;
    private String pendingOperator = "";
    private boolean startNewNumber = true;
    private boolean justCalculated = false;

    public CalculatorPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(30, 30, 40));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        buildUI();
    }

    private void buildUI() {

        historyLabel = new JLabel(" ");
        historyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyLabel.setForeground(new Color(160, 160, 180));
        historyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        historyLabel.setBorder(new EmptyBorder(0, 5, 0, 5));

        display = new JTextField("0");
        display.setFont(new Font("Segoe UI Light", Font.PLAIN, 42));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(new Color(30, 30, 40));
        display.setForeground(Color.WHITE);
        display.setCaretColor(Color.WHITE);
        display.setBorder(new EmptyBorder(5, 5, 10, 5));

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(new Color(30, 30, 40));
        displayPanel.add(historyLabel, BorderLayout.NORTH);
        displayPanel.add(display, BorderLayout.CENTER);

        JPanel btnGrid = new JPanel(new GridLayout(5, 4, 10, 10));
        btnGrid.setBackground(new Color(30, 30, 40));
        btnGrid.setBorder(new EmptyBorder(15, 0, 0, 0));

        addBtn(btnGrid, "C",   new Color(80, 80, 95),  Color.WHITE, this::onClear);
        addBtn(btnGrid, "+/-", new Color(80, 80, 95),  Color.WHITE, this::onToggleSign);
        addBtn(btnGrid, "%",   new Color(80, 80, 95),  Color.WHITE, this::onPercent);
        addBtn(btnGrid, "/",   new Color(255, 149, 0), Color.WHITE, () -> onOperator("/"));

        addBtn(btnGrid, "7", new Color(50, 50, 65), Color.WHITE, () -> onDigit("7"));
        addBtn(btnGrid, "8", new Color(50, 50, 65), Color.WHITE, () -> onDigit("8"));
        addBtn(btnGrid, "9", new Color(50, 50, 65), Color.WHITE, () -> onDigit("9"));
        addBtn(btnGrid, "x", new Color(255, 149, 0), Color.WHITE, () -> onOperator("*"));

        addBtn(btnGrid, "4", new Color(50, 50, 65), Color.WHITE, () -> onDigit("4"));
        addBtn(btnGrid, "5", new Color(50, 50, 65), Color.WHITE, () -> onDigit("5"));
        addBtn(btnGrid, "6", new Color(50, 50, 65), Color.WHITE, () -> onDigit("6"));
        addBtn(btnGrid, "-", new Color(255, 149, 0), Color.WHITE, () -> onOperator("-"));

        addBtn(btnGrid, "1", new Color(50, 50, 65), Color.WHITE, () -> onDigit("1"));
        addBtn(btnGrid, "2", new Color(50, 50, 65), Color.WHITE, () -> onDigit("2"));
        addBtn(btnGrid, "3", new Color(50, 50, 65), Color.WHITE, () -> onDigit("3"));
        addBtn(btnGrid, "+", new Color(255, 149, 0), Color.WHITE, () -> onOperator("+"));

        addBtn(btnGrid, "0",  new Color(50, 50, 65),  Color.WHITE, () -> onDigit("0"));
        addBtn(btnGrid, "<<", new Color(50, 50, 65),  new Color(255, 100, 100), this::onBackspace);
        addBtn(btnGrid, ".",  new Color(50, 50, 65),  Color.WHITE, this::onDot);
        addBtn(btnGrid, "=",  new Color(255, 149, 0), Color.WHITE, this::onEquals);

        add(displayPanel, BorderLayout.NORTH);
        add(btnGrid, BorderLayout.CENTER);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) { handleKey(e); }
        });
    }

    private void addBtn(JPanel panel, String label, Color bg, Color fg, Runnable action) {
        JButton btn = makeBtn(label, bg, fg);
        btn.addActionListener(e -> { action.run(); requestFocusInWindow(); });
        panel.add(btn);
    }

    private JButton makeBtn(String label, Color bg, Color fg) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hoverBg = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private void onDigit(String digit) {
        if (startNewNumber || justCalculated) {
            display.setText(digit.equals("0") ? "0" : digit);
            startNewNumber = false;
            justCalculated = false;
        } else {
            String cur = display.getText();
            if (cur.equals("0")) {
                display.setText(digit);
            } else if (cur.length() < 15) {
                display.setText(cur + digit);
            }
        }
    }

    private void onDot() {
        if (startNewNumber || justCalculated) {
            display.setText("0.");
            startNewNumber = false;
            justCalculated = false;
            return;
        }
        if (!display.getText().contains(".")) {
            display.setText(display.getText() + ".");
        }
    }

    private void onOperator(String op) {
        firstOperand = getDisplayValue();
        pendingOperator = op;
        startNewNumber = true;
        justCalculated = false;
        historyLabel.setText(formatNumber(firstOperand) + " " + op);
    }

    private void onEquals() {
        if (pendingOperator.isEmpty()) return;
        double second = getDisplayValue();
        String history = historyLabel.getText().trim() + " " + formatNumber(second) + " =";
        double result = calculate(firstOperand, second, pendingOperator);
        historyLabel.setText(history);
        display.setText(formatNumber(result));
        pendingOperator = "";
        justCalculated = true;
        startNewNumber = true;
    }

    private void onClear() {
        display.setText("0");
        historyLabel.setText(" ");
        firstOperand = 0;
        pendingOperator = "";
        startNewNumber = true;
        justCalculated = false;
    }

    private void onBackspace() {
        if (justCalculated || startNewNumber) { display.setText("0"); startNewNumber = true; return; }
        String cur = display.getText();
        if (cur.length() <= 1 || (cur.length() == 2 && cur.startsWith("-"))) {
            display.setText("0");
            startNewNumber = true;
        } else {
            display.setText(cur.substring(0, cur.length() - 1));
        }
    }

    private void onToggleSign() {
        double val = getDisplayValue();
        display.setText(formatNumber(-val));
    }

    private void onPercent() {
        double val = getDisplayValue();
        display.setText(formatNumber(val / 100.0));
    }

    private double calculate(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) { display.setText("Error"); startNewNumber = true; return a; }
                return a / b;
            default: return b;
        }
    }

    private double getDisplayValue() {
        try { return Double.parseDouble(display.getText()); }
        catch (NumberFormatException e) { return 0; }
    }

    private String formatNumber(double val) {
        if (val == Math.floor(val) && !Double.isInfinite(val) && Math.abs(val) < 1e15) {
            return String.valueOf((long) val);
        }
        String s = String.format("%.10f", val).replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    private void handleKey(KeyEvent e) {
        char c = e.getKeyChar();
        int code = e.getKeyCode();
        if (Character.isDigit(c))          onDigit(String.valueOf(c));
        else if (c == '+')                 onOperator("+");
        else if (c == '-')                 onOperator("-");
        else if (c == '*')                 onOperator("*");
        else if (c == '/')                 { e.consume(); onOperator("/"); }
        else if (c == '.' || c == ',')     onDot();
        else if (c == '%')                 onPercent();
        else if (code == KeyEvent.VK_ENTER || c == '=') onEquals();
        else if (code == KeyEvent.VK_BACK_SPACE)        onBackspace();
        else if (code == KeyEvent.VK_ESCAPE)            onClear();
    }
}

