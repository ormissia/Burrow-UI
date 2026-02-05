package com.hamsterbase.burrowui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.hamsterbase.burrowui.components.SettingsItem;
import com.hamsterbase.burrowui.components.SwitchSettingsItem;

public class SettingsActivity extends Activity implements NavigationBar.OnBackClickListener {

    private static final int PICK_WALLPAPER_REQUEST = 1001;

    private SettingsManager settingsManager;
    private LinearLayout settingsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        settingsContainer = findViewById(R.id.settingsContainer);
        settingsManager = new SettingsManager(this);

        addSection("HamsterBase Tasks", "E-ink todo app", R.drawable.ic_link,
                v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://tasks.hamsterbase.com/"))));

        addLine();

        addSection("App Selection", "Manage visible apps in launcher", R.drawable.ic_right,
                v -> startActivity(new Intent(SettingsActivity.this, AppSelectionActivity.class)));

        addLine();

        addSection("App Sorting", "Customize app order", R.drawable.ic_right,
                v -> startActivity(new Intent(SettingsActivity.this, AppSortActivity.class)));

        addLine();

        settingsContainer.addView(new SwitchSettingsItem(
                this,
                "Settings Icon Visibility",
                "When enabled, settings icon appears on home screen. Alternatively, long press time to open settings.",
                settingsManager.isShowSettingsIcon(),
                isChecked -> {
                    settingsManager.setShowSettingsIcon(isChecked);
                }
        ));

        addLine();

        settingsContainer.addView(new SwitchSettingsItem(
                this,
                "Pull-down Search",
                "When enabled, pull down from the top of the screen to open search.",
                settingsManager.isEnablePullDownSearch(),
                isChecked -> {
                    settingsManager.setEnablePullDownSearch(isChecked);
                }
        ));

        addLine();

        settingsContainer.addView(new SwitchSettingsItem(
                this,
                "24-Hour Time Format",
                "When enabled, time is displayed in 24-hour format. When disabled, time is displayed in 12-hour format with AM/PM.",
                settingsManager.isUse24HourFormat(),
                isChecked -> {
                    settingsManager.setUse24HourFormat(isChecked);
                }
        ));

        addLine();

        addSection("Wallpaper", "Set background image from storage", R.drawable.ic_right,
                v -> pickWallpaper());

        addLine();

        addSection("App Info", "View application details", R.drawable.ic_right,
                v -> startActivity(new Intent(SettingsActivity.this, AboutActivity.class)));

        NavigationBar navigationBar = findViewById(R.id.navigation_bar);
        navigationBar.setOnBackClickListener(this);
    }

    private void addSection(String title, String description, int iconResId, View.OnClickListener listener) {
        SettingsItem section = new SettingsItem(this);
        section.setTitle(title);
        section.setDescription(description);
        section.setIcon(iconResId);
        section.setOnClickListener(listener);
        settingsContainer.addView(section);
    }

    private void addLine() {
        View dividerView = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        int marginInPixels = (int) (24 * getResources().getDisplayMetrics().density);
        params.setMargins(marginInPixels, 0, marginInPixels, 0);
        dividerView.setLayoutParams(params);
        dividerView.setBackgroundColor(Color.BLACK);
        settingsContainer.addView(dividerView);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    private void pickWallpaper() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_WALLPAPER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_WALLPAPER_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                settingsManager.setWallpaperPath(uri.toString());
            }
        }
    }
}
