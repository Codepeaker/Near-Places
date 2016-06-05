package badhiyajobs.foursquareexample;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DistanceLimit extends AppCompatActivity {

    String Oauth;
    String location;
    String urlfood;
    String Name[] =new String[20];
    String Address[]={"Not Available","Not Available","Not Available","Not Available","Not Available","Not Available","Not Available","Not Available","Not Available","Not Available"};
    Float Rating[]=new Float[20];
    RecyclerView recyclerView;
    private ProgressDialog pDialog;
    AdapterList adapterlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        Bundle extras= getIntent().getExtras();
        location = extras.getString("location");
        Oauth=ExampleTokenStore.getToken();
        urlfood = "https://api.foursquare.com/v2/venues/explore?client_id=YCFCCQ4SOYVNEKBFKCFUYCYM0AY4FH2PSP2S1BAKGRKV3MAX&client_secret=Q4AQS04T5GOFDOQ24GRKG52BM2132G3P1K4PFRJBX4QVZ53G&v=20130815%20&oauth_token="+Oauth+"&ll="+location+"&radius=5000";
        Toast.makeText(DistanceLimit.this,urlfood,Toast.LENGTH_LONG).show();




        recyclerView = (RecyclerView) findViewById(R.id.radiusview);
        adapterlist = new AdapterList(this,getData());
        recyclerView.setAdapter(adapterlist);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    public List<Information> getData(){
        final List<Information> data = new ArrayList<>();
        showpDialog();




        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlfood, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        for (int i = 0; i < 10; i++) {
                            try {


                                JSONArray items = jsonObject.getJSONObject("response").getJSONArray("groups").getJSONObject(0).getJSONArray("items");



                                if (jsonObject.getJSONObject("response").getJSONArray("groups").getJSONObject(0).getJSONArray("items").getJSONObject(i).getJSONObject("venue").has("location")) {
                                    if(jsonObject.getJSONObject("response").getJSONArray("groups").getJSONObject(0).getJSONArray("items").getJSONObject(i).getJSONObject("venue").getJSONObject("location").has("address")){

                                        JSONObject location = jsonObject.getJSONObject("response").getJSONArray("groups").getJSONObject(0).getJSONArray("items").getJSONObject(i).getJSONObject("venue").getJSONObject("location");
                                        Address[i] = location.getString("address");}}

                                Name[i] = items.getJSONObject(i).getJSONObject("venue").getString("name");
                                String rtg=items.getJSONObject(i).getJSONObject("venue").getString("rating");
                                Rating[i]=Float.parseFloat(rtg);
                                Information current = new Information();
                                current.name = Name[i];

                                current.address = Address[i];
                                current.ratings = Rating[i];
                                data.add(current);


                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                        recyclerView.setAdapter(adapterlist);
                        hidepDialog();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DistanceLimit.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        hidepDialog();
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);


        return data;
    }


    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
