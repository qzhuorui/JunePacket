package com.choryan.junepacket.filter;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author: ChoRyan Quan
 * @date: 6/11/21
 */
public class VOOutputFilter extends GPUImageFilter {

    public VOOutputFilter() {
        super(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        super.onOutputSizeChanged(width, height);

    }

    @Override
    public void onDraw(int fboTextureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        FloatBuffer vertBuffer;
        float[] adjustedVertices = new float[8];

        cubeBuffer.position(0);
        cubeBuffer.get(adjustedVertices);

        float normalizedHeight = (float) getOutputHeight() / (float) getOutputWidth();
        adjustedVertices[1] *= normalizedHeight;
        adjustedVertices[3] *= normalizedHeight;
        adjustedVertices[5] *= normalizedHeight;
        adjustedVertices[7] *= normalizedHeight;

        vertBuffer = ByteBuffer.allocateDirect(adjustedVertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertBuffer.put(adjustedVertices).position(0);
        super.onDraw(fboTextureId, cubeBuffer, textureBuffer);
    }
}
