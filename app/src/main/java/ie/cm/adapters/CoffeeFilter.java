package ie.cm.adapters;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import ie.cm.models.Coffee;

public class CoffeeFilter extends Filter {
	private List<Coffee> 		originalCoffeeList;
	private String 				filterText;
	private CoffeeListAdapter 	adapter;

	public CoffeeFilter(List<Coffee> originalCoffeeList, String filterText,
						CoffeeListAdapter adapter) {
		super();
		this.originalCoffeeList = originalCoffeeList;
		this.filterText = filterText;
		this.adapter = adapter;
	}

	public void setFilter(String filterText) {
		this.filterText = filterText;
	}

	@Override
	protected FilterResults performFiltering(CharSequence prefix) {
		FilterResults results = new FilterResults();

		if (originalCoffeeList == null) {
			originalCoffeeList = new ArrayList<Coffee>();
		}
		if (prefix == null || prefix.length() == 0) {
			List<Coffee> newCoffees = new ArrayList<Coffee>();
			if (filterText.equals("all")) {
				results.values = originalCoffeeList;
				results.count = originalCoffeeList.size();
			} else {
				if (filterText.equals("favourites")) {
					for (Coffee c : originalCoffeeList)
						if (c.favourite)
							newCoffees.add(c);
				}
				results.values = newCoffees;
				results.count = newCoffees.size();
			}
		} else {
			String prefixString = prefix.toString().toLowerCase();
			final ArrayList<Coffee> newCoffees = new ArrayList<Coffee>();

			for (Coffee c : originalCoffeeList) {
				final String itemName = c.name.toLowerCase();
				if (itemName.contains(prefixString)) {
					if (filterText.equals("all")) {
						newCoffees.add(c);
					} else if (c.favourite) {
						newCoffees.add(c);
					}}}
			results.values = newCoffees;
			results.count = newCoffees.size();
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence prefix, FilterResults results) {

		adapter.coffeeList = (ArrayList<Coffee>) results.values;

		if (results.count >= 0)
			adapter.notifyDataSetChanged();
		else {
			adapter.notifyDataSetInvalidated();
			adapter.coffeeList = originalCoffeeList;
		}
	}
}
