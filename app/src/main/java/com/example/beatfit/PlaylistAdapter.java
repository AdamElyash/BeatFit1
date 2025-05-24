package com.example.beatfit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private List<PlaylistItem> playlists; // רשימת הפלייליסטים שתוצג
    private Context context; // הקשר של האקטיביטי הנוכחי

    /**
     * בנאי (Constructor) של המתאם (Adapter).
     *
     * @param context   הקשר של המסך שבו מוצגת הרשימה
     * @param playlists רשימה המכילה את כל הפלייליסטים
     */
    public PlaylistAdapter(Context context, List<PlaylistItem> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    /**
     * פונקציה שיוצרת תצוגה חדשה עבור כל פריט ברשימה.
     */
    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        return new PlaylistViewHolder(view);
    }

    /**
     * פונקציה שמעדכנת את הנתונים של פריט מסוים ברשימה.
     */
    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistItem playlist = playlists.get(position); // שליפת הפלייליסט המתאים למיקום הנוכחי
        holder.playlistName.setText(playlist.getName()); // הצגת שם הפלייליסט ברכיב הטקסט

        // כאשר המשתמש לוחץ על פריט ברשימה
        holder.itemView.setOnClickListener(v -> {
            String url = playlist.getUrl(); // שליפת כתובת הפלייליסט
            if (url != null && !url.isEmpty()) { // בדיקה אם הכתובת תקינה
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); // יצירת Intent לפתיחת הקישור
                    context.startActivity(intent); // פתיחת הקישור באפליקציה חיצונית (ספוטיפיי)
                } catch (Exception e) {
                    Toast.makeText(context, "Error opening playlist", Toast.LENGTH_SHORT).show(); // הודעה במקרה של שגיאה
                }
            } else {
                Toast.makeText(context, "No URL available for this playlist", Toast.LENGTH_SHORT).show(); // הודעה אם אין קישור זמין
            }
        });
    }

    /**
     * פונקציה שמחזירה את מספר הפריטים ברשימה.
     */
    @Override
    public int getItemCount() {
        // מחזיר את מספר הפריטים ברשימה
        // זה מאפשר ל-RecyclerView לדעת כמה פריטים יש לו להציג.
        return playlists.size();
    }

    /**
     * מחלקה פנימית המייצגת את ה-ViewHolder של רשימת הפלייליסטים.
     * תפקידה להחזיק ולהגדיר את רכיבי הממשק לכל פריט ברשימה.
     */
    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName; // רכיב להצגת שם הפלייליסט

        /**
         * קונסטרקטור שמאתחל את ה-ViewHolder ומקשר אותו לרכיבי הממשק
         *
         * @param itemView - ה-View של הפריט הבודד בתוך הרשימה
         */
        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            // איתור רכיב ה-TextView מתוך ה-XML של פריט הרשימה
            //מחלקה בתוך  RecyclerView.Adapter, שתפקידה להחזיק את רכיבי ה-View של כל פריט ברשימה כדי למנוע קריאות חוזרות ל-findViewById(), וכך לשפר ביצועים ולחסוך במשאבי מערכת.
            playlistName = itemView.findViewById(R.id.playlistName);
        }
    }
}