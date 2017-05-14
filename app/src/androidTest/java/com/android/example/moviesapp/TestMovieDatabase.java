package com.android.example.moviesapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.example.moviesapp.data.MovieContract.FavoritesMovieEntry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

//import com.android.example.moviesapp.data.MovieContract.FavoritesMovieEntry.COLUMN_MOVIE_ID;

/**
 * Created by ememobong on 08/05/2017.
 */
/**
 * Used to test the database we use in Sunshine to cache weather data. Within these tests, we
 * test the following:
 * <p>
 * <p>
 * 1) Creation of the database with proper table(s)
 * 2) Insertion of single record into our weather table
 * 3) When a record is already stored in the weather table with a particular date, a new record
 * with the same date will overwrite that record.
 * 4) Verify that NON NULL constraints are working properly on record inserts
 * 5) Verify auto increment is working with the ID
 * 6) Test the onUpgrade functionality of the WeatherDbHelper
 */
@RunWith(AndroidJUnit4.class)
public class TestMovieDatabase {
    /*
        * Context used to perform operations on the database and create WeatherDbHelpers.
        */
    private final Context context = InstrumentationRegistry.getTargetContext();

    private static final String packageName = "com.android.example.moviesapp";
    private static final String dataPackageName = packageName + ".data";

    private Class favoritesEntryClass;
    private Class movieDBHelperClass;
    private static final String movieContractName = ".MovieContract";
    private static final String favoritesEntryName = movieContractName + "$FavoritesMovieEntry";
    private static final String movieDBHelperName = ".MovieDBHelper";

    private static final String databaseNameVariableName = "DATABASE_NAME";
    private static String REFLECTED_DATABASE_NAME;

    private static final String databaseVersionVariableName = "DATABASE_VERSION";
    private static int REFLECTED_DATABASE_VERSION;

    private static final String tableNameVariableName = "TABLE_NAME";
    private static String REFLECTED_TABLE_NAME;

    private static final String columnMovieIdVariableName = "COLUMN_MOVIE_ID";
    static String REFLECTED_COLUMN_MOVIE_ID;

    private static final String columnMovieTitleVariableName = "COLUMN_MOVIE_TITLE";
    static String REFLECTED_COLUMN_MOVIE_TITLE;

    private static final String columnMoviePosterVariableName = "COLUMN_MOVIE_POSTER";
    static String REFLECTED_COLUMN_MOVIE_POSTER;

    private static final String columnMovieSynopsisVariableName = "COLUMN_MOVIE_SYNOPSIS";
    static String REFLECTED_COLUMN_MOVIE_SYNOPSIS;

    private static final String columnUserRatingVariableName = "COLUMN_USER_RATING";
    static String REFLECTED_COLUMN_USER_RATING;

    private static final String columnReleaseYearVariableName = "COLUMN_RELEASE_YEAR";
    static String REFLECTED_COLUMN_RELEASE_YEAR;

    private SQLiteDatabase database;
    private SQLiteOpenHelper dbHelper;

//i think this is the portion of the code that gets executed before the test cases are tried
@Before
public void before(){

    try {
        //    check if the favorites table implements BaseColumns

        favoritesEntryClass = Class.forName(dataPackageName + favoritesEntryName);
        if (!BaseColumns.class.isAssignableFrom(favoritesEntryClass)) {
            String weatherEntryDoesNotImplementBaseColumns = "WeatherEntry class needs to " +
                    "implement the interface BaseColumns, but does not.";
            fail(weatherEntryDoesNotImplementBaseColumns);
        }

        REFLECTED_TABLE_NAME = getStaticStringField(favoritesEntryClass, tableNameVariableName);
        REFLECTED_COLUMN_MOVIE_ID = getStaticStringField(favoritesEntryClass, columnMovieIdVariableName);
        REFLECTED_COLUMN_MOVIE_TITLE = getStaticStringField(favoritesEntryClass, columnMovieTitleVariableName);
        REFLECTED_COLUMN_MOVIE_POSTER = getStaticStringField(favoritesEntryClass, columnMoviePosterVariableName);
        REFLECTED_COLUMN_MOVIE_SYNOPSIS = getStaticStringField(favoritesEntryClass, columnMovieSynopsisVariableName);
        REFLECTED_COLUMN_USER_RATING = getStaticStringField(favoritesEntryClass, columnUserRatingVariableName);
        REFLECTED_COLUMN_RELEASE_YEAR = getStaticStringField(favoritesEntryClass, columnReleaseYearVariableName);

        movieDBHelperClass = Class.forName(dataPackageName + movieDBHelperName);

        Class movieDBHelperClassSuperclass = movieDBHelperClass.getSuperclass();

//        test to check that movieDBHelper extends SQLiteDBHelper
        if (movieDBHelperClassSuperclass == null || movieDBHelperClassSuperclass.equals(Object.class)) {
            String noExplicitSuperclass =
                    "MovieDBHelper needs to extend SQLiteOpenHelper, but yours currently doesn't extend a class at all.";
            fail(noExplicitSuperclass);
        } else if (movieDBHelperClassSuperclass != null) {
            String movieHelperSuperclassName = movieDBHelperClassSuperclass.getSimpleName();
            String doesNotExtendOpenHelper =
                    "WeatherDbHelper needs to extend SQLiteOpenHelper but yours extends "
                            + movieHelperSuperclassName;

            assertTrue(doesNotExtendOpenHelper,
                    SQLiteOpenHelper.class.isAssignableFrom(movieDBHelperClassSuperclass));
        }

        REFLECTED_DATABASE_NAME = getStaticStringField(
                movieDBHelperClass, databaseNameVariableName);

        REFLECTED_DATABASE_VERSION = getStaticIntegerField(
                movieDBHelperClass, databaseVersionVariableName);

        Constructor movieDBHelperCreator = movieDBHelperClass.getConstructor(Context.class);

        dbHelper = (SQLiteOpenHelper) movieDBHelperCreator.newInstance(context);

        context.deleteDatabase(REFLECTED_DATABASE_NAME);

        Method getWritableDatabase = SQLiteOpenHelper.class.getDeclaredMethod("getWritableDatabase");
        database = (SQLiteDatabase) getWritableDatabase.invoke(dbHelper);


    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
    catch (NoSuchFieldException e){
        e.printStackTrace();
    }
    catch (IllegalAccessException e){
        e.printStackTrace();
    }
    catch (NoSuchMethodException e){
        e.printStackTrace();
    }
    catch (InstantiationException e){
        e.printStackTrace();
    }
    catch (InvocationTargetException e){
        e.printStackTrace();
    }

    }

    @Test
    public void testDatabaseVersionWasIncremented() {
        int expectedDatabaseVersion = 1;
        String databaseVersionShouldBe1 = "Database version should be "
                + expectedDatabaseVersion + " but isn't."
                + "\n Database version: ";

        assertEquals(databaseVersionShouldBe1,
                expectedDatabaseVersion,
                REFLECTED_DATABASE_VERSION);
    }

    @Test
    public void testCreateDb() {

        final HashSet<String> tableNameHashSet = new HashSet<>();

        /* Here, we add the name of our only table in this particular database */
        tableNameHashSet.add(REFLECTED_TABLE_NAME);

        /* We think the database is open, let's verify that here */
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen,
                true,
                database.isOpen());

        /* This Cursor will contain the names of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'",
                null);

        /*
         * If tableNameCursor.moveToFirst returns false from this query, it means the database
         * wasn't created properly. In actuality, it means that your database contains no tables.
         */
        String errorInCreatingDatabase =
                "Error: This means that the database has not been created correctly";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        /*
         * tableNameCursor contains the name of each table in this database. Here, we loop over
         * each table that was ACTUALLY created in the database and remove it from the
         * tableNameHashSet to keep track of the fact that was added. At the end of this loop, we
         * should have removed every table name that we thought we should have in our database.
         * If the tableNameHashSet isn't empty after this loop, there was a table that wasn't
         * created properly.
         */
        do {
            tableNameHashSet.remove(tableNameCursor.getString(0));
        } while (tableNameCursor.moveToNext());

        /* If this fails, it means that your database doesn't contain the expected table(s) */
        assertTrue("Error: Your database was created without the expected tables.",
                tableNameHashSet.isEmpty());

        /* Always close the cursor when you are finished with it */
        tableNameCursor.close();
    }

    @Test
    public void testInsertSingleRecordIntoWeatherTable() {

        /* Obtain weather values from TestUtilities */
        ContentValues testFavoriteMoviesValues = createTestWeatherContentValues();

        /* Insert ContentValues into database and get a row ID back */
        long favoriteMovieRowID = database.insert(
                REFLECTED_TABLE_NAME,
                null,
                testFavoriteMoviesValues);

        /* If the insert fails, database.insert returns -1 */
        int valueOfIdIfInsertFails = -1;
        String insertFailed = "Unable to insert into the database";
        assertNotSame(insertFailed,
                valueOfIdIfInsertFails,
                favoriteMovieRowID);

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor favoritesCursor = database.query(
                /* Name of table on which to perform the query */
                REFLECTED_TABLE_NAME,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Columns to group by */
                null,
                /* Columns to filter by row groups */
                null,
                /* Sort order to return in Cursor */
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */
        String emptyQueryError = "Error: No Records returned from weather query";
        assertTrue(emptyQueryError,
                favoritesCursor.moveToFirst());

        /* Verify that the returned results match the expected results */
        String expectedWeatherDidntMatchActual =
                "Expected weather values didn't match actual values.";
        validateCurrentRecord(expectedWeatherDidntMatchActual,
                favoritesCursor,
                testFavoriteMoviesValues);

        /*
         * Since before every method annotated with the @Test annotation, the database is
         * deleted, we can assume in this method that there should only be one record in our
         * Weather table because we inserted it. If there is more than one record, an issue has
         * occurred.
         */
        assertFalse("Error: More than one record returned from weather query",
                favoritesCursor.moveToNext());

        /* Close cursor */
        favoritesCursor.close();
    }


    static ContentValues createTestWeatherContentValues() {

        ContentValues favoriteMoviesValues = new ContentValues();

        favoriteMoviesValues.put(FavoritesMovieEntry.COLUMN_MOVIE_ID, 234);
        favoriteMoviesValues.put(FavoritesMovieEntry.COLUMN_MOVIE_POSTER, "http://api.themoviedb_movie_poster");
        favoriteMoviesValues.put(FavoritesMovieEntry.COLUMN_MOVIE_SYNOPSIS, "THis is a simple test of the synopsis");
        favoriteMoviesValues.put(FavoritesMovieEntry.COLUMN_MOVIE_TITLE, "God is faithful");
        favoriteMoviesValues.put(FavoritesMovieEntry.COLUMN_RELEASE_YEAR, 1894);
        favoriteMoviesValues.put(FavoritesMovieEntry.COLUMN_USER_RATING, "9.7");


        return favoriteMoviesValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);

            /* Test to see if the column is contained within the cursor */
            String columnNotFoundError = "Column '" + columnName + "' not found. " + error;
            assertFalse(columnNotFoundError, index == -1);

            /* Test to see if the expected value equals the actual value (from the Cursor) */
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(index);

            String valuesDontMatchError = "Actual value '" + actualValue
                    + "' did not match the expected value '" + expectedValue + "'. "
                    + error;

            assertEquals(valuesDontMatchError,
                    expectedValue,
                    actualValue);
        }
    }

    static String getStaticStringField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field stringField = clazz.getDeclaredField(variableName);
        stringField.setAccessible(true);
        String value = (String) stringField.get(null);
        return value;
    }

    static Integer getStaticIntegerField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field intField = clazz.getDeclaredField(variableName);
        intField.setAccessible(true);
        Integer value = (Integer) intField.get(null);
        return value;
    }
}
