
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

        this.IP_Server_Global = IP_Server;
        this.isHostGlobal = isHost;
        this.portGlobal = port;

        System.out.println(IP_Server + " " + port);
        SwingUtilities.invokeLater(() -> {
            setSize(800, 400);
            setTitle("Video Room");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            if (isHost) {
                // Run the server initialization in a separate thread
                new Thread(() -> {
                    try {
                        ServerSocket ss = new ServerSocket(port);
                        System.out.println("ss done");
//                        while(true){
                             Socket skHost = ss.accept();
                            System.out.println(skHost);
                            handle(skHost);
//                        }
                       
                    } catch (IOException ex) {
                        Logger.getLogger(RoomInterface.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
            } else {
                try {
                    Socket skClient = new Socket(IP_Server, port);
                    handle(skClient);
                } catch (IOException ex) {
                    Logger.getLogger(RoomInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

   private void handle(Socket sk) {
       System.out.println(sk);
    SwingUtilities.invokeLater(() -> {
        JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));

        JPanel panelCenter = createPaneCenter(sk);

        containerPanelLeftAndRight.add(panelCenter);

        getContentPane().add(containerPanelLeftAndRight);
        setVisible(true);
    });
}


    private JPanel createPaneCenter(Socket sk) {

        JPanel panelCenter = new JPanel(new BorderLayout());

        JButton buttonOnOffMic = createButton("IconOnMic.png", "IconOffMic.png");
        JButton buttonOnOffVideo = createButton("IconOnVideo.png", "IconOffVideo.png");
        JButton buttonExitVideoRoom = createButton("IconExit.png", null);

        JPanel buttonPanel = new JPanel();

        if (!isHostGlobal) {
            System.out.println("RoomInterface.createPanelLeft()");
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
//                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
//                DataInputStream dis = new DataInputStream(sk.getInputStream());
//
//                video.setText(dis.readUTF());
//                panelCenter.add(video, BorderLayout.EAST);
//
//                dos.writeUTF("Client");
//                dos.flush();

                while (true) {
                    DataInputStream dis = new DataInputStream(sk.getInputStream());

                    int length = dis.readInt(); // read length of incoming message
                    bytes = new byte[length];

                    if (length > 0) {
                        dis.readFully(bytes, 0, bytes.length); // read the message
                    }
                    BufferedImage receivedImage = ImageIO.read(new ByteArrayInputStream(bytes));

                    ImageIcon imageIcon = new ImageIcon(receivedImage);

                    video.setIcon(imageIcon);
                    panelCenter.add(video, BorderLayout.EAST);
                    
                    BufferedImage image = webcam.getImage();
                     
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    
                    ImageIO.write(image, "jpg", baos);
                    byte[] bytes = baos.toByteArray();

                    DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                    dos.writeInt(bytes.length);
                    dos.write(bytes);
                    
                    
                    
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error in createPanelLeft");
            }

        } else {

            System.out.println("RoomInterface.createPanelLeft()");
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
//                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
//                DataInputStream dis = new DataInputStream(sk.getInputStream());
//
//                dos.writeUTF("host");
//                dos.flush();

//                if(dis != null)
//                {
//                    video.setText(dis.readUTF());
//                    panelCenter.add(video, BorderLayout.EAST);
//                }else{
//                    System.out.println("null");
//                }

               while (true) {
                    BufferedImage image = webcam.getImage();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpg", baos);
                    byte[] bytes = baos.toByteArray();

                    DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                    dos.writeInt(bytes.length);
                    dos.write(bytes);

                    DataInputStream dis = new DataInputStream(sk.getInputStream());

                    int length = dis.readInt(); // read length of incoming message
                    bytes = new byte[length];

                    if (length > 0) {
                        dis.readFully(bytes, 0, bytes.length); // read the message
                    }
                    BufferedImage receivedImage = ImageIO.read(new ByteArrayInputStream(bytes));

                    // Convert the receivedImage to ImageIcon
                    ImageIcon imageIcon = new ImageIcon(receivedImage);

                    // Set the ImageIcon as icon of the JLabel
                    video.setIcon(imageIcon);
                    panelCenter.add(video, BorderLayout.EAST);
                    
                }
              
                
                

                

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error in createPanelLeft");
            }
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
}
