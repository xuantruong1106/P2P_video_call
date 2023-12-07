
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainInterface extends JFrame {
    
     InetAddress ip;
     String IP_Server = "";
    public MainInterface() {
        
        try {
            
            ip = InetAddress.getLocalHost();
            IP_Server = ip.getHostAddress();
            
        } catch (Exception e) {
        }
        setTitle("Simple Swing App");
        setSize(700, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton buttonCreateVideoRoom = new JButton("Create room video");
        JButton buttonJoin = new JButton("Join");

        JLabel labelEnterIP = new JLabel("Enter IP");
        JTextField textFieldEnterIP = new JTextField(10);
        
        JLabel labelEnterPort = new JLabel("Enter Port");
        JTextField textFieldEnterPort = new JTextField(10);
        
        JLabel labelEnterName = new JLabel("Enter Name");
        JTextField textFieldEnterName = new JTextField(10);
        
        JPanel createRoomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        createRoomPanel.add(buttonCreateVideoRoom);
        
        JPanel joinPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        joinPanel.add(labelEnterIP);
        joinPanel.add(textFieldEnterIP);
        joinPanel.add(labelEnterPort);
        joinPanel.add(textFieldEnterPort);
        joinPanel.add(labelEnterName);
        joinPanel.add(textFieldEnterName);
        joinPanel.add(buttonJoin);
        
       // Create the main panel with a GridLayout for organizing the subpanels
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.add(createRoomPanel);
        mainPanel.add(joinPanel);

        add(mainPanel, BorderLayout.CENTER);

        buttonCreateVideoRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Create_Host_Video_Room ui1 = new Create_Host_Video_Room();
                ui1.setVisible(true);
                dispose();

            }
        });

        buttonJoin.addActionListener(e -> {
            try {
                
                String IP = String.format(textFieldEnterIP.getText());
                int port = Integer.parseInt(textFieldEnterPort.getText());
                String enterName = String.format(textFieldEnterName.getText());

                boolean isHost = false;
                
                System.out.println("Main interface  89: "+ IP + " " + port + " " + enterName + " " + isHost);
                
                Socket sk = new Socket(IP, port);
                
                DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
                dos.writeUTF(enterName);
                
                RoomInterface ri = new RoomInterface(IP, port, enterName, isHost);
                ri.setLocationRelativeTo(null);
                ri.setVisible(true);

                dispose();
                setVisible(false);
                
            } catch (Exception ex) {
                Logger.getLogger(MainInterface.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến máy chủ.", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
            }

        });

        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        MainInterface main = new MainInterface();

        main.setVisible(true);
    }
}
