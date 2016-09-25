package dvoraka.shaderstudio;

import javax.swing.*;
import java.awt.*;

/**
 * Editor area component.
 */
public class EditorArea extends JComponent {

    public EditorArea() {
        JTextPane textPane = new JTextPane();
        setLayout(new FlowLayout());
        add(textPane);
    }
}
