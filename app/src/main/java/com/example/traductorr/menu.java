package com.example.traductorr;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.net.Uri;
import android.widget.ImageView;

public class menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.jm);

        findViewById(R.id.ingles).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, MainActivity.class); // Envia al traductor a INGLES
                startActivity(intent);
            }
        });

        findViewById(R.id.frances).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, frances.class); // Envia al traductor a FRANCES
                startActivity(intent);
            }
        });

/*
        findViewById(R.id.inglesEsp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, spansih.class); // Envia al traductor a INGLES-ESP
                startActivity(intent);
            }
        });*/



        TextView textViewWebsite = findViewById(R.id.textView3);
        textViewWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // URL del sitio web
                String websiteUrl = "https://www.intjem.edu.bo";

                // Crear un Intent para abrir el navegador web con la URL
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
                startActivity(intent);
            }
        });
    }

}
