package com.example.absenceprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AbsenceProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.absencemanager.provider";
    private static final String PATH_ABSENCES = "absences";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_ABSENCES);

    private static final int ABSENCES = 1;
    private static final int ABSENCE_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_ABSENCES, ABSENCES);
        uriMatcher.addURI(AUTHORITY, PATH_ABSENCES + "/#", ABSENCE_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return database != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case ABSENCES:
                cursor = database.query("absences", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ABSENCE_ID:
                cursor = database.query("absences", projection, "id = ?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI inconnue: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) != ABSENCES) {
            throw new IllegalArgumentException("URI non supportée pour l'insertion: " + uri);
        }

        long rowId = database.insert("absences", null, values);
        if (rowId > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Échec de l'insertion dans " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated;
        switch (uriMatcher.match(uri)) {
            case ABSENCES:
                rowsUpdated = database.update("absences", values, selection, selectionArgs);
                break;
            case ABSENCE_ID:
                rowsUpdated = database.update("absences", values, "id = ?", new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new IllegalArgumentException("URI inconnue: " + uri);
        }
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ABSENCES:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".absences";
            case ABSENCE_ID:
                return "vnd.android.cursor.item/vnd." + AUTHORITY + ".absences";
            default:
                throw new IllegalArgumentException("URI inconnue: " + uri);
        }
    }
}
