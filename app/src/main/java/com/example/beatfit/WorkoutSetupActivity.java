package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * WorkoutSetupActivity
 * מסך זה מאפשר למשתמש להגדיר את משך ועוצמת האימון.
 */
public class WorkoutSetupActivity extends AppCompatActivity {

    private EditText durationEditText, intensityEditText;
    private Button startWorkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_setup);

        durationEditText = findViewById(R.id.duration_edit_text);
        intensityEditText = findViewById(R.id.intensity_edit_text);
        startWorkoutButton = findViewById(R.id.start_workout_button);

        startWorkoutButton.setOnClickListener(v -> {
            String duration = durationEditText.getText().toString();
            String intensity = intensityEditText.getText().toString();

            Intent intent = new Intent(WorkoutSetupActivity.this, WorkoutActivity.class);
            intent.putExtra("duration", duration);
            intent.putExtra("intensity", intensity);
            startActivity(intent);
        });
    }
}
