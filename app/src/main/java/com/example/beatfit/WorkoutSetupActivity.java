package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class WorkoutSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_setup);

        // אתחול EditText לקבלת משך האימון מהמשתמש
        EditText durationEditText = findViewById(R.id.duration_edit_text);

        // אתחול EditText לקבלת רמת העצימות מהמשתמש
        EditText intensityEditText = findViewById(R.id.intensity_edit_text);

        // אתחול כפתור התחלת האימון
        Button startWorkoutButton = findViewById(R.id.start_workout_button);

        // הגדרת פעולה שתתרחש בעת לחיצה על כפתור "Start Workout"
        startWorkoutButton.setOnClickListener(v -> {
            // קריאה של הערכים שהמשתמש הכניס למשך האימון ועצימות האימון
            String duration = durationEditText.getText().toString();
            String intensity = intensityEditText.getText().toString();

            // יצירת Intent להעברת הנתונים למסך הבא (WorkoutActivity)
            Intent intent = new Intent(WorkoutSetupActivity.this, WorkoutActivity.class);

            // הוספת הערכים שהמשתמש הכניס כנתונים ל-Intent
            intent.putExtra("duration", duration);
            intent.putExtra("intensity", intensity);

            // מעבר למסך האימון (WorkoutActivity)
            startActivity(intent);
        });
    }
}