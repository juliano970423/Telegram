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
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public class NllbSettingsActivity extends BaseFragment {

    private TextView statusTextView;
    private TextView languageTextView;

    @Override
    public View createView(Context context) {
        actionBar.setTitle(LocaleController.getString(R.string.NllbTranslation));
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));

        TextView statusLabel = new TextView(context);
        statusLabel.setText(LocaleController.getString(R.string.NllbModelStatus));
        statusLabel.setTextSize(16);
        statusLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        container.addView(statusLabel, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        statusTextView = new TextView(context);
        statusTextView.setText(LocaleController.getString(R.string.NllbModelNotDownloaded));
        statusTextView.setTextSize(14);
        statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        statusTextView.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(16));
        container.addView(statusTextView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView downloadButton = new TextView(context);
        downloadButton.setText(LocaleController.getString(R.string.NllbDownloadModel));
        downloadButton.setTextSize(16);
        downloadButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider));
        downloadButton.setPadding(0, AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16));
        container.addView(downloadButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

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
        languageTextView.setText("繁體中文");
        languageTextView.setTextSize(14);
        languageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        container.addView(languageTextView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

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

    @Override
    public void onResume() {
        super.onResume();
    }
}