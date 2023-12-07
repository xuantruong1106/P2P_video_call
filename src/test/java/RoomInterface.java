import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RoomInterface extends JFrame {

    private static final int WIDTH = 640;
    private static final int HEIGHT = 480;

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    private int port;
    private String name;
    private boolean isHost;
    private Socket sk;

    public RoomInterface(String IP_Server, int port, String name, boolean isHost) {
        this.name = name;
        this.port = port;
        this.isHost = isHost;

        SwingUtilities.invokeLater(() -> {
            setSize(800, 400);
            setTitle("Video Room");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                if (isHost) {
                    createHostServer();
                } else {
                    createClient(IP_Server);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createHostServer() {
        try (ServerSocket ss = new ServerSocket(port)) {
            sk = ss.accept();
            System.out.println("Host: Server socket created");

            DataInputStream din = new DataInputStream(sk.getInputStream());
            System.out.println("Client video room join: " + din.readUTF());

            JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
            containerPanelLeftAndRight.add(createPanelLeft());
            containerPanelLeftAndRight.add(createPanelRight());

            getContentPane().add(containerPanelLeftAndRight);
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createClient(String IP_Server) {
        try {
            sk = new Socket(IP_Server, port);

            JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
            containerPanelLeftAndRight.add(createPanelLeft());
            containerPanelLeftAndRight.add(createPanelRight());

            getContentPane().add(containerPanelLeftAndRight);
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel createPanelLeft() {
        JPanel panelLeft = new JPanel(new BorderLayout());

        if (!isHost) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(sk.getInputStream());
                JPanel videoDisplayPanel = new JPanel();

                new Thread(() -> {
                    try {
                        while (true) {
                            BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
                            updateVideoDisplay(videoDisplayPanel, receivedImage);
                            panelLeft.add(videoDisplayPanel, BorderLayout.CENTER);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            WebcamPanel webcamPanel = initializeWebcam();
            panelLeft.add(webcamPanel, BorderLayout.CENTER);

            new Thread(() -> {
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(sk.getOutputStream());
                    while (isCameraOn) {
                        BufferedImage currentFrame = new BufferedImage(WIDTH, HEIGHT, HEIGHT);
                        outputStream.writeObject(currentFrame);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        JButton buttonOnOffMic = createButton("IconOnMic.png", "IconOffMic.png");
        JButton buttonOnOffVideo = createButton("IconOnVideo.png", "IconOffVideo.png");
        JButton buttonExitVideoRoom = createButton("IconExit.png", null);

        buttonOnOffMic.addActionListener(e -> toggleMic(buttonOnOffMic));
        buttonOnOffVideo.addActionListener(e -> toggleVideo(buttonOnOffVideo));
        buttonExitVideoRoom.addActionListener(e -> exitVideoRoom());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonOnOffMic);
        buttonPanel.add(buttonOnOffVideo);
        buttonPanel.add(buttonExitVideoRoom);

        panelLeft.add(buttonPanel, BorderLayout.SOUTH);

        return panelLeft;
    }

    private JButton createButton(String iconOnPath, String iconOffPath) {
        ImageIcon iconOn = new ImageIcon(getClass().getResource("/com/mycompany/baitaplonmonhoc/img/" + iconOnPath));
        Image scaledImage = iconOn.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledIconOn = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIconOn);

        return button;
    }

    private JPanel createPanelRight() {
        JPanel panelRight = new JPanel(new BorderLayout());

        if (!isHost) {
            WebcamPanel webcamPanel = initializeWebcam();
            panelRight.add(webcamPanel, BorderLayout.NORTH);

            new Thread(() -> {
                try {
                    ObjectOutputStream outputStream = new ObjectOutputStream(sk.getOutputStream());
                    while (isCameraOn) {
                        BufferedImage currentFrame = new BufferedImage(WIDTH, HEIGHT, HEIGHT);
                        outputStream.writeObject(currentFrame);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            JLabel labelHostInfo = new JLabel("Host IP: localhost Port: " + port);
            panelRight.add(labelHostInfo, BorderLayout.PAGE_START);

            try {
                ObjectInputStream inputStream = new ObjectInputStream(sk.getInputStream());
                JPanel videoDisplayPanel = new JPanel();

                new Thread(() -> {
                    try {
                        while (true) {
                            BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
                            updateVideoDisplay(videoDisplayPanel, receivedImage);
                            panelRight.add(videoDisplayPanel, BorderLayout.CENTER);
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return panelRight;
    }

    private WebcamPanel initializeWebcam() {
        webcam = Webcam.getDefault();
        if (webcam.isOpen()) {
            webcam.close();
        }
        webcam.setViewSize(new Dimension(WIDTH, HEIGHT));
        webcam.open();

        isCameraOn = true;
        isMicOn = true;

        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setMirrored(true);

        Thread thread = new Thread(() -> {
            webcam.open();
            while (isCameraOn) {
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
        ImageIcon newIcon = new ImageIcon(getClass().getResource("/com/mycompany/baitaplonmonhoc/img/"
                + (isMicOn ? "IconOnMic.png" : "IconOffMic.png")));
        Image scaledImageMic = newIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        buttonOnOffMic.setIcon(new ImageIcon(scaledImageMic));
    }

    private void toggleVideo(JButton buttonOnOffVideo) {
        if (isCameraOn) {
            webcam.open();
            isCameraOn = false;
            ImageIcon newIcon = new ImageIcon(getClass().getResource("/com/mycompany/baitaplonmonhoc/img/IconOnVideo.png"));
            Image scaledImageVideo = newIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            buttonOnOffVideo.setIcon(new ImageIcon(scaledImageVideo));
        } else {
            webcam.close();
            isCameraOn = true;
            ImageIcon newIcon = new ImageIcon(getClass().getResource("/com/mycompany/baitaplonmonhoc/img/IconOffVideo.png"));
            Image scaledImageVideo = newIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            buttonOnOffVideo.setIcon(new ImageIcon(scaledImageVideo));
        }
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

    private void updateVideoDisplay(JPanel videoDisplayPanel, BufferedImage image) {
        SwingUtilities.invokeLater(() -> {
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            videoDisplayPanel.removeAll();
            videoDisplayPanel.add(imageLabel);
            videoDisplayPanel.revalidate();
            videoDisplayPanel.repaint();
        });
    }
}
