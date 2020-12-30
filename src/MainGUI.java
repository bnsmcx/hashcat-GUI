import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class MainGUI extends JFrame implements ActionListener {

    // hashQueue for all our hashes
    HashQueue hashQueue = new HashQueue();

    // File objects for input hashes and wordlists
    static File inputFile = new File(System.getProperty("user.dir"));
    static File wordlist = new File("/home/daisy/parrot/rockyou.txt");
    static String cewlOutputPath = "";
    static String rulePath = "";

    // inputPanel components
    private final JButton addHashButton = new JButton("Add Hash");
    private final JButton inputFileButton = new JButton("Load Hashes From File");
    private final JTextField inputHash = new JTextField();
    private final JButton magicButton = new JButton("Magic");
    private final JButton magicInfoButtion = new JButton("?");
    JPanel inputPanel = new JPanel();

    // outputPanel components
    JPanel outputPanel = new JPanel();
    String[] columnNames = {"Hash", "Type", "Password"};
    String[][] data = new String[100][3];
    JTable hashTable = new JTable(data, columnNames);
    JScrollPane scrollPane= new JScrollPane(hashTable);

    // wordlistPanel components
    JPanel wordlistPanel = new JPanel();
    JButton selectWordlistButton = new JButton("Set Wordlist");
    JTextField wordlistPathText = new JTextField(wordlist.getAbsolutePath());
    JButton customWordlistButton = new JButton("Generate Wordlist with CeWL");

    // maskPanel components
    JPanel maskPanel = new JPanel();
    JButton createRuleButton = new JButton("Create Rule");
    Dimension textDimensionLong = new Dimension(300, 25);
    Dimension textDimensionShort = new Dimension(150, 25);
    JButton setRuleButton = new JButton("Set Rule");
    JTextField rulePathText = new JTextField();
    JButton clearRuleButton = new JButton("Clear Rule");

    // attackPanel components
    JPanel attackPanel = new JPanel();
    JLabel commandLabel = new JLabel("Command to execute:");
    JTextField commandText = new JTextField();
    JButton attackButton = new JButton("Attack");

    // constructor
    public MainGUI() {

        // frame setup
        super("Hashcat GUI");
        setSize(1000, 500);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // panel and tabbed pane config
        JTabbedPane tabbedPane = new JTabbedPane();
        inputPanel.setLayout(new FlowLayout());
        outputPanel.setLayout(new FlowLayout());
        maskPanel.setLayout(new FlowLayout());
        scrollPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()/7 * 5));
        inputHash.setPreferredSize(textDimensionLong);
        tabbedPane.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()/6));
        wordlistPathText.setPreferredSize(textDimensionLong);
        rulePathText.setPreferredSize(textDimensionLong);
        commandText.setPreferredSize(new Dimension(650, 25));
        commandText.setEditable(false);
        updateAttack();

        // populate panels
        inputPanel.add(addHashButton);
        inputPanel.add(inputHash);
        inputPanel.add(inputFileButton);
        inputPanel.add(magicButton);
        inputPanel.add(magicInfoButtion);
        outputPanel.add(scrollPane);
        wordlistPanel.add(selectWordlistButton);
        wordlistPanel.add(wordlistPathText);
        wordlistPanel.add(customWordlistButton);
        maskPanel.add(clearRuleButton);
        maskPanel.add(setRuleButton);
        maskPanel.add(rulePathText);
        maskPanel.add(createRuleButton);
        attackPanel.add(commandLabel);
        attackPanel.add(commandText);
        attackPanel.add(attackButton);

        // populate tabbed top panel
        tabbedPane.add("Input Hashes", inputPanel);
        tabbedPane.add("Choose Wordlist", wordlistPanel);
        tabbedPane.add("Configure Masks", maskPanel);
        tabbedPane.add("Attack", attackPanel);

        // populate frame with top level containers
        add(tabbedPane);
        add(outputPanel);

        // listen for action on buttons
        inputFileButton.addActionListener(this);
        addHashButton.addActionListener(this);
        magicButton.addActionListener(this);
        selectWordlistButton.addActionListener(this);
        customWordlistButton.addActionListener(this);
        magicInfoButtion.addActionListener(this);
        createRuleButton.addActionListener(this);
        setRuleButton.addActionListener(this);
        attackButton.addActionListener(this);
        clearRuleButton.addActionListener(this);


    } // end constructor
    // action listener

    @Override
    public void actionPerformed(ActionEvent event) {
        // determine which button was clicked
        String buttonClicked = event.getActionCommand();

        // take appropriate action based on which button was selected
        try {
            switch (buttonClicked) {
                case "Add Hash":
                    String hash = inputHash.getText();
                    if (hash.length() > 0) {
                        hashQueue.addHash(hash);
                        updateTable();
                    };

                    break;
                case "Load Hashes From File":
                    JFileChooser jfc = new JFileChooser();
                    jfc.setCurrentDirectory(inputFile);
                    int returnValue = jfc.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) inputFile = jfc.getSelectedFile();
                    hashQueue.addHash(inputFile);
                    updateTable();
                    break;
                case "Magic":
                    attack();
                    updateTable();
                    break;
                case "Set Wordlist":
                    JFileChooser wordlistChooser = new JFileChooser();
                    wordlistChooser.setCurrentDirectory(wordlist);
                    int rv = wordlistChooser.showOpenDialog(null);
                    if (rv == JFileChooser.APPROVE_OPTION) wordlist = wordlistChooser.getSelectedFile();
                    wordlistPathText.setText(wordlist.getAbsolutePath());
                    hashQueue.wordlist = wordlist.getAbsolutePath();
                    break;
                case "Set Rule":
                    File rule = new File(System.getProperty("user.dir"));
                    JFileChooser ruleChooser = new JFileChooser();
                    ruleChooser.setCurrentDirectory(rule);
                    int ruleVariable = ruleChooser.showOpenDialog(null);
                    if (ruleVariable == JFileChooser.APPROVE_OPTION) rule = ruleChooser.getSelectedFile();
                    rulePath = rule.getAbsolutePath();
                    rulePathText.setText(rulePath);
                    updateAttack();
                    break;
                case "Generate Wordlist with CeWL":
                    JFrame cewlDialog = new JFrame("CeWL");
                    JButton run = new JButton("Run");
                    JLabel urlLabel = new JLabel("Target URL:");
                    JTextField targetUrl = new JTextField();
                    JLabel depthLabel = new JLabel("Depth to spider:");
                    JTextField depth = new JTextField();
                    JLabel outputLocation = new JLabel("CeWL output not set.");
                    JButton save = new JButton("Set CeWL output");
                    targetUrl.setPreferredSize(textDimensionShort);
                    depth.setPreferredSize(textDimensionShort);
                    cewlDialog.setSize(200, 250);
                    cewlDialog.setLayout(new FlowLayout());
                    cewlDialog.setResizable(true);
                    cewlDialog.add(urlLabel);
                    cewlDialog.add(targetUrl);
                    cewlDialog.add(depthLabel);
                    cewlDialog.add(depth);
                    cewlDialog.add(outputLocation);
                    cewlDialog.add(save);
                    cewlDialog.add(run);
                    cewlDialog.setLocationRelativeTo(null);
                    cewlDialog.setVisible(true);

                    save.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JFileChooser fileChooser = new JFileChooser();
                            int option = fileChooser.showSaveDialog(null);
                            if(option == JFileChooser.APPROVE_OPTION) {
                                File file = fileChooser.getSelectedFile();
                                outputLocation.setText(file.getName());
                                cewlOutputPath = file.getAbsolutePath();
                            }
                        }
                    });

                    run.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            cewl(depth.getText(), targetUrl.getText(), cewlOutputPath);
                            cewlDialog.dispose();
                        }
                    });
                    break;
                case "?":
                    JOptionPane.showMessageDialog(null,
                            "Clicking the 'Magic' button will perform a default dictionary attack\n " +
                                    "against all the provided hashes.  It is great for catching low hanging\n "+
                                    "fruit but beware that depending on the size of the list and the types\n "+
                                    "of hashes this can take a long time.");
                    break;
                case "Attack":
                    attack();
                    break;
                case "Clear Rule":
                    rulePath = "";
                    rulePathText.setText(rulePath);
                    updateAttack();
                    break;
                case "Create Rule":
                    JFrame createRuleFrame = new JFrame("Create Rule");
                    createRuleFrame.setSize(375,200);
                    createRuleFrame.setResizable(true);
                    JButton maskInfoButton = new JButton(" ? ");
                    JButton create = new JButton("Create");
                    JLabel prefixLabel = new JLabel("Prefix:");
                    JLabel postfixLabel = new JLabel("Postfix:");
                    JTextField prefixField = new JTextField("?d");
                    JTextField postfixField = new JTextField("?d");
                    prefixField.setPreferredSize(textDimensionLong);
                    postfixField.setPreferredSize(textDimensionLong);
                    JLabel capitalizationLabel = new JLabel("Manipulate Capitalization:");
                    JComboBox<String> capitalizationCombo = new JComboBox<String>(new String[]{"-", "All Uppercase",
                            "All Lowercase", "Capital first, lower rest", "Lower first, Capital rest", "Toggle Capitalization"});
                    maskInfoButton.addActionListener(this);
                    createRuleFrame.setLayout(new FlowLayout());
                    createRuleFrame.add(prefixLabel);
                    createRuleFrame.add(prefixField);
                    createRuleFrame.add(postfixLabel);
                    createRuleFrame.add(postfixField);
                    createRuleFrame.add(capitalizationLabel);
                    createRuleFrame.add(capitalizationCombo);
                    createRuleFrame.add(create);
                    createRuleFrame.add(maskInfoButton);
                    createRuleFrame.setLocationRelativeTo(null);
                    createRuleFrame.setVisible(true);

                    create.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JFileChooser fileChooser = new JFileChooser();
                            int option = fileChooser.showSaveDialog(null);
                            if(option == JFileChooser.APPROVE_OPTION) {
                               rulePath = fileChooser.getSelectedFile().getAbsolutePath();
                            }
                            maskProcessor(prefixField.getText(), postfixField.getText(),
                                    (String) capitalizationCombo.getSelectedItem(), rulePath);
                        }
                    });

                    maskInfoButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JOptionPane.showMessageDialog(null,
                                    "Character sets are referenced with a '?'\n" +
                                            "i.e. upper case alpha characters are '?u'\n" +
                                            "\n" +
                                            "\n" +
                                            "Built-in charsets:\n" +
                                            "\n" +
                                            "  ?l = abcdefghijklmnopqrstuvwxyz\n" +
                                            "  ?u = ABCDEFGHIJKLMNOPQRSTUVWXYZ\n" +
                                            "  ?d = 0123456789\n" +
                                            "  ?s =  !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n" +
                                            "  ?a = ?l?u?d?s\n" +
                                            "  ?b = 0x00 - 0xff\n");
                        }
                    });
                    updateAttack();
                    break;
            }
        } // end try

        // alert a user if no input detected
        catch (StringIndexOutOfBoundsException message) {
            JOptionPane.showMessageDialog(null, "No input detected");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No input detected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // end actionPerformed

    private void updateAttack() {
        String rule = "";
        if (rulePath.length() > 0) rule = " -r " + rulePath;
        String command = String.format("hashcat --force -m <mode>%s <hash> " + wordlist, rule);
        commandText.setText(command);
    }

    private void attack() {
        if (hashQueue.hashes.size() < 1) {
            JOptionPane.showMessageDialog(null, "No hashes to crack.");
            return;
        }
        try {
            hashQueue.attack(wordlist.getAbsolutePath(), rulePath);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Please set a valid wordlist.");
        }
        updateTable();
    }

    private void maskProcessor(String prefix, String postfix, String capitalization, String rulePath) {
        switch (capitalization) {
            case "-":
                capitalization = "";
                break;
            case "All Uppercase":
                capitalization = "u";
                break;
            case "All Lowercase":
                capitalization = "l";
                break;
            case "Capital first, lower rest":
                capitalization = "c";
                break;
            case "Lower first, Capital rest":
                capitalization = "C";
                break;
            case "Toggle Capitalization":
                capitalization = "t";
                break;
        }
        if (prefix != null) prefix = "^" + prefix;
        if (postfix != null) postfix = "$" + postfix;
        String[] command = {"maskprocessor", String.valueOf(capitalization + " " + prefix + " " + postfix), "-o", rulePath};
        rulePathText.setText(rulePath);
        try {
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void cewl(String depth, String url, String cewlOutputPath) {
        url = url.replace("http://", "");
        url = url.replace("https://", "");
        String command = String.format("cewl -d  %s -w %s https://%s", depth, cewlOutputPath, url);
        try {
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        wordlist = new File(cewlOutputPath);
        wordlistPathText.setText(wordlist.getAbsolutePath());
    }

    private void updateTable() {

        data = new String[hashQueue.hashes.size()][3];
        int i = 0;
        for (HashQueue.Hash hash : hashQueue.hashes) {

            data[i][0] = hash.hash;
            data[i][1] = hash.verifiedHashType;
            data[i][2] = hash.password;

            i++;
        }
        JTable updatedTable = new JTable(data, columnNames);
        hashTable.setModel(updatedTable.getModel());
    }

    public static void main(String[] args) {
        MainGUI window = new MainGUI();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    } // end main method
} // end Main class

