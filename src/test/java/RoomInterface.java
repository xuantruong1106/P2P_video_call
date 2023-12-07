
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class RoomInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    private static int port;
    private static String name;
    private static boolean isHost;
    private WebcamPanel webcamPanelRight;
    private Socket s;
    private boolean haveClients = false;
    private String IP_Server;

    public RoomInterface(String IP_Server, int port, String name, boolean isHost) {
        this.name = name;
        this.port = port;
        this.isHost = isHost;
        this.IP_Server = IP_Server;

        System.out.println("Roominterface 37: " + this.name + " "+this.port + " "+this.isHost);

        SwingUtilities.invokeLater(() -> {
            setSize(800, 400);
            setTitle("Video Room");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            if (isHost) {
                System.out.println("Room interface 45: host");
                try {

                    ServerSocket ss = new ServerSocket(this.port);
                    new Thread(() -> {
                        try {
                           Socket sk = ss.accept();
                            System.out.println("Room interface 52: create server socket host done");
                            DataInputStream din = new DataInputStream(sk.getInputStream());
                            System.out.println("Room interface 54 || Client video room join: " + din.readUTF());
                            haveClients = true;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();

                    JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));

                    JPanel panelLeft = createPanelLeft(this.isHost);
                    JPanel panelRight = createPanelRight(this.port, this.isHost);

                    containerPanelLeftAndRight.removeAll();
                    containerPanelLeftAndRight.add(panelLeft);
                    containerPanelLeftAndRight.add(panelRight);

                    getContentPane().add(containerPanelLeftAndRight);
                    revalidate();
                    repaint();

                    setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("Room interface 79: client");
                try {
                    s = new Socket(IP_Server, this.port);

                    // Modify your loop to receive and display frames
                    JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));

                    JPanel panelLeft = createPanelLeft(this.isHost);
                    JPanel panelRight = createPanelRight(this.port, this.isHost);

                    containerPanelLeftAndRight.removeAll();
                    containerPanelLeftAndRight.add(panelLeft);
                    containerPanelLeftAndRight.add(panelRight);

                    getContentPane().add(containerPanelLeftAndRight);
                    revalidate();
                    repaint();

                    setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JPanel createPanelLeft(boolean isHost) throws IOException {
        JPanel panelLeft = new JPanel(new BorderLayout());

         if (haveClients && !isHost) {
        // If there are clients and this is not the host
        ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
        JPanel videoDisplayPanel = new JPanel();

        new Thread(() -> {
            try {
                while (true) {
                    BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
                    // Assuming you have a method to update the video display
                    updateVideoDisplay(videoDisplayPanel, receivedImage);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    } else if (haveClients && isHost) {
        // If there are clients and this is the host
        WebcamPanel webcamPanel = initializeWebcam();
        panelLeft.add(webcamPanel, BorderLayout.CENTER);

        new Thread(() -> {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());

                while (true) {
                    BufferedImage currentFrame = new BufferedImage(WIDTH, HEIGHT, HEIGHT);
                    outputStream.writeObject(currentFrame);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    } else {
        // If there are no clients
        WebcamPanel webcamPanel = initializeWebcam();
        panelLeft.add(webcamPanel, BorderLayout.CENTER);
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
    
    private JButton createButton(String iconOnPath, String iconOffPath) {
        ImageIcon iconOn = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/" + iconOnPath);
        Image scaledImage = iconOn.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledIconOn = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIconOn);

//        if (iconOffPath != null) {
//            ImageIcon iconOff = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/" + iconOffPath);
//            Image scaledImageOff = iconOff.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
//            ImageIcon scaledIconOff = new ImageIcon(scaledImageOff);
//
//            button.addActionListener(e -> button.setIcon(scaledIconOff));
//        }
        return button;
    }

    private JPanel createPanelRight(int port, boolean isHost) throws IOException {
    JPanel panelRight = new JPanel(new BorderLayout());
    String portString = String.valueOf(port);

    if (haveClients && isHost) {
        JLabel labelIDRoom = new JLabel("Room Port: " + portString);
        ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
        JPanel videoDisplayPanel = new JPanel();

        new Thread(() -> {
            try {
                while (true) {
                    BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
                    // Assuming you have a method to update the video display
                    updateVideoDisplay(videoDisplayPanel, receivedImage);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

        panelRight.add(videoDisplayPanel, BorderLayout.CENTER);
        panelRight.add(labelIDRoom, BorderLayout.NORTH);

        // Your panelRight creation code here
    } else {
        if (isHost) {
            JLabel labelHostInfo = new JLabel("Host IP: " + this.IP_Server + " Port: " + this.port);
            panelRight.add(labelHostInfo, BorderLayout.PAGE_START);
        } else {
            // Display client's webcam feed and host's info
            ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
            JPanel videoDisplayPanel = new JPanel();

            new Thread(() -> {
                try {
                    while (true) {
                        BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
                        // Assuming you have a method to update the video display
                        updateVideoDisplay(videoDisplayPanel, receivedImage);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();

            JLabel labelHostInfo = new JLabel("Host IP: " + this.IP_Server + " Port: " + this.port);
            panelRight.add(videoDisplayPanel, BorderLayout.CENTER);
            panelRight.add(labelHostInfo, BorderLayout.SOUTH);
        }
    }

    return panelRight;
}


    private void toggleMic(JButton buttonOnOffMic) {
        isMicOn = !isMicOn;
        ImageIcon newIcon = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/"
                + (isMicOn ? "IconOnMic.png" : "IconOffMic.png"));
        Image scaledImageMic = newIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        buttonOnOffMic.setIcon(new ImageIcon(scaledImageMic));
    }

    private void toggleVideo(JButton buttonOnOffVideo) {
        if (isCameraOn) {
            webcam.open();
            isCameraOn = false;
            ImageIcon newIcon = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/IconOnVideo.png");
            Image scaledImageVideo = newIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            buttonOnOffVideo.setIcon(new ImageIcon(scaledImageVideo));
        } else {
            webcam.close();
            isCameraOn = true;
            ImageIcon newIcon = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/IconOffVideo.png");
            Image scaledImageVideo = newIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            buttonOnOffVideo.setIcon(new ImageIcon(scaledImageVideo));
        }
    }

    private void exitVideoRoom() {
        webcam.close();
        MainInterface main = new MainInterface();
        main.setVisible(true);
        setVisible(false);
        dispose();
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            RoomInterface roomInterface = new RoomInterface(this.name, this.port, this.isHost);
//            roomInterface.setVisible(true);
//        });
//    }
    private void updateVideoDisplay(JPanel videoDisplayPanel, BufferedImage image) {
        SwingUtilities.invokeLater(() -> {
            // Assuming you have a JLabel or other component to display images
            JLabel imageLabel = new JLabel(new ImageIcon(image));

            // Remove existing components from the panel
            videoDisplayPanel.removeAll();

            // Add the new image label to the panel
            videoDisplayPanel.add(imageLabel);

            // Repaint and revalidate the panel to reflect the changes
            videoDisplayPanel.revalidate();
            videoDisplayPanel.repaint();
        });
    }
}
