
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
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            if (isHostGlobal) {
                handleHost(in, out, video, webcamPanel, panelCenter);
            } else {
                handleClient(in, out, video, webcamPanel, panelCenter);
            }
        } catch (Exception e) {
            System.out.println("in out have problem");
        }

        return panelCenter;
    }

private void handleHost(ObjectInputStream in, ObjectOutputStream out, JLabel video, WebcamPanel webcamPanel, JPanel panelCenter) {
    try {
        final ImageIcon receivedIcon = (ImageIcon) in.readObject();
        final ImageIcon webcamIcon = new ImageIcon(webcamPanel.getImage());

        while (true) {
           

            SwingUtilities.invokeLater(() -> {
                video.setIcon(webcamIcon);
                panelCenter.revalidate();
                panelCenter.repaint();
            });

            out.writeObject(webcamIcon);
            out.flush();

            

            SwingUtilities.invokeLater(() -> {
                video.setIcon(receivedIcon);
                panelCenter.revalidate();
                panelCenter.repaint();
            });
        }
    } catch (IOException e) {
        System.err.println("Network error in host handling: " + e.getMessage());
        // Handle network error...
    } catch (ClassNotFoundException e) {
        System.err.println("Class not found error in host handling: " + e.getMessage());
        // Handle class not found error...
    }
}

private void handleClient(ObjectInputStream in, ObjectOutputStream out, JLabel video, WebcamPanel webcamPanel, JPanel panelCenter) {
    try {
        final ImageIcon receivedIcon = (ImageIcon) in.readObject();
        final ImageIcon webcamIcon = new ImageIcon(webcamPanel.getImage());

        while (true) {
            

            SwingUtilities.invokeLater(() -> {
                video.setIcon(receivedIcon);
                panelCenter.revalidate();
                panelCenter.repaint();
            });

            
            out.writeObject(webcamIcon);
            out.flush();

            SwingUtilities.invokeLater(() -> {
                video.setIcon(webcamIcon);
                panelCenter.revalidate();
                panelCenter.repaint();
            });
        }
    } catch (IOException e) {
        System.err.println("Network error in client handling: " + e.getMessage());
        // Handle network error...
    } catch (ClassNotFoundException e) {
        System.err.println("Class not found error in client handling: " + e.getMessage());
        // Handle class not found error...
    }
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
