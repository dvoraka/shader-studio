package dvoraka.shaderstudio;

import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;

/**
 * JOGL test App.
 */
public class App implements GLEventListener {

    public static void main(String[] args) {
        App testApp = new App();
    }

    public App() {
        Display display = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(display, 0);

        System.out.println("Maximum: " + GLProfile.getMaximum(true));

        GLProfile glProfile = GLProfile.get(GLProfile.GL3);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);

        GLWindow glWindow = GLWindow.create(screen, glCapabilities);
        glWindow.setSize(1024, 768);
        glWindow.setPosition(50, 50);
        glWindow.setUndecorated(false);
        glWindow.setAlwaysOnTop(false);
        glWindow.setFullscreen(false);
        glWindow.setPointerVisible(true);
        glWindow.confinePointer(false);
        glWindow.setTitle("App");

        glWindow.addGLEventListener(this);

        glWindow.setVisible(true);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        System.out.println("Init");
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Dispose");
        System.exit(0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        System.out.println("Display");

        GL3 gl3 = drawable.getGL().getGL3();
        System.out.println(gl3.glGetError());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("Reshape");
    }
}
