package br.com.vsgdev.promobeer.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.vsgdev.cursomobilelibrary.utils.DateTimeUtils;
import br.com.vsgdev.promobeer.R;
import br.com.vsgdev.promobeer.adapters.CommentAdapter;
import br.com.vsgdev.promobeer.adapters.ItemAdapter;
import br.com.vsgdev.promobeer.dialogs.CommentDialog;
import br.com.vsgdev.promobeer.models.Comment;
import br.com.vsgdev.promobeer.models.Item;
import br.com.vsgdev.promobeer.utils.WebServiceUtils;

public class ItemDetailActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {
    private static Item item;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_item_detail);
        setContentView(R.layout.activity_item_detail_paralax);
        item = MainActivity.adapter.getItem(getIntent().getExtras().getInt("position"));

        if (MainActivity.user == null) {
            MainActivity.getCurrentUser(this);
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(item.getName());
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));

        final ImageView image = (ImageView) findViewById(R.id.iv_item_detail_picture_material);
        image.setImageBitmap(item.getPicture());

        ((TextView) findViewById(R.id.tv_item_detail_price_place)).setText(ItemAdapter.getPriceAndPlace(item));
        ((TextView) findViewById(R.id.tv_item_detail_created_at)).setText("Em: " + DateTimeUtils.calendar2PortugueseDateFormat(item.getCreatedAt()));
        if (item.getObs() != null && !item.getObs().isEmpty() && !item.getObs().equals("null")) {
            ((TextView) findViewById(R.id.tv_item_detail_note)).setText(item.getObs());
        } else {
            ((TextView) findViewById(R.id.tv_item_detail_note)).setText("");
        }

        (findViewById(R.id.fab_item_detail_comment)).setOnClickListener(this);

        final List<Comment> list = new ArrayList<>();
        adapter = new CommentAdapter(list, this);
        //((ListView) findViewById(R.id.lv_item_detail)).setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("DetailActivity", "load previous comments");
    }

    @Override
    public void onClick(View v) {
        if (findViewById(R.id.fab_item_detail_comment).isPressed()) {
            final CommentDialog dialog = new CommentDialog();
            dialog.setCommentHandler(new CommentHandler(this));
            dialog.show(getSupportFragmentManager(), "comment_dialog");
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("DetailActivity", "notify comment post error");
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.d("DetalActivtity", "comment post successfull. update comments via gcm");
    }

    private static class CommentHandler extends Handler {
        private ItemDetailActivity activity;

        public CommentHandler(final ItemDetailActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(final Message msg) {
            final String content = msg.getData().getString("content");
            final Comment comment = new Comment(item, content);
            comment.setUser(MainActivity.user);
            WebServiceUtils.sendComment(comment, activity, activity);
        }
    }
}