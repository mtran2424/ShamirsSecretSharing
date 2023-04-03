import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.HashMap;

/**
 * @author My Tran
 * @version 1.0
 * @description program demonstrates SSS algorithm and its homomorphic
 *              properties on addition and scalar division via a GUI application
 */

public class SecretSharing extends JFrame implements ActionListener {

    BufferedImage secretImage, decryptedImage;
    BufferedImage downscaledDecrypted = null;
    Boolean downscaled = false;

    ImageCanvas decryptedImageCanvas;
    ImageCanvas secretCanvas;
    int selectedCount = 0;

    Share shareArray[];
    Share downscaleShareArray[];
    SSS secretSharing;

    // Radio buttons for selecting shares
    JRadioButton shareRadioButton0 = new JRadioButton("Share 0:");
    JRadioButton shareRadioButton1 = new JRadioButton("Share 1:");
    JRadioButton shareRadioButton2 = new JRadioButton("Share 2:");
    JRadioButton shareRadioButton3 = new JRadioButton("Share 3:");
    JRadioButton shareRadioButton4 = new JRadioButton("Share 4:");

    // Canvases containing share images
    ImageCanvas shareCanvas0;
    ImageCanvas shareCanvas1;
    ImageCanvas shareCanvas2;
    ImageCanvas shareCanvas3;
    ImageCanvas shareCanvas4;

    // Control buttons
    JButton decryptButton;
    JButton resetButton;
    JButton downscaleButton;
    JButton errorButton;

    // Mappings of components to indices
    HashMap<Integer, JRadioButton> radioMap;
    HashMap<Integer, ImageCanvas> shareMap;

    // Information field
    JLabel meanAvgLabel = new JLabel("Mean Average Error: ");
    JTextField MeanAvgError;

    // Used to change and fix scale of images in GUI
    int fixedImageHeight = 150;

    // Event listener/handler
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // Share radio buttons make sure that the max amount of buttons that can be
        // clicked are the minimum shares k
        if (src == shareRadioButton0) {
            if (shareRadioButton0.isSelected()) {
                selectedCount++;

                // Deselect another radio button if there are two selected before the click
                if (selectedCount > 2) {
                    for (int i : radioMap.keySet()) {
                        if (radioMap.get(i).isSelected() && i != 0 && selectedCount > 2) {
                            radioMap.get(i).setSelected(false);
                            selectedCount--;
                        }
                    }
                }
            } else {// If it has been unselected, reduce count
                selectedCount--;
            }

            // enable the decryt button when there are enough shares selected to
            // reconstruct. Disabled otherwise.
            if (selectedCount == 2) {
                decryptButton.setEnabled(true);
            } else {
                decryptButton.setEnabled(false);
            }
        }
        if (src == shareRadioButton1) {
            if (shareRadioButton1.isSelected()) {
                selectedCount++;

                // Deselect another radio button if there are two selected before the click
                if (selectedCount > 2) {
                    for (int i : radioMap.keySet()) {
                        if (radioMap.get(i).isSelected() && i != 1 && selectedCount > 2) {
                            radioMap.get(i).setSelected(false);
                            selectedCount--;
                        }
                    }
                }

            } else {// If it has been unselected, reduce count
                selectedCount--;
            }

            // enable the decryt button when there are enough shares selected to
            // reconstruct. Disabled otherwise.
            if (selectedCount == 2) {
                decryptButton.setEnabled(true);
            } else {
                decryptButton.setEnabled(false);
            }
        }
        if (src == shareRadioButton2) {
            if (shareRadioButton2.isSelected()) {
                selectedCount++;

                // Deselect another radio button if there are two selected before the click
                if (selectedCount > 2) {
                    for (int i : radioMap.keySet()) {
                        if (radioMap.get(i).isSelected() && i != 2 && selectedCount > 2) {
                            radioMap.get(i).setSelected(false);
                            selectedCount--;
                        }
                    }
                }

            } else {// If it has been unselected, reduce count
                selectedCount--;
            }

            // enable the decryt button when there are enough shares selected to
            // reconstruct. Disabled otherwise.
            if (selectedCount == 2) {
                decryptButton.setEnabled(true);
            } else {
                decryptButton.setEnabled(false);
            }
        }
        if (src == shareRadioButton3) {
            if (shareRadioButton3.isSelected()) {
                selectedCount++;

                // Deselect another radio button if there are two selected before the click
                if (selectedCount > 2) {
                    for (int i : radioMap.keySet()) {
                        if (radioMap.get(i).isSelected() && i != 3 && selectedCount > 2) {
                            radioMap.get(i).setSelected(false);
                            selectedCount--;
                        }
                    }
                }

            } else {// If it has been unselected, reduce count
                selectedCount--;
            }

            // enable the decryt button when there are enough shares selected to
            // reconstruct. Disabled otherwise.
            if (selectedCount == 2) {
                decryptButton.setEnabled(true);
            } else {
                decryptButton.setEnabled(false);
            }
        }
        if (src == shareRadioButton4) {
            if (shareRadioButton4.isSelected()) {
                selectedCount++;

                // Deselect another radio button if there are two selected before the click
                if (selectedCount > 2) {
                    for (int i : radioMap.keySet()) {
                        if (radioMap.get(i).isSelected() && i != 4 && selectedCount > 2) {
                            radioMap.get(i).setSelected(false);
                            selectedCount--;
                        }
                    }
                }

            } else {// If it has been unselected, reduce count
                selectedCount--;
            }

            // enable the decryt button when there are enough shares selected to
            // reconstruct. Disabled otherwise.
            if (selectedCount == 2) {
                decryptButton.setEnabled(true);
            } else {
                decryptButton.setEnabled(false);
            }
        }

        // Reset button undoes any downscaling, clears any selection, and clears the
        // decrytion result
        if (src == resetButton) {
            // Deselect radio buttons and reset share images to original size
            for (int i : radioMap.keySet()) {
                radioMap.get(i).setSelected(false);
                if (downscaled) {
                    shareMap.get(i)
                            .setImage(shareArray[i].shareImage.getScaledInstance(
                                    (fixedImageHeight * decryptedImage.getWidth()) / decryptedImage.getHeight(),
                                    fixedImageHeight, DO_NOTHING_ON_CLOSE));
                    shareMap.get(i).repaint();
                }
            }
            selectedCount = 0;

            // Reset the decrypted image to be blank and apply to GUI
            decryptedImage = new BufferedImage(decryptedImage.getWidth(), decryptedImage.getHeight(),
                    decryptedImage.getType());
            decryptedImageCanvas.setImage(
                    decryptedImage.getScaledInstance(
                            (fixedImageHeight * decryptedImage.getWidth()) / decryptedImage.getHeight(),
                            fixedImageHeight, DO_NOTHING_ON_CLOSE));
            decryptedImageCanvas.repaint();

            // Reset secret image if downscaled
            if (downscaled) {
                secretCanvas
                        .setImage(secretImage.getScaledInstance(
                                (fixedImageHeight * secretImage.getWidth()) / secretImage.getHeight(),
                                fixedImageHeight, DO_NOTHING_ON_CLOSE));
                secretCanvas.repaint();
            }

            // Reset state members and info fields
            downscaled = false;
            downscaleButton.setEnabled(true);
            resetButton.setEnabled(false);
            decryptButton.setEnabled(false);
            MeanAvgError.setText("   ");
            errorButton.setVisible(false);
            MeanAvgError.setVisible(false);
            meanAvgLabel.setVisible(false);
        }

        // Decrypt button takes the selected shares, and reconstructs the secret image
        if (src == decryptButton) {
            // Required amount of shares must be ensured
            if (selectedCount == 2) {
                // Get the corresponding shares from the selected radio buttons
                int count = 0;

                // Store the shares that have been selected for reconstruction
                Share selectedShares[] = new Share[2];
                for (int i : radioMap.keySet()) {
                    if (radioMap.get(i).isSelected()) {
                        // Get the downscaled shares if downscaled has been toggled
                        if (downscaled) {
                            selectedShares[count] = downscaleShareArray[i];
                        } else {
                            selectedShares[count] = shareArray[i];
                        }
                        count++;
                    }
                }

                // Apply decryption algorithm
                try {
                    if (downscaled) {
                        downscaledDecrypted = secretSharing.DecryptSecret(selectedShares);
                    } else {
                        decryptedImage = secretSharing.DecryptSecret(selectedShares);
                    }
                    decryptedImage = secretSharing.DecryptSecret(selectedShares);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                // Apply the decrypted image to the GUI with the appropriate scale
                if (downscaled) {
                    decryptedImageCanvas.setImage(
                            decryptedImage.getScaledInstance((fixedImageHeight * decryptedImage.getWidth()) /
                                    (2 * decryptedImage.getHeight()),
                                    fixedImageHeight / 2, DO_NOTHING_ON_CLOSE));

                } else {

                    decryptedImageCanvas.setImage(
                            decryptedImage.getScaledInstance((fixedImageHeight * decryptedImage.getWidth()) /
                                    (decryptedImage.getHeight()),
                                    fixedImageHeight, DO_NOTHING_ON_CLOSE));
                }

                decryptedImageCanvas.repaint();
            }
        }

        // Downscale all shares
        if (src == downscaleButton) {
            // Enable all components for downscaled only operations
            MeanAvgError.setVisible(true);
            meanAvgLabel.setVisible(true);
            errorButton.setVisible(true);
            downscaled = true;
            downscaleButton.setEnabled(false);

            // Downscale all shares
            for (int i : shareMap.keySet()) {
                shareMap.get(i)
                        .setImage(downscaleShareArray[i].shareImage.getScaledInstance(
                                (fixedImageHeight * decryptedImage.getWidth()) / (2 * decryptedImage.getHeight()),
                                fixedImageHeight / 2, DO_NOTHING_ON_CLOSE));
                shareMap.get(i).repaint();
            }

            // Downscale secret image
            try {
                secretCanvas.setImage(secretSharing.Downscale(secretImage).getScaledInstance(
                        (fixedImageHeight * secretImage.getWidth()) / (2 * secretImage.getHeight()),
                        fixedImageHeight / 2, DO_NOTHING_ON_CLOSE));
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            secretCanvas.repaint();

            // downscale decrypted image
            try {
                decryptedImageCanvas.setImage(secretSharing.Downscale(
                        (new BufferedImage(secretImage.getWidth(null),
                                secretImage.getHeight(null), secretImage.getType())))
                        .getScaledInstance(
                                (fixedImageHeight * secretImage.getWidth()) / (2 * secretImage.getHeight()),
                                fixedImageHeight / 2, DO_NOTHING_ON_CLOSE));
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            decryptedImageCanvas.repaint();
        }

        // Calculates mean average error between downscaled secret and reconstruction
        // from downscaled shares
        if (src == errorButton) {
            MeanAvgError.setText("");

            // If there is a decrypted image from the downscaled shares, proceed with
            // calculations
            if (downscaledDecrypted != null) {
                // -1 indicates the mean could not be calculated
                long meanAvg = -1;
                try {
                    meanAvg = secretSharing.MeanAverageError(secretSharing.Downscale(secretImage),
                            downscaledDecrypted);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                // update the text field to the value MAE
                MeanAvgError.setText(String.valueOf(meanAvg));
                // Scale the GUI to show the value
                setSize(this.getWidth() + MeanAvgError.getWidth() - 3, this.getHeight());
            } else {// error message box if there is no decryoted
                JOptionPane.showMessageDialog(this, "Please decrypt while in downscaled mode...", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        // if there is any event that being observed, enable the reset button
        if (src != resetButton && !resetButton.isEnabled()) {
            resetButton.setEnabled(true);
        }

    }

    /**
     * Grab secret image from working directory. Must be named secret.bmp
     * 
     * @return
     * @throws FileNotFoundException
     */
    public BufferedImage GetSecretImage() throws FileNotFoundException {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResource("/secret.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Constructor for secretSharing. Initiallizes and creates GUI and its
     * components
     * 
     * @throws IOException
     */
    public SecretSharing() throws IOException {
        // Main application layout
        GridBagLayout mainLayout = new GridBagLayout();
        GridBagConstraints mainConstraints = new GridBagConstraints();
        setTitle("Shamir's Secret Sharing"); // set application title

        // Title section
        JLabel title = new JLabel("Shamir's Secret Sharing");
        setLayout(mainLayout);

        // add title to app screen
        add(title);
        mainConstraints.gridwidth = GridBagConstraints.REMAINDER;
        mainLayout.setConstraints(title, mainConstraints);

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~ Secret image section ~~~~~~~~~~~~~~~~~~~~~~~~~~~

        // layout for secret image section
        GridBagLayout secretImageLayout = new GridBagLayout();
        GridBagConstraints secretImageConstraints = new GridBagConstraints();

        // Panel to contain secret image
        JPanel secretImagePanel = new JPanel();
        secretImagePanel.setLayout(secretImageLayout);

        // adds label above image
        JLabel secretImageLabel = new JLabel("Secret Image:");
        secretImageConstraints.gridx = 1;
        secretImageConstraints.gridy = 0;
        secretImagePanel.add(secretImageLabel, secretImageConstraints);

        // add image to panel below label
        secretImage = GetSecretImage();
        secretCanvas = new ImageCanvas(
                secretImage.getScaledInstance((secretImage.getWidth() * fixedImageHeight) / secretImage.getHeight(),
                        fixedImageHeight, DO_NOTHING_ON_CLOSE));
        secretImageConstraints.gridx = 1;
        secretImageConstraints.gridy = 1;
        secretImagePanel.add(secretCanvas, secretImageConstraints);

        // add panel below title
        mainConstraints.gridx = 1;
        mainConstraints.gridy = 1;
        add(secretImagePanel, mainConstraints);

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~ Share Images section ~~~~~~~~~~~~~~~~~~~~~~~~

        secretSharing = new SSS(secretImage, 2, 5);
        secretSharing.CreateShares();

        shareArray = secretSharing.GetShares();

        downscaleShareArray = new Share[5];
        for (int i = 0; i < 5; i++) {
            try {
                downscaleShareArray[i] = new Share();
                downscaleShareArray[i].shareImage = secretSharing.Downscale(shareArray[i].shareImage);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            downscaleShareArray[i].keyX = shareArray[i].keyX;
        }

        // layout for share images
        GridBagLayout shareImageLayout = new GridBagLayout();
        GridBagConstraints shareImageConstraints = new GridBagConstraints();

        radioMap = new HashMap<>();
        radioMap.put(0, shareRadioButton0);
        radioMap.put(1, shareRadioButton1);
        radioMap.put(2, shareRadioButton2);
        radioMap.put(3, shareRadioButton3);
        radioMap.put(4, shareRadioButton4);

        // add padding between images
        shareImageConstraints.ipadx = 2;
        shareImageConstraints.ipady = 2;
        JPanel shareImagePanel = new JPanel(shareImageLayout);

        // first share
        shareImageConstraints.gridx = 0;
        shareImageConstraints.gridy = 0;
        shareImagePanel.add(shareRadioButton0, shareImageConstraints);
        shareRadioButton0.addActionListener(this);

        shareCanvas0 = new ImageCanvas(
                shareArray[0].shareImage.getScaledInstance(
                        (shareArray[0].shareImage.getWidth() * fixedImageHeight) / shareArray[0].shareImage.getHeight(),
                        fixedImageHeight,
                        DO_NOTHING_ON_CLOSE));
        shareImageConstraints.gridx = 0;
        shareImageConstraints.gridy = 1;
        shareImagePanel.add(shareCanvas0, shareImageConstraints);

        // second share
        shareImageConstraints.gridx = 0;
        shareImageConstraints.gridy = 2;
        shareImagePanel.add(shareRadioButton1, shareImageConstraints);
        shareRadioButton1.addActionListener(this);

        shareCanvas1 = new ImageCanvas(
                shareArray[1].shareImage.getScaledInstance(
                        (shareArray[1].shareImage.getWidth() * fixedImageHeight) / shareArray[1].shareImage.getHeight(),
                        fixedImageHeight,
                        DO_NOTHING_ON_CLOSE));
        shareImageConstraints.gridx = 0;
        shareImageConstraints.gridy = 3;
        shareImagePanel.add(shareCanvas1, shareImageConstraints);

        // third share
        shareImageConstraints.gridx = 1;
        shareImageConstraints.gridy = 0;
        shareImagePanel.add(shareRadioButton2, shareImageConstraints);
        shareRadioButton2.addActionListener(this);

        shareCanvas2 = new ImageCanvas(
                shareArray[2].shareImage.getScaledInstance(
                        (shareArray[2].shareImage.getWidth() * fixedImageHeight) / shareArray[2].shareImage.getHeight(),
                        fixedImageHeight,
                        DO_NOTHING_ON_CLOSE));
        shareImageConstraints.gridx = 1;
        shareImageConstraints.gridy = 1;
        shareImagePanel.add(shareCanvas2, shareImageConstraints);

        // fourth share
        shareImageConstraints.gridx = 1;
        shareImageConstraints.gridy = 2;
        shareImagePanel.add(shareRadioButton3, shareImageConstraints);
        shareRadioButton3.addActionListener(this);

        shareCanvas3 = new ImageCanvas(
                shareArray[3].shareImage.getScaledInstance(
                        (shareArray[3].shareImage.getWidth() * fixedImageHeight) / shareArray[3].shareImage.getHeight(),
                        fixedImageHeight,
                        DO_NOTHING_ON_CLOSE));
        shareImageConstraints.gridx = 1;
        shareImageConstraints.gridy = 3;
        shareImagePanel.add(shareCanvas3, shareImageConstraints);

        // fifth share
        shareImageConstraints.gridx = 2;
        shareImageConstraints.gridy = 0;
        shareImagePanel.add(shareRadioButton4, shareImageConstraints);
        shareRadioButton4.addActionListener(this);

        shareCanvas4 = new ImageCanvas(
                shareArray[4].shareImage.getScaledInstance(
                        (shareArray[4].shareImage.getWidth() * fixedImageHeight) / shareArray[4].shareImage.getHeight(),
                        fixedImageHeight,
                        DO_NOTHING_ON_CLOSE));
        shareImageConstraints.gridx = 2;
        shareImageConstraints.gridy = 1;
        shareImagePanel.add(shareCanvas4, shareImageConstraints);

        shareMap = new HashMap<>();
        shareMap.put(0, shareCanvas0);
        shareMap.put(1, shareCanvas1);
        shareMap.put(2, shareCanvas2);
        shareMap.put(3, shareCanvas3);
        shareMap.put(4, shareCanvas4);

        mainConstraints.gridx = 1;
        mainConstraints.gridy = 2;
        add(shareImagePanel, mainConstraints);

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~ Decrypting section ~~~~~~~~~~~~~~~~~~~~~~~~

        GridBagLayout decryptingLayout = new GridBagLayout();
        GridBagConstraints decryptingConstraints = new GridBagConstraints();
        decryptingConstraints.ipadx = 2;
        decryptingConstraints.ipady = 2;
        JPanel decryptionPanel = new JPanel();
        decryptingLayout.setConstraints(decryptionPanel, decryptingConstraints);

        // add button below image
        decryptButton = new JButton("Decrypt");
        decryptingConstraints.gridx = 0;
        decryptingConstraints.gridy = 0;
        decryptionPanel.add(decryptButton, mainConstraints);
        decryptButton.addActionListener(this);
        decryptButton.setEnabled(false);

        // reset button
        resetButton = new JButton("Reset");
        decryptingConstraints.gridx = 1;
        decryptingConstraints.gridy = 0;
        decryptionPanel.add(resetButton, decryptingConstraints);
        resetButton.addActionListener(this);
        resetButton.setEnabled(false);

        // reset button
        downscaleButton = new JButton("Downscale");
        decryptingConstraints.gridx = 2;
        decryptingConstraints.gridy = 0;
        decryptionPanel.add(downscaleButton, decryptingConstraints);
        downscaleButton.addActionListener(this);

        // add image to panel below label
        decryptedImage = new BufferedImage(secretImage.getWidth(), secretImage.getHeight(), secretImage.getType());
        decryptedImageCanvas = new ImageCanvas(
                decryptedImage.getScaledInstance(
                        (decryptedImage.getWidth() * fixedImageHeight) / decryptedImage.getHeight(),
                        fixedImageHeight,
                        DO_NOTHING_ON_CLOSE));
        // decryptingConstraints.gridx = 0;
        // decryptingConstraints.gridy = 1;
        // decryptionPanel.add(decryptedImageCanvas, decryptingConstraints);
        shareImageConstraints.gridx = 2;
        shareImageConstraints.gridy = 3;
        shareImagePanel.add(decryptedImageCanvas, shareImageConstraints);

        JLabel decryptedImageLabel = new JLabel("Decrypted Image:");
        shareImageConstraints.gridx = 2;
        shareImageConstraints.gridy = 2;
        shareImagePanel.add(decryptedImageLabel, shareImageConstraints);

        errorButton = new JButton("Error");
        decryptingConstraints.gridx = 3;
        decryptingConstraints.gridy = 0;
        decryptionPanel.add(errorButton, decryptingConstraints);
        errorButton.addActionListener(this);

        decryptingConstraints.gridx = 4;
        decryptingConstraints.gridy = 0;
        decryptionPanel.add(meanAvgLabel, decryptingConstraints);

        MeanAvgError = new JTextField("   ");
        MeanAvgError.setEditable(false);
        decryptingConstraints.gridx = 5;
        decryptingConstraints.gridy = 0;
        decryptionPanel.add(MeanAvgError, decryptingConstraints);

        errorButton.setVisible(false);
        MeanAvgError.setVisible(false);
        meanAvgLabel.setVisible(false);

        // add panel to make layout
        mainConstraints.gridx = 1;
        mainConstraints.gridy = 3;
        add(decryptionPanel, mainConstraints);

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        // center application window in screen
        setLocationRelativeTo(null);
    }

    public static void main(String Args[]) throws IOException {
        SecretSharing program = new SecretSharing();
        program.setVisible(true);
    }

    /**
     * Object that displays Image
     * 
     * @author Atrey Pradeep
     */
    public class ImageCanvas extends JPanel {
        Image img;

        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, this);
        }

        public void setLocale(float centerAlignment) {
        }

        public void setImage(Image img) {
            this.img = img;
        }

        public ImageCanvas(Image img) {
            this.img = img;

            this.setPreferredSize(new Dimension(img.getWidth(this), img.getHeight(this)));
        }
    }
}