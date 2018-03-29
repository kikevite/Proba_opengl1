package kikevite.proba_opengl1;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GLSurfaceView view = new GLSurfaceView(this);
        setContentView(view);

        view.setEGLContextClientVersion(2);
        view.setRenderer(new EffectsRenderer(this));
        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}

