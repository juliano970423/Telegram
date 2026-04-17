package org.telegram.ui.Components;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public class NllbTranslateBottomSheet extends BottomSheet {

    private final MessageObject messageObject;
    private final int currentAccount;
    private final Theme.ResourcesProvider resourcesProvider;

    public NllbTranslateBottomSheet(BaseFragment fragment, int currentAccount, MessageObject messageObject, Theme.ResourcesProvider resourcesProvider) {
        super(fragment.getContext(), false);
        this.messageObject = messageObject;
        this.currentAccount = currentAccount;
        this.resourcesProvider = resourcesProvider;
        
        init();
    }

    private void init() {
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16), AndroidUtilities.dp(16));

        int[] msgId = new int[] { messageObject.getId() };
        CharSequence originalText = messageObject.getMessageTextToTranslate(null, msgId);
        
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
        translationLabel.setText(LocaleController.getString(R.string.PassportTranslation));
        translationLabel.setTextSize(14);
        translationLabel.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, resourcesProvider));
        translationLabel.setPadding(0, AndroidUtilities.dp(16), 0, AndroidUtilities.dp(8));
        container.addView(translationLabel, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView translationTextView = new TextView(getContext());
        translationTextView.setTextSize(16);
        translationTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        translationTextView.setText("Translation feature coming soon...");
        container.addView(translationTextView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, AndroidUtilities.dp(400));
        setContentView(container, layoutParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    }
}