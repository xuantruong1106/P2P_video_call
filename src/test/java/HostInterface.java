
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HostInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    private static JLabel video = new JLabel();
    private static JLabel videoOut = new JLabel();
    private ImageIcon icOut;
    private BufferedImage br;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private ServerSocket serverSocket;
    
    public HostInterface(int port, String name) throws ClassNotFoundException {
        SwingUtilities.invokeLater(() -> {

            try {
                setSize(1200, 700);
                setTitle("Host Video Room");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
                
                JPanel panelCenter = new JPanel(new BorderLayout());
                JButton buttonOnOffMic = createButton("IconOnMic.png", "IconOffMic.png");
                JButton buttonOnOffVideo = createButton("IconOnVideo.png", "IconOffVideo.png");
                JButton buttonExitVideoRoom = createButton("IconExit.png", null);
                JPanel buttonPanel = new JPanel();
                
                buttonOnOffMic.addActionListener(e -> toggleMic(buttonOnOffMic));
                buttonOnOffVideo.addActionListener(e -> toggleVideo(buttonOnOffVideo));
                buttonExitVideoRoom.addActionListener(e -> {
                    try {
                        exitVideoRoom();
                    } catch (IOException ex) {
                        Logger.getLogger(HostInterface.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                
                InetAddress ip = InetAddress.getLocalHost();
                String portString = String.valueOf(port);
                
                JLabel ipRoom = new JLabel("IP: " + ip.getHostAddress());
                JLabel portRoom = new JLabel("Port: " + portString);
                
                buttonPanel.add(buttonOnOffMic);
                buttonPanel.add(buttonOnOffVideo);
                buttonPanel.add(buttonExitVideoRoom);
                buttonPanel.add(ipRoom);
                buttonPanel.add(portRoom);
                
                JPanel webcamPanel = new JPanel();
                webcamPanel.setLayout(new BorderLayout());
                webcam = Webcam.getDefault();
                webcam.setViewSize(new Dimension(640, 480));
                
                WebcamPanel camPanel = new WebcamPanel(webcam);
                camPanel.setFPSDisplayed(true);
                camPanel.setDisplayDebugInfo(true);
                camPanel.setImageSizeDisplayed(true);
                webcamPanel.add(camPanel, BorderLayout.CENTER);
                
                panelCenter.add(buttonPanel, BorderLayout.SOUTH);
                panelCenter.add(video, BorderLayout.EAST);
                panelCenter.add(webcamPanel, BorderLayout.CENTER);
                
                containerPanelLeftAndRight.add(panelCenter);
                
                getContentPane().add(containerPanelLeftAndRight);
                setVisible(true);
            } catch (UnknownHostException ex) {
                Logger.getLogger(HostInterface.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        new Thread(() -> {
            try {
                System.out.println(port);
                serverSocket = new ServerSocket(port);
                System.out.println("Server socket initialized");
                clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Thread for receiving data
                new Thread(() -> {
                    try {
                        in = new ObjectInputStream(clientSocket.getInputStream());
                       
                            while (true) {
                                ImageIcon ic = (ImageIcon) in.readObject();
                                video.setIcon(ic);
                                System.out.println("inFromClient");
                        }
                    } catch (IOException | ClassNotFoundException ex) {
                        Logger.getLogger(HostInterface.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();

                new Thread(() -> {
                    try {
                        out = new ObjectOutputStream(clientSocket.getOutputStream());

                        new Thread(() -> {
                            webcam.open();
                            isCameraOn = true;
                            isMicOn = true;
                        }).start();

                        while (true) {
                            
                            br = webcam.getImage();
                            icOut = new ImageIcon(br);
                            out.writeObject(icOut);
                            out.flush();
                            System.out.println("outToClient");
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(HostInterface.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
            } catch (IOException ex) {
                Logger.getLogger(HostInterface.class.getName()).log(Level.SEVERE, null, ex);
                video.setIcon(null);
            }  
        }).start();
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

    private void exitVideoRoom() throws IOException {
        if(clientSocket == null)
        {
            webcam.close();
            video.setIcon(null);
            MainInterface main = new MainInterface();
            main.setVisible(true);
            setVisible(false);
            dispose();
        }
            serverSocket.close();
            clientSocket.close();
            in.close();
            out.close();
            out.flush();
            webcam.close();
            video.setIcon(null);
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
