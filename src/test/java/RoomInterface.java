
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    private static boolean isHostGlobal;
    private static String IP_Server_Global;
    private static int portGlobal;


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
                try {
                    ServerSocket ss = new ServerSocket(port);
                    System.out.println("ss done");
                    while(true){
                         Socket skHost = ss.accept();
                         if(skHost != null){
                              handleHost(skHost);
                         }
                        
                    }
                } catch (IOException ex) {
                    Logger.getLogger(RoomInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    Socket skClient = new Socket(IP_Server, port);
                    handleClient(skClient);
//                    while(true){
//                        if(skClient !=null)
//                        {
//                            
//                        }   
//                    }
                } catch (IOException ex) {
                    Logger.getLogger(RoomInterface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void handleHost(Socket sk) {
        
        System.out.println("RoomInterface.handleHost()");
        JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));

        JPanel panelLeft = createPanelLeft(sk);
        JPanel panelRight = createPanelRight(sk);

        containerPanelLeftAndRight.add(panelLeft);
        containerPanelLeftAndRight.add(panelRight);

        getContentPane().add(containerPanelLeftAndRight);
        setVisible(true);
    }

   

    private void handleClient(Socket sk) {
        JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));

        JPanel panelLeft = createPanelLeft(sk);
        JPanel panelRight = createPanelRight(sk);

        containerPanelLeftAndRight.add(panelLeft);
        containerPanelLeftAndRight.add(panelRight);

        getContentPane().add(containerPanelLeftAndRight);
        setVisible(true);

    }

    private JPanel createPanelLeft(Socket sk) {

        JPanel panelLeft = new JPanel(new BorderLayout());
        JLabel videoLabel = new JLabel();
        
        JButton buttonOnOffMic = createButton("IconOnMic.png", "IconOffMic.png");
        JButton buttonOnOffVideo = createButton("IconOnVideo.png", "IconOffVideo.png");
        JButton buttonExitVideoRoom = createButton("IconExit.png", null);

        JPanel buttonPanel = new JPanel();
        
        if (!isHostGlobal) {
            
             System.out.println("!isHost in createPanelLeft");         
            
            if (sk != null) {
//                new Thread(() -> {
                    try {
//                        ObjectInputStream inputStream = new ObjectInputStream(sk.getInputStream());
                        DataInputStream inputStream = new DataInputStream(sk.getInputStream());
//                        while (true) {
//                            byte[] imageData = (byte[]) inputStream.readObject();

                        // Display image on JLabel
//                            ImageIcon imageIcon = new ImageIcon(imageData);
//                            videoLabel.setIcon(imageIcon);
//                            panelLeft.revalidate();
//                            panelLeft.repaint();
//                            System.out.println("data 127" + inputStream.readUTF());
                        videoLabel.setText(inputStream.readUTF());
                        panelLeft.add(videoLabel, BorderLayout.CENTER);
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error in createPanelLeft");
                    }
//                }).start();
            } else {
                System.out.println("sk null in createPanelLeft");
            }
        } else {
            
            System.out.println("RoomInterface.createPanelLeft()");
            WebcamPanel webcamPanel = initializeWebcam();           
            
            buttonOnOffMic.addActionListener(e -> toggleMic(buttonOnOffMic));
            buttonOnOffVideo.addActionListener(e -> toggleVideo(buttonOnOffVideo));
            buttonExitVideoRoom.addActionListener(e -> exitVideoRoom());
            
            buttonPanel.add(buttonOnOffMic);
            buttonPanel.add(buttonOnOffVideo);
            buttonPanel.add(buttonExitVideoRoom);
            
            panelLeft.add(webcamPanel, BorderLayout.CENTER);
            panelLeft.add(buttonPanel, BorderLayout.SOUTH);
            
            if (sk != null) {
//                new Thread(() -> {
                    try {
//                        ObjectOutputStream outputStream = new ObjectOutputStream(skHost.getOutputStream());
//                        ImageIcon ic;
//                        BufferedImage br;
                        DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                        dos.writeUTF(" 148");
                        dos.flush();

//                        while (true) {
//                            br = initializeWebcam().getImage();
//                            ic = new ImageIcon(br);
//                            outputStream.writeObject(ic);
//                            outputStream.flush();

//                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error in createPanelLeft");
                    }
//                }).start();
            } else {
                System.out.println("skHost null in createPanelLeft");
            }
        }
        return panelLeft;
    }

    private JPanel createPanelRight(Socket sk) {

        JPanel panelRight = new JPanel(new BorderLayout());
        JLabel videoLabel = new JLabel();
        JPanel buttonPanel = new JPanel();
        
        JButton buttonOnOffMic = createButton("IconOnMic.png", "IconOffMic.png");
        JButton buttonOnOffVideo = createButton("IconOnVideo.png", "IconOffVideo.png");
        JButton buttonExitVideoRoom = createButton("IconExit.png", null);
        
        
        if (!isHostGlobal) {
            System.out.println("!isHost in createPanelRight");
            WebcamPanel webcamPanel = initializeWebcam();
            
             
            buttonOnOffMic.addActionListener(e -> toggleMic(buttonOnOffMic));
            buttonOnOffVideo.addActionListener(e -> toggleVideo(buttonOnOffVideo));
            buttonExitVideoRoom.addActionListener(e -> exitVideoRoom());
            
            buttonPanel.add(buttonOnOffMic);
            buttonPanel.add(buttonOnOffVideo);
            buttonPanel.add(buttonExitVideoRoom);
            
            
            panelRight.add(webcamPanel, BorderLayout.CENTER);
            panelRight.add(buttonPanel, BorderLayout.SOUTH);
            
            if (sk != null) {
//                new Thread(() -> {
                    try {
                        DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                        dos.writeUTF("220");
                        dos.flush();
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error in createPanelRight");
                    }
//                }).start();
            } else {
                System.out.println("sk null in createPanelRight");
            }
        } else {
            System.out.println("RoomInterface.createPanelRight()");
 
            System.out.println("isHost in createPanelRight");
            JLabel labelHostInfo = new JLabel("Host IP: " + IP_Server_Global + " Port: " + portGlobal);
            panelRight.add(labelHostInfo, BorderLayout.PAGE_START);
           

            if (sk != null) {
//                new Thread(() -> {
                    try {
                        DataInputStream dis = new DataInputStream(sk.getInputStream());
                        while(true){
                            String message = dis.readUTF();
                            System.out.println(message);
                            videoLabel.setText(message);
                            panelRight.add(videoLabel, BorderLayout.CENTER);
                        } 
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error in createPanelRight");
                    }
//                }).start();
                
                
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
//        new RoomInterface("192.168.0.144", 1235, "d", true);
//    }
}
