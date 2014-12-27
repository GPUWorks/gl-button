package me.ghoscher.gl;

import android.content.Context;
import android.opengl.GLES20;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hisham on 22/12/2014.
 */
public class Program {

    private int handle;

    private Map<String, Integer> uniforms = new HashMap<>();
    private Map<String, Integer> attributes = new HashMap<>();

    public Program(Context context, int vertexShaderRes, int fragmentShaderRes) {
        this(
                Utils.readRawText(context, vertexShaderRes),
                Utils.readRawText(context, fragmentShaderRes)
        );
    }

    public Program(String vertexShader, String fragmentShader) {
        int vShader = Builder.compileShader(vertexShader, Builder.ShaderType.Vertex);
        int fShader = Builder.compileShader(fragmentShader, Builder.ShaderType.Fragment);

        if (vShader == 0 || fShader == 0)
            return;

        handle = GLES20.glCreateProgram();

        GLES20.glAttachShader(handle, vShader);
        GLES20.glAttachShader(handle, fShader);

        GLES20.glLinkProgram(handle);

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, linkStatus, 0);

        // If the link failed, delete the program.
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(handle);
            handle = 0;
        }
    }

    public int getAttributeLocation(String name) {
        Integer location = attributes.get(name);

        if (location == null) {
            location = GLES20.glGetAttribLocation(handle, name);
            attributes.put(name, location);
        }

        return location;
    }

    public int getUniformLocation(String name) {
        Integer location = uniforms.get(name);

        if (location == null) {
            location = GLES20.glGetUniformLocation(handle, name);
            uniforms.put(name, location);
        }

        return location;
    }

    public void setFloat(String name, float value) {
        GLES20.glUniform1f(getUniformLocation(name), value);
    }

    public void setVector2(String name, float x, float y) {
        GLES20.glUniform2f(getUniformLocation(name), x, y);
    }

    public void setVector3(String name, float x, float y, float z) {
        GLES20.glUniform3f(getUniformLocation(name), x, y, z);
    }

    public void setVector4(String name, float x, float y, float z, float w) {
        GLES20.glUniform4f(getUniformLocation(name), x, y, z, w);
    }

    public boolean isValid() {
        return handle != 0;
    }

    public void use() {
        GLES20.glUseProgram(handle);
    }

    public int getHandle() {
        return handle;
    }

    public static class Builder {
        public enum ShaderType {
            Vertex, Fragment;

            public int getType() {
                if (this == Vertex)
                    return GLES20.GL_VERTEX_SHADER;
                else
                    return GLES20.GL_FRAGMENT_SHADER;
            }
        }


        public static int compileShader(String source, ShaderType type) {
            int pointer = GLES20.glCreateShader(type.getType());

            if (pointer != 0) {
                GLES20.glShaderSource(pointer, source);

                // Compile the shader.
                GLES20.glCompileShader(pointer);

                // Get the compilation status.
                final int[] compileStatus = new int[1];
                GLES20.glGetShaderiv(pointer, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

                // If the compilation failed, delete the shader.
                if (compileStatus[0] == 0) {
                    GLES20.glDeleteShader(pointer);
                    pointer = 0;
                }
            }

            return pointer;
        }
    }
}
