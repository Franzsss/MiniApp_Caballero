/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.desktopapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class HomePanel extends JPanel {

    private JTabbedPane tabbedPane;

    public HomePanel(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 32));
        buildUI();
    }

    private void buildUI() {
        
        //mao ni ang Header nato
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(20, 20, 32));
        topPanel.setBorder(new EmptyBorder(50, 40, 20, 40));

        JLabel appName = new JLabel("FranzNuke");
        appName.setFont(new Font("Segoe UI Light", Font.PLAIN, 48));
        appName.setForeground(Color.WHITE);
        appName.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel dateLabel = new JLabel(getTodayDate());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(new Color(120, 120, 150));
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dateLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        topPanel.add(appName,   BorderLayout.NORTH);
        topPanel.add(dateLabel, BorderLayout.SOUTH);

        //mao ni cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        cardsPanel.setBackground(new Color(20, 20, 32));
        cardsPanel.setBorder(new EmptyBorder(30, 60, 30, 60));

        cardsPanel.add(makeCard(
            "Calculator",
            "Perform quick calculations\nwith keyboard support",
            new Color(255, 149, 0),
            new Color(40, 30, 15),
            1
        ));

        cardsPanel.add(makeCard(
            "Notes",
            "Write, save and manage\nyour personal notes",
            new Color(100, 160, 255),
            new Color(15, 25, 45),
            2
        ));

        // Instruction para sa atong user
        JLabel tip = new JLabel("Click a card");
        tip.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        tip.setForeground(new Color(100, 100, 130));
        tip.setHorizontalAlignment(SwingConstants.CENTER);
        tip.setBorder(new EmptyBorder(0, 0, 30, 0));

        add(topPanel,   BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
        add(tip,        BorderLayout.SOUTH);
    }

    private JPanel makeCard(String title, String desc, Color accent, Color bg, int tabIndex) {

        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(28, 28, 28, 28));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JPanel accentLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 4, 4));
                g2.dispose();
            }
        };
        accentLine.setOpaque(false);
        accentLine.setPreferredSize(new Dimension(0, 4));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(accent);

        // Description
        JTextArea descArea = new JTextArea(desc);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setForeground(new Color(180, 180, 200));
        descArea.setBackground(new Color(0, 0, 0, 0));
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        // mo toggle sa buttons
        JButton openBtn = new JButton("Open " + title + " →");
        openBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        openBtn.setForeground(accent);
        openBtn.setBackground(new Color(255, 255, 255, 20));
        openBtn.setOpaque(true);
        openBtn.setFocusPainted(false);
        openBtn.setBorderPainted(false);
        openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openBtn.setBorder(new EmptyBorder(8, 0, 0, 0));

        // action or switching tabs ni
        ActionListener goToTab = e -> tabbedPane.setSelectedIndex(tabIndex);
        openBtn.addActionListener(goToTab);
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { tabbedPane.setSelectedIndex(tabIndex); }
            @Override public void mouseEntered(MouseEvent e) { card.repaint(); }
        });

        JPanel textSection = new JPanel(new BorderLayout(0, 8));
        textSection.setOpaque(false);
        textSection.add(titleLabel, BorderLayout.NORTH);
        textSection.add(descArea,   BorderLayout.CENTER);
        textSection.add(openBtn,    BorderLayout.SOUTH);

        card.add(accentLine,  BorderLayout.NORTH);
        card.add(textSection, BorderLayout.CENTER);

        return card;
    }

    private String getTodayDate() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return today.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
    }
}