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

    public ClientInterface(String IP_Server, int port, String name) throws ClassNotFoundException {
        setSize(800, 400);
        setTitle("Host Video Room");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        JPanel containerPanelLeftAndRight = new JPanel(new GridLayout(1, 2));
        
        WebcamPanel webcam = initializeWebcam();
        
        JPanel panelCenter = new JPanel(new BorderLayout());
        
        JButton buttonOnOffMic = createButton("IconOnMic.png", "IconOffMic.png");
        JButton buttonOnOffVideo = createButton("IconOnVideo.png", "IconOffVideo.png");
        JButton buttonExitVideoRoom = createButton("IconExit.png", null);
        JPanel buttonPanel = new JPanel();
       
        JLabel video = new JLabel();

        buttonOnOffMic.addActionListener(e -> toggleMic(buttonOnOffMic));
        buttonOnOffVideo.addActionListener(e -> toggleVideo(buttonOnOffVideo));
        buttonExitVideoRoom.addActionListener(e -> exitVideoRoom());

        buttonPanel.add(buttonOnOffMic);
        buttonPanel.add(buttonOnOffVideo);
        buttonPanel.add(buttonExitVideoRoom);

        panelCenter.add(buttonPanel, BorderLayout.SOUTH);
        panelCenter.add(video, BorderLayout.CENTER);
        
        containerPanelLeftAndRight.add(panelCenter);
        
         getContentPane().add(containerPanelLeftAndRight);
         setVisible(true);
        try {
            Socket Socket = new Socket(IP_Server, port);
           
            
           ObjectOutputStream out = new ObjectOutputStream(Socket.getOutputStream());
           
            ImageIcon ic ;
            BufferedImage br;
            
            while (true) {            
            br = webcam.getImage();
            ic = new ImageIcon(br);
            out.writeObject(ic);
            out.flush();
            video.setIcon(ic);
        }
            
        } catch (IOException ex) {
            Logger.getLogger(HostInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
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
        return new JButton(new ImageIcon(scaledImage));
    }
}
