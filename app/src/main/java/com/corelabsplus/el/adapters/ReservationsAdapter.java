package com.corelabsplus.el.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.corelabsplus.el.R;
import com.corelabsplus.el.utils.Reservation;

import java.util.List;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder> {

    private List<Reservation> reservations;
    private Context context;

    public ReservationsAdapter(List<Reservation> reservations, Context context) {
        this.reservations = reservations;
        this.context = context;
    }

    @NonNull
    @Override
    public ReservationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.reservation, parent, false);
        return new ReservationsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        holder.name.setText(reservation.getUserName());
        holder.email.setText(reservation.getEmail());
        holder.reference.setText(reservation.getReference());
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, reference, email;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.reserv_name);
            reference = (TextView) itemView.findViewById(R.id.reserv_ref);
            email = (TextView) itemView.findViewById(R.id.reserv_email);
        }
    }
}
