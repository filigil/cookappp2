package com.example.cookappp;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    // Основные элементы
    private TextView tvTitle, tvIngredients, tvSteps, tvTimersTitle;
    private VideoView videoView;

    // Таймеры
    private TextView tvTimer1, tvTimer2, tvTimer3;
    private Button btnTimer1Start, btnTimer1Reset;
    private Button btnTimer2Start, btnTimer2Reset, btnTimer2Set;
    private Button btnTimer3Start, btnTimer3Reset;

    // Логика таймеров
    private CountDownTimer currentTimer1, currentTimer2, currentTimer3;
    private long timeLeft1 = 300000;   // 5 минут (омлет)
    private long timeLeft2 = 480000;   // 8 минут (спагетти)
    private long timeLeft3 = 180000;   // 3 минуты (бекон)

    private boolean isTimer1Running = false;
    private boolean isTimer2Running = false;
    private boolean isTimer3Running = false;

    // Медиа
    private MediaPlayer mediaPlayer;
    private TextToSpeech tts;

    // Выбор рецепта
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
            timeLeft1 = savedInstanceState.getLong("time1", 300000);
            timeLeft2 = savedInstanceState.getLong("time2", 480000);
            timeLeft3 = savedInstanceState.getLong("time3", 180000);
        }

        showRecipe();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_recipe_title);
        tvIngredients = findViewById(R.id.tv_ingredients);
        tvSteps = findViewById(R.id.tv_steps);
        tvTimersTitle = findViewById(R.id.tv_timers_title);
        videoView = findViewById(R.id.videoView);

        tvTimer1 = findViewById(R.id.tv_timer1);
        tvTimer2 = findViewById(R.id.tv_timer2);
        tvTimer3 = findViewById(R.id.tv_timer3);

        btnTimer1Start = findViewById(R.id.btn_timer1_start);
        btnTimer1Reset = findViewById(R.id.btn_timer1_reset);

        btnTimer2Start = findViewById(R.id.btn_timer2_start);
        btnTimer2Reset = findViewById(R.id.btn_timer2_reset);
        btnTimer2Set = findViewById(R.id.btn_timer2_set);

        btnTimer3Start = findViewById(R.id.btn_timer3_start);
        btnTimer3Reset = findViewById(R.id.btn_timer3_reset);
    }

    private void setupListeners() {
        // Смена языка
        findViewById(R.id.btn_ru).setOnClickListener(v -> changeLanguage("ru"));
        findViewById(R.id.btn_en).setOnClickListener(v -> changeLanguage("en"));
        findViewById(R.id.btn_de).setOnClickListener(v -> changeLanguage("de"));

        // Фильтры рецептов
        findViewById(R.id.btn_breakfast).setOnClickListener(v -> { playClickSound(); selectedMealType = "breakfast"; showRecipe(); });
        findViewById(R.id.btn_lunch).setOnClickListener(v -> { playClickSound(); selectedMealType = "lunch"; showRecipe(); });
        findViewById(R.id.btn_dinner).setOnClickListener(v -> { playClickSound(); selectedMealType = "dinner"; showRecipe(); });

        findViewById(R.id.btn_french).setOnClickListener(v -> { playClickSound(); selectedCuisine = "french"; showRecipe(); });
        findViewById(R.id.btn_russian).setOnClickListener(v -> { playClickSound(); selectedCuisine = "russian"; showRecipe(); });
        findViewById(R.id.btn_italian).setOnClickListener(v -> { playClickSound(); selectedCuisine = "italian"; showRecipe(); });

        findViewById(R.id.btn_reset).setOnClickListener(v -> { playClickSound(); resetAll(); });

        findViewById(R.id.btn_speak).setOnClickListener(v -> speakCurrentRecipe());

        // Таймеры
        btnTimer1Start.setOnClickListener(v -> toggleTimer1());
        btnTimer1Reset.setOnClickListener(v -> resetTimer1());

        btnTimer2Start.setOnClickListener(v -> toggleTimer2());
        btnTimer2Reset.setOnClickListener(v -> resetTimer2());
        btnTimer2Set.setOnClickListener(v -> setCustomTimeTimer2());

        btnTimer3Start.setOnClickListener(v -> toggleTimer3());
        btnTimer3Reset.setOnClickListener(v -> resetTimer3());
    }

    private void showRecipe() {
        // Скрываем всё
        videoView.setVisibility(View.GONE);
        tvTimersTitle.setVisibility(View.GONE);
        findViewById(R.id.timer1_layout).setVisibility(View.GONE);
        findViewById(R.id.timer2_layout).setVisibility(View.GONE);
        findViewById(R.id.timer3_layout).setVisibility(View.GONE);

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

        if (selectedCuisine.equals("french") && selectedMealType.equals("breakfast")) showRecipe1();
        else if (selectedCuisine.equals("russian") && selectedMealType.equals("lunch")) showRecipe2();
        else if (selectedCuisine.equals("italian") && selectedMealType.equals("dinner")) showRecipe3();
        else {
            tvTitle.setText(getString(R.string.no_recipe));
            tvIngredients.setText("");
            tvSteps.setText("");
        }
    }

    private void showRecipe1() {
        tvTitle.setText(getString(R.string.recipe_1_title));
        tvIngredients.setText(getString(R.string.recipe_1_ingr));
        tvSteps.setText(getString(R.string.recipe_1_steps));

        tvTimersTitle.setVisibility(View.VISIBLE);
        findViewById(R.id.timer1_layout).setVisibility(View.VISIBLE);
        updateTimerDisplay(tvTimer1, timeLeft1);
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

        // Видео
        videoView.setVisibility(View.VISIBLE);
        setupVideoPlayer();

        // Таймеры для карбонары
        tvTimersTitle.setVisibility(View.VISIBLE);
        findViewById(R.id.timer2_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.timer3_layout).setVisibility(View.VISIBLE);

        updateTimerDisplay(tvTimer2, timeLeft2);
        updateTimerDisplay(tvTimer3, timeLeft3);
    }

    private void setupVideoPlayer() {
        try {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.carbonara_video);
            videoView.setVideoURI(videoUri);
            MediaController controller = new MediaController(this);
            controller.setAnchorView(videoView);
            videoView.setMediaController(controller);
        } catch (Exception e) {
            Log.e("Video", "Ошибка видео", e);
        }
    }

    // ====================== ТАЙМЕР 1 ======================
    private void toggleTimer1() {
        if (isTimer1Running) pauseTimer1();
        else startTimer1();
    }

    private void startTimer1() {
        if (currentTimer1 != null) currentTimer1.cancel();
        currentTimer1 = createCountDownTimer(timeLeft1, tvTimer1, () -> {
            isTimer1Running = false;
            playFinishSound();
        });
        currentTimer1.start();
        isTimer1Running = true;
    }

    private void pauseTimer1() {
        if (currentTimer1 != null) currentTimer1.cancel();
        isTimer1Running = false;
    }

    private void resetTimer1() {
        if (currentTimer1 != null) currentTimer1.cancel();
        isTimer1Running = false;
        timeLeft1 = 300000; // 5 минут
        updateTimerDisplay(tvTimer1, timeLeft1);
    }

    // ====================== ТАЙМЕР 2 ======================
    private void toggleTimer2() {
        if (isTimer2Running) pauseTimer2();
        else startTimer2();
    }

    private void startTimer2() {
        if (currentTimer2 != null) currentTimer2.cancel();
        currentTimer2 = createCountDownTimer(timeLeft2, tvTimer2, () -> {
            isTimer2Running = false;
            playFinishSound();
        });
        currentTimer2.start();
        isTimer2Running = true;
    }

    private void pauseTimer2() {
        if (currentTimer2 != null) currentTimer2.cancel();
        isTimer2Running = false;
    }

    private void resetTimer2() {
        if (currentTimer2 != null) currentTimer2.cancel();
        isTimer2Running = false;
        timeLeft2 = 480000; // 8 минут
        updateTimerDisplay(tvTimer2, timeLeft2);
    }

    private void setCustomTimeTimer2() {
        // Простая реализация: ставим 10 минут
        // Можно потом улучшить через AlertDialog
        timeLeft2 = 600000; // 10 минут
        updateTimerDisplay(tvTimer2, timeLeft2);
    }

    // ====================== ТАЙМЕР 3 ======================
    private void toggleTimer3() {
        if (isTimer3Running) pauseTimer3();
        else startTimer3();
    }

    private void startTimer3() {
        if (currentTimer3 != null) currentTimer3.cancel();
        currentTimer3 = createCountDownTimer(timeLeft3, tvTimer3, () -> {
            isTimer3Running = false;
            playFinishSound();
        });
        currentTimer3.start();
        isTimer3Running = true;
    }

    private void pauseTimer3() {
        if (currentTimer3 != null) currentTimer3.cancel();
        isTimer3Running = false;
    }

    private void resetTimer3() {
        if (currentTimer3 != null) currentTimer3.cancel();
        isTimer3Running = false;
        timeLeft3 = 180000; // 3 минуты
        updateTimerDisplay(tvTimer3, timeLeft3);
    }

    // Общий метод создания таймера
    private CountDownTimer createCountDownTimer(long millisInFuture, TextView timerView, Runnable onFinishAction) {
        return new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (timerView == tvTimer1) timeLeft1 = millisUntilFinished;
                else if (timerView == tvTimer2) timeLeft2 = millisUntilFinished;
                else if (timerView == tvTimer3) timeLeft3 = millisUntilFinished;

                updateTimerDisplay(timerView, millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timerView.setText("00:00");
                timerView.setTextColor(getColor(android.R.color.holo_red_dark));
                onFinishAction.run();
            }
        };
    }

    private void updateTimerDisplay(TextView tv, long millisLeft) {
        int minutes = (int) (millisLeft / 1000) / 60;
        int seconds = (int) (millisLeft / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tv.setText(timeFormatted);

        if (millisLeft < 60000) {
            tv.setTextColor(getColor(android.R.color.holo_red_dark));
        } else {
            tv.setTextColor(getColor(R.color.green)); // или #2E7D32
        }
    }

    private void playFinishSound() {
        try {
            MediaPlayer mp = MediaPlayer.create(this, R.raw.timer_finish);
            if (mp != null) {
                mp.start();
                mp.setOnCompletionListener(MediaPlayer::release);
            }
        } catch (Exception ignored) {}
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

    private void speakCurrentRecipe() {
        if (tts == null) return;
        String text = tvTitle.getText() + ". " +
                getString(R.string.ingredients_header) + " " + tvIngredients.getText() + ". " +
                getString(R.string.steps_header) + " " + tvSteps.getText();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "recipe");
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

    private void resetAll() {
        selectedCuisine = "";
        selectedMealType = "";
        resetTimer1();
        resetTimer2();
        resetTimer3();
        showRecipe();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cuisine", selectedCuisine);
        outState.putString("mealType", selectedMealType);
        outState.putLong("time1", timeLeft1);
        outState.putLong("time2", timeLeft2);
        outState.putLong("time3", timeLeft3);
    }

    @Override
    protected void onDestroy() {
        if (currentTimer1 != null) currentTimer1.cancel();
        if (currentTimer2 != null) currentTimer2.cancel();
        if (currentTimer3 != null) currentTimer3.cancel();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (mediaPlayer != null) mediaPlayer.release();
        super.onDestroy();
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
}