package org.akvo.caddisfly.sensor.colorimetry.strip.colorimetry_strip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.akvo.caddisfly.R;

import java.util.List;

/**
 * Created by linda on 9/5/15
 */
class ColorimetryStripAdapter extends ArrayAdapter<String> {

    private final List<String> brandNames;
    private final int resource;
    private final Context context;
    private final StripTest stripTest;

    @SuppressWarnings("SameParameterValue")
    public ColorimetryStripAdapter(Context context, int resource, List<String> brandNames) {
        super(context, resource);

        this.context = context;
        this.resource = resource;
        this.brandNames = brandNames;
        this.stripTest = new StripTest();
    }

    @Override
    public int getCount() {
        return brandNames.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (brandNames != null) {
            if (stripTest != null) {
                StripTest.Brand brand = stripTest.getBrand(brandNames.get(position));

                if (brand != null) {

                    List<StripTest.Brand.Patch> patches = brand.getPatches();

                    if (patches != null && patches.size() > 0) {
                        String subtext = "";
                        for (int i = 0; i < patches.size(); i++) {
                            subtext += patches.get(i).getDesc() + ", ";
                        }
                        int indexLastSep = subtext.lastIndexOf(",");
                        subtext = subtext.substring(0, indexLastSep);
                        holder.textView.setText(brand.getName());

                        holder.subtextView.setText(subtext);
                    }
                }
            } else holder.textView.setText(brandNames.get(position));
        }
        return view;

    }

    private static class ViewHolder {

        private final TextView textView;
        private final TextView subtextView;

        public ViewHolder(View v) {

            textView = (TextView) v.findViewById(R.id.adapter_instructionsTextView);
            subtextView = (TextView) v.findViewById(R.id.adapter_instructionsSubTextView);

        }
    }
}