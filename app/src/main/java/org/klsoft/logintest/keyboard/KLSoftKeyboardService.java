package org.klsoft.logintest.keyboard;

import android.app.Activity;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.klsoft.logintest.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KLSoftKeyboardService implements KeyboardView.OnKeyboardActionListener {

    private static final Pattern ENG_PATTERN = Pattern.compile("[a-zA-Z]");
    private KeyboardView keyboardView;
    private KLSoftKeyboard.LanguageKey languageKey;
    private Timer timerLongPress;
    private boolean isSymbol = false;
    private boolean isKoreaKeyBoard = false;
    private boolean isKeyRelease = false;
    private boolean isFirstPopUp;
    private int prevOrientation = Configuration.ORIENTATION_UNDEFINED;
    private EditText editText;

    private AppCompatActivity activity;

    public KLSoftKeyboardService(AppCompatActivity activity) {
        this.activity = activity;
        languageKey = KLSoftKeyboard.LanguageKey.ENGLISH;
        timerLongPress = new Timer();
        isFirstPopUp = true;
        keyboardView = activity.findViewById(R.id.keyboard);
        setKeyboard(activity, languageKey);
        keyboardView.setOnKeyboardActionListener(this);
        // Hide the standard keyboard initially
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onPress(int primaryCode) {
        InputConnection ic = new CustomInputConnection(editText);
        isKeyRelease = false;
        timerLongPress.schedule(new TimerTask() {
            @Override
            public void run() {
                if (primaryCode == KLSoftKeyboard.DELETE_KEY_CODE) {
                    while (!isKeyRelease) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        deleteText(ic);
                    }
                }
            }
        }, ViewConfiguration.getLongPressTimeout());
    }

    @Override
    public void onRelease(int primaryCode) {
        isKeyRelease = true;
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = new CustomInputConnection(editText);
        switch (primaryCode) {
            case KLSoftKeyboard.DELETE_KEY_CODE:
                deleteText(ic);
                break;

            case KLSoftKeyboard.SHIFT_KEY_CODE:
                if (!isKoreaKeyBoard)
                    keyboardView.setShifted(!keyboardView.isShifted());
                else {
                    languageKey = KLSoftKeyboard.LanguageKey.KOREA_SHIFT;
                    setKeyboard(activity, languageKey);
                }
                break;

            case KLSoftKeyboard.LANGUAGE_KEY_CODE: //switch keyboard_default
                isKoreaKeyBoard = !isKoreaKeyBoard;
                languageKey = isKoreaKeyBoard
                        ? KLSoftKeyboard.LanguageKey.KOREA
                        : KLSoftKeyboard.LanguageKey.ENGLISH;
                setKeyboard(activity, languageKey);
                isSymbol = false;
                break;

            case KLSoftKeyboard.ENTER_KEY_CODE:
                hideCustomKeyboard();
                break;

            case KLSoftKeyboard.SPACE_KEY_CODE:
                ic.commitText(" ", 1);
                break;

            case KLSoftKeyboard.SYMBOL_KEY_CODE:
                if (isSymbol) {
                    languageKey = isKoreaKeyBoard
                            ? KLSoftKeyboard.LanguageKey.KOREA
                            : KLSoftKeyboard.LanguageKey.ENGLISH;
                    isSymbol = false;
                } else {
                    languageKey = KLSoftKeyboard.LanguageKey.SYMBOL;
                    isSymbol = true;
                }
                setKeyboard(activity, languageKey);
                break;

            default:
                char code = (char) primaryCode;
                String c;
                if (Character.isLetter(code)) {
                    if (!isKoreaKeyBoard) {
                        if (keyboardView.isShifted()) code = Character.toUpperCase(code);
                        c = String.valueOf(code);
                    } else {
                        CharSequence sequence = ic.getTextBeforeCursor(1, 0);
                        Matcher mc = ENG_PATTERN.matcher(sequence);
                        if (mc.find()) {
                            c = String.valueOf(code);
                        } else {
                            sequence = EngKorTypingConvertor.convertKor2Eng(sequence.toString());
                            c = EngKorTypingConvertor.convertKor2Eng(String.valueOf(code));
                            c = EngKorTypingConvertor.convertEng2Kor(sequence + c);
                            ic.deleteSurroundingText(1, 0);
                        }
                    }
                } else {
                    c = String.valueOf(code);
                }
                ic.commitText(c, 1);
        }

    }

    public void onComputeInsets() {
        int orientation = activity.getResources().getConfiguration().orientation;
        if (prevOrientation != orientation) {
            setKeyboard(activity, languageKey);
            prevOrientation = orientation;
        }
    }

    private void showWindow() {
        int orientation = activity.getResources().getConfiguration().orientation;
        if (prevOrientation != orientation) {
            setKeyboard(activity, languageKey);
            isFirstPopUp = true;
        }
        if (isFirstPopUp) {
            //validate keyboard first popup arrange incorrectly
            AtomicInteger i = new AtomicInteger();
            final View activityRootView = keyboardView.findViewById(R.id.keyboard);
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 0 && i.get() < 1) {
                    setKeyboard(activity, languageKey);
                    i.getAndIncrement();
                    isFirstPopUp = false;
                }
            });
        }
    }

    private void setKeyboard(AppCompatActivity activity, KLSoftKeyboard.LanguageKey languageKey) {
        Keyboard keyboard = new KLSoftKeyboard(activity, R.xml.keyboard, languageKey, 10, 0);
        keyboardView.setKeyboard(keyboard);
    }

    private void deleteText(InputConnection ic) {
        if (isKoreaKeyBoard) {
            CharSequence sequence = ic.getTextBeforeCursor(1, 0);
            if (!"".contentEquals(sequence)) {
                sequence = EngKorTypingConvertor.convertKor2Eng(sequence.toString());
                sequence = sequence.subSequence(0, sequence.length() - 1);
                sequence = EngKorTypingConvertor.convertEng2Kor(sequence.toString());
                ic.deleteSurroundingText(1, 0);
                ic.commitText(sequence, 1);
            }
        } else {
            ic.deleteSurroundingText(1, 0);
        }
    }

    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    private void showCustomKeyboard(View v) {
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        this.editText = (EditText) v;
        if (v != null)
            ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(v.getWindowToken(), 0);
        showWindow();
    }

    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    public void registerEditText(int resid) {
        EditText editText = activity.findViewById(resid);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                showCustomKeyboard(v);
            else
                hideCustomKeyboard();
        });
        editText.setOnClickListener(this::showCustomKeyboard);
        editText.setOnTouchListener((v, event) -> {
            EditText edittext1 = (EditText) v;
            int inType = edittext1.getInputType();          // Backup the input type
            edittext1.setInputType(InputType.TYPE_NULL);    // Disable standard keyboard
            edittext1.onTouchEvent(event);                  // Call native handler
            edittext1.setInputType(inType);                 // Restore input type
            return true;                                    // Consume touch event
        });
        editText.setInputType(editText.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void log(String message) {
        Log.d("KLSoft", "message:  " + message);
    }

    public void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
