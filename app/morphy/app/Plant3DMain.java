package morphy.app;

import com.hackoeur.jglm.Mat4;
import com.hackoeur.jglm.Matrices;
import com.hackoeur.jglm.Vec3;
import com.jhlabs.vecmath.Matrix4f;
import com.jhlabs.vecmath.Vector3f;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Plant3DMain {

    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback   keyCallback;

    // The window handle
    private long window;

    private String source(String path) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(this.getClass().getResourceAsStream(path), writer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

        try {
            init();
            loop();

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }

    private void init() {

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit() != GL11.GL_TRUE )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        int WIDTH = 300;
        int HEIGHT = 300;

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
            }
        });

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(60);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.4f, 0.0f);

        int progId = glCreateProgram();

        int vShader = glCreateShader(GL_VERTEX_SHADER);
        int fShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(vShader, source("SimpleVertexShader.glsl"));
        glShaderSource(fShader, source("SimpleFragmentShader.glsl"));
        glCompileShader(vShader);
        glCompileShader(fShader);
        glAttachShader(progId, vShader);
        glAttachShader(progId, fShader);
        glLinkProgram(progId);
        glValidateProgram(progId);
        System.out.println(glGetShaderInfoLog(vShader));
        System.out.println(glGetShaderInfoLog(fShader));
        glDeleteShader(vShader);
        glDeleteShader(fShader);

        FloatBuffer vertexes = createFloatBuffer(9);
        vertexes.put(new float[]{
                -1, -1, 0,
                1, -1, 0,
                0, 1, 0,
        });
        vertexes.flip();

        Mat4 projection = Matrices.perspective(45.0f, 4.0f / 3.0f, 0.1f, 100.0f);
        Mat4 view = Matrices.lookAt(
                new Vec3(4, 3, 3),// Camera is at (4,3,3), in World Space
                new Vec3(0,0,0),  // and looks at the origin
                new Vec3(0,-1,0)); // Head is up (set to 0,-1,0 to look upside-down)
        Mat4 model = new Mat4(1.0f);// Changes for each model !
        Mat4 transform = projection.multiply(view).multiply(model);

        FloatBuffer transBuffer = transform.getBuffer();
        transBuffer.flip().limit(transBuffer.capacity());
        FloatBuffer floatBuffer = ByteBuffer.allocateDirect (16 * 4).asFloatBuffer();
        floatBuffer.put(transBuffer);
        floatBuffer.flip().limit(floatBuffer.capacity());
        ByteBuffer byteBuffer = (ByteBuffer) ((sun.nio.ch.DirectBuffer)floatBuffer).attachment();
        byteBuffer.flip().limit(byteBuffer.capacity());

        //int transId = glGetUniformLocation(progId, "transform");
        //glUniformMatrix4fv(transId, false, floatBuffer);

        glUseProgram(progId);

        int vaoId = glGenBuffers();
        glBindBuffer(GL_VERTEX_ARRAY_BUFFER_BINDING, vaoId);
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexes, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBufferData(GL_PROJECTION_MATRIX, floatBuffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_VERTEX_ARRAY_BUFFER_BINDING, 0);

        glUseProgram(0);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (glfwWindowShouldClose(window) == GL_FALSE) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glUseProgram(progId);

            glBindBuffer(GL_VERTEX_ARRAY_BUFFER_BINDING, vaoId);
            glEnableVertexAttribArray(0);
            glDrawArrays(GL_TRIANGLES, 0, 3);
            glDisableVertexAttribArray(0);
            glBindBuffer(GL_VERTEX_ARRAY_BUFFER_BINDING, 0);

            glUseProgram(0);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        SharedLibraryLoader.load();
        new Plant3DMain().run();
    }

}
