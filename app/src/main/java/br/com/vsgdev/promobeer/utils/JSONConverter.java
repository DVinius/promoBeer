package br.com.vsgdev.promobeer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import br.com.vsgdev.cursomobilelibrary.utils.DateTimeUtils;
import br.com.vsgdev.cursomobilelibrary.utils.ImageUtils;
import br.com.vsgdev.promobeer.models.Comment;
import br.com.vsgdev.promobeer.models.Item;
import br.com.vsgdev.promobeer.models.ItemType;
import br.com.vsgdev.promobeer.models.Place;
import br.com.vsgdev.promobeer.models.User;


public class JSONConverter {

    public static JSONObject item2Json(final Item item) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", item.getId());
        params.put("name", item.getName());
        params.put("price", item.getPrice());
        params.put("obs", item.getObs());
        if (item.getCreatedAt() != null) {
            params.put("createdAt", DateTimeUtils.convertCalendar(item.getCreatedAt()));
        }
        if (item.getPlace() != null) {
            params.put("place", place2Json(item.getPlace()));
        }
        if (item.getPicture() != null) {
            final String encoded = ImageUtils.convertBitmapToString64(item.getPicture());
            params.put("image", encoded);
        }
        if (item.getItemType() != null) {
            params.put("type", item.getItemType().getId());
        }
        final JSONObject jsonObject = new JSONObject(params);
        return jsonObject;
    }

    public static JSONObject place2Json(final Place place) {
        final Map<String, Object> params = new HashMap<>();
        if (place.getId() != null) {
            params.put("id", place.getId());
        }
        params.put("name", place.getName());
        if (place.getLocation() != null) {
            params.put("latitude", place.getLocation().getLatitude());
            params.put("longitude", place.getLocation().getLongitude());
        }
        final JSONObject jsonObject = new JSONObject(params);
        return jsonObject;
    }

    public static Item response2Item(final JSONObject response) {
        try {
            final int id = response.getInt("id");
            if (id != 0) {
                final Item item = new Item(id);
                final String name = response.getString("name");
                item.setName(name);
                final double price = response.getDouble("price");
                item.setPrice(price);
                if (response.has("createdAt")) {
                    item.setCreatedAt(DateTimeUtils.strToCalendar(response.getString("createdAt")));
                }
                if (response.has("place")) {
                    item.setPlace(response2Place(response.getJSONObject("place")));
                }
                if (response.has("image")) {
                    //convert image data to bitmap
                    final String encodedImage = response.getString("image");

                    final byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    item.setPicture(bitmap);
                }
                if (response.has("type")) {
                    item.setItemType(ItemType.getTypeById(response.getInt("type")));
                }
                if (response.has("note")){
                    item.setObs(response.getString("note"));
                }

                return item;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Place response2Place(final JSONObject response) {
        final Place place = new Place();
        try {
            if (response.has("name")) {
                place.setName(response.getString("name"));
            }
            if (response.has("id")) {
                place.setId(response.getInt("id"));
            }
            place.setLocation(new Location(""));
            if (response.has("latitude")) {
                place.getLocation().setLatitude(response.getDouble("latitude"));
            }
            if (response.has("longitude")) {
                place.getLocation().setLongitude(response.getDouble("longitude"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return place;
    }

    public static String getReadableCurrencyValue(final double currencyValue) {
        return String.format("%.2f", currencyValue);
    }

    public static JSONObject comment2Json(final Comment comment) {
        final Map<String, Object> params = new HashMap<>();

        params.put("id", comment.getId());
        params.put("content", comment.getContent());
        params.put("itemId", comment.getItem().getId());
        //params.put("date", DateTimeUtils.convertCalendar(comment.getDate()));
        params.put("date", comment.getDate().getTime().getTime());
        params.put("userFid", comment.getUser().getFid());

        final JSONObject jsonObject = new JSONObject(params);
        return jsonObject;
    }

    public static JSONObject user2Json(final User user, final boolean convertImage) {
        final Map<String, Object> params = new HashMap<>();

        params.put("fid", user.getFid());
        params.put("name", user.getName());
        if (convertImage) {
            Log.d("JSONConverter", "converter imagem do usuario");
        }

        final JSONObject jsonObject = new JSONObject(params);
        return jsonObject;
    }
}