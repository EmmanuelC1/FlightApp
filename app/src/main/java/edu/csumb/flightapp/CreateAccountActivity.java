package edu.csumb.flightapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;

import edu.csumb.flightapp.model.FlightDao;
import edu.csumb.flightapp.model.FlightRoom;
import edu.csumb.flightapp.model.LogRecord;
import edu.csumb.flightapp.model.User;

public class CreateAccountActivity  extends AppCompatActivity {
    //Check if password & account meet the requirements
    private static String symbol_req = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$])(?=.*[0-9]).*$";
    private Pattern pattern = Pattern.compile(symbol_req);
    private Matcher matcher;

    int numTries = 0;

    public boolean validate(final String password) {

        matcher = pattern.matcher(password);
        return matcher.matches();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button create_button = findViewById(R.id.create_account_button);
        create_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                EditText username = findViewById(R.id.username);
                EditText password = findViewById(R.id.password);

                String error = "";
                String pwFormatError = "Password does not meet the requirements.\n";
                String nameFormatError = "Username does not meet the requirements.\n";

                boolean userCheck = !validate(username.getText().toString());
                boolean passCheck = !validate(password.getText().toString());

                if(userCheck || passCheck){
                    if(userCheck){
                        error += nameFormatError;
                    }
                    if(passCheck){
                        error += pwFormatError;
                    }
                    TextView msg = findViewById(R.id.message);
                    msg.setText(error);

                    numTries++;
                    if(numTries == 2){
                        // Alert user that he exceeded num of tries and return to Main
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);
                        builder.setTitle("Exceeded amount of tries. Please try again.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    return;
                }


                if (username.getText().toString().equals("!admiM2")) {
                    // username already exists.
                    TextView msg = findViewById(R.id.message);
                    msg.setText("Username not available.");
                    return;
                }

                User user = FlightRoom.getFlightRoom(CreateAccountActivity.this).
                        dao().getUserByName(username.getText().toString());

                if (user == null) {
                    // username does not exist, so add the new account
                    String name = username.getText().toString();
                    String pw = password.getText().toString();

                    User newUser = new User(name, pw);
                    FlightDao dao = FlightRoom.getFlightRoom(CreateAccountActivity.this).dao();
                    dao.addUser(newUser);

                    Date now = new Date();
                    LogRecord rec = new LogRecord(now, LogRecord.TYPE_NEW_ACCOUNT,
                            name, "");
                    dao.addLogRecord(rec);


                    // Account created successfully
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);
                    builder.setTitle("Account successfully created.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    // username already exists.
                    TextView msg = findViewById(R.id.message);
                    msg.setText("Username not available.\n Please enter a new username and password.");

                    numTries += 1;
                    if(numTries == 2){
                        numTries = 0;

                        // Alert user that he exceeded num  of tries
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);
                        builder.setTitle("Exceeded amount of tries. Please try again.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }

            }
        });
    }
}