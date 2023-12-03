
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainInterface extends JFrame {
     String IP_Server = "192.168.0.146";
    public MainInterface() {
        setTitle("Simple Swing App");
        setSize(500, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton buttonCreateVideoRoom = new JButton("Create room video");
        JButton buttonJoin = new JButton("Join");

        JLabel labelEnterIP = new JLabel("Enter IP");
        JTextField textFieldEnterIP = new JTextField(10);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.add(buttonCreateVideoRoom);
        panel.add(labelEnterIP);
        panel.add(textFieldEnterIP);
        panel.add(buttonJoin);

        add(panel, BorderLayout.CENTER);

        buttonCreateVideoRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Create_Host_Video_Room ui1 = new Create_Host_Video_Room();
                ui1.setVisible(true);
                dispose();

            }
        });

        buttonJoin.addActionListener(e -> {
            System.out.println("Button Join clicked");
            // Thêm mã xử lý cho sự kiện tương ứng với JavaFX

            Enter_Name_Join__VIdeo_Room nj;
            
            try {
                int port = Integer.parseInt(textFieldEnterIP.getText());
                Socket sk = new Socket(IP_Server, port);
                nj = new Enter_Name_Join__VIdeo_Room(port);
                nj.setVisible(true);
                dispose();
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
