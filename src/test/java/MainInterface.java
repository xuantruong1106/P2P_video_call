import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainInterface extends JFrame {

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
                IP_Name ui1 = new IP_Name();
                ui1.setVisible(true);
                dispose();
                
            }
        });

        buttonJoin.addActionListener(e -> {
            System.out.println("Button Join clicked");
            // Thêm mã xử lý cho sự kiện tương ứng với JavaFX
            int port = Integer.parseInt(textFieldEnterIP.getText());
            NameJoin nj = new NameJoin(port);
            nj.setVisible(true);
             dispose();
            
        });
        
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        MainInterface main = new MainInterface();
        
        main.setVisible(true);
    }
}
