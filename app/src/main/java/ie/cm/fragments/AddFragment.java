package ie.cm.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import ie.cm.R;
import ie.cm.activities.Home;
import ie.cm.api.CoffeeApi;
import ie.cm.main.CoffeeMateApp;
import ie.cm.models.Coffee;

public class AddFragment extends Fragment implements View.OnClickListener{

    private TextView    titleBar;
    private String 		coffeeName, coffeeShop;
    private double 		coffeePrice, ratingValue;
    private EditText    name, shop, price;
    private RatingBar   ratingBar;
    public CoffeeMateApp app = CoffeeMateApp.getInstance();

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_add, container, false);

        Button saveButton = (Button) v.findViewById(R.id.saveCoffeeBtn);
        name = (EditText) v.findViewById(R.id.nameEditText);
        shop = (EditText) v.findViewById(R.id.shopEditText);
        price = (EditText) v.findViewById(R.id.priceEditText);
        ratingBar = (RatingBar) v.findViewById(R.id.coffeeRatingBar);
        saveButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        titleBar = (TextView) getActivity().findViewById(R.id.recentAddedBarTextView);
        titleBar.setText(R.string.addACoffeeLbl);
    }

    public void onClick(View v) {

        coffeeName = name.getText().toString();
        coffeeShop = shop.getText().toString();
        try {
            coffeePrice = Double.parseDouble(price.getText().toString());
        } catch (NumberFormatException e) {
            coffeePrice = 0.0;
        }
        ratingValue = ratingBar.getRating();

        if ((coffeeName.length() > 0) && (coffeeShop.length() > 0)
                && (price.length() > 0)) {
            Coffee c = new Coffee(coffeeName, coffeeShop, ratingValue,
                    coffeePrice, false,app.googleToken,0,0,app.googlePhotoURL);

            CoffeeApi.post("/coffees/" + app.googleToken,c);
            Intent intent = new Intent(getActivity(), Home.class);
            getActivity().startActivity(intent);

        } else
            Toast.makeText(
                    getActivity(),
                    "You must Enter Something for "
                            + "\'Name\', \'Shop\' and \'Price\'",
                    Toast.LENGTH_SHORT).show();
    }
}
