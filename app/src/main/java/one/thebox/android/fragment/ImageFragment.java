package one.thebox.android.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import one.thebox.android.R;
import ooo.oxo.library.widget.TouchImageView;

/**
 * Created by nbansal2211 on 23/12/16.
 */

public class ImageFragment extends BaseFragment {
    private TouchImageView fullImage;
    private String image;

    public static ImageFragment getInstance(String image) {
        ImageFragment imageFragment = new ImageFragment();
        imageFragment.image = image;
        return imageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootLayout = inflater.inflate(R.layout.fragment_image, container, false);
        fullImage = (TouchImageView) rootLayout.findViewById(R.id.iv_full_image);
        Picasso.with(getActivity()).load(image).into(fullImage);
        return rootLayout;
    }


}
