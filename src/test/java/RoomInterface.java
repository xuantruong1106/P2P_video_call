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
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    private static int port;
    private static String name;
    private static boolean isHost;
    private WebcamPanel webcamPanelRight;
    private Socket s;
    private Socket sk;
    
    private String IP_Server;
   public RoomInterface(String IP_Server, int port, String name, boolean isHost) {
        this.name = name;
        this.port = port;
        this.isHost = isHost;
        this.IP_Server = IP_Server;

        System.out.println("Roominterface 37: " + this.name + " " + this.port + " " + this.isHost);

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
                            while (true) {
                                sk = ss.accept();
                                System.out.println("Room interface 52: create server socket host done");

                                // Handle client connection
                                DataInputStream din = new DataInputStream(sk.getInputStream());
                                System.out.println("Room interface 59 || Client video room join: " + din.readUTF());
                                
                                // Continue listening for more clients
                            }
                        } catch (SocketException se) {
                            // Handle client disconnection
                            System.out.println("Client disconnected");
                            // Set haveClients to false when there are no connected clients
                            while (true) {
                                try {
                                    sk = ss.accept();
                                    // Handle client connection
                                    DataInputStream din1 = new DataInputStream(sk.getInputStream());
                                    System.out.println("Room interface 54 || Client video room join: " + din1.readUTF());
                                } catch (IOException ex) {
                                    Logger.getLogger(RoomInterface.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                // Continue listening for more clients
                            }
                        } catch (IOException e) {
                            // Handle other exceptions, e.g., if the server socket is closed
                            e.printStackTrace();
                        } finally {
                            // Optionally, you can close the server socket or perform cleanup here
                        }
                    }).start();

                    JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));

                    JPanel panelLeft = createPanelLeft(sk);
                    JPanel panelRight = createPanelRight(sk);

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
                System.out.println("Room interface 111: client");
                try {
                    s = new Socket(IP_Server, this.port);

                    // Modify your loop to receive and display frames
                    JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));

                    JPanel panelLeft = createPanelLeft(sk);
                    JPanel panelRight = createPanelRight(sk);

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


    private JPanel createPanelLeft(Socket sk) throws IOException {
        JPanel panelLeft = new JPanel(new BorderLayout());

        if (!this.isHost) {
            // If there are clients and this is not the host
//            ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
//            JPanel videoDisplayPanel = new JPanel();

//            new Thread(() -> {
//                try {
//                    while (true) {
//                        BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
//                        updateVideoDisplay(videoDisplayPanel, receivedImage);
//                        panelLeft.add(videoDisplayPanel, BorderLayout.CENTER);
//                    }
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }).start();

            System.out.println("no host");
        } else {
            WebcamPanel webcamPanel = initializeWebcam();
            panelLeft.add(webcamPanel, BorderLayout.CENTER);

//            new Thread(() -> {
//                try {
//                    ObjectOutputStream outputStream = new ObjectOutputStream(sk.getOutputStream());
//
//                    while (true) {
//                        BufferedImage currentFrame = new BufferedImage(WIDTH, HEIGHT, HEIGHT);
//                        outputStream.writeObject(currentFrame);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();
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
        ImageIcon iconOn = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/" + iconOnPath);
        Image scaledImage = iconOn.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledIconOn = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIconOn);
        
        return  button;
    }

    private JPanel createPanelRight(Socket sk) throws IOException {
    JPanel panelRight = new JPanel(new BorderLayout());

    if (!this.isHost) {
        System.out.println("220: !isHost");
        WebcamPanel webcamPanel = initializeWebcam();
        panelRight.add(webcamPanel, BorderLayout.NORTH);
        
//        new Thread(() -> {
//                try {
//                    ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());
//
//                    while (true) {
//                        BufferedImage currentFrame = new BufferedImage(WIDTH, HEIGHT, HEIGHT);
//                        outputStream.writeObject(currentFrame);
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();
    } else {
        System.out.println("193: isHost");
        JLabel labelHostInfo = new JLabel("Host IP: " + this.IP_Server + " Port: " + this.port);
        panelRight.add(labelHostInfo, BorderLayout.PAGE_START);
        
//        ObjectInputStream inputStream = new ObjectInputStream(sk.getInputStream());
//        JPanel videoDisplayPanel = new JPanel();

//        new Thread(() -> {
//                try {
//                    while (true) {
//                        BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
//                        updateVideoDisplay(videoDisplayPanel, receivedImage);
//                        panelRight.add(videoDisplayPanel, BorderLayout.CENTER);
//                    }
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }).start();
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