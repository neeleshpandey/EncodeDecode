import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;


public class EncodeDecode extends JFrame implements ActionListener {

    private static int bytesForTextLengthData = 4;
    private static int bitsInByte = 8;
    private JToggleButton encodeToggle;
    private JToggleButton decodeToggle;
    private JTextArea textArea;
    private JButton chooseFileButton;
    private JButton submitButton;
    int f=-1;
    String fileSelected = null;


    public EncodeDecode() {
        super("Encode/Decode");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components
        encodeToggle = new JToggleButton("Encode");
        decodeToggle = new JToggleButton("Decode");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(encodeToggle);
        buttonGroup.add(decodeToggle);
        textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);
        chooseFileButton = new JButton("Choose File");
        submitButton = new JButton("Submit");

        // Set layout
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // Add components to content panel
        JPanel topPanel = new JPanel();
        topPanel.add(encodeToggle);
        topPanel.add(decodeToggle);
        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(chooseFileButton);
        bottomPanel.add(submitButton);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // Add event listeners
        encodeToggle.addActionListener(this);
        decodeToggle.addActionListener(this);
        chooseFileButton.addActionListener(this);
        submitButton.addActionListener(this);

        // Set window size and show
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        String text = textArea.getText();
        if (e.getSource() == encodeToggle) {
            if (encodeToggle.isSelected()) {
                f=0;
                decodeToggle.setSelected(false);
            } else {
                encodeToggle.setSelected(true);
            }
        } else if (e.getSource() == decodeToggle) {
            if (decodeToggle.isSelected()) {
                f=1;
                encodeToggle.setSelected(false);
            } else {
                decodeToggle.setSelected(true);
            }
        } else if (e.getSource() == chooseFileButton) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                fileSelected = fileChooser.getSelectedFile().getPath();
            }
        } if (e.getSource() == submitButton) {
            if(f==0){
                encode(fileSelected, text);
            }
            else if(f==1){
                textArea.setText(decode(fileSelected));
            }
        }
    }


        // Encode

    private static void encode(String imagePath, String text) {
        BufferedImage originalImage = getImageFromPath(imagePath);
        BufferedImage imageInUserSpace = getImageInUserSpace(originalImage);

        byte[] imageInBytes = getBytesFromImage(imageInUserSpace);
        byte[] textInBytes = text.getBytes();
        byte[] textLengthInBytes = getBytesFromInt(textInBytes.length);

        try {
            encodeImage(imageInBytes, textLengthInBytes,  0);
            encodeImage(imageInBytes, textInBytes, bytesForTextLengthData*bitsInByte);
        }
        catch (Exception exception) {
            System.out.println("Couldn't hide text in image. Error: " + exception);
            return;
        }

        String fileName = imagePath;
        int position = fileName.lastIndexOf(".");
        if (position > 0) {
            fileName = fileName.substring(0, position);
        }

        String finalFileName = fileName + "_with_hidden_message.png";
        System.out.println("Successfully encoded text in: " + finalFileName);
        saveImageToPath(imageInUserSpace, new File(finalFileName));
        return;
    }

    private static void encodeImage(byte[] image, byte[] addition, int offset) {
        if (addition.length + offset > image.length) {
            throw new IllegalArgumentException("Image file is not long enough to store provided text");
        }
        for (int additionByte : addition) {
            for (int bit = bitsInByte - 1; bit >= 0; --bit, offset++) {
                int b = (additionByte >>> bit) & 0x1;
                image[offset] = (byte) ((image[offset] & 0xFE) | b);
            }
        }
    }


    // Decode

    private static String decode(String imagePath) {
        byte[] decodedHiddenText;
        try {
            BufferedImage imageFromPath = getImageFromPath(imagePath);
            BufferedImage imageInUserSpace = getImageInUserSpace(imageFromPath);
            byte[] imageInBytes = getBytesFromImage(imageInUserSpace);
            decodedHiddenText = decodeImage(imageInBytes);
            String hiddenText = new String(decodedHiddenText);
            return hiddenText;
        } catch (Exception exception) {

            return "No hidden message. Error";
        }
    }

    private static byte[] decodeImage(byte[] image) {
        int length = 0;
        int offset  = bytesForTextLengthData*bitsInByte;

        for (int i=0; i<offset; i++) {
            length = (length << 1) | (image[i] & 0x1);
        }

        byte[] result = new byte[length];

        for (int b=0; b<result.length; b++ ) {
            for (int i=0; i<bitsInByte; i++, offset++) {
                result[b] = (byte)((result[b] << 1) | (image[offset] & 0x1));
            }
        }
        return result;
    }


    // File I/O methods

    private static void saveImageToPath(BufferedImage image, File file) {
        try {
            file.delete();
            ImageIO.write(image, "png", file);
        } catch (Exception exception) {
            System.out.println("Image file could not be saved. Error: " + exception);
        }
    }


    private static BufferedImage getImageFromPath(String path) {
        BufferedImage image	= null;
        File file = new File(path);
        try {
            image = ImageIO.read(file);
        } catch (Exception exception) {
            System.out.println("Input image cannot be read. Error: " + exception);
        }
        return image;
    }


    // Helpers

    private static BufferedImage getImageInUserSpace(BufferedImage image) {
        BufferedImage imageInUserSpace  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = imageInUserSpace.createGraphics();
        graphics.drawRenderedImage(image, null);
        graphics.dispose();
        return imageInUserSpace;
    }

    private static byte[] getBytesFromImage(BufferedImage image) {
        WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
        return buffer.getData();
    }

    private static byte[] getBytesFromInt(int integer) {
        return ByteBuffer.allocate(bytesForTextLengthData).putInt(integer).array();
    }

    public static void main(String[] args) {
        new EncodeDecode();
    }
}
