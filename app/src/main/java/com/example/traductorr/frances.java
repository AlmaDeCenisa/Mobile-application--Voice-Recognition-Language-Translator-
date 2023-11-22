package com.example.traductorr;

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

public class frances extends AppCompatActivity {
    TextView txtTraduccion;
    TextToSpeech txtToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frances); // Asegúrate de usar el layout correcto

        txtTraduccion = findViewById(R.id.textoTraduccion);

        // Inicialización del TextToSpeech para reproducir la traducción en voz
        txtToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                   // txtToSpeech.setLanguage(new Locale("it", "IT"));
                   txtToSpeech.setLanguage(Locale.FRENCH);
                }
            }
        });
    }

    // Lanzador de la actividad de reconocimiento de voz
    ActivityResultLauncher<Intent> lanzadorIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent info = result.getData();
                        ArrayList<String> data = info.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        // Configuración del traductor y descarga del modelo si es necesario
                        TranslatorOptions options = new TranslatorOptions.Builder()
                                .setSourceLanguage(TranslateLanguage.SPANISH)
                                .setTargetLanguage(TranslateLanguage.FRENCH)
                                .build();
                        final Translator spanishPortugueseTranslator = Translation.getClient(options);

                        DownloadConditions conditions = new DownloadConditions.Builder()
                                .requireWifi()
                                .build();

                        spanishPortugueseTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Traducción y reproducción del resultado en voz
                                        spanishPortugueseTranslator.translate(data.get(0)).addOnSuccessListener(
                                                new OnSuccessListener<String>() {
                                                    @Override
                                                    public void onSuccess(String s) {
                                                        txtTraduccion.setText(s);
                                                        txtToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                                                    }
                                                }
                                        ).addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), "Error al traducir", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                        );
                                    }
                                }
                        ).addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error al descargar modelo", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }
            }
    );

    // Método para activar el reconocimiento de voz y traducción
    public void BtnHablar(View v) {
        Intent intentReconocimiento = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentReconocimiento.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentReconocimiento.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES"); // Cambia el idioma a español
        try {
            lanzadorIntent.launch(intentReconocimiento);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Error al iniciar el reconocimiento de voz", Toast.LENGTH_SHORT).show();
        }
    }
}
