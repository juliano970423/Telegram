package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Translation.TranslationManager;

import androidx.annotation.NonNull;

public class NllbTranslateBottomSheet extends BottomSheet {

    private final MessageObject messageObject;
    private final int currentAccount;
    private final BaseFragment fragment;

    public NllbTranslateBottomSheet(BaseFragment fragment, int currentAccount, MessageObject messageObject, Theme.ResourcesProvider resourcesProvider) {
        super(fragment.getContext(), resourcesProvider);
        this.fragment = fragment;
        this.currentAccount = currentAccount;
        this.messageObject = messageObject;
        
        init();
    }

    private void init() {
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));

        CharSequence originalText = messageObject.getMessageText();
        
        TextView originalLabel = new TextView(getContext());
        originalLabel.setText(LocaleController.getString(R.string.QualityOriginal));
        originalLabel.setTextSize(14);
        originalLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        container.addView(originalLabel, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView originalTextView = new TextView(getContext());
        originalTextView.setText(originalText);
        originalTextView.setTextSize(16);
        originalTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        originalTextView.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(16));
        container.addView(originalTextView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        View divider = new View(getContext());
        divider.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
        container.addView(divider, LinearLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(1));

        TextView translationLabel = new TextView(getContext());
        translationLabel.setText(LocaleController.getString(R.string.PassportTranslation) + " (" + TranslationManager.getInstance(getContext()).getTargetLanguageName() + ")");
        translationLabel.setTextSize(14);
        translationLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        translationLabel.setPadding(0, AndroidUtilities.dp(16), 0, AndroidUtilities.dp(8));
        container.addView(translationLabel, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        FrameLayout translationContainer = new FrameLayout(getContext());
        
        TextView translationTextView = new TextView(getContext());
        translationTextView.setTextSize(16);
        translationTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        translationTextView.setText("Translating...");
        translationContainer.addView(translationTextView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        
        container.addView(translationContainer, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        FrameLayout.LayoutParams layoutParams = FrameLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = AndroidUtilities.dp(400);
        setContentView(container, layoutParams);

        translateText(originalText.toString(), translationTextView);
    }

    private void translateText(String text, TextView translationTextView) {
        TranslationManager translationManager = TranslationManager.getInstance(getContext());
        
        if (!translationManager.isModelDownloaded()) {
            translationTextView.setText("Translation model not downloaded. Please download it in Settings.");
            return;
        }

        translationManager.initialize();
        
        new Thread(() -> {
            try {
                String result = null;
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(500);
                    if (translationManager.isReady()) {
                        break;
                    }
                }
                
                if (translationManager.isReady()) {
                    result = translationManager.translate(text, "eng_Latn");
                } else {
                    result = "Translation engine not ready. Please wait and try again.";
                }
                
                final String finalResult = result;
                AndroidUtilities.runOnUIThread(() -> {
                    if (finalResult != null) {
                        translationTextView.setText(finalResult);
                    } else {
                        translationTextView.setText("Translation failed. Please try again.");
                    }
                });
            } catch (Exception e) {
                AndroidUtilities.runOnUIThread(() -> {
                    translationTextView.setText("Error: " + e.getMessage());
                });
            }
        }).start();
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    }
}