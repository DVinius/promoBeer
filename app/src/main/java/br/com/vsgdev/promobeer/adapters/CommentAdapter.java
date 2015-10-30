package br.com.vsgdev.promobeer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.vsgdev.promobeer.R;
import br.com.vsgdev.promobeer.models.Comment;


public class CommentAdapter extends BaseAdapter {
    private List<Comment> list;
    private Context context;
    private LayoutInflater mLayoutInflater = null;

    public CommentAdapter(List<Comment> list, Context context) {
        this.list = list;
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Comment getLastItem() {
        if (!getList().isEmpty()) {
            return getList().get(getList().size() - 1);
        }
        return null;
    }

    public List<Comment> getList() {
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
    public Comment getItem(int position) {
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
            view = mLayoutInflater.inflate(R.layout.list_view_comment_adapter, parent, false);
            holder = new ViewHolder();
            holder.ivUserPicture = (ImageView) view.findViewById(R.id.iv_comment_user_picture);
            holder.tvUserName = (TextView) view.findViewById(R.id.tv_comment_user_name);
            holder.tvComment = (TextView) view.findViewById(R.id.tv_comment_content);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        final Comment comment = getItem(position);
        if (comment.getUser().getPicture() != null){
            Log.e("CommentAdapter", "show user picture");
        }
        holder.tvUserName.setText(comment.getUser().getName());
        holder.tvComment.setText(comment.getContent());

        return view;
    }

    private class ViewHolder {
        public ImageView ivUserPicture;
        public TextView tvUserName;
        public TextView tvComment;
    }
}
