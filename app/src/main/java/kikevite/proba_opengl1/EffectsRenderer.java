package kikevite.proba_opengl1;

import android.graphics.Bitmap;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EffectsRenderer implements GLSurfaceView.Renderer {

    private Bitmap photo;                   // Bitmap on es guarda la imatge
    private int photoWidth, photoHeight;    // Ample i alt del bitmap 'photo'
    private EffectContext effectContext;
    private Effect effect;
    private int textures[] = new int[2];    // ID de les textures OpenGL de la imatge normal i amb efecte
    private Square square;                  // Lienzo on es dibuixaran les textures
    private int fxValue = 1;                // Parametre de quantitat d'efecte
    private int limit = 630;                // Longitud maxima tant de alt com d'ample (en pixels)

    public void setFxValue(int parametre) {
        this.fxValue = parametre;
    }

    // Assigna un Bitmap a la Surface
    public void setTexture(Bitmap texture) {
        photo = texture;
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

    // Constructor heredat
    public EffectsRenderer() {
        super();
    }

    // Es crida quan el Surface es crea o es re-crea
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i("kike", "onSurfaceCreated");
    }

    // Es crida una vegada quan es crea el Surface i cada cop que es canvia el tamany del Surface
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i("kike", "onSurfaceChanged");
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0, 0, 0, 1);
        generateSquare();
    }

    // Es crida quan es dibuixa el 'frame' actual
    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i("kike", "onDrawFrame");
        if (effectContext == null) {
            effectContext = EffectContext.createWithCurrentGlContext();
        }
        if (effect != null) {
            effect.release();
        }
        ////////////////////////////Efecte//////////////////////////
        //noEffect();
        //autofixEffect(fxValue);       // ok
        //backdropperEffect();          // no probat
        //bitmapoverlayEffect();        // no probat
        //blackwhiteEffect(100, 150);   // ok, ... :(
        brightnessEffect(fxValue);    // ok
        //contrastEffect(fxValue);      // ok
        //cropEffect();                 // no probat
        //crossprocessEffect();         // ok
        //documentaryEffect();          // ok
        //duotoneEffect();              // no probat
        //filllightEffect(fxValue);     // ok
        //fisheyeEffect(fxValue);       // ok
        //flipEffect(false, false);     // ok
        //grainEffect(fxValue);         // l'efecte que fa no es massa bo
        //grayScaleEffect();            // ok
        //lomoishEffect();              // ok
        //negativeEffect();             // ok
        //posterizeEffect();            // ok
        //redeyeEffect();               // no probat
        //rotateEffect(fxValue);        // error
        //saturateEffect(fxValue);      // ok
        //sepiaEffect();                // ok
        //sharpenEffect(fxValue);       // l'efecte que fa no es massa bo
        //straightenEffect(fxValue);    // ok, retalla la imatge tambe
        //temperatureEffect(fxValue);   // ok
        //tintEffect();                 // no probat
        //vignetteEffect(fxValue);      // nomes fa forma rodona i negra??
        ////////////////////////////Efecte//////////////////////////
        square.draw(textures[1]);
    }

    private void generateSquare() {
        GLES20.glGenTextures(2, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        if (photo != null) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, photo, 0);
        }
        square = new Square();
    }

    private void noEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
        effect.setParameter("brightness", 1f);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void autofixEffect(int barra) {
        // float (al manual posa de 0 a 1, pero funciona fora del rang)
        float val = barra / -20f + 5f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_AUTOFIX);
        effect.setParameter("scale", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void backdropperEffect(Uri uri) {
        // Uri.toString()
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_BACKDROPPER);
        effect.setParameter("source", uri.toString());
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void bitmapoverlayEffect(Bitmap b) {
        // Bitmap no null
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_BITMAPOVERLAY);
        effect.setParameter("bitmap", b);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void blackwhiteEffect(float min, float max) {
        // float 0 a 1
        float val_min = min / 200f;
        float val_max = max / 200f;
        Log.i("kike", "valor min : " + val_min + " valor max : " + val_max + " barra: " + min + " " + max);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_BLACKWHITE);
        effect.setParameter("black", val_min);
        effect.setParameter("white", val_max);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void brightnessEffect(int barra) {
        // float positiu, 1 = no fa res, 0 fosc, com mes alt mes brillant
        float val = barra / 20f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
        effect.setParameter("brightness", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void contrastEffect(int barra) {
        // float, 1 = no fa res, com mes alt mes contrast
        float val = barra / 40f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_CONTRAST);
        effect.setParameter("contrast", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void cropEffect(int x, int y, int width, int height) {
        // int entre 0 i width
        // int entre 0 i height
        // int que estigui dins la imatge
        // int que estigui dins la imatge
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_CROP);
        effect.setParameter("xorigin", x);
        effect.setParameter("yorigin", y);
        effect.setParameter("width", width);
        effect.setParameter("height", height);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void crossprocessEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_CROSSPROCESS);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void documentaryEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void duotoneEffect(int firstC, int secC) {
        // int ARGB 8 bits Color.algo() ...
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_DUOTONE);
        effect.setParameter("first_color", firstC);
        effect.setParameter("second_color", secC);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void filllightEffect(int barra) {
        // float (al manual posa de 0 a 1, pero funciona fora del rang)
        float val = barra / 75f - 1.3333333f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_FILLLIGHT);
        effect.setParameter("strength", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void fisheyeEffect(int barra) {
        // float (al manual posa de 0 a 1, pero funciona fora del rang)
        float val = barra / 127f - 0.08f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_FISHEYE);
        effect.setParameter("scale", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void flipEffect(boolean vertical, boolean horizontal) {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_FLIP);
        effect.setParameter("vertical", vertical);
        effect.setParameter("horizontal", horizontal);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void grainEffect(int barra) {
        // float (al manual posa de 0 a 1, pero funciona fora del rang)
        float val = barra / 200f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_GRAIN);
        effect.setParameter("strength", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void grayScaleEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void lomoishEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_LOMOISH);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void negativeEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_NEGATIVE);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void posterizeEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_POSTERIZE);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void redeyeEffect(float[] f) {
        // array de floats on ( f[2*i], f[2*i+1]) es el centre del 'i' ull
        // les coordenades han d'estar normalitzades entre 0 i 1
        float val = 0;
        Log.i("kike", "valor efecte: " + val + " barra: ");
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_REDEYE);
        effect.setParameter("centers", f);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void rotateEffect(int barra) {
        // s'arrodoniral al multiple de 90 mes proxim
        int val = barra;
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_ROTATE);
        effect.setParameter("angle", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void saturateEffect(int barra) {
        // float entre -1 i 1
        float val = barra / 100f - 1f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_SATURATE);
        effect.setParameter("scale", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void sepiaEffect() {
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_SEPIA);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void sharpenEffect(int barra) {
        // float (al manual posa de 0 a 1, pero funciona fora del rang)
        float val = barra / 100f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_SHARPEN);
        effect.setParameter("scale", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void straightenEffect(int barra) {
        // float (al manual posa de -45 a 45, pero funciona fora del rang)
        float val = barra * 1.8f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_STRAIGHTEN);
        effect.setParameter("angle", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void temperatureEffect(int barra) {
        // float entre 0 i 1 (no s'ha probat pero suposo que funciona fora dels rangs tambe)
        float val = barra / 200f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
        effect.setParameter("scale", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void tintEffect(int color) {
        // int ARGB 8 bits Color.algo() ...
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_TINT);
        effect.setParameter("tint", color);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

    private void vignetteEffect(int barra) {
        // float entre 0 i 1
        float val = barra / 200f;
        Log.i("kike", "valor efecte: " + val + " barra: " + barra);
        EffectFactory factory = effectContext.getFactory();
        effect = factory.createEffect(EffectFactory.EFFECT_VIGNETTE);
        effect.setParameter("scale", val);
        effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }

}
