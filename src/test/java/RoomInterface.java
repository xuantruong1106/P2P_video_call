import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RoomInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    private static int port;
    private static String name;
    private static boolean isHost;
    private Socket skClient;
    private Socket skHost;
    private String IP_Server;
    private ServerSocket ss;
    
    public RoomInterface(String IP_Server, int port, String name, boolean isHost) {
        this.name = name;
        this.port = port;
        this.isHost = isHost;
        this.IP_Server = IP_Server;
        
        System.out.println(this.IP_Server + " " + this.port);
        SwingUtilities.invokeLater(() -> {
            setSize(800, 400);
            setTitle("Video Room");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                if (isHost) {
                    handleHost();
                } else {
                    handleClient();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void handleHost() throws IOException {
         ss = new ServerSocket(port);
         System.out.println(ss);
        new Thread(() -> {
            try {
                while (true) {
                    this.skHost = ss.accept();
                    System.out.println("Client connected: " + this.skHost);
                    
                    // Handle client connection
//                    DataInputStream din = new DataInputStream(this.skHost.getInputStream());
//                    System.out.println("Client video room join: " + din.readUTF());

                    // Continue listening for more clients or perform other actions
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
        JPanel panelLeft = createPanelLeft(this.skHost);
        JPanel panelRight = createPanelRight(this.skHost);
        JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
       
        containerPanelLeftAndRight.removeAll();
        containerPanelLeftAndRight.add(panelLeft);
        containerPanelLeftAndRight.add(panelRight);

        getContentPane().add(containerPanelLeftAndRight);
        revalidate();
        repaint();

        setVisible(true);
    }

    private void handleClient() throws IOException {
        this.skClient = new Socket(this.IP_Server, this.port);
        System.out.println("Client connected: " + this.skClient);
        JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));

        JPanel panelLeft = createPanelLeft(this.skClient);
        JPanel panelRight = createPanelRight(this.skClient);

        containerPanelLeftAndRight.removeAll();
        containerPanelLeftAndRight.add(panelLeft);
        containerPanelLeftAndRight.add(panelRight);

        getContentPane().add(containerPanelLeftAndRight);
        revalidate();
        repaint();

        setVisible(true);
    }

    private JPanel createPanelLeft(Socket sk) {
        JPanel panelLeft = new JPanel(new BorderLayout());
        JLabel videoLabel = new JLabel();

        if (!this.isHost) {
            if (this.skClient != null) {
                new Thread(() -> {
                    try {
                        ObjectInputStream inputStream = new ObjectInputStream(this.skClient.getInputStream());
                        while (true) {
                            byte[] imageData = (byte[]) inputStream.readObject();

                            // Display image on JLabel
                            ImageIcon imageIcon = new ImageIcon(imageData);
                            videoLabel.setIcon(imageIcon);
                            panelLeft.revalidate();
                            panelLeft.repaint();
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("Error in createPanelLeft");
                    }
                }).start();
            } else {
                System.out.println("skClient null in createPanelLeft");
            }
        } else {
            WebcamPanel webcamPanel = initializeWebcam();
            panelLeft.add(webcamPanel, BorderLayout.CENTER);

            if (this.skHost != null) {
                new Thread(() -> {
                    try {
                        ObjectOutputStream outputStream = new ObjectOutputStream(this.skHost.getOutputStream());
                        ImageIcon ic;
                        BufferedImage br;

                        while (true) {
                            br = initializeWebcam().getImage();
                            ic = new ImageIcon(br);
                            outputStream.writeObject(ic);
                            outputStream.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error in createPanelLeft");
                    }
                }).start();
            } else {
                System.out.println("skHost null in createPanelLeft");
                
                new Thread(() -> {
                    try {
                        while (true) {
                            this.skHost = ss.accept();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                
            }
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

    private JPanel createPanelRight(Socket sk) {
        JPanel panelRight = new JPanel(new BorderLayout());

        if (!this.isHost) {
            System.out.println("!isHost in createPanelRight");
            WebcamPanel webcamPanel = initializeWebcam();
            panelRight.add(webcamPanel, BorderLayout.CENTER);

            if (this.skClient != null) {
                new Thread(() -> {
                    try {
                        ObjectOutputStream outputStream = new ObjectOutputStream(this.skClient.getOutputStream());
                        ImageIcon ic;
                        BufferedImage br;

                        while (true) {
                            br = initializeWebcam().getImage();
                            ic = new ImageIcon(br);
                            outputStream.writeObject(ic);
                            outputStream.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error in createPanelRight");
                    }
                }).start();
            } else {
                System.out.println("skClient null in createPanelRight");
            }
        } else {
            System.out.println("isHost in createPanelRight");
            JLabel labelHostInfo = new JLabel("Host IP: " + this.IP_Server + " Port: " + this.port);
            panelRight.add(labelHostInfo, BorderLayout.PAGE_START);
            JLabel videoLabel = new JLabel();

            if (this.skHost != null) {
                new Thread(() -> {
                    try {
                        ObjectInputStream inputStream = new ObjectInputStream(skHost.getInputStream());
                        while (true) {
                            byte[] imageData = (byte[]) inputStream.readObject();

                            // Display image on JLabel
                            ImageIcon imageIcon = new ImageIcon(imageData);
                            videoLabel.setIcon(imageIcon);
                            panelRight.revalidate();
                            panelRight.repaint();
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("Error in createPanelRight");
                    }
                }).start();
            } else {
                System.out.println("skHost null in createPanelRight");
            }
        }

        return panelRight;
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
        ImageIcon scaledIconOn = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIconOn);

        return button;
    }
     
//     public static void main(String[] args) {
//        new RoomInterface("localhost", 1235, "d", true);
//    }
}
