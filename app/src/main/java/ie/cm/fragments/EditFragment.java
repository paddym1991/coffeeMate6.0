package ie.cm.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ie.cm.R;
import ie.cm.api.CoffeeApi;
import ie.cm.api.VolleyListener;
import ie.cm.main.CoffeeMateApp;
import ie.cm.models.Coffee;


public class EditFragment extends Fragment implements VolleyListener{

    private OnFragmentInteractionListener mListener;
    TextView    titleBar,titleName,titleShop;
    Coffee      aCoffee;
    Boolean     isFavourite;

    EditText    name, shop, price;
    RatingBar   ratingBar;
    ImageView   favouriteImage;

    String      coffeeID;

    public CoffeeMateApp app = CoffeeMateApp.getInstance();

    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance(Bundle coffeeBundle) {
        EditFragment fragment = new EditFragment();
        fragment.setArguments(coffeeBundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            coffeeID = getArguments().getString("coffeeID");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit, container, false);

        titleName = ((TextView)v.findViewById(R.id.coffeeNameTextView));
        titleShop = ((TextView)v.findViewById(R.id.coffeeShopTextView));
        name = (EditText)v.findViewById(R.id.nameEditText);
        shop = (EditText)v.findViewById(R.id.shopEditText);
        price = (EditText)v.findViewById(R.id.priceEditText);
        ratingBar = (RatingBar) v.findViewById(R.id.coffeeRatingBar);
        favouriteImage = (ImageView) v.findViewById(R.id.favouriteImageView);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CoffeeApi.attachListener(this);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        CoffeeApi.detachListener();
    }

    @Override
    public void onResume() {
        super.onResume();

        CoffeeApi.attachListener(this);
        CoffeeApi.get("/coffees/" + app.googleToken +"/" + coffeeID);
        titleBar = (TextView) getActivity().findViewById(R.id.recentAddedBarTextView);
        titleBar.setText(R.string.updateACoffeeLbl);
    }


    @Override
    public void setList(List list) {
        app.coffeeList = list;
    }

    @Override
    public void setCoffee(Coffee c) {
        aCoffee = c;
        updateUI();
    }

    public interface OnFragmentInteractionListener {
        void toggle(View v);
        void update(View v);
    }

    public void toggle(View v) {

        if (isFavourite) {
            aCoffee.favourite = false;
            toastMessage("Removed From Favourites");
            isFavourite = false;
            favouriteImage.setImageResource(R.drawable.ic_favourite_off);
        } else {
            aCoffee.favourite = true;
            toastMessage("Added to Favourites !!");
            isFavourite = true;
            favouriteImage.setImageResource(R.drawable.ic_favourite_on);
        }
    }

    public void update(View v) {
        if (mListener != null) {
            String coffeeName = name.getText().toString();
            String coffeeShop = shop.getText().toString();
            String coffeePriceStr = price.getText().toString();
            double ratingValue = ratingBar.getRating();

            double coffeePrice;
            try {
                coffeePrice = Double.parseDouble(coffeePriceStr);
            } catch (NumberFormatException e)
            {            coffeePrice = 0.0;        }

            if ((coffeeName.length() > 0) && (coffeeShop.length() > 0) && (coffeePriceStr.length() > 0)) {
                aCoffee.name = coffeeName;
                aCoffee.shop = coffeeShop;
                aCoffee.price = coffeePrice;
                aCoffee.rating = ratingValue;
            }

            CoffeeApi.put("/coffees/" + app.googleToken +"/" + aCoffee._id, aCoffee);

            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
                return;
            }
        } else
            toastMessage("You must Enter Something for Name and Shop");
    }

    protected void toastMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public void updateUI() {
        titleName.setText(aCoffee.name);
        titleShop.setText(aCoffee.shop);
        name.setText(aCoffee.name);
        shop.setText(aCoffee.shop);
        price.setText(""+aCoffee.price);
        ratingBar.setRating((float)aCoffee.rating);

        if (aCoffee.favourite == true) {
            favouriteImage.setImageResource(R.drawable.ic_favourite_on);
            isFavourite = true;
        } else {
            favouriteImage.setImageResource(R.drawable.ic_favourite_off);
            isFavourite = false;
        }
    }

}

