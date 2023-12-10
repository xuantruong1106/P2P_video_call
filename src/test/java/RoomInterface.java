import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class RoomInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    private static boolean isHostGlobal;
    private static String IP_Server_Global;
    private static int portGlobal;
    private byte[] bytes;

    public RoomInterface(String IP_Server, int port, String name, boolean isHost) {

        IP_Server_Global = IP_Server;
        isHostGlobal = isHost;
        portGlobal = port;

        System.out.println(IP_Server + " " + port);
        SwingUtilities.invokeLater(() -> {
            initializeUI();
        });
    }

    private void initializeUI() {
        setSize(800, 400);
        setTitle("Video Room");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (isHostGlobal) {
            startServer();
        } else {
            connectToServer();
        }
    }

    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(portGlobal);
                System.out.println("Server socket initialized");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                handle(clientSocket);
            } catch (IOException ex) {
                Logger.getLogger(RoomInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();
    }

    private void connectToServer() {
        try {
            Socket clientSocket = new Socket(IP_Server_Global, portGlobal);
            handle(clientSocket);
        } catch (IOException ex) {
            Logger.getLogger(RoomInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handle(Socket socket) {
        System.out.println(socket);
        SwingUtilities.invokeLater(() -> {
            JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
            JPanel panelCenter = createPaneCenter(socket);
            containerPanelLeftAndRight.add(panelCenter);
            getContentPane().add(containerPanelLeftAndRight);
            setVisible(true);
        });
    }

    private JPanel createPaneCenter(Socket socket) {
        JPanel panelCenter = new JPanel(new BorderLayout());
        JButton buttonOnOffMic = createButton("IconOnMic.png", "IconOffMic.png");
        JButton buttonOnOffVideo = createButton("IconOnVideo.png", "IconOffVideo.png");
        JButton buttonExitVideoRoom = createButton("IconExit.png", null);
        JPanel buttonPanel = new JPanel();

        WebcamPanel webcamPanel = initializeWebcam();
        JLabel video = new JLabel();

        buttonOnOffMic.addActionListener(e -> toggleMic(buttonOnOffMic));
        buttonOnOffVideo.addActionListener(e -> toggleVideo(buttonOnOffVideo));
        buttonExitVideoRoom.addActionListener(e -> exitVideoRoom());

        buttonPanel.add(buttonOnOffMic);
        buttonPanel.add(buttonOnOffVideo);
        buttonPanel.add(buttonExitVideoRoom);

        panelCenter.add(webcamPanel, BorderLayout.CENTER);
        panelCenter.add(buttonPanel, BorderLayout.SOUTH);

        try {
            while (true) {
                BufferedImage image = webcam.getImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                byte[] bytes = baos.toByteArray();

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeInt(bytes.length);
                dos.write(bytes);

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                int length = dis.readInt();
                bytes = new byte[length];

                if (length > 0) {
                    dis.readFully(bytes, 0, bytes.length);
                }

                BufferedImage receivedImage = ImageIO.read(new ByteArrayInputStream(bytes));
                ImageIcon imageIcon = new ImageIcon(receivedImage);
                video.setIcon(imageIcon);
                panelCenter.add(video, BorderLayout.EAST);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in createPaneCenter");
        }

        return panelCenter;
    }

    private WebcamPanel initializeWebcam() {
        webcam = Webcam.getDefault();
        if (webcam.isOpen()) {
            webcam.close();
        }
        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();
        isCameraOn = true;
        isMicOn = true;
        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setMirrored(true);
        Thread thread = new Thread(() -> {
            webcam.open();
            while (true) {
                if (webcam.isOpen()) {
                    webcamPanel.start();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return webcamPanel;
    }

    private void toggleMic(JButton buttonOnOffMic) {
        isMicOn = !isMicOn;
        updateButtonIcon(buttonOnOffMic, "IconOnMic.png", "IconOffMic.png");
    }

    private void toggleVideo(JButton buttonOnOffVideo) {
        if (isCameraOn) {
            webcam.open();
            isCameraOn = false;
            updateButtonIcon(buttonOnOffVideo, "IconOnVideo.png", "IconOffVideo.png");
        } else {
            webcam.close();
            isCameraOn = true;
            updateButtonIcon(buttonOnOffVideo, "IconOffVideo.png", "IconOnVideo.png");
        }
    }

    private void updateButtonIcon(JButton button, String iconOnPath, String iconOffPath) {
        ImageIcon iconOn = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/" + iconOnPath);
        Image scaledImage = iconOn.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage));
    }

    private void exitVideoRoom() {
        if (isCameraOn) {
            webcam.close();
        }
        MainInterface main = new MainInterface();
        main.setVisible(true);
        setVisible(false);
        dispose();
    }

    private JButton createButton(String iconOnPath, String iconOffPath) {
        ImageIcon iconOn = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/" + iconOnPath);
        Image scaledImage = iconOn.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        return new JButton(new ImageIcon(scaledImage));
    }
}
