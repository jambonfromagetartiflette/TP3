package ca.ulaval.ima.tp3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MarkListFragment extends Fragment {

    public ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    public MarkListFragment() {
        // Required empty public constructor
    }

    public static MarkListFragment newInstance() {
        MarkListFragment fragment = new MarkListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_offer_list, container, false);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        mainListView = (ListView) view.findViewById( R.id.ListView );
        final ArrayList<String> offerList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(getContext(), R.layout.offer_raw, offerList);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, "http://159.203.33.206/api/v1/offer/", null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray resArray = response.optJSONArray("content");
                        try {
                            HashSet<String> hashSet = new HashSet<String>();
                            for (int i = 0; i < resArray.length(); i++) {
                                //Log.d("elem= ", resArray.optJSONObject(i).optJSONObject("model").optJSONObject("brand").toString());
                                String mark = resArray.optJSONObject(i).optJSONObject("model").optJSONObject("brand").optString("name");
                                hashSet.add(mark);
                            }
                            listAdapter.clear();
                            listAdapter.addAll(hashSet);
                            Collections.sort(offerList);
                            mainListView.setAdapter(listAdapter);
                        } catch (Exception e) {
                            Log.d("Error" +
                                    ": ",  e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error1.Response", "volley error");
                    }
                }
        );
        queue.add(getRequest);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object elem = mainListView.getItemAtPosition(position);
                Log.d("elem clicked = ", elem.toString());
                Log.d("elem id = ", Long.toString(id));
                //FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                //fragmentTransaction.replace(R.id.container, new ModelListFragment());
                //fragmentTransaction.commit();
            }
        });

        return view;
    }
}
