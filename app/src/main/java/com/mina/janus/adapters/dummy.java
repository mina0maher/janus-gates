package com.mina.janus.adapters;

import android.content.Intent;
import android.net.Uri;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class dummy {


   private void direction(LatLng origin,LatLng dest){
     // RequestQueue requestQueue = Volley.newRequestQueue(this);
      String url = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
              .buildUpon()
              .appendQueryParameter("origin",origin.latitude+", "+origin.longitude)
              .appendQueryParameter("destination",dest.latitude+", "+dest.longitude)
              .appendQueryParameter("mode","driving")
              .appendQueryParameter("key","AIzaSyACj6pL3ihZZ5lLcHctfEPYn54bbx4GhMQ")
              .toString();
      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
         @Override
         public void onResponse(JSONObject response) {
            try{
               String status = response.getString("status");
               if(status.equals("OK")){
                  JSONArray routes = response.getJSONArray("routes");
                  ArrayList<LatLng> points;
                  PolylineOptions polylineOptions = null;
                  for(int i = 0 ;i<routes.length();i++){
                     points = new ArrayList<>();
                     polylineOptions = new PolylineOptions();
                     JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
                     for(int j = 0 ; j<legs.length();j++){
                           JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");
                           for(int k = 0;k<steps.length();k++){
                              String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                              List<LatLng> list = decodePloy(polyline);
                              for(int l = 0 ;l<list.size();l++){
                                 LatLng position = new LatLng((list.get(l)).latitude,(list.get(l)).longitude);
                                 points.add(position);
                              }
                           }
                     }
                     polylineOptions.addAll(points);
                     polylineOptions.width(10);
                 //    polylineOptions.color();
                     polylineOptions.geodesic(true);
                  }
               }
            }catch (JSONException e){
               e.printStackTrace();
            }
         }
      }, new Response.ErrorListener() {
         @Override
         public void onErrorResponse(VolleyError error) {

         }
      });
      RetryPolicy retryPolicy = new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
      jsonObjectRequest.setRetryPolicy(retryPolicy);
     // requestQueue.add(jsonObjectRequest);
   }

   private List<LatLng> decodePloy(String encoded){
      List<LatLng> poly = new ArrayList<>();
      int index = 0,len=encoded.length();
      int lat=0,lng=0;
      while(index<len){
         int b,shift=0,result=0;
         do{
            b = encoded.charAt(index++)-63;
            result |= (b&0x1f)<<shift;
            shift+=5;
         }while (b>=0x20);
         int dlat = ((result&1)!= 0 ?~(result>>1):(result>>1));
         lat+=dlat;
         shift=0;
         result=0;
         do{
            b=encoded.charAt(index++)-63;
            result |=(b&0x1f)<<shift;
            shift+=5;
         }while (b>0x20);
         int dlng = ((result&1)!=0 ? ~(result>>1):(result>>1));
         lng += dlng;
         LatLng p = new LatLng((((double) lat/1E5)),
                 (((double) lng/1E5)));
         poly.add(p);
      }
      return poly;
   }

   }
