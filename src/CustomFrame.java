import javax.swing.JFrame;

public class CustomFrame extends JFrame {
    public CustomFrame(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(200,200,900,600);
        //setVisible(true);
    }
}
