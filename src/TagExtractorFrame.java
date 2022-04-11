import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

public class TagExtractorFrame extends JFrame{
    ArrayList<String> noiseWords = new ArrayList<>();
    TreeMap<String, Integer> map = new TreeMap<>();
    String[] words;
    boolean deny;
    JPanel mainPnl;
    JPanel chooserPnl;
    JButton txtFileBtn;
    JFileChooser txtFileChooser;
    File txtSelectedFile;
    JButton noiseFileBtn;
    JFileChooser noiseFileChooser;
    File noiseSelectedFile;
    JButton startBtn;
    JPanel txtAreaPnl;
    JTextArea txtArea;
    JScrollPane scroller;
    JPanel ctrlPnl;
    JButton quitBtn;
    JButton clearBtn;
    JButton saveBtn;
    JFileChooser saveTxtFile;
    File workingDirectory = new File(System.getProperty("user.dir"));
    TagExtractorFrame(){
        setTitle("Tag Extractor");
        mainPnl = new JPanel();
        setLayout(new BorderLayout());
        setSize(1050, 950);
        setResizable(false);
        createChooserPnl();
        mainPnl.add(chooserPnl, BorderLayout.NORTH);
        createTxtAreaPnl();
        mainPnl.add(txtAreaPnl, BorderLayout.CENTER);
        createCtrlPnl();
        mainPnl.add(ctrlPnl, BorderLayout.SOUTH);
        add(mainPnl);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    private void createChooserPnl(){
        chooserPnl = new JPanel();
        chooserPnl.setLayout(new GridLayout(1, 2));
        txtFileBtn = new JButton();
        txtFileBtn.setText("Select text file");
        txtFileBtn.setFont(new Font("Gaegu", Font.BOLD, 20));
        txtFileBtn.addActionListener((ActionEvent ae) ->{
            txtFileChooser = new JFileChooser();
            txtFileChooser.setCurrentDirectory(workingDirectory);
            txtFileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt","text"));
            int result = txtFileChooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION){
                txtSelectedFile = txtFileChooser.getSelectedFile();
                txtFileBtn.setText(txtSelectedFile.getName());
            }
            else if(result == JFileChooser.CANCEL_OPTION){
                txtSelectedFile = null;
            }
        });
        noiseFileBtn = new JButton();
        noiseFileBtn.setText("Select tag file");
        noiseFileBtn.setFont(new Font("Gaegu", Font.BOLD, 20));
        noiseFileBtn.addActionListener((ActionEvent ae) -> {
            noiseFileChooser = new JFileChooser();
            noiseFileChooser.setCurrentDirectory(workingDirectory);
            noiseFileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
            int result = noiseFileChooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION){
                noiseSelectedFile = noiseFileChooser.getSelectedFile();
                noiseFileBtn.setText(noiseSelectedFile.getName());
            }
            else if(result == JFileChooser.CANCEL_OPTION){
                noiseSelectedFile = null;
            }
        });
        startBtn = new JButton();
        startBtn.setText("Start");
        startBtn.setFont(new Font("Gaegu", Font.BOLD, 20));
        startBtn.addActionListener((ActionEvent ae) -> {
            if (noiseSelectedFile == null && txtSelectedFile == null) {
                JOptionPane.showMessageDialog(this, "Select text and noise file before starting tag extractor", "[ERROR]", JOptionPane.ERROR_MESSAGE);
            } else if (noiseSelectedFile == null) {
                JOptionPane.showMessageDialog(this, "Select noise file before starting tag extractor", "[ERROR]", JOptionPane.ERROR_MESSAGE);
            } else if (txtSelectedFile == null) {
                JOptionPane.showMessageDialog(this, "Select text file before starting tag extractor", "[ERROR]", JOptionPane.ERROR_MESSAGE);
            } else {
                txtArea.append("=========================================================\n       File Name: " + txtSelectedFile.getName() + "\n=========================================================\n");
                txtArea.append("\n");
                validWords();
                display();
            }
        });
        chooserPnl.add(txtFileBtn);
        chooserPnl.add(noiseFileBtn);
        chooserPnl.add(startBtn);
    }
    private void createTxtAreaPnl(){
        txtAreaPnl = new JPanel();
        txtArea = new JTextArea(30, 47);
        txtArea.setFont(new Font("Hack", Font.BOLD, 20));
        txtArea.setEditable(false);
        scroller = new JScrollPane(txtArea);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        txtAreaPnl.add(scroller);
    }
    private void createCtrlPnl() {
        ctrlPnl = new JPanel();
        ctrlPnl.setLayout(new GridLayout(1, 2));
        clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Gaegu", Font.BOLD, 20));
        clearBtn.addActionListener((ActionEvent ae) -> {
            txtArea.setText("");
            txtSelectedFile = null;
            txtFileBtn.setText("Select text file");
            noiseSelectedFile = null;
            noiseFileBtn.setText("Select tag file");
        });
        quitBtn = new JButton("Quit");
        quitBtn.setFont(new Font("Gaegu", Font.BOLD, 20));
        quitBtn.addActionListener((ActionEvent ae) -> System.exit(0));

        saveBtn = new JButton("Save");
        saveBtn.setFont(new Font("Gaegu", Font.BOLD, 20));
        saveBtn.addActionListener((ActionEvent ae) -> {
            if (txtArea.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "File is Null. Please Start Tag Extractor", "[ERROR]", JOptionPane.ERROR_MESSAGE);
            } else {
                saveTxtFile = new JFileChooser();
                saveTxtFile.setCurrentDirectory(workingDirectory);
                saveTxtFile.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text"));
                int result = saveTxtFile.showSaveDialog(this);
                File file = saveTxtFile.getSelectedFile();
                BufferedWriter writer;
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        writer = new BufferedWriter(new FileWriter(file));
                        writer.write(txtArea.getText());
                        writer.close();
                        JOptionPane.showMessageDialog(this, "The file was saved successfully!", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(this, "The file could not be saved!", "[ERROR]", JOptionPane.ERROR_MESSAGE);

                    }
                }
            }
        });
        ctrlPnl.add(saveBtn);
        ctrlPnl.add(clearBtn);
        ctrlPnl.add(quitBtn);
    }
    private void validWords(){
        Scanner noiseFileSc = null;
        try {
            noiseFileSc = new Scanner(noiseSelectedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(true){
            assert noiseFileSc != null;
            if (!noiseFileSc.hasNextLine()) break;
            String line = noiseFileSc.nextLine();
            noiseWords.add(line.toLowerCase());
        }
        Scanner txtFileSc = null;
        try {
            txtFileSc = new Scanner(txtSelectedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(true){
            assert txtFileSc != null;
            if (!txtFileSc.hasNextLine()) break;
            String line = txtFileSc.nextLine().toLowerCase();
            words = line.split("[^a-zA-Z]+");
            for (String word : words){
                deny = false;
                for(String noise : noiseWords){
                    if(word.equals(noise)){
                        deny = true;
                        break;
                    }
                }
                if(!deny){
                    if(!map.containsKey(word)){
                        map.put(word, 1);
                    }
                    else{
                        map.put(word, map.get(word) + 1);
                    }
                }
            }
        }
    }
    private void display(){
        for(String key: map.keySet()){
            if(key.length() > 6){
                txtArea.append(" Word  \"" + key + "\"\t\tdetected " + map.get(key) + " times!\n");
            }
            else if(key.length() < 5){
                txtArea.append(" Word  \"" + key + "\"\t\t\tdetected " + map.get(key) + " times!\n");
            }
            else if(key.length() > 15){
                txtArea.append(" Word  \"" + key + "\"\tdetected " + map.get(key) + " times!\n");
            }
        }
    }
}