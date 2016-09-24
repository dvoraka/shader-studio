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
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import dvoraka.shaderstudio.examples.framework.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static com.jogamp.opengl.GL.*;

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

        Animator animator = new Animator(glWindow);
        animator.start();
    }

    private static class Buffer {
        public static final int VERTEX = 0;
        public static final int ELEMENT = 1;
        public static final int TRANSFORM = 2;
        public static final int MAX = 3;
    }

    private void initBuffers(GL3 gl3) {

        int vertexCount = 3;
        int vertexSize = vertexCount * 5 * Float.BYTES;
        float[] vertexData = new float[]{
                -1, -1,/**/ 1, 0, 0,
                +0, +2,/**/ 0, 0, 1,
                +1, -1,/**/ 0, 1, 0
        };

        int elementCount = 3;
        int elementSize = elementCount * Short.BYTES;
        short[] elementData = new short[]{
                0, 2, 1
        };

        IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Buffer.MAX);

        FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(vertexData);
        ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(elementData);

        gl3.glGenBuffers(Buffer.MAX, bufferName);
        gl3.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
        gl3.glBufferData(GL_ARRAY_BUFFER, vertexSize, vertexBuffer, GL_STATIC_DRAW);
        gl3.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl3.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
        gl3.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementSize, elementBuffer, GL_STATIC_DRAW);
        gl3.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        BufferUtils.destroyDirectBuffer(vertexBuffer);
        BufferUtils.destroyDirectBuffer(elementBuffer);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        System.out.println("Init");

        GL3 gl3 = drawable.getGL().getGL3();

        initBuffers(gl3);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Dispose");
        System.exit(0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
//        System.out.println("Display");

        GL3 gl3 = drawable.getGL().getGL3();
//        System.out.println(gl3.glGetError());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("Reshape");
    }
}
