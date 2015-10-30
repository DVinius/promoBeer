package br.com.vsgdev.promobeer.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.vsgdev.cursomobilelibrary.utils.DateTimeUtils;
import br.com.vsgdev.cursomobilelibrary.utils.ImageUtils;
import br.com.vsgdev.promobeer.R;
import br.com.vsgdev.promobeer.models.Item;


public class ItemAdapter extends BaseAdapter {
    private List<Item> list;
    private Context context;
    private LayoutInflater mLayoutInflater = null;

    public ItemAdapter(List<Item> list, Context context) {
        this.list = list;
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Item getLastItem() {
        if (!getList().isEmpty()) {
            return getList().get(getList().size() - 1);
        }
        return null;
    }

    public List<Item> getList() {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        return this.list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Item getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.list_view_item_adapter, parent, false);
            holder = new ViewHolder();
            holder.ivItemPicture = (ImageView) view.findViewById(R.id.iv_item_list_picture);
            holder.tvItemName = (TextView) view.findViewById(R.id.tv_item_list_name);
            holder.tvItemPrice = (TextView) view.findViewById(R.id.tv_item_list_price_place);
            holder.tvItemCreatedAt = (TextView) view.findViewById(R.id.tv_item_list_created_at);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        final Item item = getItem(position);
        holder.tvItemName.setText(item.getName());

        holder.tvItemPrice.setText(getPriceAndPlace(item));
        if (item.getCreatedAt() != null){
            holder.tvItemCreatedAt.setText("Em: "+ DateTimeUtils.calendar2PortugueseDateFormat(item.getCreatedAt()));
        }
        if (item.getPicture() != null){
            holder.ivItemPicture.setImageBitmap(ImageUtils.getRoundedCornerBitmap(item.getPicture(), 25));
        } else {
            holder.ivItemPicture.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        }

        return view;
    }

    public static String getPriceAndPlace(final Item item){
        StringBuilder builder = new StringBuilder();
        if (item.getPrice() != null){
            builder.append("R$ ");
            builder.append(String.format("%.02f", item.getPrice()));
        }
        if (item.getPlace() != null){
            builder.append(" - " + item.getPlace().getName());
        }
        return builder.toString();
    }

    private class ViewHolder {
        public ImageView ivItemPicture;
        public TextView tvItemName;
        public TextView tvItemPrice;
        public TextView tvItemCreatedAt;
    }
}
