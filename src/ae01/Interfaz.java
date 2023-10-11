package ae01;

import javax.swing.*;
import javax.swing.ButtonGroup;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Interfaz extends JFrame {
    private JTextField directoryField;
    private JButton bttnBrowse;
    private JButton bttnMerge;
    private JButton bttnSearch;
    private JButton bttnListFiles;
    private JTextArea fileListTextArea;

    private List<File> arxiusSeleccionats = new ArrayList<>();

    private String ordenarPer = "name"; 
    private boolean ascendent = true;

    public Interfaz() {
    	setBackground(Color.GRAY);
    	setResizable(false);
        setTitle("Gestió de Fitxers de Text");
        setSize(450, 321);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JFrame frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(Color.LIGHT_GRAY);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        directoryField = new JTextField();
        directoryField.setEditable(false);
        directoryField.setBounds(10, 11, 414, 20);
        contentPane.add(directoryField);
        directoryField.setColumns(10);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(170, 53, 254, 170);
        contentPane.add(scrollPane);
        
        fileListTextArea = new JTextArea();
        fileListTextArea.setEditable(false);
        scrollPane.setViewportView(fileListTextArea);

        bttnBrowse = new JButton("Navegar");
        bttnBrowse.setBounds(23, 77, 122, 23);
        contentPane.add(bttnBrowse);

        bttnMerge = new JButton("Fusionar Fitxers");
        bttnMerge.setBounds(23, 179, 122, 23);
        contentPane.add(bttnMerge);

        bttnSearch = new JButton("Cercar");
        bttnSearch.setBounds(23, 145, 122, 23);
        contentPane.add(bttnSearch);

        bttnListFiles = new JButton("Llistar Fitxers");
        bttnListFiles.setBounds(23, 111, 122, 23);
        contentPane.add(bttnListFiles);
        
        JComboBox cmbFilter = new JComboBox();
        cmbFilter.setModel(new DefaultComboBoxModel(new String[] {"Nom", "Grandaria", "Data Modificacio"}));
        cmbFilter.setSelectedIndex(0);
        cmbFilter.setToolTipText("");
        cmbFilter.setBounds(170, 230, 130, 22);
        contentPane.add(cmbFilter);
        
        ButtonGroup radioButtonGroup = new ButtonGroup();
        
        JRadioButton rdbtnAsc = new JRadioButton("Ascendent");
        rdbtnAsc.setBackground(Color.LIGHT_GRAY);
        rdbtnAsc.setBounds(306, 230, 109, 23);
        contentPane.add(rdbtnAsc);
        
        JRadioButton rdbtnDesc = new JRadioButton("Descendent");
        rdbtnDesc.setBackground(Color.LIGHT_GRAY);
        rdbtnDesc.setBounds(306, 249, 109, 23);
        contentPane.add(rdbtnDesc);

        radioButtonGroup.add(rdbtnAsc);
        radioButtonGroup.add(rdbtnDesc);	

        bttnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int resultat = fileChooser.showOpenDialog(null);
                if (resultat == JFileChooser.APPROVE_OPTION) {
                    File directoriSeleccionat = fileChooser.getSelectedFile();
                    directoryField.setText(directoriSeleccionat.getAbsolutePath());
                }
            }
        });

        bttnMerge.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String directori = directoryField.getText();
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(true);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Arxius de text", "txt");
                fileChooser.setFileFilter(filter);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    
                    if (files.length > 0) {
                        directori = files[0].getParent();
                    }
                    
                    Collections.addAll(arxiusSeleccionats, files);
                    Principal.MostrarLlistaFitxers(arxiusSeleccionats, fileListTextArea);
                    String nouArxiuNom = JOptionPane.showInputDialog(frame, "Nom de l'arxiu fusionat:");
                    Principal.FusionarArxius(arxiusSeleccionats, nouArxiuNom, directori);
                    JOptionPane.showMessageDialog(frame, "Arxius fusionats amb exit");
                }
            }
        });


        bttnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String directori = directoryField.getText();
                String stringBusqueda = JOptionPane.showInputDialog("Introdueix el string a cercar:");
                List<File> arxiusTxt = Principal.LlistarFitxersTxt(directori, ordenarPer, ascendent);
                StringBuilder resultat = new StringBuilder();
                for (File arxiuTxt : arxiusTxt) {
                    int count = Principal.CercarEnFitxer(arxiuTxt, stringBusqueda);
                    resultat.append(arxiuTxt.getName()).append(" -> ").append(count).append(" coincidències\n");
                }
                fileListTextArea.setText(resultat.toString());
            }
        });

        bttnListFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ordenarPer = cmbFilter.getSelectedItem().toString();
                ascendent = rdbtnAsc.isSelected();
                String directori = directoryField.getText();
                List<File> arxiusTxt = Principal.LlistarFitxersTxt(directori, ordenarPer, ascendent);
                Principal.MostrarLlistaFitxers(arxiusTxt, fileListTextArea);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Interfaz app = new Interfaz();
                app.setVisible(true);
            }
        });
    }
}
