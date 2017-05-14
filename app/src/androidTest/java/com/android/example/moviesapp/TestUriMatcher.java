package com.android.example.moviesapp;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import com.android.example.moviesapp.data.MovieContract;
import com.android.example.moviesapp.data.MovieProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created by ememobong on 08/05/2017.
 */
@RunWith(AndroidJUnit4.class)
public class TestUriMatcher {

    private static final Uri TEST_MOVIE_DIR = MovieContract.FavoritesMovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_ID_DIR = MovieContract.FavoritesMovieEntry
            .buildMovieUriWithID(234);

    private static final String movieIDVariableName = "CODE_MOVIE_ID";
    private static int REFLECTED_CODE_MOVIE_ID;

    private UriMatcher testMatcher;

    @Before
    public void before(){

        try {
            Method buildUriMatcher = MovieProvider.class.getDeclaredMethod("buildUriMatcher");
            testMatcher = (UriMatcher) buildUriMatcher.invoke(MovieProvider.class);

            REFLECTED_CODE_MOVIE_ID = getStaticIntegerField(
                    MovieProvider.class,
                    movieIDVariableName);


        } catch (NoSuchFieldException e) {
            fail(studentReadableNoSuchField(e));
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        } catch (NoSuchMethodException e) {
            String noBuildUriMatcherMethodFound =
                    "It doesn't appear that you have created a method called buildUriMatcher in " +
                            "the WeatherProvider class.";
            fail(noBuildUriMatcherMethodFound);
        } catch (InvocationTargetException e) {
            fail(e.getMessage());
        }
    }


    static String studentReadableNoSuchField(NoSuchFieldException e) {
        String message = e.getMessage();

        Pattern p = Pattern.compile("No field (\\w*) in class L.*/(\\w*\\$?\\w*);");

        Matcher m = p.matcher(message);

        if (m.find()) {
            String missingFieldName = m.group(1);
            String classForField = m.group(2).replaceAll("\\$", ".");
            String fieldNotFoundReadableMessage = "Couldn't find "
                    + missingFieldName + " in class " + classForField + "."
                    + "\nPlease make sure you've declared that field and followed the TODOs.";
            return fieldNotFoundReadableMessage;
        } else {
            return e.getMessage();
        }
    }

    static Integer getStaticIntegerField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field intField = clazz.getDeclaredField(variableName);
        intField.setAccessible(true);
        Integer value = (Integer) intField.get(null);
        return value;
    }

    @Test
    public void testUriMatcher() {

        /* Test that the code returned from our matcher matches the expected weather code */
        String movieUriDoesNotMatch = "Error: The CODE_WEATHER URI was matched incorrectly.";
        int actualWeatherCode = testMatcher.match(TEST_MOVIE_DIR);
        int expectedWeatherCode = REFLECTED_CODE_MOVIE_ID;
        assertEquals(movieUriDoesNotMatch,
                expectedWeatherCode,
                actualWeatherCode);
    }
}
