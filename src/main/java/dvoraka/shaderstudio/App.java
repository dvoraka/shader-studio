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
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import dvoraka.shaderstudio.examples.framework.BufferUtils;
import dvoraka.shaderstudio.examples.framework.Semantic;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;

/**
 * JOGL test App.
 */
public class App implements GLEventListener {

    private IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Buffer.SIZE);
    private IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(1);
    private int programName;
    private long start;
    private float[] scale = new float[16];
    private float[] zRotazion = new float[16];
    private int modelToClipMatrixUL;
    private int elementSize;

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
        public static final int SIZE = 3;
    }

    private void initBuffers(GL3 gl3) {

        int vertexCount = 3;
        int vertexSize = vertexCount * 5 * Float.BYTES;
        // interleaved array - position and color
        float[] vertexData = new float[]{
                -1, -1,/**/ 1, 0, 0,
                +0, +2,/**/ 0, 0, 1,
                +1, -1,/**/ 0, 1, 0
        };

        int elementCount = 3;
        elementSize = elementCount * Short.BYTES;
        // indices array
        short[] elementData = new short[]{
                0, 2, 1
        };

        FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(vertexData);
        ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(elementData);

        gl3.glGenBuffers(Buffer.SIZE, bufferName);

        gl3.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
        gl3.glBufferData(GL_ARRAY_BUFFER, vertexSize, vertexBuffer, GL_STATIC_DRAW);
        gl3.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl3.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
        gl3.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementSize, elementBuffer, GL_STATIC_DRAW);
        gl3.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        BufferUtils.destroyDirectBuffer(vertexBuffer);
        BufferUtils.destroyDirectBuffer(elementBuffer);
    }

    private void initVertexArray(GL3 gl3) {

        vertexArrayName = GLBuffers.newDirectIntBuffer(1);
        gl3.glGenVertexArrays(1, vertexArrayName);

        gl3.glBindVertexArray(vertexArrayName.get(0));

        gl3.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
        int stride = (2 + 3) * Float.BYTES;
        int offset = 0;
        gl3.glEnableVertexAttribArray(Semantic.Attr.POSITION);
        gl3.glVertexAttribPointer(Semantic.Attr.POSITION, 2, GL_FLOAT, false, stride, offset);
        offset = 2 * Float.BYTES;
        gl3.glEnableVertexAttribArray(Semantic.Attr.COLOR);
        gl3.glVertexAttribPointer(Semantic.Attr.COLOR, 3, GL_FLOAT, false, stride, offset);
        gl3.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl3.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
        gl3.glBindVertexArray(0);
    }

    private void initProgram(GL3 gl3) {

        String root = "shaders";
        String name = "hello-triangle";

        ShaderCode vertShader = ShaderCode.create(
                gl3, GL_VERTEX_SHADER, this.getClass(), root,
                null, name, "vert", null, true);
        ShaderCode fragShader = ShaderCode.create(
                gl3, GL_FRAGMENT_SHADER, this.getClass(), root,
                null, name, "frag", null, true);

        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.add(vertShader);
        shaderProgram.add(fragShader);

        shaderProgram.init(gl3);

        programName = shaderProgram.program();

        /*
         * These links don't go into effect until you link the program. If you
         * want to change index, you need to link the program again.
         */
        gl3.glBindAttribLocation(programName, Semantic.Attr.POSITION, "position");
        gl3.glBindAttribLocation(programName, Semantic.Attr.COLOR, "color");
        gl3.glBindFragDataLocation(programName, Semantic.Frag.COLOR, "outputColor");

        shaderProgram.link(gl3, System.out);
        /*
         * Take in account that JOGL offers a GLUniformData class, here we don't
         * use it, but take a look to it since it may be interesting for you.
         */
        modelToClipMatrixUL = gl3.glGetUniformLocation(programName, "modelToClipMatrix");

        vertShader.destroy(gl3);
        fragShader.destroy(gl3);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        System.out.println("Init");

        GL3 gl3 = drawable.getGL().getGL3();

        initBuffers(gl3);
        initVertexArray(gl3);
        initProgram(gl3);

        gl3.glEnable(GL_DEPTH_TEST);

        start = System.currentTimeMillis();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Dispose");

        GL3 gl3 = drawable.getGL().getGL3();
        gl3.glDeleteProgram(programName);
        gl3.glDeleteVertexArrays(1, vertexArrayName);
        gl3.glDeleteBuffers(Buffer.SIZE, bufferName);

        System.exit(0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl3 = drawable.getGL().getGL3();

        gl3.glClearColor(0f, .2f, 0.3f, 1f);
        gl3.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        long now = System.currentTimeMillis();
        float diff = (float) (now - start) / 1000;
        /*
         * Here we build the matrix that will multiply our original vertex
         * positions. We scale, halving it, and rotate it.
         */
        scale = FloatUtil.makeScale(scale, true, 0.5f, 0.5f, 0.5f);
        zRotazion = FloatUtil.makeRotationEuler(zRotazion, 0, 0, 0, diff);
        float[] modelToClip = FloatUtil.multMatrix(scale, zRotazion);

        gl3.glUseProgram(programName);
        gl3.glBindVertexArray(vertexArrayName.get(0));

        gl3.glUniformMatrix4fv(modelToClipMatrixUL, 1, false, modelToClip, 0);
//        gl3.glDrawElements(GL_TRIANGLES, elementSize, GL_UNSIGNED_SHORT, 0);
        gl3.glDrawArrays(GL_TRIANGLES, 0, 3);

        gl3.glBindVertexArray(0);
        gl3.glUseProgram(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("Reshape");
    }
}
