package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * מסך הגדרת אימון — מאפשר למשתמש לבחור כמה דקות יימשך האימון.
 */
public class WorkoutSetupActivity extends AppCompatActivity {

    private EditText minutesInput;
    private Button startWorkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_setup);

        minutesInput = findViewById(R.id.minutesInput);
        startWorkoutButton = findViewById(R.id.startWorkoutButton);

        startWorkoutButton.setOnClickListener(v -> {
            String minutesStr = minutesInput.getText().toString();

            if (TextUtils.isEmpty(minutesStr)) {
                Toast.makeText(WorkoutSetupActivity.this, "Please enter workout time (minutes)", Toast.LENGTH_SHORT).show();
                return;
            }

            int minutes;
            try {
                minutes = Integer.parseInt(minutesStr);
            } catch (NumberFormatException e) {
                Toast.makeText(WorkoutSetupActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (minutes <= 0) {
                Toast.makeText(WorkoutSetupActivity.this, "Workout time must be greater than zero", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(WorkoutSetupActivity.this, WorkoutActivity.class);
            intent.putExtra("workoutMinutes", minutes);
            startActivity(intent);
        });
    }
}
