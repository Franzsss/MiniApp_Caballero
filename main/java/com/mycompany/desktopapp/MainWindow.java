/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.desktopapp;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private JTabbedPane tabbedPane;

    public MainWindow() {
        initComponents();
    }

    private void initComponents() {
        setTitle("MiniApp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(20, 20, 32));

        //para sa home ayaw na hilabti
        HomePanel homePanel = new HomePanel(tabbedPane);
        tabbedPane.addTab("  Home", homePanel);

        //same para sa calcu donut hilabti
        CalculatorPanel calcPanel = new CalculatorPanel();
        tabbedPane.addTab("  Calculator", calcPanel);

        //same para pannel
        NotesPanel notesPanel = new NotesPanel();
        tabbedPane.addTab("  Notes", notesPanel);

        add(tabbedPane);
    }
}