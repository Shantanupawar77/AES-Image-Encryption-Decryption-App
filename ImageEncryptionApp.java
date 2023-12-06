import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.*;
import javax.swing.*;

public class ImageEncryptionApp {

    private JFrame frame;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton selectFileButton;
    private JLabel selectedFileLabel;
    private JLabel keyLabel;
    private JTextField keyField;
    private File selectedFile;
    private File encryptedFile;
    private File decryptedFile;
    private SecretKey encryptionKey;

    public ImageEncryptionApp() {
        frame = new JFrame("Image Encryption and Decryption");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400); 
        frame.setContentPane(new ImagePanel());

        selectedFileLabel = new JLabel("Selected File");
        keyLabel = new JLabel("Encryption Key");
        keyField = new JTextField(20);
        selectFileButton = new JButton("Select Image");
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");

        // Set button backgrounds and text color
        selectFileButton.setBackground(Color.BLACK);
        selectFileButton.setForeground(Color.BLACK);
        encryptButton.setBackground(Color.BLACK);
        encryptButton.setForeground(Color.BLACK);
        decryptButton.setBackground(Color.BLACK);
        decryptButton.setForeground(Color.BLACK);

        // Set font and style for labels
        Font labelFont = new Font(
                selectedFileLabel.getFont().getName(),
                Font.BOLD,
                30);
     
        selectedFileLabel.setFont(labelFont);
        selectedFileLabel.setForeground(Color.black);
        keyLabel.setFont(labelFont);
        keyLabel.setForeground(Color.darkGray);

        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(selectedFileLabel, gbc);
        gbc.gridy++;
        frame.add(keyLabel, gbc);
        gbc.gridy++;
        frame.add(keyField, gbc);
        gbc.gridy++;
        frame.add(selectFileButton, gbc);
        gbc.gridy++;
        frame.add(encryptButton, gbc);
        gbc.gridy++;
        frame.add(decryptButton, gbc);

        selectFileButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        int returnValue = fileChooser.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            selectedFile = fileChooser.getSelectedFile();
                            selectedFileLabel.setText(
                                    "Selected File: " + selectedFile.getName());
                        }
                    }
                });

        encryptButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            encryptFile();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

        decryptButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            decryptFile();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

        frame.setVisible(true);
    }

    public void encryptFile()
            throws NoSuchAlgorithmException, InvalidKeyException {
        if (selectedFile == null || !selectedFile.exists()) {
            return;
        }

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // 128-bit key for AES encryption
            encryptionKey = keyGen.generateKey(); // Store the key for decryption

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);

            FileInputStream input = new FileInputStream(selectedFile);
            encryptedFile = new File(
                    selectedFile.getParentFile(),
                    "encrypted_" + selectedFile.getName() + ".txt");
            FileOutputStream output = new FileOutputStream(encryptedFile);
            CipherOutputStream cipherOutput = new CipherOutputStream(output, cipher);

            int b;
            while ((b = input.read()) != -1) {
                cipherOutput.write(b);
            }

            input.close();
            cipherOutput.close();

            selectedFileLabel.setText("Selected File: Encrypted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decryptFile()
            throws NoSuchAlgorithmException, InvalidKeyException {
        if (encryptedFile == null || !encryptedFile.exists()) {
            return;
        }

        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey); // Use the stored key

            encryptedFile = new File(
                    selectedFile.getParentFile(),
                    "encrypted_" + selectedFile.getName() + ".txt");
            decryptedFile = new File(
                    selectedFile.getParentFile(),
                    "decrypted_" + selectedFile.getName());
            FileInputStream input = new FileInputStream(encryptedFile);
            FileOutputStream output = new FileOutputStream(decryptedFile);
            CipherInputStream cipherInput = new CipherInputStream(input, cipher);

            int b;
            while ((b = cipherInput.read()) != -1) {
                output.write(b);
            }

            input.close();
            output.close();
            cipherInput.close();

            selectedFileLabel.setText("Selected File: Decrypted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ImageEncryptionApp();
        });
    }

    // Custom JPanel for setting the background image
    class ImagePanel extends JPanel {

        private Image backgroundImg;

        public ImagePanel() {
            backgroundImg =
                    new ImageIcon(".\\background.jpg").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImg != null) {
                g.drawImage(
                        backgroundImg,
                        0,
                        0,
                        this.getWidth(),
                        this.getHeight(),
                        this);
            }
        }
    }
}