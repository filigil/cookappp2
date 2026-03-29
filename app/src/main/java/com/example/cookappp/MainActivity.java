package com.example.cookappp;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView tvTitle, tvIngredients, tvSteps;
    private Button btnBreakfast, btnLunch, btnDinner;
    private Button btnFrench, btnRussian, btnItalian;
    private Button btnReset, btnSpeak;
    private VideoView videoView;
    private MediaController mediaController;

    private MediaPlayer mediaPlayer;
    private TextToSpeech tts;

    private String selectedCuisine = "";
    private String selectedMealType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();

        tts = new TextToSpeech(this, this);

        playStartupSound();

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
        videoView = findViewById(R.id.videoView);

        btnBreakfast = findViewById(R.id.btn_breakfast);
        btnLunch = findViewById(R.id.btn_lunch);
        btnDinner = findViewById(R.id.btn_dinner);

        btnFrench = findViewById(R.id.btn_french);
        btnRussian = findViewById(R.id.btn_russian);
        btnItalian = findViewById(R.id.btn_italian);

        btnReset = findViewById(R.id.btn_reset);
        btnSpeak = findViewById(R.id.btn_speak);
    }

    private void setupListeners() {
        findViewById(R.id.btn_ru).setOnClickListener(v -> changeLanguage("ru"));
        findViewById(R.id.btn_en).setOnClickListener(v -> changeLanguage("en"));
        findViewById(R.id.btn_de).setOnClickListener(v -> changeLanguage("de"));

        btnBreakfast.setOnClickListener(v -> { playClickSound(); selectedMealType = "breakfast"; showRecipe(); });
        btnLunch.setOnClickListener(v -> { playClickSound(); selectedMealType = "lunch"; showRecipe(); });
        btnDinner.setOnClickListener(v -> { playClickSound(); selectedMealType = "dinner"; showRecipe(); });

        btnFrench.setOnClickListener(v -> { playClickSound(); selectedCuisine = "french"; showRecipe(); });
        btnRussian.setOnClickListener(v -> { playClickSound(); selectedCuisine = "russian"; showRecipe(); });
        btnItalian.setOnClickListener(v -> { playClickSound(); selectedCuisine = "italian"; showRecipe(); });

        btnReset.setOnClickListener(v -> {
            playClickSound();
            selectedCuisine = "";
            selectedMealType = "";
            showRecipe();
        });

        btnSpeak.setOnClickListener(v -> speakCurrentRecipe());
    }

    private void showRecipe() {
        videoView.setVisibility(View.GONE);

        if (selectedMealType.isEmpty()) {
            tvTitle.setText(getString(R.string.welcome));
            tvIngredients.setText("");
            tvSteps.setText("");
            return;
        }

        if (selectedCuisine.isEmpty()) {
            switch (selectedMealType) {
                case "breakfast": showRecipe1(); break;
                case "lunch": showRecipe2(); break;
                case "dinner": showRecipe3(); break;
            }
            return;
        }

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

        // Показываем видео с контроллером
        videoView.setVisibility(View.VISIBLE);
        setupVideoPlayer();
    }

    private void setupVideoPlayer() {
        try {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.carbonara_video);
            videoView.setVideoURI(videoUri);

            // Создаём MediaController (кнопки воспроизведения)
            mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            // Опционально: можно автоматически начать воспроизведение
            // videoView.start();

        } catch (Exception e) {
            Log.e("Video", "Ошибка загрузки видео", e);
        }
    }

    private void speakCurrentRecipe() {
        if (tts == null) return;

        String text = tvTitle.getText() + ". " +
                getString(R.string.ingredients_header) + " " + tvIngredients.getText() + ". " +
                getString(R.string.steps_header) + " " + tvSteps.getText();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "recipe");
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale current = getResources().getConfiguration().getLocales().get(0);
            int result = tts.setLanguage(current);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.US);
            }
        }
    }

    private void playStartupSound() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.startup_sound);
            if (mediaPlayer != null) mediaPlayer.start();
        } catch (Exception ignored) {}
    }

    private void playClickSound() {
        try {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.click_sound);
            if (mp != null) {
                mp.start();
                mp.setOnCompletionListener(MediaPlayer::release);
            }
        } catch (Exception ignored) {}
    }

    private void changeLanguage(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
        recreate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cuisine", selectedCuisine);
        outState.putString("mealType", selectedMealType);
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) mediaPlayer.release();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}