package br.com.vsgdev.promobeer.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.vsgdev.cursomobilelibrary.utils.ImageUtils;
import br.com.vsgdev.promobeer.R;
import br.com.vsgdev.promobeer.activities.MainActivity;

public class CommentDialog extends AppCompatDialogFragment {
    private Handler commentHandler;
    private LayoutInflater inflater;
    private View view;

    public void setCommentHandler(Handler commentHandler) {
        this.commentHandler = commentHandler;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_comment, null);

        if (MainActivity.user != null) {
            ((ImageView) view.findViewById(R.id.iv_dialog_comment_user_picture)).setImageBitmap(ImageUtils.getRoundedCornerBitmap(MainActivity.user.getPicture(), 100));
            ((TextView) view.findViewById(R.id.tv_dialog_comment_user_name)).setText(MainActivity.user.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String content = ((EditText) view.findViewById(R.id.et_dialog_comment_content)).getText().toString();
                        if (!content.isEmpty()) {
                            final Message message = Message.obtain();
                            final Bundle bundle = new Bundle();
                            bundle.putString("content", content);
                            message.setData(bundle);
                            commentHandler.sendMessage(message);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }
}
