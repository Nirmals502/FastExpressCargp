package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import mobile.fastexpresscargp.R;


/**
 * Created by Nirmal on 6/24/2016.
 */
public class History_adapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> subscriptionarray = new ArrayList<HashMap<String, String>>();
    LayoutInflater inflater;
    Context context;


    public History_adapter(Context context, ArrayList<HashMap<String, String>> Array_subscription) {
        this.subscriptionarray = Array_subscription;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return subscriptionarray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_recycleview_listing, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        try {
            //if(Str_profile_image!=null) {
            //img_loader.DisplayImage(fl,vh.Img_profilepic);


        } catch (IllegalArgumentException e) {
            e.printStackTrace();

        }

        mViewHolder.Dockect.setText(("Dockect No: " + subscriptionarray.get(position).get("dockect")));
        mViewHolder.Remarks.setText(("Remarks: " + subscriptionarray.get(position).get("remarks")));
        mViewHolder.Created.setText(("Created at: " + subscriptionarray.get(position).get("time")));
        Picasso.with(context)
                .load(subscriptionarray.get(position).get("image"))
                // optional
                // optional
                // optional
                .into(mViewHolder.Sign_image);

        return convertView;
    }

    private class MyViewHolder {
        TextView Dockect, Remarks, Created;
        ImageView Sign_image;

        public MyViewHolder(View item) {
            Dockect = (TextView) item.findViewById(R.id.Txt_docket);
            Remarks = (TextView) item.findViewById(R.id.textView2);
            Created = (TextView) item.findViewById(R.id.textView27);
            Sign_image = (ImageView) item.findViewById(R.id.imageView9);
//            tvDesc = (TextView) item.findViewById(R.id.tvDesc);

        }
    }
}