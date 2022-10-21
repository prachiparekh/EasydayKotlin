package com.passiondroid.imageeditorlib;

import static com.passiondroid.imageeditorlib.ImageEditor.EXTRA_IMAGE_PATH;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.passiondroid.imageeditorlib.utils.FragmentUtil;

public class ImageEditActivity extends BaseImageEditActivity
        implements PhotoEditorFragment.OnFragmentInteractionListener {
    private Rect cropRect;

    //private View touchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        String imagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
        if (imagePath != null) {
            FragmentUtil.addFragment(this, R.id.fragment_container,
                    PhotoEditorFragment.newInstance(imagePath));
        }
    }

    @Override
    public void onCropClicked(Bitmap bitmap) {

    }

    @Override
    public void onDoneClicked(String imagePath) {

        Intent intent = new Intent();
        intent.putExtra(ImageEditor.EXTRA_EDITED_PATH, imagePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
