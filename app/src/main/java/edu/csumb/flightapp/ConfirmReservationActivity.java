package edu.csumb.flightapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;

import edu.csumb.flightapp.model.Flight;
import edu.csumb.flightapp.model.FlightDao;
import edu.csumb.flightapp.model.FlightRoom;
import edu.csumb.flightapp.model.LogRecord;

public class ConfirmReservationActivity extends AppCompatActivity {
    FlightDao dao = FlightRoom.getFlightRoom(ConfirmReservationActivity.this).dao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reservation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String information = CreateReservationActivity.res.toString();

        //Show reservation info
        TextView summary = findViewById(R.id.summary);
        summary.setText(information);

        //Confirm reservation info
        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Flight Capacity gets updated
                Flight updateFlight = SearchActivity.selectedFlight;
                updateFlight.setCapacity(updateFlight.getCapacity()-SearchActivity.amountTickets);
                dao.updateFlight(updateFlight);

                Date now = new Date();
                LogRecord rec = new LogRecord(now, LogRecord.TYPE_RESERVATION,
                        MainActivity.username, CreateReservationActivity.res.getLog());
                dao.addLogRecord(rec);

                // Back to Main Activity
                Intent intent = new Intent(ConfirmReservationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //If user cancels the information, delete reservation form DataBase
        Button decline = findViewById(R.id.decline);
        decline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Delete res from Database
                dao.deleteReservation(CreateReservationActivity.res);
                // Back to Main Activity
                Intent intent = new Intent(ConfirmReservationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}

