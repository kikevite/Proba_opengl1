package kikevite.proba_opengl1;

import android.content.Intent;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mySurfaceView;
    private SeekBar bar;
    private EffectsRenderer efRend;
    private int max_barra = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        efRend = new EffectsRenderer();
        mySurfaceView = (GLSurfaceView) findViewById(R.id.surface);
        mySurfaceView.setEGLContextClientVersion(2);
        mySurfaceView.setRenderer(efRend);
        mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        // Obrim la galeria per triar la foto
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 0);

        // Quan fem click al OpenGL, s'obra la galeria per triar una imatge
        mySurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 0);
            }
        });

        bar = (SeekBar) findViewById(R.id.seekBar);
        bar.setMax(max_barra);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float temp = i / 20f;
                Log.i("kike", String.valueOf(temp));
                efRend.serParam(temp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            try {
                efRend.setTexture(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
