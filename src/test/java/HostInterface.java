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

public class HostInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    public static JLabel video = new JLabel();
    public static JLabel  videoOut = new JLabel();
    public HostInterface(int port, String name) throws ClassNotFoundException {
        SwingUtilities.invokeLater(() -> {
            
                setSize(800, 400);
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
                buttonExitVideoRoom.addActionListener(e -> exitVideoRoom());

                buttonPanel.add(buttonOnOffMic);
                buttonPanel.add(buttonOnOffVideo);
                buttonPanel.add(buttonExitVideoRoom);

                panelCenter.add(buttonPanel, BorderLayout.SOUTH);
                panelCenter.add(video, BorderLayout.CENTER);
                panelCenter.add(videoOut, BorderLayout.EAST);
                
                containerPanelLeftAndRight.add(panelCenter);

                getContentPane().add(containerPanelLeftAndRight);
                setVisible(true);
               
            
        });
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Server socket initialized");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                
                ImageIcon icOut;
                BufferedImage br;
                Webcam cam = Webcam.getDefault();
                
                if (cam.isOpen()) {
                    cam.close();
                }

                cam.setViewSize(new Dimension(640, 480));
                cam.open();
                isCameraOn = true;
                isMicOn = true;
            
                while (true) {
                    ImageIcon ic = (ImageIcon) in.readObject();
                    video.setIcon(ic);
                    System.out.println("in");
                    
                    
                    br = cam.getImage();
                    icOut = new ImageIcon(br);
                    out.writeObject(icOut);
                    videoOut.setIcon(icOut);
                    out.flush();
                    System.out.println("out");
                    
                }
            } catch (IOException ex) {
                Logger.getLogger(HostInterface.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(HostInterface.class.getName()).log(Level.SEVERE, null, ex);
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

    private void exitVideoRoom() {
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
    
//    public static void main(String[] args) throws ClassNotFoundException {
//        new HostInterface(1111, "d");
//    }
}
