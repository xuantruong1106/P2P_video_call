
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.util.logging.Level;
//import java.util.logging.Logger;

public class Enter_Name_Join__VIdeo_Room extends JFrame {
    
    public Enter_Name_Join__VIdeo_Room(int port) throws Exception {
        // Set layout manager
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton buttonJoin = new JButton("Join");
        buttonJoin.setPreferredSize(new Dimension(80, 50));
        buttonJoin.setBackground(Color.WHITE);
        buttonJoin.setForeground(new Color(51, 102, 204));

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel labelEnterName = new JLabel("Enter Name");

        JTextField textFieldEnterName = new JTextField();
        textFieldEnterName.setPreferredSize(new Dimension(100, 50));

        panel.add(labelEnterName);
        panel.add(textFieldEnterName);

        add(panel);
        add(buttonJoin);

        // Set up the action listener for the button
        buttonJoin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add your logic for the button click here
                String enterName = textFieldEnterName.getText();
                boolean isHost = false;
//                     DataInputStream din = new DataInputStream(sk.getInputStream());
//                     DataOutputStream dos = new DataOutputStream(sk.getOutputStream());

                RoomInterface ri = new RoomInterface(enterName, port, isHost);
                ri.setLocationRelativeTo(null);
                ri.setVisible(true);

                // Other actions
                dispose();
                setVisible(false);

            }
        });

        // Set frame properties
        setTitle("Simple Swing App");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
    }

}
