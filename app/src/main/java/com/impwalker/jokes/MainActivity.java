package com.impwalker.jokes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
  public final static String EXTRA_JOKE="com.impwalker.jokes.JOKE";
  private String joke_string;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public class FetchJokesTask extends AsyncTask<Void, Void, String>{

    @Override
    protected String doInBackground(Void... params) {
      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;
      String jokeJsonStr = null;


      try {
        String random_joke_url = "http://api.icndb.com/jokes/random";

        URL url = new URL(random_joke_url);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();

        if (inputStream == null) {
          // Nothing to do.
          return null;
        }

        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
          // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
          // But it does make debugging a *lot* easier if you print out the completed
          // buffer for debugging.
          buffer.append(line).append("\n");
        }

        if (buffer.length() == 0) {
          // Stream was empty.  No point in parsing.
          return null;
        }
        jokeJsonStr = buffer.toString();

      } catch (IOException e) {
        Log.e("JOKE REQUEST","Bad request URL",e);
      }finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (final IOException e) {
            Log.e("JOKE REQUEST", "Error closing stream", e);
          }
        }
      }

      try {
        return parseJoke(jokeJsonStr);

      }catch (JSONException e){
        Log.e("JSON", e.getMessage());
      }

      return null;
    }

    @Override
    protected void onPostExecute(String joke){
      super.onPostExecute(joke);
      if (joke != null){
        joke_string = joke;
      }
    }
  }

  private String parseJoke(String jsonStr) throws JSONException {
    final String VALUE = "value";
    final String JOKE= "joke";

    JSONObject reader = new JSONObject((jsonStr));
    JSONObject joke_object = reader.getJSONObject(VALUE);
    return joke_object.getString(JOKE);
  }

  public void getJoke(View view){
    FetchJokesTask joke_task = new FetchJokesTask();
    joke_task.execute();
    Intent intent = new Intent(this, JokeActivity.class);
    intent.putExtra(EXTRA_JOKE, joke_string);
    startActivity(intent);
  }
}
