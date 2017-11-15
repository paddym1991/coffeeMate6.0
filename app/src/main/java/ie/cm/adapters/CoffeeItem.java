package ie.cm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import ie.cm.R;
import ie.cm.models.Coffee;

public class CoffeeItem {
	View view;

	public CoffeeItem(Context context, ViewGroup parent,
					  OnClickListener deleteListener, Coffee coffee)
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.coffeerow, parent, false);
		view.setTag(coffee._id);

		updateControls(coffee);

		ImageView imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
		imgDelete.setTag(coffee);
		imgDelete.setOnClickListener(deleteListener);
	}

	private void updateControls(Coffee coffee) {
		((TextView) view.findViewById(R.id.rowCoffeeName)).setText(coffee.name);
		((TextView) view.findViewById(R.id.rowCoffeeShop)).setText(coffee.shop);
		((TextView) view.findViewById(R.id.rowRating)).setText(coffee.rating + " *");
		((TextView) view.findViewById(R.id.rowPrice)).setText("€" +
				new DecimalFormat("0.00").format(coffee.price));

		ImageView imgIcon = (ImageView) view.findViewById(R.id.RowImage);

		if (coffee.favourite == true)
			imgIcon.setImageResource(R.drawable.ic_favourite_on);
		else
			imgIcon.setImageResource(R.drawable.ic_favourite_off);
	}
}
