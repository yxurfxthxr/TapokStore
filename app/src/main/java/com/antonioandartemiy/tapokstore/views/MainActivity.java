package com.antonioandartemiy.tapokstore.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antonioandartemiy.tapokstore.R;
import com.antonioandartemiy.tapokstore.utils.adapter.ShoeItemAdapter;
import com.antonioandartemiy.tapokstore.utils.model.ShoeCart;
import com.antonioandartemiy.tapokstore.utils.model.ShoeItem;
import com.antonioandartemiy.tapokstore.viewmodel.CartViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ShoeItemAdapter.ShoeClickedListeners {

    private RecyclerView recyclerView;
    private List<ShoeItem> shoeItemList;
    private ShoeItemAdapter adapter;
    private CartViewModel viewModel;
    private List<ShoeCart> shoeCartList;
    private CoordinatorLayout coordinatorLayout;
    private ImageView cartImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();
        setUpList();

        adapter.setShoeItemList(shoeItemList);
        recyclerView.setAdapter(adapter);


        cartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getAllCartItems().observe(this, new Observer<List<ShoeCart>>() {
            @Override
            public void onChanged(List<ShoeCart> shoeCarts) {
                shoeCartList.addAll(shoeCarts);
            }
        });
    }

    private void setUpList() {
        shoeItemList.add(new ShoeItem("Nike Quest 5", "Nike", R.drawable.nike_quest_5, 13999.99));
        shoeItemList.add(new ShoeItem("Nike React Pegasus", "Nike", R.drawable.nike_react_pegasus_trail, 22499.99));
        shoeItemList.add(new ShoeItem("Nike Air Monarch IV", "Nike", R.drawable.nike_air_monarch, 14999.69));
        shoeItemList.add(new ShoeItem("Nike Zoom Fly 5", "Nike", R.drawable.nike_zoom_fly_5, 27899.52));
        shoeItemList.add(new ShoeItem("Nike Revolution", "Nike", R.drawable.nike_revolution_road, 10799.49));
        shoeItemList.add(new ShoeItem("Nike Flex Run 2021", "Nike", R.drawable.flex_run_road_running, 27999.39));
        shoeItemList.add(new ShoeItem("Court Zoom Vapor", "Nike", R.drawable.nikecourt_zoom_vapor_cage, 21999.29));
        shoeItemList.add(new ShoeItem("EQ21 Run COLD.RDY", "Adidas", R.drawable.adidas_eq_run, 13999.19));
        shoeItemList.add(new ShoeItem("Adidas Ozelia", "Adidas", R.drawable.adidas_ozelia_shoes_grey, 161999.09));
        shoeItemList.add(new ShoeItem("Adidas Questar", "Adidas", R.drawable.adidas_questar_shoes, 11499.99));
        shoeItemList.add(new ShoeItem("Adidas Ultraboost", "Adidas", R.drawable.adidas_ultraboost, 29699.52));
        shoeItemList.add(new ShoeItem("Supernova Stride", "Adidas", R.drawable.supernova_stride, 17699.89));
        shoeItemList.add(new ShoeItem("Adidas Ozelle", "Adidas", R.drawable.adidas_ozelle, 10999.79));
        shoeItemList.add(new ShoeItem("Adidas Runfalcon 3.0", "Adidas", R.drawable.adidas_runfalcon, 10999.69));
        shoeItemList.add(new ShoeItem("PUMA X-Ray 3 Sd", "Puma", R.drawable.puma_xray, 9999.59));
        shoeItemList.add(new ShoeItem("Skyrocket Lite Alt", "Puma", R.drawable.skyrocket_lite_alt, 6999.49));
        shoeItemList.add(new ShoeItem("Skyrocket Lite Engine", "Puma", R.drawable.skyrocket_lite_engine, 7999.39));
    }

    private void initializeVariables() {

        cartImageView = findViewById(R.id.cartIv);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        shoeCartList = new ArrayList<>();
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        shoeItemList = new ArrayList<>();
        recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new ShoeItemAdapter(this);

    }

    @Override
    public void onCardClicked(ShoeItem shoe) {

        Intent intent = new Intent(MainActivity.this, DetailedActivity.class);
        intent.putExtra("shoeItem", shoe);
        startActivity(intent);
    }

    @Override
    public void onAddToCartBtnClicked(ShoeItem shoeItem) {
        ShoeCart shoeCart = new ShoeCart();
        shoeCart.setShoeName(shoeItem.getShoeName());
        shoeCart.setShoeBrandName(shoeItem.getShoeBrandName());
        shoeCart.setShoePrice(shoeItem.getShoePrice());
        shoeCart.setShoeImage(shoeItem.getShoeImage());

        final int[] quantity = {1};
        final int[] id = new int[1];

        if (!shoeCartList.isEmpty()) {
            for (int i = 0; i < shoeCartList.size(); i++) {
                if (shoeCart.getShoeName().equals(shoeCartList.get(i).getShoeName())) {
                    quantity[0] = shoeCartList.get(i).getQuantity();
                    quantity[0]++;
                    id[0] = shoeCartList.get(i).getId();
                }
            }
        }

        Log.d("TAG", "onAddToCartBtnClicked: " + quantity[0]);

        if (quantity[0] == 1) {
            shoeCart.setQuantity(quantity[0]);
            shoeCart.setTotalItemPrice(quantity[0] * shoeCart.getShoePrice());
            viewModel.insertCartItem(shoeCart);
        } else {
            viewModel.updateQuantity(id[0], quantity[0]);
            viewModel.updatePrice(id[0], quantity[0] * shoeCart.getShoePrice());
        }

        makeSnackBar("Добавлено в корзину");
    }

    private void makeSnackBar(String msg) {
        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT)
                .setAction("В корзину", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, CartActivity.class));
                    }
                }).show();
    }
}