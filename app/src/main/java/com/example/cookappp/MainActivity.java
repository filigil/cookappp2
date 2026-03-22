package com.example.cookappp;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Объявляем переменные для элементов интерфейса
    private TextView tvTitle, tvIngredients, tvSteps;
    private Button btnRu, btnEn, btnDe;
    private Button btnBreakfast, btnLunch, btnDinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Инициализируем (находим) все элементы по ID из XML
        tvTitle = findViewById(R.id.tv_recipe_title);
        tvIngredients = findViewById(R.id.tv_ingredients);
        tvSteps = findViewById(R.id.tv_steps);

        btnRu = findViewById(R.id.btn_ru);
        btnEn = findViewById(R.id.btn_en);
        btnDe = findViewById(R.id.btn_de);

        btnBreakfast = findViewById(R.id.btn_breakfast);
        btnLunch = findViewById(R.id.btn_lunch);
        btnDinner = findViewById(R.id.btn_dinner);

        // 2. Логика переключения языков
        btnRu.setOnClickListener(v -> setLocale("ru"));
        btnEn.setOnClickListener(v -> setLocale("en"));
        btnDe.setOnClickListener(v -> setLocale("de"));

        // 3. Логика отображения рецептов
        // Мы берем текст из ресурсов (getString), которые сами создали
        btnBreakfast.setOnClickListener(v -> {
            tvTitle.setText(getString(R.string.recipe_1_title));
            tvIngredients.setText(getString(R.string.recipe_1_ingr));
            tvSteps.setText(getString(R.string.recipe_1_steps));
        });

        btnLunch.setOnClickListener(v -> {
            tvTitle.setText(getString(R.string.recipe_2_title));
            tvIngredients.setText(getString(R.string.recipe_2_ingr));
            tvSteps.setText(getString(R.string.recipe_2_steps));
        });

        btnDinner.setOnClickListener(v -> {
            tvTitle.setText(getString(R.string.recipe_3_title));
            tvIngredients.setText(getString(R.string.recipe_3_ingr));
            tvSteps.setText(getString(R.string.recipe_3_steps));
        });
    }

    // Метод для принудительной смены языка в приложении
    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        // Обновляем конфигурацию
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Перезапускаем Activity, чтобы интерфейс перерисовался на новом языке
        recreate();
    }
}