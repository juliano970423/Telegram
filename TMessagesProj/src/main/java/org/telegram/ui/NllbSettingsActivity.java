package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;
import org.telegram.ui.Components.UniversityPicker;
import org.telegram.ui.Translation.TranslationManager;

public class NllbSettingsActivity extends BaseFragment {

    private TextView statusTextView;
    private TextView languageTextView;
    private Switch enableSwitch;
    private TranslationManager translationManager;

    @Override
    public View createView(Context context) {
        actionBar.setTitle(LocaleController.getString(R.string.NllbTranslation));
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);

        translationManager = TranslationManager.getInstance(context);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));

        // Status section
        TextView statusLabel = new TextView(context);
        statusLabel.setText(LocaleController.getString(R.string.NllbModelStatus));
        statusLabel.setTextSize(16);
        statusLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        container.addView(statusLabel, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        statusTextView = new TextView(context);
        updateStatusText();
        statusTextView.setTextSize(14);
        statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        statusTextView.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(16));
        container.addView(statusTextView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Download button
        TextView downloadButton = new TextView(context);
        if (translationManager.isModelDownloaded()) {
            downloadButton.setText(LocaleController.getString(R.string.NllbDeleteModel));
            downloadButton.setOnClickListener(v -> {
                translationManager.deleteModel();
                updateStatusText();
                BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.NllbModelDeleted), resourcesProvider).show();
            });
        } else {
            downloadButton.setText(LocaleController.getString(R.string.NllbDownloadModel));
            downloadButton.setOnClickListener(v -> {
                downloadButton.setText(LocaleController.getString(R.string.NllbDownloading));
                downloadButton.setEnabled(false);
                
                new Thread(() -> {
                    boolean success = translationManager.downloadModel();
                    AndroidUtilities.runOnUIThread(() -> {
                        if (success) {
                            downloadButton.setText(LocaleController.getString(R.string.NllbDeleteModel));
                            downloadButton.setEnabled(true);
                            BulletinFactory.of(NllbSettingsActivity.this).createSimpleBulletin(R.drawable.about_cat, 
                                LocaleController.getString(R.string.NllbModelDownloaded),
                                LocaleController.getString(R.string.NllbModelDownloadedDesc)).show();
                        } else {
                            downloadButton.setText(LocaleController.getString(R.string.NllbDownloadFailed));
                            downloadButton.setEnabled(true);
                        }
                        updateStatusText();
                    });
                }).start();
            });
        }
        downloadButton.setTextSize(16);
        downloadButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider));
        downloadButton.setPadding(0, AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16));
        container.addView(downloadButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Language section
        View divider = new View(context);
        divider.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
        container.addView(divider, LinearLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(1));

        TextView languageLabel = new TextView(context);
        languageLabel.setText(LocaleController.getString(R.string.NllbTargetLanguage));
        languageLabel.setTextSize(16);
        languageLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        languageLabel.setPadding(0, AndroidUtilities.dp(16), 0, AndroidUtilities.dp(8));
        container.addView(languageLabel, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        languageTextView = new TextView(context);
        languageTextView.setText(translationManager.getTargetLanguageName());
        languageTextView.setTextSize(14);
        languageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        languageTextView.setOnClickListener(v -> showLanguagePicker());
        container.addView(languageTextView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Info text
        TextView infoText = new TextView(context);
        infoText.setText(LocaleController.getString(R.string.NllbTranslationInfo));
        infoText.setTextSize(14);
        infoText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        infoText.setPadding(0, AndroidUtilities.dp(24), 0, AndroidUtilities.dp(16));
        container.addView(infoText, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
        
        return container;
    }

    private void updateStatusText() {
        if (translationManager == null || statusTextView == null) return;
        
        if (translationManager.isModelDownloaded()) {
            long size = translationManager.getModelSize();
            String sizeStr = String.format("%.1f MB", size / (1024.0 * 1024.0));
            statusTextView.setText(LocaleController.getString(R.string.NllbModelDownloaded) + " (" + sizeStr + ")");
        } else {
            statusTextView.setText(LocaleController.getString(R.string.NllbModelNotDownloaded));
        }
    }

    private void showLanguagePicker() {
        UniversityPicker universityPicker = new UniversityPicker(LocaleController.getString(R.string.NllbSelectLanguage), false);
        universityPicker.setHasSearch(true);
        universityPicker.setFilter(new UniversityPicker.Filter() {
            @Override
            public boolean filter(UniversityPicker.PickerEntity pickerEntity) {
                return true;
            }
        });
        
        java.util.ArrayList<UniversityPicker.PickerEntity> entities = new java.util.ArrayList<>();
        for (String code : TranslationManager.NllbLanguageCodes.SUPPORTED_LANGUAGES.keySet()) {
            entities.add(new UniversityPicker.PickerEntity(code, 
                TranslationManager.NllbLanguageCodes.SUPPORTED_LANGUAGES.get(code), 
                null, null, null));
        }
        universityPicker.setEntities(entities);
        universityPicker.setCallback(new UniversityPicker.Callback() {
            @Override
            public void didSelect(UniversityPicker.PickerEntity entity) {
                translationManager.targetLanguage = entity.getCode();
                languageTextView.setText(translationManager.getTargetLanguageName());
            }

            @Override
            public void didScrollTo(UniversityPicker.PickerEntity entity) {
            }
        });
        
        presentFragment(universityPicker);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatusText();
    }
}