package br.com.vsgdev.promobeer.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import br.com.vsgdev.cursomobilelibrary.utils.ImageUtils;
import br.com.vsgdev.promobeer.R;
import br.com.vsgdev.promobeer.models.Item;
import br.com.vsgdev.promobeer.models.ItemType;
import br.com.vsgdev.promobeer.models.Place;
import br.com.vsgdev.promobeer.utils.WebServiceUtils;

public class NewItemActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {
    private static final int REQUEST_CODE_GET_IMAGE_FILE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_IMAGE_CROP = 3;
    private Uri pictureUri;
    private Spinner spnItemType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        findViewById(R.id.btn_new_item_confirm).setOnClickListener(this);
        ((TextInputLayout) findViewById(R.id.til_new_item_name)).setErrorEnabled(true);
        ((TextInputLayout) findViewById(R.id.til_new_item_price)).setErrorEnabled(true);
        ((TextInputLayout) findViewById(R.id.til_new_item_place)).setErrorEnabled(true);

        spnItemType = (Spinner) findViewById(R.id.spn_new_item_type);
        spnItemType.setAdapter((new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ItemType.values())));

        findViewById(R.id.iv_new_item_picture).setOnClickListener(this);
    }

    public void pickImage() {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, REQUEST_CODE_GET_IMAGE_FILE);
    }

    /**
     * Prepara recursos para captura de foto utilizando a camera do dispositivo.
     */
    private void prepareCamera() {
        //Intent solicitando utilização da camera
        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Verifica se existe atividade para tal finalidade
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //cria o diretorio onde serao armazenadas as imagens
            //final File dir = new File(getCacheDir(), "internal");
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
            /*
            final boolean makeDir = dir.mkdirs();
            if (makeDir) {
                Log.i(MainActivity.class.getName(), "Directory created");
            } else {
                Log.i(MainActivity.class.getName(), "Directory already exists");
            }
            */
            //cria arquivo temporario para salvar foto
            File imageFile = null;
            if (dir.exists()) {
                try {
                    //indica prefixo, sufixo (extensao) e diretorio (dir) do arquivo temporario
                    imageFile = File.createTempFile("IMG_", ".jpg", dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (imageFile != null) {
                //converte o local (path) em Uri (necessario para camera salvar/substituir o arquivo
                pictureUri = Uri.fromFile(imageFile);
                //informa Uri
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
                //inicia Activity da cam
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "cropImage", null);
        return Uri.parse(path);
    }

    public void cropImage(final Uri imageUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            //final Uri fileUri = getImageUri(this, image);
            cropIntent.setDataAndType(imageUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 480);
            cropIntent.putExtra("outputY", 480);
            cropIntent.putExtra("return-data", true);
            //cropIntent.putExtra("output", imageUri);
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public void cropImage(final Bitmap image) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            final Uri fileUri = getImageUri(this, image);
            cropIntent.setDataAndType(fileUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 480);
            cropIntent.putExtra("outputY", 480);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (findViewById(R.id.btn_new_item_confirm).isPressed()) {
            final String itemName = ((EditText) findViewById(R.id.et_new_item_name)).getText().toString();
            final String itemPrice = ((EditText) findViewById(R.id.et_new_item_price)).getText().toString();
            final String placeName = ((EditText) findViewById(R.id.et_new_item_place)).getText().toString();
            final String note = ((EditText) findViewById(R.id.et_new_item_obs)).getText().toString();

            if (itemName.isEmpty()) {
                ((TextInputLayout) findViewById(R.id.til_new_item_name)).setError("Não esqueça o nome do produto!");
                return;
            } else {
                ((TextInputLayout) findViewById(R.id.til_new_item_name)).setError(null);
            }
            if (itemPrice.isEmpty()) {
                ((TextInputLayout) findViewById(R.id.til_new_item_price)).setError("Sem informar o preço fica difícil!");
                return;
            } else {
                ((TextInputLayout) findViewById(R.id.til_new_item_price)).setError(null);
            }
            //check place name
            if (placeName.isEmpty()) {
                ((TextInputLayout) findViewById(R.id.til_new_item_place)).setError("O local é importantíssimo!");
                return;
            } else {
                ((TextInputLayout) findViewById(R.id.til_new_item_place)).setError(null);
            }

            final Item item = new Item(itemName, Double.parseDouble(itemPrice));
            item.setPlace(new Place(placeName));
            item.setObs(note);
            final ImageView ivItemPic = (ImageView) findViewById(R.id.iv_new_item_picture);
            item.setPicture(((BitmapDrawable) ivItemPic.getDrawable()).getBitmap());
            WebServiceUtils.registerItem(item, this, this, this);
            return;
        }
        if (findViewById(R.id.iv_new_item_picture).isPressed()) {
            prepareCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            switch (resultCode) {
                //o usuario cancelou a acao (pressionou 'back button', nao tirou a foto, em nosso exemplo)
                case RESULT_CANCELED:
                    File file = new File(pictureUri.getPath());
                    file.delete();
                    break;
                //O usuario tirou uma foto.
                case RESULT_OK:
                    //final ImageView imageView = (ImageView) findViewById(R.id.iv_photo_taken);
                    Bitmap photoTaken = BitmapFactory.decodeFile(pictureUri.getPath());

                    photoTaken = scaleBitmap(photoTaken);

                    final ExifInterface ei;
                    try {
                        ei = new ExifInterface(pictureUri.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                photoTaken = ImageUtils.rotateImage(photoTaken, 90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                photoTaken = ImageUtils.rotateImage(photoTaken, 180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                photoTaken = ImageUtils.rotateImage(photoTaken, 270);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cropImage(photoTaken);
                    //((ImageView) findViewById(R.id.iv_new_item_picture)).setImageBitmap(photoTaken);
                    break;
            }
            return;
        }
        if (requestCode == REQUEST_CODE_GET_IMAGE_FILE) {
            try {
                final Uri selectedImage = data.getData();

                /*
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                //bitmap = scaleBitmap(bitmap);
                */

                cropImage(selectedImage);
                //((ImageView) findViewById(R.id.iv_new_item_picture)).setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ((ImageView) findViewById(R.id.iv_new_item_picture)).setImageBitmap(imageBitmap);
        }
    }

    private Bitmap scaleBitmap(final Bitmap bitmap) {
        int newWidth;
        int newHeight;
        float scale;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            if (bitmap.getHeight() == 640) {
                return bitmap;
            }
            scale = bitmap.getHeight() / 640;
        } else {
            if (bitmap.getWidth() == 640) {
                return bitmap;
            }
            scale = bitmap.getWidth() / 640;
        }
        newWidth = (int) (bitmap.getWidth() / scale);
        newHeight = (int) (bitmap.getHeight() / scale);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("new item", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(this, "Item registrado com sucesso!", Toast.LENGTH_LONG).show();
    }
}
