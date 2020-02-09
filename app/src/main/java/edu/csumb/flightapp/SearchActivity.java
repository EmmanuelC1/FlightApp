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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.csumb.flightapp.model.Flight;
import edu.csumb.flightapp.model.FlightRoom;


public class SearchActivity extends AppCompatActivity {

    public static List<Flight> flights = new ArrayList<Flight>();
    public static Flight selectedFlight = null;
    public static int amountTickets;
    Adapter adapter;
    public void setAmountTickets(@NonNull int amountTickets) {
        this.amountTickets = amountTickets;
    }

    @NonNull
    public int getAmountTickets() {
        return amountTickets;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flight);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button return_main_button = findViewById(R.id.return_button);
        return_main_button.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("SearchActivity", "return called");
                finish();
            }
        });

        Button search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText from = findViewById(R.id.from_city);
                EditText to = findViewById(R.id.to_city);
                EditText no_tickets = findViewById(R.id.no_tickets);

                // Check that input is not empty
                if(from.getText().toString().isEmpty() || to.getText().toString().isEmpty() ||
                no_tickets.getText().toString().isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                    builder.setTitle("Please fill out all the fields.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SearchActivity.this,
                                    SearchActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                amountTickets = Integer.parseInt(no_tickets.getText().toString());
                setAmountTickets(amountTickets);

                flights = FlightRoom.getFlightRoom(SearchActivity.this).dao().
                        searchFlight(from.getText().toString(),
                                to.getText().toString(),amountTickets);

                // Does flight exist?
                if(flights.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                    builder.setTitle("There are no flights available.");
                    builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SearchActivity.this,
                                    MainActivity.class);

                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

                // Flight exists (does some checking)
                else{
                    // Check that user does not get more than 7 tickets and it is more than 0
                    if(amountTickets > 0 && amountTickets <= 7){

                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                        builder.setTitle("Select your flight");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SearchActivity.this,
                                        SearchActivity.class);

                                startActivity(intent);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        // notifies recycler view that list of flights has changed
                        adapter.notifyDataSetChanged();
                    }
                    // User wants more than 7 tickets (no good)
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                        builder.setTitle("Sorry you can't excceed 7 tickets.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SearchActivity.this,
                                        MainActivity.class);

                                startActivity(intent);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });

        RecyclerView rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager( new LinearLayoutManager(this));
        adapter = new Adapter();
        rv.setAdapter( adapter );

    }

    private class Adapter  extends RecyclerView.Adapter<ItemHolder> {

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(SearchActivity.this);
            return new  ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position){
            holder.bind(flights.get(position));
        }

        @Override
        public int getItemCount() { return flights.size(); }

    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item, parent, false));
        }

        public void bind(Flight f ) {
            TextView item = itemView.findViewById(R.id.item_id);
            item.setText(f.toString());

            // make the item clickable
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("onClick2","f is: \n" + f.toString());
                    selectedFlight = f;
                    Log.d("onClick2","selected Flight is: \n" + selectedFlight.toString());
                    flights.clear();
                    Intent intent = new Intent(SearchActivity.this, CreateReservationActivity.class);
                    finish();
                    startActivity(intent);
                }
            });


        }
    }
}


