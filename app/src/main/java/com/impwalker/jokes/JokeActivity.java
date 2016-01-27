package com.impwalker.jokes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JokeActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_joke);
    RelativeLayout layout = (RelativeLayout) findViewById(R.id.joke_content);

    Intent intent = getIntent();
    String joke = intent.getStringExtra(MainActivity.EXTRA_JOKE);
    TextView textView = new TextView(this);

    textView.setTextSize(40);
    textView.setText(joke);

    layout.addView(textView);
  }

}
