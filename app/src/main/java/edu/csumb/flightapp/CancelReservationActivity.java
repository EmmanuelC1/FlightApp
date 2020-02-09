package edu.csumb.flightapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.csumb.flightapp.model.Flight;
import edu.csumb.flightapp.model.FlightDao;
import edu.csumb.flightapp.model.FlightRoom;
import edu.csumb.flightapp.model.LogRecord;
import edu.csumb.flightapp.model.Reservation;
import edu.csumb.flightapp.model.User;

public class CancelReservationActivity extends AppCompatActivity {
    FlightDao dao = FlightRoom.getFlightRoom(CancelReservationActivity.this).dao();
    public static List<Reservation> reservations = new ArrayList<Reservation>();
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reservation);
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

                FlightDao dao = FlightRoom.getFlightRoom(CancelReservationActivity.this).dao();
                User user = dao.login(name, pw);
                if (user == null) {
                    // unsuccessful login
                    TextView msg = findViewById(R.id.message);
                    msg.setText("Username or password is invalid. Please try again.");


                } else {
                    // successful login
                    reservations = FlightRoom.getFlightRoom(CancelReservationActivity.this).dao()
                            .getReservationForUser(name);

                    MainActivity.username = name;
                    // Handles no reservations
                    if(reservations.isEmpty()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(CancelReservationActivity.this);
                        builder.setTitle("There are no reservation made.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(CancelReservationActivity.this,
                                        MainActivity.class);

                                startActivity(intent);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager( new LinearLayoutManager(this));
        adapter = new Adapter();
        rv.setAdapter( adapter );

    }

    private class Adapter extends RecyclerView.Adapter<ItemHolder> {

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(CancelReservationActivity.this);
            return new ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position){
           holder.bind(reservations.get(position));
        }

        @Override
        public int getItemCount() { return reservations.size(); }

    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item, parent, false));
        }

        public void bind(Reservation r) {
            TextView item = itemView.findViewById(R.id.item_id);
            item.setText(r.toString());

            // make the item clickable
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CancelReservationActivity.this);
                    builder.setTitle("Are you sure you want to cancel this reservation?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Date now = new Date();
                            LogRecord rec = new LogRecord(now, LogRecord.TYPE_CANCEL,
                                    MainActivity.username, r.getCancelLog());
                            dao.addLogRecord(rec);

                            //Delete reservation
                            dao.deleteReservation(r);
                            adapter.notifyDataSetChanged();
                            reservations.clear();

                            Flight updateFlight = dao.getFlightByFlightNo(r.getFlightNo());
                            updateFlight.setCapacity(updateFlight.getCapacity() + r.getTickets());
                            dao.updateFlight(updateFlight);
                            //Start new activity
                            Intent intent = new Intent(CancelReservationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            AlertDialog.Builder builder2 = new AlertDialog.Builder(CancelReservationActivity.this);
                            builder2.setTitle("Failed to cancel reservation.");
                            builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog2, int which) {
                                    finish();
                                    Intent intent = new Intent(CancelReservationActivity.this,
                                            MainActivity.class);

                                    startActivity(intent);
                                }
                            });

                            AlertDialog dialog2 = builder2.create();
                            dialog2.show();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


        }
    }

}


