package com.example.traductorr;

// Importaciones de bibliotecas y clases necesarias
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // Declaración de variables miembro
    TextView txtTraduccion;
    TextToSpeech txtToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de los elementos de la interfaz
        txtTraduccion = findViewById(R.id.textoTraduccion);

        // Inicialización del objeto TextToSpeech para lectura de voz
        txtToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                     //txtToSpeech.setLanguage(new Locale("es", "ES"));
                    txtToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    // Definición del lanzador para el reconocimiento de voz
    ActivityResultLauncher<Intent> lanzadorIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent info = result.getData();
                        // Se obtienen los datos del reconocimiento de voz
                        ArrayList<String> data = info.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        // Creación de opciones del traductor para español a inglés
                        TranslatorOptions options = new TranslatorOptions.Builder()
                                //del idioma que queremos convertir
                                .setSourceLanguage(TranslateLanguage.SPANISH)
                                //al idioma que vamos a convertir
                                .setTargetLanguage(TranslateLanguage.ENGLISH)
                                .build();
                        // Creación del objeto Translator
                        final Translator spanishEnglishTranslator = Translation.getClient(options);

                        // Condiciones para la descarga del modelo
                        DownloadConditions conditions = new DownloadConditions.Builder()
                                .requireWifi()
                                .build();

                        // Descarga del modelo si es necesario
                        spanishEnglishTranslator.downloadModelIfNeeded(conditions)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Traducción del texto reconocido
                                        spanishEnglishTranslator.translate(data.get(0))
                                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        // Actualización del TextView con la traducción
                                                        txtTraduccion.setText(s);
                                                        // Reproducción del texto traducido en voz
                                                        txtToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Manejo de error en caso de falla en la traducción
                                                        Toast.makeText(getApplicationContext(), "error al traducir", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Manejo de error en caso de falla en la descarga del modelo
                                        Toast.makeText(getApplicationContext(), "error al descargar modelo", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
    );

    // Método para manejar el clic en el botón de reconocimiento de voz
    public void BtnHablar(View v) {
        // Creación de un intent para el reconocimiento de voz
        Intent intentReconocimiento = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Especificación del modelo de lenguaje en español
        intentReconocimiento.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        try {
            // Lanzamiento del intent para iniciar el reconocimiento de voz
            lanzadorIntent.launch(intentReconocimiento);
        } catch (ActivityNotFoundException e) {
            // Manejo de error en caso de que el reconocimiento de voz no esté disponible
            Toast.makeText(getApplicationContext(), "error al iniciar el reconocimiento de voz", Toast.LENGTH_SHORT).show();
        }
    }
}
