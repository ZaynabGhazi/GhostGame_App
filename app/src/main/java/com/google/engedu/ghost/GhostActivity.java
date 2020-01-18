/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private String word_fragment="";
    private static final int WORD_MIN =4;
    private Button challenge;
    private Button restart;
    private String resume_val ="";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            word_fragment = savedInstanceState.getString("wordFragment");
            resume_val = savedInstanceState.getString("gameState");
        }
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        //added:
       try{
           InputStream in = this.getAssets().open("words.txt");
           //dictionary = new SimpleDictionary(in);
           dictionary = new FastDictionary(in);

       }
        catch(IOException e) {
            Log.e("", "onCreate: " );
        }
        challenge = (Button) findViewById(R.id.button5);
        challenge.setOnClickListener(myhandler1);
        restart = (Button) findViewById(R.id.button7);
        restart.setOnClickListener(myhandler2);
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
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

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        Log.e("", "onStart: " );
        //System.out.printf("onStart");
        return true;
    }

    private void computerTurn() {
        Log.e("","computerTurn()");
        // System.out.printf("computerTurn");

        TextView label = (TextView) findViewById(R.id.gameStatus);
        // Do computer turn stuff then make it the user's turn again
        //added:
        //String prefix = dictionary.getGoodWordStartingWith(word_fragment);
        String prefix = dictionary.getAnyWordStartingWith(word_fragment);

        if (word_fragment.length() >= WORD_MIN && dictionary.isWord(word_fragment)){
            label.setText("Complete word => Computer WINS!");
        }
        else if( prefix== null){
            label.setText("This word is not a prefix. Computer WINS!");
        }
        else if (prefix != null){
            System.out.println("DBEUG "+word_fragment+" "+prefix);
            word_fragment+=Character.toString(prefix.charAt(word_fragment.length()));
            TextView label1 = (TextView) findViewById(R.id.ghostText);
            label1.setText(word_fragment);
            userTurn = true;
            label.setText(USER_TURN);
        }

    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
       //added:
        //check if key corresponds to alphabetic character:
        char uni_char = (char)event.getUnicodeChar();
        if (Character.isLetter(uni_char)) {
            word_fragment += uni_char;
            TextView word = (TextView)findViewById(R.id.ghostText);
            word.setText(word_fragment);
            TextView status = (TextView)findViewById(R.id.gameStatus);

            if (dictionary.isWord(word_fragment)){
                status.setText("Word fragment is complete!");
            }
            else{
                status.setText(COMPUTER_TURN);
                computerTurn();

            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    //challenge_handler:

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            TextView word = (TextView)findViewById(R.id.gameStatus);
            if (word_fragment.length() >= WORD_MIN && dictionary.isWord(word_fragment)){
                word.setText("User WINS!");
            }
            else if (dictionary.getAnyWordStartingWith(word_fragment) != null){
                word.setText("Computer WINS! Possible word: "+dictionary.getAnyWordStartingWith(word_fragment));

            }
            else{
                word.setText("User WINS!");
            }
        }
    };

    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View v) {
            word_fragment="";
            onStart(v);

        }
    };
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("wordFragment",word_fragment);
        savedInstanceState.putString("gameState",((TextView)(findViewById(R.id.gameStatus))).getText().toString());
        super.onSaveInstanceState(savedInstanceState);

    }
    @Override
    public void onResume(){
        super.onResume();
        if (resume_val != null){
            TextView txt = (TextView)findViewById(R.id.gameStatus);
            (txt).setText(resume_val);
            TextView txt1 = (TextView)findViewById(R.id.ghostText);
            (txt1).setText(word_fragment);

        }
    }
}
