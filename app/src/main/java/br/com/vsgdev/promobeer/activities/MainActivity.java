package br.com.vsgdev.promobeer.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.vsgdev.promobeer.R;
import br.com.vsgdev.promobeer.adapters.ItemAdapter;
import br.com.vsgdev.promobeer.models.Item;
import br.com.vsgdev.promobeer.models.User;
import br.com.vsgdev.promobeer.utils.JSONConverter;
import br.com.vsgdev.promobeer.utils.WebServiceUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {
    private ListView lvItems;
    public static ItemAdapter adapter;
    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        final Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));


        findViewById(R.id.fab_main_new_item).setOnClickListener(this);
        if (lvItems == null) {
            lvItems = (ListView) findViewById(R.id.lv_main_items);
            final List<Item> items = new ArrayList<>();
            adapter = new ItemAdapter(items, this);
            lvItems.setAdapter(adapter);
            lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Intent intent = new Intent(getApplicationContext(), ItemDetailActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
        }
        if (user == null) {
            getCurrentUser(this);
        }
    }

    public static void getCurrentUser(final Context context) {

        /* make the API call */
        final Bundle params = new Bundle();
        params.putString("fields", "picture,name");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("main", response.toString());
                        final User user = new User();
                        try {
                            user.setFid(response.getJSONObject().getLong("id"));
                            user.setName(response.getJSONObject().getString("name"));


                            final Uri uri = Profile
                                    .getCurrentProfile()
                                    .getProfilePictureUri(200, 200);


                            //final String urlStr = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                            final String urlStr = uri.toString();
                            final URL url = new URL(urlStr);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoInput(true);
                                        connection.connect();
                                        InputStream input = connection.getInputStream();
                                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                        user.setPicture(myBitmap);
                                        MainActivity.user = user;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("download user pic", e.toString());
                                    }

                                }
                            }).start();
                            MainActivity.user = user;
                            WebServiceUtils.registerUser(MainActivity.user, context);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("download user pic", e.toString());
                        }
                    }
                }
        ).executeAsync();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    @Override
    public void onClick(View v) {
        if (findViewById(R.id.fab_main_new_item).isPressed()) {
            startActivity(new Intent(this, NewItemActivity.class));
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    private void loadItems() {
        WebServiceUtils.loadItems(this, adapter, this, this);
    }

    @Override
    public void onResponse(JSONObject response) {
        final Item item = JSONConverter.response2Item(response);
        if (item != null && item.getId() != 0) {
            adapter.getList().add(item);
            adapter.notifyDataSetChanged();
            loadItems();
        }
    }
}
