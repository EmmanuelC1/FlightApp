package edu.csumb.flightapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;

import edu.csumb.flightapp.model.FlightDao;

import edu.csumb.flightapp.model.Flight;
import edu.csumb.flightapp.model.FlightRoom;
import edu.csumb.flightapp.model.LogRecord;

public class AddNewFlightActivity extends AppCompatActivity {
    FlightDao dao = FlightRoom.getFlightRoom(AddNewFlightActivity.this).dao();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flight);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText flightNo_ = findViewById(R.id.flight_number_edit);
        EditText from_ = findViewById(R.id.from_edit);
        EditText to_ = findViewById(R.id.to_edit);
        EditText time_ = findViewById(R.id.time_edit);
        EditText capacity_ = findViewById(R.id.capacity_edit);
        EditText price_ = findViewById(R.id.price_edit);


        Button addFlight = findViewById(R.id.add_flight);
        addFlight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String flightNo = flightNo_.getText().toString();
                String from = from_.getText().toString();
                String to = to_.getText().toString();
                String time = time_.getText().toString();
                int capacity = Integer.parseInt(capacity_.getText().toString());
                double price = Double.parseDouble(price_.getText().toString());

                boolean empty = false;
                if(flightNo.isEmpty() || from.isEmpty() || to.isEmpty() || time.isEmpty() ||
                        capacity_.getText().toString().isEmpty() || price_.getText().toString().isEmpty()){
                    empty = true;
                }
                if(dao.getFlightByFlightNo(flightNo)==null && !empty){
                    Flight addFlight = new Flight(flightNo,from,to,time,capacity,price);
                    addFlight.setId((int)dao.addFlight(addFlight));

                    Date now = new Date();
                    LogRecord rec = new LogRecord(now, LogRecord.TYPE_NEW_FLIGHT,
                            MainActivity.username, addFlight.toString());
                    dao.addLogRecord(rec);

                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNewFlightActivity.this);
                    builder.setTitle("Flight succesfully added.");
                    builder.setPositiveButton("Confirm.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(AddNewFlightActivity.this,
                                    MainActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();



                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNewFlightActivity.this);
                    builder.setTitle("Flight already exists.");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(AddNewFlightActivity.this,
                                    MainActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }
}
