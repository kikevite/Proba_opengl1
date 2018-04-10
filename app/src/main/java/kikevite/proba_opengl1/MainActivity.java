package kikevite.proba_opengl1;

import android.content.Intent;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

/*
IMPORTANT S'ha d'afegir al manifest que s'ha de tenir OpenGL instalat a Android!!
 */

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mySurfaceView;        // "Quadre" del layout on es dibuixa la imatge
    private SeekBar bar;                        // Barra de valor de l'efecte
    private EffectsRenderer efRend;             // Render de la imatge
    private int max_barra = 200;                // Valor maxim de la barra
    private Button btn;

    /*
    S'haurien d'implementar però de moment dona error
    @Override
    protected void onPause() {
        super.onPause();
        mySurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mySurfaceView.onResume();
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        efRend = new EffectsRenderer();
        mySurfaceView = (GLSurfaceView) findViewById(R.id.surface);
        mySurfaceView.setEGLContextClientVersion(2);
        mySurfaceView.setRenderer(efRend);
        mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Obrim la galeria per triar la foto
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 0);

        // Quan fem click al SurfaceView, s'obra la galeria per triar una imatge
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
                // S'actualitza el valor de l'efecte i es refresca la imatge
                efRend.setFxValue(i);
                mySurfaceView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Per guardar el contingut d'un Surface en un arxiu
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String photoName = getPhotoName();
                Toast.makeText(MainActivity.this, photoName, Toast.LENGTH_SHORT).show();
                //saveImage(photoName);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            try {
                //efRend.setLimit(Math.min(mySurfaceView.getWidth(), mySurfaceView.getHeight()));
                efRend.setTexture(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            bar.setProgress(max_barra / 10);
        } else {
            Toast.makeText(this, "No s'ha escollit cap imatge nova!", Toast.LENGTH_SHORT).show();
        }

    }

    // Genera un nom per una imatge EX: '2017.12.15_20.31.46.jpg'
    private String getPhotoName() {
        Calendar calendar = Calendar.getInstance();
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(calendar.get(Calendar.MINUTE));
        String second = Integer.toString(calendar.get(Calendar.SECOND));

        day = (day.length() == 1) ? "0" + day : day;
        month = (month.length() == 1) ? "0" + month : month;
        hour = (hour.length() == 1) ? "0" + hour : hour;
        minute = (minute.length() == 1) ? "0" + minute : minute;
        second = (second.length() == 1) ? "0" + second : second;

        return year + "." + month + "." + day + "_" + hour + "." + minute + "." + second + ".jpg";
    }


    /*
    // Guardar una foto en un arxiu JPG
    private boolean saveImage(String name) {
        // ima_final = Imageview
        int final_foto = efRend.getFXphoto();
        Log.i("kike", String.valueOf(final_foto));
        String image_dir = "/OpenGL_Proba/";
        boolean guardat_ok = true;
        BitmapDrawable draw = (BitmapDrawable) ima_final.getDrawable();
        Bitmap bitmap = draw.getBitmap();
        FileOutputStream outStream;
        File dir = new File(Environment.getExternalStorageDirectory(), image_dir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File outFile = new File(dir, name);
        try {
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            // Per actualitzar les fotos a la galeria
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outFile));
            sendBroadcast(intent);
        } catch (FileNotFoundException e) {
            guardat_ok = false;
            e.printStackTrace();
        } catch (IOException e) {
            guardat_ok = false;
            e.printStackTrace();
        }
        return guardat_ok;
    }
    */
}
