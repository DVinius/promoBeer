package br.com.vsgdev.promobeer.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import br.com.vsgdev.promobeer.adapters.CommentAdapter;
import br.com.vsgdev.promobeer.adapters.ItemAdapter;
import br.com.vsgdev.promobeer.models.Comment;
import br.com.vsgdev.promobeer.models.Item;
import br.com.vsgdev.promobeer.models.User;


public class WebServiceUtils {
    public static RequestQueue queue;
    public static final String SERVER = "http://107.170.121.53";
    //public static final String SERVER = "http://192.168.0.49";
    //9000 = SocialBus
    //9001 = Meu Leilao
    //9002 = SocialSOft
    public static final String PORT = "9003";

    public static final String WS_REGISTER_ITEM = SERVER + ":" + PORT + "/registerItem";
    public static final String WS_LOAD_NEXT_ITEM = SERVER + ":" + PORT + "/getNextItem";
    public static final String WS_POST_COMMENT = SERVER + ":" + PORT + "/postComment";
    public static final String WS_REGISTER_USER = SERVER + ":" + PORT + "/registerUser";

    private static RequestQueue getRequestQueue(final Context context) {
        Log.d(WebServiceUtils.class.getName(), "Using: " + SERVER + " as server.");
        if (queue == null && context != null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    private static RequestQueue getRequestQueue() {
        return getRequestQueue(null);
    }

    public static void sendComment(final Comment comment, final Response.Listener<JSONObject> response, final Response.ErrorListener errorListener) {
        final JSONObject jsonObject = JSONConverter.comment2Json(comment);
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                WS_POST_COMMENT,
                jsonObject,
                response, errorListener
        );
        getRequestQueue().add(request);
    }

    public static void loadItemComments(final CommentAdapter adapter, final Response.Listener<JSONObject> response, final Response.ErrorListener errorListener) {
        final Comment last = adapter.getLastItem();
    }

    public static void loadItems(final Context context, final ItemAdapter adapter, final Response.Listener<JSONObject> response, final Response.ErrorListener errorListener) {
        final Item last = adapter.getLastItem();
        if (last == null) {
            loadItems(context, new Item(0), response, errorListener);
        } else {
            loadItems(context, last, response, errorListener);
        }
    }

    public static void loadItems(final Context context, final Item reference, final Response.Listener<JSONObject> response, final Response.ErrorListener errorListener) {
        final JSONObject jsonObject = JSONConverter.item2Json(reference);
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                WS_LOAD_NEXT_ITEM,
                jsonObject,
                response, errorListener
        );
        getRequestQueue(context).add(request);
    }

    public static void registerUser(final User user, final Context context) {
        final JSONObject jsonObject = JSONConverter.user2Json(user, false);
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                WS_REGISTER_USER,
                jsonObject,
                null, null
        );
        getRequestQueue(context).add(request);
    }

    public static void registerItem(final Item item, final Context context, final Response.Listener<JSONObject> response, final Response.ErrorListener errorListener) {
        final JSONObject jsonObject = JSONConverter.item2Json(item);
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                WS_REGISTER_ITEM,
                jsonObject,
                response, errorListener
        );
        getRequestQueue(context).add(request);
    }

    /*
    public static void loadBills(final List<Bill> bills, final BillViewListAdapter adapter, final Activity activity) {
        final Bill referenceBill = new Bill();
        referenceBill.setId(0);
        loadNextBill(referenceBill, bills, adapter, activity);
    }

    public static void loadNextBill(final Bill referenceBill, final List<Bill> bills, final BillViewListAdapter adapter, final Activity activity) {
        final JSONObject jsonObject = JSONConverter.billToJson(referenceBill);

    }

    public static void registerBill(final Bill bill, final Activity activity) {
        final JSONObject jsonBill = JSONConverter.billToJson(bill);
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                WS_REGISTER_BILL,
                jsonBill,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        try {
                            final int responseCode = response.getInt("responseCode");
                            switch (responseCode) {
                                case 0:
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity, "Despesa registrada com sucesso", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Game.showMessage(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                Log.e(WebServiceUtils.class.getName(), "volleyError");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "VolleyError", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        );
        getRequestQueue(activity).add(request);
    }

    public static void loadUsersToSpinner(final Spinner spinner, final Activity activity) {
        final Map<String, String> params = new HashMap<>();
        final String deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        params.put("deviceId", deviceId);
        final JSONObject jsonObject = new JSONObject(params);
        final JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.POST,
                WS_LOAD_ALL_USERS,
                jsonObject,
                new Response.Listener<JSONArray>() {
                    public void onResponse(JSONArray response) {
                        try {
                            final List<User> loadedUsers = JSONConverter.responseToUsers(response);
                            final User[] usersArray = loadedUsers.toArray(new User[loadedUsers.size()]);
                            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_item, usersArray);
                            spinner.setAdapter(spinnerArrayAdapter);

                        } catch (Exception e) {
                            final String errorMsg = e.getMessage();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                final String errorMsg = "Erro ao buscar usuarios";
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        );
        getRequestQueue(activity).add(request);
    }

    public static void loadUser(final Activity activity) {
        final Map<String, String> params = new HashMap<>();
        final String deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        params.put("deviceId", deviceId);
        final JSONObject jsonObject = new JSONObject(params);
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                SERVER + ":" + PORT + WS_LOAD_USER,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        try {
                            final User loadedUser = JSONConverter.responseToUser(response);
                            if (loadedUser.getId() != -1) {
                                Application.getInstance().setUser(loadedUser);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Game.showMessage(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                Log.e(WebServiceUtils.class.getName(), "volleyError");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "VolleyError", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        );
        getRequestQueue(activity).add(request);
    }


    public static void registerUser(final String name, final Activity activity) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        final String deviceId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        params.put("deviceId", deviceId);
        final JSONObject jsonObject = new JSONObject(params);
        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                SERVER + ":" + PORT + WS_REGISTER_USER,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        try {
                            final int responseCode = response.getInt("responseCode");
                            switch (responseCode) {
                                case 0://ok
                                    final int userId = response.getInt("userId");
                                    final User user = new User();
                                    user.setName(name);
                                    user.setId(userId);
                                    Application.getInstance().setUser(user);
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity, "Usuário registrado com sucesso", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                                case 1://error ocurred
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(activity, "Usuário já registrado", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Game.showMessage(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

            public void onErrorResponse(VolleyError error) {
                Log.e(WebServiceUtils.class.getName(), "volleyError");
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "VolleyError", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        );
        getRequestQueue(activity).add(request);
    }
    */
}
