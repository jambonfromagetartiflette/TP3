package ca.ulaval.ima.tp3;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SellFragment extends Fragment {
    private Spinner modelsForm;
    private Spinner transmissionForm;
    private EditText yearForm;
    private View submitForm;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText priceForm;
    private EditText kmForm;

    public SellFragment() {
        // Required empty public constructor
    }

    public static SellFragment newInstance() {
        SellFragment fragment = new SellFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void sendNewOffer(final String token, RequestQueue queue, final Dialog mydialog) {
        int model = getIdModel(modelsForm.getSelectedItem().toString());
        String transmission;
        final Toast toast = Toast.makeText(getContext(), "Offre ajouté", Toast.LENGTH_SHORT);
        final Toast toast_error = Toast.makeText(getContext(), "Formulaire invalide", Toast.LENGTH_SHORT);;
        if (transmissionForm.getSelectedItem().toString().equals("Automatique")) {
            transmission = "AT";
        } else if (transmissionForm.getSelectedItem().toString().equals("Manuel")) {
            transmission = "MA";
        } else if (transmissionForm.getSelectedItem().toString().equals("Robotique")) {
            transmission = "RB";
        } else {
            transmission = "AT";
        }
        int year = Integer.valueOf(yearForm.getText().toString());
        Boolean owner = radioButton.getText().toString().equals("Oui");
        int price = Integer.valueOf(priceForm.getText().toString());
        int km = Integer.valueOf(kmForm.getText().toString());

        JSONObject postparams = new JSONObject();
        try {
        postparams.put("from_owner", owner);
        postparams.put("kilometers", km);
        postparams.put("year", year);
        postparams.put("price", price);
        postparams.put("transmission", transmission);
        postparams.put("model", model);
        } catch (Exception e){
            Log.d("Error post method=", e.toString());
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                "http://159.203.33.206/api/v1/offer/add", postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mydialog.dismiss();
                        toast.show();                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error:", error.getCause().toString());
                        toast_error.show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Basic " +  token);

                return params;
            }
        };
       queue.add(jsonObjReq);
    }

    private void callLoginDialog()
    {
        final Dialog myDialog = new Dialog(getContext());
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
            public void onClick(View v)
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
                                sendNewOffer(token, queue, myDialog);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        this.modelsForm = view.findViewById(R.id.model_choice_spinner);
        this.transmissionForm = view.findViewById(R.id.transmission_choice_spinner);
        this.yearForm = view.findViewById(R.id.year);
        this.submitForm = view.findViewById(R.id.submit);
        this.radioGroup = view.findViewById(R.id.owner);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        this.radioButton = view.findViewById(selectedId);
        this.priceForm = view.findViewById(R.id.price);
        this.kmForm = view.findViewById(R.id.kilometers);

        submitForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (!(yearForm.getText().toString().equals(""))
                        && !(radioButton.getText().toString().equals(""))
                        && !(priceForm.getText().toString().equals(""))
                        && !(kmForm.getText().toString().equals(""))) {
                    callLoginDialog();
                }
                else {
                    Toast.makeText(getActivity(), "Formulaire Invalide !", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    public int getIdModel(String name) {
        String[] tab = {"Acura", "Aston", "Martin", "Audi", "BMW", "Buick", "Cadillac", "Chevrolet",
                "Chrysler", "Dodge", "Ferarri", "Ford", "GMC", "Honda", "Hummer", "Hyundai",
                "Isuzu", "Jeep", "Kia", "Land Rover", "Lotus", "Maserati", "Mazda", "Mercedes-Benz",
                "Nissan", "Porsche", "Rolls-Royce", "Saab", "Saturn", "Scion", "Smart", "Subaru",
                "Toyota", "Volkswagon", "Volvo"};
        int i = 0;
        while (i < tab.length){
            if (name.equals(tab[i]))
                return i + 1;
            i++;
        }
        return 1;
    }
}
