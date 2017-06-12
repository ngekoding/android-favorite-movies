package com.example.nur.finalproject.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nur.finalproject.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nur on 5/29/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "MoviesManager";

    protected static final String TABLE_FAVORITE = "favorites";
    protected static final String KEY_ID = "id";
    protected static final String KEY_IMAGE_PATH = "image_path";
    protected static final String KEY_TITLE = "title";
    protected static final String KEY_DESCRIPTION = "description";
    protected static final String KEY_RELEASE_DATE = "release_date";
    protected static final String KEY_RATING = "rating";

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITE_TABLE = "CREATE TABLE " + TABLE_FAVORITE + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_IMAGE_PATH + " VARCHAR(255),"
                + KEY_TITLE + " VARCHAR(255),"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_RELEASE_DATE + " VARCHAR(10),"
                + KEY_RATING + " REAL" + ")";
        db.execSQL(CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        onCreate(db);
    }

    /*
    * -----------------------------------------
    * CREATE, READ, UPDATE & DELETE
    * -----------------------------------------
    * */

    public void addMovie(Movie movie) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, movie.getId());
        values.put(KEY_IMAGE_PATH, movie.getImagePath());
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_DESCRIPTION, movie.getDescription());
        values.put(KEY_RELEASE_DATE, movie.getReleaseDate());
        values.put(KEY_RATING, movie.getRating());

        // Inserting row
        db.insert(TABLE_FAVORITE, null, values);
        db.close();
    }

    public List<Movie> getAllMovies() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Movie> movieList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_FAVORITE;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(cursor.getString(0));
                movie.setImagePath(cursor.getString(1));
                movie.setTitle(cursor.getString(2));
                movie.setDescription(cursor.getString(3));
                movie.setReleaseDate(cursor.getString(4));
                movie.setRating(Double.parseDouble(cursor.getString(5)));
                // Add data
                movieList.add(movie);
            } while (cursor.moveToNext());
        }
        return movieList;
    }

    public void deleteMovie(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        db.delete(TABLE_FAVORITE, KEY_ID + "=?", new String[]{id});
        db.close();
    }

    public boolean alreadyExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String Query = "SELECT * FROM " + TABLE_FAVORITE + " WHERE " + KEY_ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
