package com.giorgio.ParseContactsFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by giorgiofellipe on 12/08/14.
 */
public class ContactsAdapter extends ArrayAdapter<ParseContact> {
    private Context mContext;
    private List<ParseContact> mContact;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    private Filter contactFilter;

    public ContactsAdapter(Context context, List<ParseContact> objects) {
        super(context, R.layout.include_list_item_contact, objects);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(1000))
                .showImageForEmptyUri(R.drawable.royalblue)
                .showImageOnFail(R.drawable.royalblue)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        this.mContext = context;
        this.mContact = objects;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.include_list_item_contact, null);
        }

        ParseContact contact = mContact.get(position);
        TextView nameView = (TextView) convertView.findViewById(R.id.lbl_contact_name);
        nameView.setText(contact.getName());

        final ImageView photoView = (ImageView) convertView.findViewById(R.id.image);
        photoView.setVisibility(View.INVISIBLE);

        ImageLoadingListener listener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String arg0, View arg1) {
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                photoView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String arg0, View view, FailReason arg2) {
            }

        };
        if (getItem(position).getPhotoFile() != null && getItem(position).getPhotoFile().getUrl().length() > 0) {
            imageLoader.displayImage(getItem(position).getPhotoFile().getUrl(), photoView, options, listener);
        }
        final View clickedView = convertView;
        // set the on click listener for each of the items
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Clicked "+mContact.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return mContact.size();
    }
}