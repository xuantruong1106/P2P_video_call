
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class RoomInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = false;
    private boolean isMicOn = false;
    public static int port;
    public static String name;
    private static boolean isHost;
    private static WebcamPanel webcamPanelRight;

    // Phương thức để hiển thị RoomInterface
    public void displayRoomInterface(String name, int port, boolean isHost) {
        this.name = name;
        this.port = port;
        this.isHost = isHost;

        setTitle("Video Room");
        setSize(1000, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (isHost) {
            try {
                Socket sk1 = new Socket("localhost", 1106);
                DataInputStream din = new DataInputStream(sk1.getInputStream());
                DataOutputStream dos = new DataOutputStream(sk1.getOutputStream());

                dos.writeUTF(name);
                dos.writeInt(port);

                ServerSocket ss = new ServerSocket(port);

                while (true) {
                    Socket sk = ss.accept();

                    JPanel panelLeft = createPanelLeft();
                    JPanel panelRight = createPanelRight(port, isHost);
                    JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
                    containerPanelLeftAndRight.add(panelLeft);
                    containerPanelLeftAndRight.add(panelRight);
                    add(containerPanelLeftAndRight);

                    setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Socket s = new Socket("localhost", port);
                JPanel panelLeft = createPanelLeft();
                JPanel panelRight = createPanelRight(port, isHost);
                JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
                containerPanelLeftAndRight.add(panelLeft);
                containerPanelLeftAndRight.add(panelRight);
                add(containerPanelLeftAndRight);

                setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private JPanel createPanelLeft() {
        JPanel panelLeft = new JPanel(new BorderLayout());

        // Your webcam initialization code here
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        isCameraOn = true;
        isMicOn = true;

        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setMirrored(true);

        Thread thread = new Thread(() -> {
            webcam.open(); // Mở webcam
            while (true) {
                if (webcam.isOpen()) {
                    webcamPanel.start();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();

        ImageIcon iconOnMic = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/IconOnMic.png");
        Image scaledImage = iconOnMic.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledIconOnMic = new ImageIcon(scaledImage);
        JButton buttonOnOffMic = new JButton(scaledIconOnMic);

        // Load the image from file
        ImageIcon iconOnVideo = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/IconOnVideo.png");
        Image scaledImageVideo = iconOnVideo.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledIconOnVideo = new ImageIcon(scaledImageVideo);
        JButton buttonOnOffVideo = new JButton(scaledIconOnVideo);

        ImageIcon iconOnExit = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/IconExit.png");
        Image scaledImageExit = iconOnExit.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon scaledIconExit = new ImageIcon(scaledImageExit);
        JButton buttonExitVideoRoom = new JButton(scaledIconExit);

        buttonOnOffMic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle mic button action
                if (isMicOn) {

                    isMicOn = false;

                    ImageIcon iconOnMic = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/IconOnMic.png");
                    Image scaledImageMic = iconOnMic.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                    ImageIcon scaledIconOnMic = new ImageIcon(scaledImageMic);
                    buttonOnOffMic.setIcon(scaledIconOnMic);

                } else {
                    isMicOn = true;

                    ImageIcon iconOffMic = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/IconOffMic.png");
                    Image scaledImageMic = iconOffMic.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                    ImageIcon scaledIconOffMic = new ImageIcon(scaledImageMic);
                    buttonOnOffMic.setIcon(scaledIconOffMic);
                }
            }
        });

        buttonOnOffVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isCameraOn) {

                    webcam.open();
                    isCameraOn = false;
                    // If video is currently on, turn it off

                    // Load the image from file
                    ImageIcon iconOnVideo = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/IconOnVideo.png");

                    // Scale the image to the desired size
                    Image scaledImageVideo = iconOnVideo.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

                    // Create a new ImageIcon with the scaled image
                    ImageIcon scaledIconOnVideo = new ImageIcon(scaledImageVideo);

                    buttonOnOffVideo.setIcon(scaledIconOnVideo);

                } else {
                    webcam.close();
                    isCameraOn = true;
                    ImageIcon iconOnVideo = new ImageIcon("src/main/java/com/mycompany/baitaplonmonhoc/img/IconOffVideo.png");

                    // Scale the image to the desired size
                    Image scaledImageVideo = iconOnVideo.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);

                    // Create a new ImageIcon with the scaled image
                    ImageIcon scaledIconOnVideo = new ImageIcon(scaledImageVideo);

                    buttonOnOffVideo.setIcon(scaledIconOnVideo);
                }
            }
        });

        buttonExitVideoRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the JFrame
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                MainInterface main = new MainInterface();
                main.setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonOnOffMic);
        buttonPanel.add(buttonOnOffVideo);
        buttonPanel.add(buttonExitVideoRoom);

        panelLeft.add(webcamPanel, BorderLayout.CENTER);
        panelLeft.add(buttonPanel, BorderLayout.SOUTH);

        return panelLeft;
    }

    private JPanel createPanelRight(int port, boolean isHost) {
        JPanel panelRight = new JPanel(new BorderLayout());
        String portString = String.valueOf(port);

        if (isHost) {
            JLabel labelIDRoom = new JLabel("Room Port: " + portString);
            panelRight.add(labelIDRoom, BorderLayout.NORTH);
            // Your panelRight creation code here
        } else {
            // Create a new WebcamPanel for the client
            Webcam clientWebcam = Webcam.getDefault();
            clientWebcam.setViewSize(WebcamResolution.VGA.getSize());
            webcamPanelRight = new WebcamPanel(clientWebcam);
            webcamPanelRight.setMirrored(true);

            panelRight.add(webcamPanelRight, BorderLayout.CENTER);
        }

        return panelRight;
    }
}
