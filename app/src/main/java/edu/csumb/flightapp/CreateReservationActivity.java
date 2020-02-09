package edu.csumb.flightapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import edu.csumb.flightapp.model.FlightDao;
import edu.csumb.flightapp.model.FlightRoom;
import edu.csumb.flightapp.model.Reservation;
import edu.csumb.flightapp.model.User;

public class CreateReservationActivity extends AppCompatActivity {

    public static Reservation res = null;
    int numTries = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reservation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = findViewById(R.id.username);
                EditText password = findViewById(R.id.password);

                if (username.getText().toString().equals("!admiM2") &&
                        password.getText().toString().equals("!admiM2")) {

                    finish();
                }

                String name = username.getText().toString();
                String pw = password.getText().toString();

                FlightDao dao = FlightRoom.getFlightRoom(CreateReservationActivity.this).dao();
                User user = dao.login(name, pw);
                if (user == null) {
                    // unsuccessful login
                    TextView msg = findViewById(R.id.message);
                    msg.setText("Username or password is invalid. Please enter again.");

                    numTries += 1;
                    if(numTries == 2){
                        numTries = 0;

                        // Alert user that he exceeded num of tries
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateReservationActivity.this);
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


                } else {
                    // successful login
                    MainActivity.username = username.getText().toString();
                    CreateReservationActivity.res = new Reservation(MainActivity.username, SearchActivity.selectedFlight, SearchActivity.amountTickets);
                    res.setId((int)dao.addReservation(res));
                    Intent intent = new Intent(CreateReservationActivity.this, ConfirmReservationActivity.class);
                    startActivity(intent);
                }

            }
        });

    }
}

