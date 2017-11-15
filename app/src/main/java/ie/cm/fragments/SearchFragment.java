package ie.cm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import ie.cm.R;
import ie.cm.adapters.CoffeeFilter;

public class SearchFragment extends CoffeeFragment
		implements AdapterView.OnItemSelectedListener, TextWatcher {

	String selected;

	public SearchFragment() {
		// Required empty public constructor
	}

	public static SearchFragment newInstance() {
		SearchFragment fragment = new SearchFragment();
		return fragment;
	}

	@Override
	public void onAttach(Context c) {
		super.onAttach(c);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		super.onCreateView(inflater,container,savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_search, container, false);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(getActivity(), R.array.coffeeTypes,
						android.R.layout.simple_spinner_item);

		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = ((Spinner) v.findViewById(R.id.searchCoffeeTypeSpinner));
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(this);

		EditText nameText = (EditText)v.findViewById(R.id.searchCoffeeNameEditText);
		nameText.addTextChangedListener(this);

		listView = (ListView) v.findViewById(R.id.coffeeList); //Bind to the list on our Search layout

		setListView(listView);

		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.coffee_swipe_refresh_layout);
		setSwipeRefreshLayout();

		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		titleBar = (TextView)getActivity().findViewById(R.id.recentAddedBarTextView);
		titleBar.setText(R.string.searchCoffeesLbl);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		selected = parent.getItemAtPosition(position).toString();
		checkSelected(selected);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {}

	@Override
	public void deleteCoffees(ActionMode actionMode) {
		super.deleteCoffees(actionMode);
		checkSelected(selected);
	}

	private void checkSelected(String selected)
	{

		if(coffeeFilter == null)
			coffeeFilter = new CoffeeFilter(app.coffeeList,"all",listAdapter);

		if (selected != null) {
			if (selected.equals("All Types")) {
				coffeeFilter.setFilter("all");
			} else if (selected.equals("Favourites")) {
				coffeeFilter.setFilter("favourites");
			}

			String filterText = ((EditText)getActivity()
					.findViewById(R.id.searchCoffeeNameEditText))
					.getText().toString();

			if(filterText.length() > 0)
				coffeeFilter.filter(filterText);
			else
				coffeeFilter.filter("");
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		coffeeFilter.filter(s);
	}

	@Override
	public void afterTextChanged(Editable s) {}
}