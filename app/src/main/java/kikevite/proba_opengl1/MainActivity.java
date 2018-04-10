package kikevite.proba_opengl1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;

/*
IMPORTANT
S'ha d'afegir al manifest que s'ha de tenir OpenGL instalat a Android!!
Per la part de guardar el fitxer, s'ha d'afegir al manifest demanar permis
 */

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mySurfaceView;        // "Quadre" del layout on es dibuixa la imatge
    private SeekBar bar;                        // Barra de valor de l'efecte
    private EffectsRenderer efRend;             // Render de la imatge
    private int max_barra = 200;                // Valor maxim de la barra
    private Button btn;                         // boto de guardar

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

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

        // Es demana el permis per escriure fitxers
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }

        efRend = new EffectsRenderer();
        mySurfaceView = (GLSurfaceView) findViewById(R.id.surface);
        mySurfaceView.setEGLContextClientVersion(2);
        mySurfaceView.setRenderer(efRend);
        mySurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

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
                if (saveImage(photoName)) {
                    Log.i("kike", "Imatge guardada");
                } else {
                    Log.i("kike", "Imatge no guardada");
                }
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

    // Guardar una foto en un arxiu JPG
    private boolean saveImage(String name) {

        //???????????????????????????
        //GLES20.glCopyTexImage2D(GL_TEXTURE_2D, 10, GL_RGBA8, 0, 0, 200, 200, 0);
        //GLES20.glGetTexImage(GL_TEXTURE_2D, 10, GL_RGBA, GL_SIGNED_INT_8_8_8_8, array);




        int w = 100;
        int h = w;
        ByteBuffer buffer = ByteBuffer.allocateDirect(w * h * 4);
        //buffer.order(ByteOrder.LITTLE_ENDIAN);
        GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);



        //Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //bitmap.copyPixelsFromBuffer(buffer);


        Drawable d = mySurfaceView.getForeground();

        String image_dir = "/aaaa_OpenGLTest/";
        boolean guardat_ok = true;

        /**/
        // ima_final es un Imageview
        BitmapDrawable draw = (BitmapDrawable) d;
        Bitmap bitmap = draw.getBitmap();
        /**/

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
}
