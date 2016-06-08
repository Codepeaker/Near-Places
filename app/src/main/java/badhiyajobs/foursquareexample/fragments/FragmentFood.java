package badhiyajobs.foursquareexample.fragments;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarException;

import badhiyajobs.foursquareexample.AdapterList;
import badhiyajobs.foursquareexample.AppController;
import badhiyajobs.foursquareexample.ExampleTokenStore;
import badhiyajobs.foursquareexample.FoursquareActivity;
import badhiyajobs.foursquareexample.Information;
import badhiyajobs.foursquareexample.MainActivity;
import badhiyajobs.foursquareexample.R;


public class FragmentFood extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public View view;

    String Oauth;
    String location;
    String urlfood;
    String Name[] =new String[20];
    String Address[]={"Not Available","Not Available","Not Available","Not Available","Not Available","Not Available","Not Available","Not Available","Not Available","Not Available"};
    Float Rating[]=new Float[20];
    RecyclerView recyclerView;
    AdapterList adapterlist;

    public static FragmentFood newInstance(String param1, String param2) {
        FragmentFood fragment = new FragmentFood();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private String mParam1;
    private String mParam2;
    private ProgressDialog pDialog;
    public FragmentFood(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_fragment_food, container, false);


        Bundle extras= getActivity().getIntent().getExtras();
        location = extras.getString("location");
        Oauth=ExampleTokenStore.getToken();
        urlfood = "https://api.foursquare.com/v2/venues/explore?client_id=YCFCCQ4SOYVNEKBFKCFUYCYM0AY4FH2PSP2S1BAKGRKV3MAX&limit=10&client_secret=Q4AQS04T5GOFDOQ24GRKG52BM2132G3P1K4PFRJBX4QVZ53G&v=20130815%20&oauth_token="+Oauth+"&ll="+location+"&section=food";
        Toast.makeText(getActivity(),urlfood,Toast.LENGTH_LONG).show();




        recyclerView = (RecyclerView) view.findViewById(R.id.listviewfood);
        adapterlist = new AdapterList(getActivity(),getData());
        recyclerView.setAdapter(adapterlist);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));







        return view;
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
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                        recyclerView.setAdapter(adapterlist);
                        hidepDialog();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),
                                "error:"+error.getMessage(), Toast.LENGTH_SHORT).show();

                       hidepDialog();
                    }
                }


                );
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);


        return data;
    }

    /*

    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response){
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        serverEtag = headers.get("ETag");

        final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
        final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;

        return entry;
    }

    */

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
