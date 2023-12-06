
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
import java.net.ServerSocket;
import java.net.Socket;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_java;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

public class RoomInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    private static int port;
    private static String name;
    private static boolean isHost;
    private WebcamPanel webcamPanelRight;

//    public RoomInterface(String name, int port, boolean isHost) {
//        this.name = name;
//        this.port = port;
//        this.isHost = isHost;
//
//        System.out.println(this.name + this.port + this.isHost);
//
//        SwingUtilities.invokeLater(() -> {
//            setSize(500, 700);
//            setTitle("Video Room");
//            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//            if (isHost) {
//                System.out.println("host");
//                try {
//
//                    ServerSocket ss = new ServerSocket(this.port);
//                    Socket sk = ss.accept();
//                    System.out.println("create server socket host done");
//                    ObjectInputStream inputStream = new ObjectInputStream(sk.getInputStream());
//                    ObjectOutputStream outputStream = new ObjectOutputStream(sk.getOutputStream());
//
//                    new Thread(() -> {
//                        try {
//                            while (true) {
//                                BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
//                                // Display receivedImage on the client's GUI
//                            }
//                        } catch (IOException | ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }).start();
//
//                    new Thread(() -> {
//                        try {
//                            while (true) {
//                                // Assuming you have a method to capture the webcam image, e.g., captureWebcamImage()
//                                BufferedImage webcamImage = captureWebcamImage();
//                                outputStream.writeObject(webcamImage);
//                                outputStream.flush();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }).start();
//
//                    JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
//
//                    JPanel panelLeft = createPanelLeft();
//                    JPanel panelRight = createPanelRight(this.port, this.isHost);
//
//                    containerPanelLeftAndRight.removeAll();
//                    containerPanelLeftAndRight.add(panelLeft);
//                    containerPanelLeftAndRight.add(panelRight);
//
//                    getContentPane().add(containerPanelLeftAndRight);
//                    revalidate();
//                    repaint();
//
//                    setVisible(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//                System.out.println("client");
//                try {
//                    Socket s = new Socket("192.168.0.146", this.port);
//
//                    ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
//                    ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());
//
//                    new Thread(() -> {
//                        try {
//                            while (true) {
//                                BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
//                                // Display receivedImage on the client's GUI
//                            }
//                        } catch (IOException | ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }).start();
//
//                    new Thread(() -> {
//                        try {
//                            while (true) {
//                                // Assuming you have a method to capture the webcam image, e.g., captureWebcamImage()
//                                BufferedImage webcamImage = captureWebcamImage();
//                                outputStream.writeObject(webcamImage);
//                                outputStream.flush();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }).start();
//
//                    // Modify your loop to receive and display frames
//                    JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
//
//                    JPanel panelLeft = createPanelLeft();
//                    JPanel panelRight = createPanelRight(this.port, this.isHost);
//
//                    containerPanelLeftAndRight.removeAll();
//                    containerPanelLeftAndRight.add(panelLeft);
//                    containerPanelLeftAndRight.add(panelRight);
//
//                    getContentPane().add(containerPanelLeftAndRight);
//                    revalidate();
//                    repaint();
//
//                    setVisible(true);
//
//                    while (true) {
//
//                        BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
//                        // Display receivedImage on the client's GUI
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
     public RoomInterface(String name, int port, boolean isHost) {
        this.name = name;
        this.port = port;
        this.isHost = isHost;

        SwingUtilities.invokeLater(() -> {
            setSize(500, 700);
            setTitle("Video Room");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            if (isHost) {
                System.out.println("host");
                try {
                    ServerSocket ss = new ServerSocket(this.port);
                    Socket sk = ss.accept();
                    System.out.println("create server socket host done");
                    ObjectInputStream inputStream = new ObjectInputStream(sk.getInputStream());
                    ObjectOutputStream outputStream = new ObjectOutputStream(sk.getOutputStream());

                    startImageReceiverThread(inputStream);
                    startImageSenderThread(outputStream);

                    setupGUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("client");
                try {
                    Socket s = new Socket("192.168.0.146", this.port);

                    ObjectInputStream inputStream = new ObjectInputStream(s.getInputStream());
                    ObjectOutputStream outputStream = new ObjectOutputStream(s.getOutputStream());

                    startImageReceiverThread(inputStream);
                    startImageSenderThread(outputStream);

                    setupGUI();

                    while (true) {
                        BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
                        // Display receivedImage on the client's GUI
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startImageReceiverThread(ObjectInputStream inputStream) {
        new Thread(() -> {
            try {
                while (true) {
                    BufferedImage receivedImage = (BufferedImage) inputStream.readObject();
                    // Display receivedImage on the client's GUI
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startImageSenderThread(ObjectOutputStream outputStream) {
        new Thread(() -> {
            try {
                while (true) {
                    BufferedImage webcamImage = captureWebcamImage();
                    outputStream.writeObject(webcamImage);
                    outputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupGUI() {
        JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
        JPanel panelLeft = createPanelLeft();
        JPanel panelRight = createPanelRight(this.port, this.isHost);

        containerPanelLeftAndRight.removeAll();
        containerPanelLeftAndRight.add(panelLeft);
        containerPanelLeftAndRight.add(panelRight);

        getContentPane().add(containerPanelLeftAndRight);
        revalidate();
        repaint();

        setVisible(true);
    }

    private BufferedImage captureWebcamImage() {
        // Implement your webcam capture logic here
        if (webcam != null && webcam.isOpen()) {
            return webcam.getImage();
        }
        return null;
    }

    private JPanel createPanelLeft() {
        JPanel panelLeft = new JPanel(new BorderLayout());

        webcam = Webcam.getDefault();

// Đảm bảo rằng webcam đã được đóng
        if (webcam.isOpen()) {
            webcam.close();
            // Bây giờ bạn có thể thay đổi độ phân giải

        }

        webcam.setViewSize(new Dimension(640, 480));
// Sau đó, bạn có thể mở lại webcam
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

        panelLeft.add(webcamPanel, BorderLayout.CENTER);
        panelLeft.add(buttonPanel, BorderLayout.SOUTH);

        return panelLeft;
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

    private JPanel createPanelRight(int port, boolean isHost) {
        JPanel panelRight = new JPanel(new BorderLayout());
        String portString = String.valueOf(this.port);

        if (this.isHost) {
            JLabel labelIDRoom = new JLabel("Room Port: " + portString);
            panelRight.add(labelIDRoom, BorderLayout.NORTH);
            // Your panelRight creation code here
        } else {
            webcam = Webcam.getDefault();

            // Đảm bảo rằng webcam đã được đóng
            if (webcam.isOpen()) {
                webcam.close();
                // Bây giờ bạn có thể thay đổi độ phân giải

            }

            webcam.setViewSize(new Dimension(640, 480));
            // Sau đó, bạn có thể mở lại webcam
            webcam.open();

            isCameraOn = true;
            isMicOn = true;

            webcamPanelRight = new WebcamPanel(webcam);
            webcamPanelRight.setMirrored(true);

            Thread thread = new Thread(() -> {
                webcam.open();
                while (true) {
                    if (webcam.isOpen()) {
                        webcamPanelRight.start();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();

            panelRight.add(webcamPanelRight, BorderLayout.CENTER);
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
        dispose();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
   
}
