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

public class ClientInterface extends JFrame {

    private Webcam webcam;
    private boolean isCameraOn = true;
    private boolean isMicOn = true;
    private static JLabel video = new JLabel();
    private static JLabel videoIn = new JLabel();
    
    public ClientInterface(String IP_Server, int port, String name) throws ClassNotFoundException {
        SwingUtilities.invokeLater(() -> {
        
                setSize(800, 400);
                setTitle("Client Video Room");
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
//                panelCenter.add(videoIn, BorderLayout.EAST);
                
                containerPanelLeftAndRight.add(panelCenter);

                getContentPane().add(containerPanelLeftAndRight);
                setVisible(true);
        });
        
     new Thread(() -> {
        
        try {
            Socket Socket = new Socket(IP_Server, port);
            
//            ObjectInputStream in = new ObjectInputStream(Socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(Socket.getOutputStream());
           
            ImageIcon ic ;
            BufferedImage br;
            
            webcam = Webcam.getDefault();
            
            if (webcam.isOpen()) {
                webcam.close();
            }
            
            webcam.setViewSize(new Dimension(640, 480));
            webcam.open();
            isCameraOn = true;
            isMicOn = true;
            
            while (true) {            
                br = webcam.getImage();
                ic = new ImageIcon(br);
                out.writeObject(ic);
                video.setIcon(ic);
                out.flush();
                System.out.println("outToHost");
                
//                ImageIcon icIn = (ImageIcon) in.readObject();
//                videoIn.setIcon(ic);
//                System.out.println("inFromClient");
            }
            
        } catch (IOException ex) {
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
    
//     public static void main(String[] args) throws ClassNotFoundException {
//        new ClientInterface( "192.168.0.144",1111, "d");
//    }
}
