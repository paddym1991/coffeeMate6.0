package ie.cm.api;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ie.cm.main.CoffeeMateApp;
import ie.cm.models.Coffee;

public class CoffeeApi {

    private static final    String          hostURL = "http://coffeemateweb.herokuapp.com";
    private static final    String          LocalhostURL = "http://192.168.0.13:3000";
    private static          VolleyListener  vListener;
    public static           CoffeeMateApp   app = CoffeeMateApp.getInstance();
    public static final     String          TAG = CoffeeMateApp.class.getName();
    public static           ProgressDialog  dialog;

    public static void attachListener(VolleyListener fragment) {
        vListener = fragment;
    }

    public static void detachListener() {
        vListener  = null;
    }

    public static void attachDialog(ProgressDialog mDialog) {
        dialog = mDialog;
    }

    private static void showDialog(String message) {
        dialog.setMessage(message);
        if (!dialog.isShowing())
            dialog.show();
    }
    private static void hideDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void getAll(String url, final SwipeRefreshLayout mSwipeRefreshLayout) {
        Log.v(TAG, "GETing All Coffees from " + url);
        showDialog("Downloading Coffees...");
        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hostURL + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        List<Coffee> result = null;
                        Type collectionType = new TypeToken<List<Coffee>>(){}.getType();
                        result = new Gson().fromJson(response, collectionType);
                        vListener.setList(result);
                        mSwipeRefreshLayout.setRefreshing(false);
                        hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.v(TAG,"Something went wrong with GET ALL!");
                mSwipeRefreshLayout.setRefreshing(false);
                error.printStackTrace();
            }
        });
        // Add the request to the queue
        app.add(stringRequest);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void get(String url) {
        showDialog("Downloading all User Coffees...");
        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, hostURL + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Result handling
                        Coffee result = null;
                        Type objType = new TypeToken<Coffee>(){}.getType();
                        result = new Gson().fromJson(response, objType);
                        vListener.setCoffee(result);
                        hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.v(TAG,"Something went wrong wit GET!");
                error.printStackTrace();
            }
        });

// Add the request to the queue
        app.add(stringRequest);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void post(String url,Coffee aCoffee) {

        Log.v(TAG, "POSTing to : " + url);
        showDialog("Adding a Coffee...");
        Type objType = new TypeToken<Coffee>(){}.getType();
        String json = new Gson().toJson(aCoffee, objType);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest gsonRequest = new JsonObjectRequest( Request.Method.POST, hostURL + url,

                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        Log.v(TAG, "insert new Coffee " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.v(TAG, "Unable to insert new Coffee");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };

        // Add the request to the queue
        app.add(gsonRequest);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void put(String url,Coffee aCoffee) {

        Log.v(TAG, "PUTing to : " + url);
        showDialog("Updating a Coffee...");
        Type objType = new TypeToken<Coffee>(){}.getType();
        String json = new Gson().toJson(aCoffee, objType);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest gsonRequest = new JsonObjectRequest( Request.Method.PUT, hostURL + url,

                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Result handling
                        Coffee result = null;
                        Type objType = new TypeToken<Coffee>(){}.getType();

                        try {
                            result = new Gson().fromJson(response.getString("data"), objType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        vListener.setCoffee(result);
                        hideDialog();
                        Log.v(TAG, "Updating a Coffee successful with :" + result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        Log.v(TAG, "Unable to update Coffee with error : " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", System.getProperty("http.agent"));
                return headers;
            }
        };
        // Add the request to the queue
        app.add(gsonRequest);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void delete(String url) {
        Log.v(TAG, "DELETEing from " + url);

        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, hostURL + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.v(TAG, "DELETE success " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                Log.v(TAG,"Something went wrong with DELETE!");
                error.printStackTrace();
            }
        });

        // Add the request to the queue
        app.add(stringRequest);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void getGooglePhoto(String url,final ImageView googlePhoto) {

        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        app.googlePhoto = response;
                        googlePhoto.setImageBitmap(app.googlePhoto);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Something went wrong!");
                error.printStackTrace();
            }
        });

// Add the request to the queue
        app.add(imgRequest);
    }
}