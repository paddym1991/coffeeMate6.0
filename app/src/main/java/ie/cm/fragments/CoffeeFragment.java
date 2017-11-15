package ie.cm.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ie.cm.R;

import ie.cm.adapters.CoffeeFilter;
import ie.cm.adapters.CoffeeListAdapter;
import ie.cm.api.CoffeeApi;
import ie.cm.api.VolleyListener;
import ie.cm.main.CoffeeMateApp;
import ie.cm.models.Coffee;

public class CoffeeFragment  extends Fragment implements AdapterView.OnItemClickListener,
        View.OnClickListener,
        AbsListView.MultiChoiceModeListener,
        VolleyListener
{
  protected static  CoffeeListAdapter 	listAdapter;
  protected         ListView 			listView;
  protected         CoffeeFilter        coffeeFilter;
  public            boolean             favourites = false;
  protected         TextView            titleBar;
  protected         SwipeRefreshLayout  mSwipeRefreshLayout;

  public CoffeeMateApp app = CoffeeMateApp.getInstance();

  public CoffeeFragment() {
    // Required empty public constructor
  }

  public static CoffeeFragment newInstance() {
    CoffeeFragment fragment = new CoffeeFragment();
    return fragment;
  }
  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);
    CoffeeApi.attachListener(this);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View v = null;
    v = inflater.inflate(R.layout.fragment_home, container, false);
    listView = (ListView) v.findViewById(R.id.coffeeList);

    mSwipeRefreshLayout =   (SwipeRefreshLayout) v.findViewById(R.id.coffee_swipe_refresh_layout);
    setSwipeRefreshLayout();

    CoffeeApi.getAll("/coffees/" + app.googleToken, mSwipeRefreshLayout);

    return v;
  }

  protected void setSwipeRefreshLayout()
  {
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        CoffeeApi.getAll("/coffees/" + app.googleToken, mSwipeRefreshLayout);
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();

    CoffeeApi.attachListener(this);
    CoffeeApi.getAll("/coffees/" + app.googleToken, mSwipeRefreshLayout);
  }

  @Override
  public void onPause() {
    super.onPause();
    CoffeeApi.detachListener();
  }

  @Override
  public void setList(List list) {
    app.coffeeList = list;
    updateUI();
  }


  @Override
  public void setCoffee(Coffee c) {}

  public void updateUI() {

    titleBar = (TextView)getActivity().findViewById(R.id.recentAddedBarTextView);
    titleBar.setText(R.string.recentlyViewedLbl);

    listAdapter = new CoffeeListAdapter(getActivity(), this, app.coffeeList);
    coffeeFilter = new CoffeeFilter(app.coffeeList,"all",listAdapter);
    setListView(listView);

    if (favourites) {
      titleBar.setText(R.string.favouritesCoffeeLbl);
      ((TextView)getActivity().findViewById(R.id.empty_list_view)).setText(R.string.favouritesEmptyMessage);

      coffeeFilter.setFilter("favourites"); // Set the filter text field from 'all' to 'favourites'
      coffeeFilter.filter(null); // Filter the data, but don't use any prefix
    }

    if(app.coffeeList.isEmpty())
      ((TextView)getActivity().findViewById(R.id.empty_list_view)).setText(R.string.recentlyViewedEmptyMessage);
    else
      ((TextView)getActivity().findViewById(R.id.empty_list_view)).setText("");

    listAdapter.notifyDataSetChanged(); // Update the adapter
  }

  public void setListView(ListView listview) {

    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    listview.setMultiChoiceModeListener(this);
    listview.setAdapter (listAdapter);
    listview.setOnItemClickListener(this);
    listview.setEmptyView(getActivity().findViewById(R.id.empty_list_view));
  }

  @Override
  public void onDetach() {
    super.onDetach();
    CoffeeApi.detachListener();
  }

  @Override
  public void onStart()
  {
    super.onStart();
  }

  @Override
  public void onClick(View view)
  {
    if (view.getTag() instanceof Coffee)
    {
      onCoffeeDelete ((Coffee) view.getTag());
    }
  }

  public void onCoffeeDelete(final Coffee coffee)
  {
    String stringName = coffee.name;
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage("Are you sure you want to Delete the \'Coffee\' " + stringName + "?");
    builder.setCancelable(false);

    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialog, int id)
      {
        CoffeeApi.delete("/coffees/" + app.googleToken +"/" + coffee._id);
        CoffeeApi.getAll("/coffees/" + app.googleToken, mSwipeRefreshLayout);
      }
    }).setNegativeButton("No", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialog, int id)
      {
        dialog.cancel();
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Bundle activityInfo = new Bundle();
    activityInfo.putString("coffeeID", (String) view.getTag());

    FragmentTransaction ft = getFragmentManager().beginTransaction();
    Fragment fragment = EditFragment.newInstance(activityInfo);
    ft.replace(R.id.homeFrame, fragment);
    ft.addToBackStack(null);
    ft.commit();
  }

  /* ************ MultiChoiceModeListener methods (begin) *********** */
  @Override
  public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
  {
    MenuInflater inflater = actionMode.getMenuInflater();
    inflater.inflate(R.menu.delete_list_context, menu);
    return true;
  }

  @Override
  public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
  {
    return false;
  }

  @Override
  public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
  {
    switch (menuItem.getItemId())
    {
      case R.id.menu_item_delete_coffee:
        deleteCoffees(actionMode);
        return true;
      default:
        return false;
    }
  }

  public void deleteCoffees(ActionMode actionMode)
  {
    Coffee c = null;
    for (int i = listAdapter.getCount() - 1; i >= 0; i--) {
      if (listView.isItemChecked(i)) {
        CoffeeApi.delete("/coffees/" + app.googleToken +"/" + listAdapter.getItem(i)._id);
      }
      CoffeeApi.getAll("/coffees/" + app.googleToken, mSwipeRefreshLayout);
    }

    actionMode.finish();

    if (favourites) {
      //Update the filters data
      coffeeFilter = new CoffeeFilter(app.coffeeList,"favourites",listAdapter);
      coffeeFilter.filter(null);
    }
    listAdapter.notifyDataSetChanged();
  }

  @Override
  public void onDestroyActionMode(ActionMode actionMode)
  {}

  @Override
  public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked)
  {}
  /* ************ MultiChoiceModeListener methods (end) *********** */
}

