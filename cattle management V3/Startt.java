import javax.swing.SwingUtilities;

public class Startt {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FrameIntroo frame = new FrameIntroo();
            frame.setVisible(true);
        });
    }
}