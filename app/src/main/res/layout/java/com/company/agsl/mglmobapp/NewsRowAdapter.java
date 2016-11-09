package layout.java.com.company.agsl.mglmobapp;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsRowAdapter extends ArrayAdapter<Item> {

	private Activity activity;
	private List<Item> items;
	private Item objBean;
	private int row;

	public NewsRowAdapter(Activity act, int resource, List<Item> arrayList) {
		super(act, resource, arrayList);
		this.activity = act;
		this.row = resource;
		this.items = arrayList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(row, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {

			holder = (ViewHolder) view.getTag();

		}

		if ((items == null) || ((position + 1) > items.size()))
			return view;

		objBean = items.get(position);

		holder.tvName = (TextView) view.findViewById(R.id.tvname);
		holder.tvCity = (TextView) view.findViewById(R.id.tvcity);
		holder.tvBDate = (TextView) view.findViewById(R.id.tvbdate);
		holder.tvGender = (TextView) view.findViewById(R.id.tvmanager);
		holder.tvAge = (TextView) view.findViewById(R.id.tvage);

		if (holder.tvName != null && null != objBean.getName()
				&& objBean.getName().trim().length() > 0) {
			//holder.tvName.setText(Html.fromHtml("Name:"+objBean.getName()));
			holder.tvName.setText(Html.fromHtml(objBean.getName()));
		}
		if (holder.tvCity != null && null != objBean.getDate()
				&& objBean.getDate().trim().length() > 0) {
			holder.tvCity.setText(Html.fromHtml(objBean.getDate() + "/" + objBean.getTime()));
		}
		if (holder.tvBDate != null && objBean.getStatus()!=0) {
           /* if(objBean.getStatus()==7){holder.tvBDate.setText(Html.fromHtml("Status : <b>Pending</b>"));}
            else if(objBean.getStatus()==8){holder.tvBDate.setText(Html.fromHtml("Status : <b>Approved</b>"));}
            else{holder.tvBDate.setText(Html.fromHtml("Status : <b>Rejected</b>"));}*/
            if(objBean.getStatus()==7){holder.tvBDate.setText(Html.fromHtml("<b>Pending</b>"));holder.tvBDate.setBackgroundResource(R.drawable.mgl_header_blue);}
            else if(objBean.getStatus()==8){holder.tvBDate.setText(Html.fromHtml("<b>Approved</b>"));holder.tvBDate.setBackgroundResource(R.drawable.mgl_green);}
            else{holder.tvBDate.setText(Html.fromHtml("<b>Rejected</b>"));holder.tvBDate.setBackgroundResource(R.drawable.red);}
			//holder.tvBDate.setText(Html.fromHtml(objBean.getTime()));
		}
		if (holder.tvGender != null && null != objBean.getManagerName()
				&& objBean.getManagerName().trim().length() > 0) {
			holder.tvGender.setText(Html.fromHtml("Manager:" + objBean.getManagerName()));
		}

        if (holder.tvAge != null && objBean.getInOut()!=0) {
            if(objBean.getInOut()==1){holder.tvAge.setText("In");holder.tvAge.setBackgroundResource(R.drawable.mgl_green);}
            else if(objBean.getInOut()==2){holder.tvAge.setText("Out");holder.tvAge.setBackgroundResource(R.drawable.red);}

        }
		/*if (holder.tvAge != null && objBean.get() > 0) {
			holder.tvAge.setText(Html.fromHtml("" + objBean.getAge()));
		}*/

		return view;
	}

	public class ViewHolder {
		public TextView tvName, tvCity, tvBDate, tvGender, tvAge;
	}
}