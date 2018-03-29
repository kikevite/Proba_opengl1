package kikevite.proba_opengl1;

import android.graphics.Bitmap;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EffectsRenderer implements GLSurfaceView.Renderer {

    private Bitmap photo;
    private int photoWidth, photoHeight;
    private EffectContext effectContext;
    private Effect effect;
    private int textures[] = new int[2];
    private Square square;
    private float param = 1f;

    public void serParam(float param) {
        this.param = param;
    }

    public void setTexture(Bitmap texture) {
        photo = texture;
        int limit = 700;
        int alt = texture.getWidth();
        int ample = texture.getHeight();
        int max = Math.max(alt, ample);
        float ratio = (float) ample / (float) alt;
        if (max > limit) {
            if (max == ample) {
                photoWidth = limit;
                photoHeight = (int) (limit * ratio);
            } else if (max == alt) {
                photoWidth = (int) (limit / ratio);
                photoHeight = limit;
            }
        }
    }

    public EffectsRenderer() {
        super();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0, 0, 0, 1);
        generateSquare();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (effectContext == null) {
            effectContext = EffectContext.createWithCurrentGlContext();
        }
        if (effect != null) {
            effect.release();
        }
        brightnessEffect(param);
        square.draw(textures[1]);
    }

    private void generateSquare() {
        GLES20.glGenTextures(2, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, photo, 0);
        square = new Square();
    }

    private void grayScaleEffect(float f) {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void brightnessEffect(float f) {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
        effect.setParameter("brightness", f);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }
}
