
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Create_Host_Video_Room extends JFrame {
    
    String IP_Server = "192.168.0.146";
    Scanner scanner = new Scanner(System.in);

    public Create_Host_Video_Room() {
        // Tạo một JFrame mới

        setTitle("Create Room");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tạo một JPanel để chứa các thành phần
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));

        // Tạo một JButton
        JButton buttonJoin = new JButton("Join");
        buttonJoin.setPreferredSize(new Dimension(80, 30));

        // Tạo JLabel và JTextField cho Enter Name
        JLabel labelEnterName = new JLabel("Enter Name:");
        JTextField textFieldEnterName = new JTextField();
        textFieldEnterName.setPreferredSize(new Dimension(180, 30));

        // Tạo JLabel và JTextField cho Enter Port
        JLabel labelEnterPort = new JLabel("Enter Port:");
        JTextField textFieldEnterPort = new JTextField();
        textFieldEnterPort.setPreferredSize(new Dimension(180, 30));

        // Thêm các thành phần vào JPanel
        panel.add(labelEnterPort);
        panel.add(textFieldEnterPort);
        panel.add(labelEnterName);
        panel.add(textFieldEnterName);
        panel.add(buttonJoin);

        // Thêm JPanel vào JFrame
        add(panel);

        // Căn giữa JFrame trên màn hình
        setLocationRelativeTo(null);

        buttonJoin.addActionListener((var arg0) -> {
            String name = textFieldEnterName.getText();
            int port = Integer.parseInt(textFieldEnterPort.getText());
            boolean isHost = true;

            Socket sk1;
            try {
                sk1 = new Socket(IP_Server, 1106);
                
                
                DataInputStream din = new DataInputStream(sk1.getInputStream());
                DataOutputStream dos = new DataOutputStream(sk1.getOutputStream());

                dos.writeUTF(name);
                dos.writeInt(port);

                RoomInterface ri = new RoomInterface(name, port, isHost);

                ri.setLocationRelativeTo(null);
                ri.setVisible(true);

                // Other actions
                dispose();
                setVisible(false);
            } catch (IOException ex) {
                Logger.getLogger(Create_Host_Video_Room.class.getName()).log(Level.SEVERE, null, ex);
                 ex.printStackTrace(); // In thông báo lỗi ra console (hoặc xử lý theo ý bạn)
                 JOptionPane.showMessageDialog(this, "Không thể kết nối đến máy chủ.", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
            }

        });

    }

}
