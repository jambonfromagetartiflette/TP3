package ca.ulaval.ima.tp3;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyOfferListFragment extends Fragment {
    private TextView mMarkName;
    private TextView mModelName;
    private ImageView mImage;
    private TextView mYear;
    private TextView mOwner;
    private TextView mKilometers;
    private TextView mPrice;
    private TextView mCreated;
    private Dialog myDialog;
    public ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;
    public MyOfferListFragment() {
        // Required empty public constructor
    }

    public static MyOfferListFragment newInstance() {
        MyOfferListFragment fragment = new MyOfferListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void displayMyOffer(final String token, RequestQueue queue, View view) {
        //mainListView = (ListView) view.findViewById( R.id.ListView);
        final ArrayList<String> offerList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(getContext(), R.layout.offer_raw, offerList);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, "http://159.203.33.206/api/v1/offer/mine", null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray resArray = response.optJSONArray("content");
                        try {
                            // HashSet<String> hashSet = new HashSet<String>();
                            Log.d("res= ", response.toString());
                            for (int i = 0; i < resArray.length(); i++) {
                                String content = resArray.optJSONObject(i).toString();
                                listAdapter.add(content);
                            }
                            mainListView.setAdapter(listAdapter);
                        }catch (Exception e) {
                            Log.d("Error: ",  e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Basic " +  token);

                return params;
            }
        };
        queue.add(getRequest);

    }

    private void callLoginDialog2()
    {
        this.myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.dialog_form);
        myDialog.setCancelable(true);
        final Button login = (Button) myDialog.findViewById(R.id.dialogButtonOk);

        final EditText emailaddr = (EditText) myDialog.findViewById(R.id.mail);
        final EditText password = (EditText) myDialog.findViewById(R.id.ni);
        myDialog.getWindow().setLayout(1000, 1000);
        myDialog.show();

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final RequestQueue queue = Volley.newRequestQueue(getContext());

                JSONObject postparams = new JSONObject();
                try {
                    postparams.put("email", emailaddr.getText().toString());
                    postparams.put("identification_number", Integer.valueOf(password.getText().toString()));
                } catch (Exception e){
                    Log.d("Error post method=", e.toString());
                }
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        "http://159.203.33.206/api/v1/account/login", postparams,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String token = response.optJSONObject("content").optString("token");
                                myDialog.dismiss();
                                displayMyOffer(token, queue, v);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error:", error.getMessage());
                                Toast.makeText(getActivity(), "Identifiants invalides !", Toast.LENGTH_LONG).show();

                            }
                        });
                queue.add(jsonObjReq);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_offer_list, container, false);

        this.mMarkName = view.findViewById(R.id.mark_name);
        this.mModelName = view.findViewById(R.id.model_name);
        this.mImage= view.findViewById(R.id.image);
        this.mYear = view.findViewById(R.id.year);
        this.mOwner = view.findViewById(R.id.owner);
        this.mKilometers = view.findViewById(R.id.kilometers);
        this.mPrice = view.findViewById(R.id.price);
        this.mCreated = view.findViewById(R.id.created);

        //
        // this.callLoginDialog2();

        return view;
    }
}
