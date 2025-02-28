package com.example.absenceprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText studentName, absenceDate, status, subject; // Ajout du champ Matière
    private Button addAbsence, viewAbsences, clearAbsences;
    private static final Uri CONTENT_URI = Uri.parse("content://com.example.absencemanager.provider/absences");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentName = findViewById(R.id.studentName);
        absenceDate = findViewById(R.id.absenceDate);
        status = findViewById(R.id.status);
        subject = findViewById(R.id.subject); // Ajout de l'input pour la matière

        addAbsence = findViewById(R.id.addAbsence);
        viewAbsences = findViewById(R.id.viewAbsences);
        clearAbsences = findViewById(R.id.clearAbsences);

        addAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAbsence();
            }
        });

        viewAbsences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAbsences();
            }
        });

        clearAbsences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAbsences();
            }
        });
    }

    private void clearAbsences() {
        int deletedRows = getContentResolver().delete(CONTENT_URI, null, null);
        if (deletedRows > 0) {
            Toast.makeText(this, "Liste vidée avec succès!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Aucune absence à supprimer", Toast.LENGTH_SHORT).show();
        }
    }

    private void addAbsence() {
        String name = studentName.getText().toString().trim();
        String date = absenceDate.getText().toString().trim();
        String statusText = status.getText().toString().trim();
        String subjectText = subject.getText().toString().trim(); // Récupération de la matière

        if (name.isEmpty() || date.isEmpty() || statusText.isEmpty() || subjectText.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("student_name", name);
        values.put("date", date);
        values.put("status", statusText);
        values.put("subject", subjectText); // Ajout de la matière

        Uri resultUri = getContentResolver().insert(CONTENT_URI, values);

        if (resultUri != null) {
            Toast.makeText(this, "Absence ajoutée avec succès!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewAbsences() {
        Cursor cursor = getContentResolver().query(CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            StringBuilder result = new StringBuilder();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("student_name"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject")); // Récupération de la matière

                result.append("ID: ").append(id).append("\n")
                        .append("Nom: ").append(name).append("\n")
                        .append("Date: ").append(date).append("\n")
                        .append("Statut: ").append(status).append("\n")
                        .append("Matière: ").append(subject).append("\n\n"); // Affichage de la matière
            }
            cursor.close();
            Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Aucune absence trouvée", Toast.LENGTH_SHORT).show();
        }
    }
}
