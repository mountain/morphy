package morphy.app;

import com.jhlabs.vecmath.Matrix4f;
import com.jhlabs.vecmath.Vector3f;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.io.StringWriter;
import java.nio.ByteBuffer;
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

    int WIDTH = 300;
    int HEIGHT = 300;

    private Matrix4f projection;
    private Matrix4f view;
    private Matrix4f model;
    private FloatBuffer matrix44Buffer;

    private int progId;
    private int projectionId;
    private int viewId;
    private int modelId;
    private int vaoId;

    private Vector3f modelPos = new Vector3f(0, 0, 0);
    private Vector3f modelAngle = new Vector3f(0, 0, 0);
    private Vector3f modelScale = new Vector3f(1, 1, 1);
    private Vector3f cameraPos = new Vector3f(0, 0, -1);

    private String source(String path) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(this.getClass().getResourceAsStream(path), writer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public void store(Matrix4f matrix, FloatBuffer buf) {
        buf.put(matrix.m00);
        buf.put(matrix.m01);
        buf.put(matrix.m02);
        buf.put(matrix.m03);
        buf.put(matrix.m10);
        buf.put(matrix.m11);
        buf.put(matrix.m12);
        buf.put(matrix.m13);
        buf.put(matrix.m20);
        buf.put(matrix.m21);
        buf.put(matrix.m22);
        buf.put(matrix.m23);
        buf.put(matrix.m30);
        buf.put(matrix.m31);
        buf.put(matrix.m32);
        buf.put(matrix.m33);
    }

    private float coTangent(float angle) {
        return (float)(1f / Math.tan(angle));
    }

    private float degreesToRadians(float degrees) {
        return degrees * (float)(Math.PI / 180d);
    }

    private void translate(Vector3f vec, Matrix4f src, Matrix4f dest) {
        dest.m30 += src.m00 * vec.x + src.m10 * vec.y + src.m20 * vec.z;
        dest.m31 += src.m01 * vec.x + src.m11 * vec.y + src.m21 * vec.z;
        dest.m32 += src.m02 * vec.x + src.m12 * vec.y + src.m22 * vec.z;
        dest.m33 += src.m03 * vec.x + src.m13 * vec.y + src.m23 * vec.z;
    }

    private void scale(Vector3f vec, Matrix4f src, Matrix4f dest) {
        dest.m00 = src.m00 * vec.x;
        dest.m01 = src.m01 * vec.x;
        dest.m02 = src.m02 * vec.x;
        dest.m03 = src.m03 * vec.x;
        dest.m10 = src.m10 * vec.y;
        dest.m11 = src.m11 * vec.y;
        dest.m12 = src.m12 * vec.y;
        dest.m13 = src.m13 * vec.y;
        dest.m20 = src.m20 * vec.z;
        dest.m21 = src.m21 * vec.z;
        dest.m22 = src.m22 * vec.z;
        dest.m23 = src.m23 * vec.z;
    }

    private void rotate(float angle, Vector3f axis, Matrix4f src, Matrix4f dest) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        float oneminusc = 1.0f - c;
        float xy = axis.x*axis.y;
        float yz = axis.y*axis.z;
        float xz = axis.x*axis.z;
        float xs = axis.x*s;
        float ys = axis.y*s;
        float zs = axis.z*s;

        float f00 = axis.x*axis.x*oneminusc+c;
        float f01 = xy*oneminusc+zs;
        float f02 = xz*oneminusc-ys;
        // n[3] not used
        float f10 = xy*oneminusc-zs;
        float f11 = axis.y*axis.y*oneminusc+c;
        float f12 = yz*oneminusc+xs;
        // n[7] not used
        float f20 = xz*oneminusc+ys;
        float f21 = yz*oneminusc-xs;
        float f22 = axis.z*axis.z*oneminusc+c;

        float t00 = src.m00 * f00 + src.m10 * f01 + src.m20 * f02;
        float t01 = src.m01 * f00 + src.m11 * f01 + src.m21 * f02;
        float t02 = src.m02 * f00 + src.m12 * f01 + src.m22 * f02;
        float t03 = src.m03 * f00 + src.m13 * f01 + src.m23 * f02;
        float t10 = src.m00 * f10 + src.m10 * f11 + src.m20 * f12;
        float t11 = src.m01 * f10 + src.m11 * f11 + src.m21 * f12;
        float t12 = src.m02 * f10 + src.m12 * f11 + src.m22 * f12;
        float t13 = src.m03 * f10 + src.m13 * f11 + src.m23 * f12;
        dest.m20 = src.m00 * f20 + src.m10 * f21 + src.m20 * f22;
        dest.m21 = src.m01 * f20 + src.m11 * f21 + src.m21 * f22;
        dest.m22 = src.m02 * f20 + src.m12 * f21 + src.m22 * f22;
        dest.m23 = src.m03 * f20 + src.m13 * f21 + src.m23 * f22;
        dest.m00 = t00;
        dest.m01 = t01;
        dest.m02 = t02;
        dest.m03 = t03;
        dest.m10 = t10;
        dest.m11 = t11;
        dest.m12 = t12;
        dest.m13 = t13;
    }


    private void setupMatrices() {
        // Setup projection matrix
        projection = new Matrix4f();
        float fieldOfView = 60f;
        float aspectRatio = (float)WIDTH / (float)HEIGHT;
        float near_plane = 0.1f;
        float far_plane = 100f;

        float y_scale = this.coTangent(this.degreesToRadians(fieldOfView / 2f));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far_plane - near_plane;

        projection.m00 = x_scale;
        projection.m11 = y_scale;
        projection.m22 = -((far_plane + near_plane) / frustum_length);
        projection.m23 = -1;
        projection.m32 = -((2 * near_plane * far_plane) / frustum_length);
        projection.m33 = 0;

        // Setup view matrix
        view = new Matrix4f();

        // Setup model matrix
        model = new Matrix4f();

        // Create a FloatBuffer with the proper size to store our matrices later
        matrix44Buffer = BufferUtils.createFloatBuffer(16);
    }

    private void logicCycle() {
        //-- Update matrices
        // Reset view and model matrices
        view = new Matrix4f();
        model = new Matrix4f();

        // Translate camera
        translate(cameraPos, view, view);

        // Scale, translate and rotate model
        scale(modelScale, model, model);
        translate(modelPos, model, model);
        rotate(this.degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1),
                model, model);
        rotate(this.degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0),
                model, model);
        rotate(this.degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0),
                model, model);

        // Upload matrices to the uniform variables
        glUseProgram(progId);

        store(projection, matrix44Buffer);
        matrix44Buffer.flip();
        glUniformMatrix4fv(projectionId, false, matrix44Buffer);
        store(view, matrix44Buffer);
        matrix44Buffer.flip();
        glUniformMatrix4fv(viewId, false, matrix44Buffer);
        store(model, matrix44Buffer);
        matrix44Buffer.flip();
        glUniformMatrix4fv(modelId, false, matrix44Buffer);

        glUseProgram(0);
    }

    private void renderCycle() {
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

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            private float rotationDelta = 15f;
            private float scaleDelta = 0.1f;
            private float posDelta = 0.1f;
            private Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
            private Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta, -scaleDelta);

            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                if ( key == GLFW_KEY_UP  && action == GLFW_RELEASE )
                    modelPos.y += posDelta;
                if ( key == GLFW_KEY_DOWN  && action == GLFW_RELEASE )
                    modelPos.y -= posDelta;
                if ( key == GLFW_KEY_LEFT  && action == GLFW_RELEASE )
                    modelAngle.z += rotationDelta;
                if ( key == GLFW_KEY_RIGHT  && action == GLFW_RELEASE )
                    modelAngle.z -= rotationDelta;
                if ( key == GLFW_KEY_P  && action == GLFW_RELEASE )
                    modelScale.add(scaleAddResolution);
                if ( key == GLFW_KEY_M  && action == GLFW_RELEASE )
                    modelScale.add(scaleMinusResolution);
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

        progId = glCreateProgram();
        projectionId = glGetUniformLocation(progId, "projection");
        viewId = glGetUniformLocation(progId, "view");
        modelId = glGetUniformLocation(progId, "model");

        setupMatrices();

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

        glUseProgram(progId);

        vaoId = glGenBuffers();
        glBindBuffer(GL_VERTEX_ARRAY_BUFFER_BINDING, vaoId);
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexes, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_VERTEX_ARRAY_BUFFER_BINDING, 0);

        glUseProgram(0);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (glfwWindowShouldClose(window) == GL_FALSE) {
            this.logicCycle();
            this.renderCycle();
        }
    }

    public static void main(String[] args) {
        SharedLibraryLoader.load();
        new Plant3DMain().run();
    }

}
