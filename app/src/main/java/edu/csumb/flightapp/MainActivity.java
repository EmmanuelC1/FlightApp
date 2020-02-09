package edu.csumb.flightapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import edu.csumb.flightapp.model.FlightRoom;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    public static String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // checks database
        FlightRoom.getFlightRoom(MainActivity.this).loadData(this);

        Button create_account_button = findViewById(R.id.create_account);

        create_account_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "create account called");
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);

            }
        });

        Button reserve_seat_button = findViewById(R.id.reserve_seat);
        reserve_seat_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "reserve seat called");
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);

            }
        });

        Button cancel_reservation_button = findViewById(R.id.cancel_reservation);
        cancel_reservation_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "cancel reservation called");
                Intent intent = new Intent(MainActivity.this, CancelReservationActivity.class);
                startActivity(intent);

            }
        });

        Button manage_system_button = findViewById(R.id.manage_system);
        manage_system_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // call the ShowUser Activity
                Log.d("MainActivity", "manage systems called");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
