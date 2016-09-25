package dvoraka.shaderstudio;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;

import javax.swing.*;
import java.awt.*;

/**
 * Shader studio GUI.
 */
public class ShaderStudio extends JFrame {

    public ShaderStudio() {
        setTitle("Shader studio");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        System.out.println("Maximum: " + GLProfile.getMaximum(true));
        Display display = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(display, 0);

        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        GLWindow glWindow = GLWindow.create(screen, glCapabilities);
        glWindow.addGLEventListener(new App());
        Animator animator = new Animator(glWindow);
        animator.start();

        EditorArea editorArea = new EditorArea();
        JButton testButton = new JButton("TEST");
        NewtCanvasAWT newtCanvasAWT = new NewtCanvasAWT(glWindow);

        Container pane = getContentPane();
        pane.setLayout(new FlowLayout());

        pane.add(editorArea);
        pane.add(testButton);
        pane.add(newtCanvasAWT);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ShaderStudio shaderStudio = new ShaderStudio();
            shaderStudio.setVisible(true);
        });
    }
}
