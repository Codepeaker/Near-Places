package badhiyajobs.foursquareexample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by vishal on 1/16/2016.
 */
public class AdapterList extends RecyclerView.Adapter<AdapterList.MyeventViewHolder>{

    private int mPreviousPosition = 0;
    private LayoutInflater inflater;
    private View view;
    private Context context;
    List<Information> data = Collections.emptyList();
    public AdapterList(Context context, List<Information> data){
        inflater=LayoutInflater.from(context);
        this.data = data;
        this.context=context;
    }

    @Override
    public MyeventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.contentview,parent,false);
        MyeventViewHolder holder = new MyeventViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(AdapterList.MyeventViewHolder holder, int position) {
        final Information current = data.get(position);
        holder.name.setText(current.name);
        holder.address.setText(current.address);
        holder.ratings.setText(Float.toString(current.ratings));


        mPreviousPosition = position;

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyeventViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView address;
        TextView ratings;
        public MyeventViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.textview_name);
            address= (TextView) itemView.findViewById(R.id.textview_address);
            ratings = (TextView)itemView.findViewById(R.id.textview_ratings);


        }
    }
}
