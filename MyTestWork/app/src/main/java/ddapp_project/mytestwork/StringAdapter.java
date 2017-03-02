package ddapp_project.mytestwork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class StringAdapter extends ArrayAdapter<ArrayList<String>> {
    private static ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
    LayoutInflater mInflater;

    public StringAdapter(Context context) {
        super(context, R.layout.list_element, new ArrayList[0]);
}

    public void add(Collection<ArrayList<String>> data) {
        this.data.addAll(data);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public int getPosition(ArrayList<String> item) {
        return data.indexOf(item);
    }

    @Override
    public ArrayList<String> getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArrayList<String> data = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_element, null);
            }

             ((TextView) convertView.findViewById(R.id.textFirstName))
                     .setText(data.get(0));
             ((TextView) convertView.findViewById(R.id.textLastName))
                     .setText(data.get(1));
             ((TextView) convertView.findViewById(R.id.textBirthday))
                     .setText(data.get(2));
             ((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.mipmap.ic_launcher);

        return convertView;
    }

}
