package me.ghoscher.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import me.ghoscher.gl.Program;

/**
 * Created by Hisham on 22/12/2014.
 */
public class Ripple extends GLSurfaceView implements GLSurfaceView.Renderer {

    Program program;
    FloatBuffer buffer;

    // Default params
    float radiusFraction = 0.5f;    // Percentage of max(width,height) to use as the maximum radius
    int color = 0xffffffff;         // Main view color

    int maxDimen;                   // Max(width, height)
    float radius = 0;               // Radius being rendered
    float targetRadius = 0;         // Radius value to animate towards
    int centerX, centerY;           // Touch event position

    int glPositionHandle = 0;       // Position attribute in shader

    public Ripple(Context context) {
        super(context);
        init(null);
    }

    public Ripple(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    private void init(AttributeSet attrs) {

        // Getting attributes set in XML if any
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.Ripple, 0, 0);

            int count = a.getIndexCount();

            for (int i = 0; i < count; i++) {
                int attr = a.getIndex(i);

                switch (attr) {
                    case R.styleable.Ripple_viewColor:
                        color = a.getColor(attr, color);
                        break;
                    case R.styleable.Ripple_rippleRadius:
                        radiusFraction = a.getFraction(attr, 1, 1, 0.5f);
                        radiusFraction /= 2f;
                        break;
                }
            }

            a.recycle();
        }

        setEGLContextClientVersion(2);
        setRenderer(this);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);

        //region Vertices initialization
        final float[] vertices = {
                -1f, -1f,
                +1f, -1f,
                -1f, 1f,

                -1f, 1f,
                +1f, -1f,
                +1f, 1f
        };

        buffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        buffer.put(vertices).position(0);
        //endregion

        program = new Program(getContext(), R.raw.me_ghoscher_button_vshader, R.raw.me_ghoscher_button_fshader);

        if (!program.isValid())
            throw new RuntimeException("Error creating program.");


        glPositionHandle = program.getAttributeLocation("a_Position");

        program.use();

        // Setting view color

        float a = (color >> 32) & 0xFF;
        float r = (color >> 16) & 0xFF;
        float g = (color >> 8) & 0xFF;
        float b = (color >> 0) & 0xFF;

        program.setVector4("u_Color", r / 255f, g / 255f, b / 255f, a / 255f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        program.setVector2("u_Size", width, height);
        program.setVector2("u_Center", width / 2, height / 2);

        maxDimen = Math.max(width, height);
        program.setFloat("u_Radius", radiusFraction * maxDimen);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handler.removeCallbacks(runnable);

        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            targetRadius = 0;
        } else {
            targetRadius = radiusFraction * maxDimen;
            centerX = (int) event.getX();
            centerY = (int) -event.getY();
            centerY += getHeight();
        }

        handler.post(runnable);
        return true;
    }

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (Math.abs(radius - targetRadius) < 1f)
                return;

            float a = 0.08f;

            if (targetRadius == 0)
                a *= 3f;

            radius = (1 - a) * radius + a * targetRadius;

            handler.postDelayed(this, 16);
            requestRender();
        }
    };

    @Override
    public void onDrawFrame(GL10 gl) {
        program.setVector2("u_Center", centerX, centerY);
        program.setFloat("u_Radius", radius);

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Pass in the position information
        buffer.position(0);
        GLES20.glVertexAttribPointer(glPositionHandle, 2, GLES20.GL_FLOAT, false,
                0, buffer);

        GLES20.glEnableVertexAttribArray(glPositionHandle);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            canvas.drawColor(color);
        }
    }
}
