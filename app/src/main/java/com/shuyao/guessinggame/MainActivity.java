package com.shuyao.guessinggame;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText txtGuess;
    private Button btnGuess;
    private TextView lblOutput;
    private TextView lblRange;
    private int range = 100;
    private int num;
    private int numOfTries;
    private boolean won = false;

    public void checkGuess(){
        String guessText = txtGuess.getText().toString();
        String message = "";
        try {
            int guess = Integer.parseInt(guessText);
            numOfTries++;
            if (guess < num)
                message = guess + " is too low. Try again.";
            else if (guess > num)
                message = guess + " is too high. Try again.";
            else {
                message = guess + " is correct. You win!\nIt took you " + numOfTries + " tries.";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                won = true;
                btnGuess.setText("Play Again!");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("wins", preferences.getInt("wins", 0) + 1);
                editor.apply();
            }

        }
        catch (Exception e){
            message = "Please enter a whole number between 1 and " + range + ".";
        }

        lblOutput.setText(message);
        txtGuess.selectAll();
    }

    public void newGame(){
        num = (int) (Math.random() * (range-1+1)) + 1;
        won = false;
        btnGuess.setText("Guess!");
        numOfTries = 0;
        lblRange.setText("Enter a number between 1 and " + range + ":");
        lblOutput.setText("Enter your number above and click \"Guess!\"");
        txtGuess.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // links views on screen to code
        txtGuess = (EditText) findViewById(R.id.txtGuess);
        btnGuess = (Button) findViewById(R.id.btnGuess);
        lblOutput = (TextView) findViewById(R.id.lblOutput);
        lblRange = (TextView) findViewById(R.id.lblRange);

        // creates SharedPreferences object to acess stored preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // sets the range variable to the value stored when the app was closed the last time
        // the second parameter is the value used when the keyword range is not found
        range = preferences.getInt("range", 100);

        lblRange.setText("Enter a number between 1 and " + range + ":");
        // sets the text in lblRange so that it is centered regardless of the length of the text
        lblRange.setGravity(Gravity.CENTER);
        newGame();
        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(won == false) {
                    checkGuess();
                }
                else{
                    newGame();
                }
            }
        });
        txtGuess.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkGuess();
                // we return true to keep the keypad on screen
                // if we return false, program removes the number keypad
                return true;
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        switch(id){
            case R.id.action_gamedifficulty:
                // question: do both Strings and CharSequence work?
                final CharSequence[] items = {"1 to 10", "1 to 100", "1 to 1000"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select the Range:");
                builder.setItems(items,new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        if(item == 0){
                            range = 10;
                        }
                        else if(item == 1){
                            range = 100;
                        }
                        else if(item == 2){
                            range = 1000;
                        }
                        storeRange(range);
                        newGame();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.action_newgame:
                newGame();
                return true;
            case R.id.action_gamestats:
                final AlertDialog statsDialog = new AlertDialog.Builder(MainActivity.this).create();
                statsDialog.setTitle("Game statistics");
                statsDialog.setMessage("Players have won this game " + preferences.getInt("wins", 0) + " times");
                statsDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        statsDialog.dismiss();
                    }
                });
                statsDialog.show();
                return true;
            case R.id.action_about:
                final AlertDialog aboutDialog = new AlertDialog.Builder(MainActivity.this).create();
                aboutDialog.setTitle("About This Game");
                aboutDialog.setMessage("Created by Shuyao Wen\n(c)2018 Shuyao Wen");
                aboutDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        aboutDialog.dismiss();
                    }
                });
                aboutDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void storeRange(int storedRange){
        // default preferences are created when you make a app.
        // To access them, create a SharedPreferences object
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // to edit the preferences, you need an editor
        SharedPreferences.Editor editor = preferences.edit();
        // Shared preferences are stored as key/value pairs
        // use the put method to store a pair
        editor.putInt("range", storedRange);
        // apply method tells android you are done
        editor.apply();
    }
}
