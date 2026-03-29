package com.example.cookappp;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTitle, tvIngredients, tvSteps;

    private Button btnBreakfast, btnLunch, btnDinner;
    private Button btnFrench, btnRussian, btnItalian;
    private Button btnReset;

    private String selectedCuisine = "";
    private String selectedMealType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();

        if (savedInstanceState != null) {
            selectedCuisine = savedInstanceState.getString("cuisine", "");
            selectedMealType = savedInstanceState.getString("mealType", "");
        }

        showRecipe();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_recipe_title);
        tvIngredients = findViewById(R.id.tv_ingredients);
        tvSteps = findViewById(R.id.tv_steps);

        btnBreakfast = findViewById(R.id.btn_breakfast);
        btnLunch = findViewById(R.id.btn_lunch);
        btnDinner = findViewById(R.id.btn_dinner);

        btnFrench = findViewById(R.id.btn_french);
        btnRussian = findViewById(R.id.btn_russian);
        btnItalian = findViewById(R.id.btn_italian);
        btnReset = findViewById(R.id.btn_reset);
    }

    private void setupListeners() {
        // Смена языка
        findViewById(R.id.btn_ru).setOnClickListener(v -> changeLanguage("ru"));
        findViewById(R.id.btn_en).setOnClickListener(v -> changeLanguage("en"));
        findViewById(R.id.btn_de).setOnClickListener(v -> changeLanguage("de"));

        // Типы блюд
        btnBreakfast.setOnClickListener(v -> { selectedMealType = "breakfast"; showRecipe(); });
        btnLunch.setOnClickListener(v -> { selectedMealType = "lunch"; showRecipe(); });
        btnDinner.setOnClickListener(v -> { selectedMealType = "dinner"; showRecipe(); });

        // Кухни
        btnFrench.setOnClickListener(v -> { selectedCuisine = "french"; showRecipe(); });
        btnRussian.setOnClickListener(v -> { selectedCuisine = "russian"; showRecipe(); });
        btnItalian.setOnClickListener(v -> { selectedCuisine = "italian"; showRecipe(); });

        // Сброс фильтров
        btnReset.setOnClickListener(v -> {
            selectedCuisine = "";
            selectedMealType = "";
            showRecipe();
        });
    }

    private void showRecipe() {
        if (selectedMealType.isEmpty()) {
            tvTitle.setText(getString(R.string.welcome));
            tvIngredients.setText("");
            tvSteps.setText("");
            return;
        }

        if (selectedCuisine.isEmpty()) {
            // Только тип блюда
            switch (selectedMealType) {
                case "breakfast":
                    tvTitle.setText(getString(R.string.recipe_1_title));
                    tvIngredients.setText(getString(R.string.recipe_1_ingr));
                    tvSteps.setText(getString(R.string.recipe_1_steps));
                    break;
                case "lunch":
                    tvTitle.setText(getString(R.string.recipe_2_title));
                    tvIngredients.setText(getString(R.string.recipe_2_ingr));
                    tvSteps.setText(getString(R.string.recipe_2_steps));
                    break;
                case "dinner":
                    tvTitle.setText(getString(R.string.recipe_3_title));
                    tvIngredients.setText(getString(R.string.recipe_3_ingr));
                    tvSteps.setText(getString(R.string.recipe_3_steps));
                    break;
            }
            return;
        }

        // Кухня + тип блюда
        if (selectedCuisine.equals("french") && selectedMealType.equals("breakfast")) {
            showRecipe1();
        } else if (selectedCuisine.equals("russian") && selectedMealType.equals("lunch")) {
            showRecipe2();
        } else if (selectedCuisine.equals("italian") && selectedMealType.equals("dinner")) {
            showRecipe3();
        } else {
            tvTitle.setText(getString(R.string.no_recipe));
            tvIngredients.setText("");
            tvSteps.setText("");
        }
    }

    private void showRecipe1() {
        tvTitle.setText(getString(R.string.recipe_1_title));
        tvIngredients.setText(getString(R.string.recipe_1_ingr));
        tvSteps.setText(getString(R.string.recipe_1_steps));
    }

    private void showRecipe2() {
        tvTitle.setText(getString(R.string.recipe_2_title));
        tvIngredients.setText(getString(R.string.recipe_2_ingr));
        tvSteps.setText(getString(R.string.recipe_2_steps));
    }

    private void showRecipe3() {
        tvTitle.setText(getString(R.string.recipe_3_title));
        tvIngredients.setText(getString(R.string.recipe_3_ingr));
        tvSteps.setText(getString(R.string.recipe_3_steps));
    }

    private void changeLanguage(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        recreate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cuisine", selectedCuisine);
        outState.putString("mealType", selectedMealType);
    }
}