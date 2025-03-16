package com.example.beatfit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * אקטיביטי זה מאפשר למשתמש להגדיר את פרטי האימון לפני תחילתו.
 * המשתמש מזין את משך האימון ורמת העצימות, ונתונים אלו מועברים למסך האימון בפועל.
 */
public class WorkoutSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { // פונקציה המופעלת כאשר האקטיביטי נטען
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_setup); // קביעת ממשק המשתמש מהקובץ XML

        // רכיב EditText להזנת משך האימון (בשניות/דקות)
        EditText durationEditText = findViewById(R.id.duration_edit_text);

        // רכיב EditText להזנת רמת העצימות של האימון
        EditText intensityEditText = findViewById(R.id.intensity_edit_text);

        // כפתור "Start Workout" להתחלת האימון
        Button startWorkoutButton = findViewById(R.id.start_workout_button);

        // כאשר המשתמש לוחץ על כפתור "Start Workout"
        startWorkoutButton.setOnClickListener(v -> {
            // קריאה של הערכים שהמשתמש הכניס למשך האימון ולעצימות
            String duration = durationEditText.getText().toString().trim();
            String intensity = intensityEditText.getText().toString().trim();

            // יצירת Intent להעברת הנתונים למסך האימון (WorkoutActivity)
            Intent intent = new Intent(WorkoutSetupActivity.this, WorkoutActivity.class);

            // הוספת הערכים שהמשתמש הכניס כנתונים ל-Intent
            intent.putExtra("duration", duration);
            intent.putExtra("intensity", intensity);

            // מעבר למסך האימון בפועל
            startActivity(intent);
        });
    }
}
