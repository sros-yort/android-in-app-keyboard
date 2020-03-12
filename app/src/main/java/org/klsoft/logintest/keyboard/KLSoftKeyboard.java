package org.klsoft.logintest.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.klsoft.logintest.R;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class KLSoftKeyboard extends Keyboard {

    static final char SHIFT_KEY_CODE = '\1';
    static final char SPACE_KEY_CODE = '\2';
    static final char SYMBOL_KEY_CODE = '\3';
    static final char ENTER_KEY_CODE = '\4';
    static final char DELETE_KEY_CODE = '\5';
    static final char LANGUAGE_KEY_CODE = '\6';

    private static int SHIFT_KEY_INDEX;
    private static int DELETE_KEY_INDEX;
    private static int SYMBOL_KEY_INDEX;
    private static int LANGUAGE_KEY_INDEX;
    private static int SPACE_KEY_INDEX;
    private static int ENTER_KEY_INDEX;

    private final int NUM_KEY_ROWS = 5;
    private Context context;

    enum LanguageKey {
        ENGLISH,
        KOREA,
        KOREA_SHIFT,
        SYMBOL
    }

    private static char[] englishKeys =
            {
                    '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', ' ',
                    SHIFT_KEY_CODE, 't', 'u', 'v', 'w', 'x', 'y', 'z', DELETE_KEY_CODE, ' ',
                    SYMBOL_KEY_CODE, ',', LANGUAGE_KEY_CODE, SPACE_KEY_CODE, '.', ' ', ENTER_KEY_CODE
            };

    private static char[] koreaKeys =
            {
                    '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                    'ㄱ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅅ', 'ㅈ', 'ㅊ', 'ㅋ',
                    'ㅌ', 'ㅍ', 'ㅎ', 'ㅇ', 'ㅏ', 'ㅑ', 'ㅓ', 'ㅕ', 'ㅗ', ' ',
                    SHIFT_KEY_CODE, 'ㅛ', 'ㅜ', 'ㅠ', 'ㅡ', 'ㅣ', 'ㅐ', 'ㅔ', DELETE_KEY_CODE, ' ',
                    SYMBOL_KEY_CODE, ',', LANGUAGE_KEY_CODE, SPACE_KEY_CODE, '.', ' ', ENTER_KEY_CODE
            };

    private static char[] koreaShiftKeys =
            {
                    '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                    'ㄲ', 'ㄴ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅃ', 'ㅆ', 'ㅉ', 'ㅊ', 'ㅋ',
                    'ㅌ', 'ㅍ', 'ㅎ', 'ㅇ', 'ㅏ', 'ㅑ', 'ㅓ', 'ㅕ', 'ㅗ', ' ',
                    SHIFT_KEY_CODE, 'ㅛ', 'ㅜ', 'ㅠ', 'ㅡ', 'ㅣ', 'ㅒ', 'ㅖ', DELETE_KEY_CODE, ' ',
                    SYMBOL_KEY_CODE, ',', LANGUAGE_KEY_CODE, SPACE_KEY_CODE, '.', ' ', ENTER_KEY_CODE
            };

    private static char[] symbolKeys =
            {
                    '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                    '+', '×', '÷', '=', '/', '_', '€', '£', '¥', '₩',
                    '!', '@', '#', '$', '%', '^', '&', '(', ')', ' ',
                    SHIFT_KEY_CODE, '-', '\'', '"', ':', ';', '*', '?', ' ', DELETE_KEY_CODE,
                    SYMBOL_KEY_CODE, ',', LANGUAGE_KEY_CODE, SPACE_KEY_CODE, '.', ' ', ENTER_KEY_CODE
            };

    KLSoftKeyboard(Context context, int xmlLayoutResId, LanguageKey languageKey, int columns, int horizontalPadding) {
        super(context, xmlLayoutResId, getRandomKeyboard(languageKey), columns, horizontalPadding);
        this.context = context;
        changeKeyHeight();
    }

    private static CharSequence getRandomKeyboard(LanguageKey languageKey) {

        char[] ranKeyboard;
        switch (languageKey) {
            case KOREA:
                ranKeyboard = koreaKeys;
                break;
            case KOREA_SHIFT:
                ranKeyboard = koreaShiftKeys;
                break;
            case SYMBOL:
                ranKeyboard = symbolKeys;
                break;
            default:
                ranKeyboard = englishKeys;
        }
        setSpecialKeyIndex(ranKeyboard);

        int min = 0;
        int max = 10;
        //Random number only
        for (int i = 0; i < 10; i++) {
            int ranIndex = new Random().nextInt(max - min) + min;
            char temp = ranKeyboard[i];
            ranKeyboard[i] = ranKeyboard[ranIndex];
            ranKeyboard[ranIndex] = temp;
            min = i + 1;
        }
        //Random for characters
        min = 10;
        max = ranKeyboard.length;
        for (int i = min; i < ranKeyboard.length; i++) {
            if (isRandomKey(i, ranKeyboard)) {
                int ranIndex = new Random().nextInt((max - min)) + min;
                //if key is swapable..
                if (isRandomKey(ranIndex, ranKeyboard)) {
                    char temp = ranKeyboard[i];
                    ranKeyboard[i] = ranKeyboard[ranIndex];
                    ranKeyboard[ranIndex] = temp;
                    min = i + 1;
                }
            }
        }
        return new String(ranKeyboard);
    }

    private static boolean isRandomKey(int i, char[] ch) {
        return i != SHIFT_KEY_INDEX &&
                i != DELETE_KEY_INDEX &&
                i != SPACE_KEY_INDEX &&
                i != ENTER_KEY_INDEX &&
                i != SYMBOL_KEY_INDEX &&
                i != LANGUAGE_KEY_INDEX &&
                ch[i] != ' ';
    }

    private static void setSpecialKeyIndex(char[] chars) {
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            switch (ch) {
                case SPACE_KEY_CODE:
                    SPACE_KEY_INDEX = i;
                    break;
                case SHIFT_KEY_CODE:
                    SHIFT_KEY_INDEX = i;
                    break;
                case SYMBOL_KEY_CODE:
                    SYMBOL_KEY_INDEX = i;
                    break;
                case ENTER_KEY_CODE:
                    ENTER_KEY_INDEX = i;
                    break;
                case DELETE_KEY_CODE:
                    DELETE_KEY_INDEX = i;
                    break;
                case LANGUAGE_KEY_CODE:
                    LANGUAGE_KEY_INDEX = i;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int[] getNearestKeys(int x, int y) {
        //Re-calculate keys' position
        List<Key> keys = getKeys();
        for (int i = 0; i < keys.size(); i++) {
            int startX = keys.get(i).x;
            int endX = startX + keys.get(i).width;
            int startY = keys.get(i).y;
            int endY = startY + keys.get(i).height;
            if (x > startX && x < endX && y > startY && y < endY) {
                return new int[]{i};
            }
        }
        return new int[0];
    }

    @Override
    public int getHeight() {
        return getKeyHeight() * NUM_KEY_ROWS;
    }

    private void changeKeyHeight() {
        List<Key> keys = getKeys();
        int rowIndex = 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        int defaultHeight = (screenHeight / 3) / NUM_KEY_ROWS;
        int defaultWidth = (screenWidth / 10);
        //fill width/height gap in case last digit of screenWidth/screenHeight is bigger than 0. eg. 768
        if(screenWidth % 10 != 0)
            defaultWidth += 1;
        if(screenHeight % 10 != 0)
            defaultHeight += 1;
        int width = defaultWidth;
        int key_y = 0;
        int key_x = 0;
        int totalWidth = 0;
        for (int i = 0; i < keys.size(); i++) {
            Key key = keys.get(i);
            String[] special_key_index = new String[]{
                    Integer.toString(SHIFT_KEY_INDEX),
                    Integer.toString(DELETE_KEY_INDEX),
                    Integer.toString(LANGUAGE_KEY_INDEX),
                    Integer.toString(SYMBOL_KEY_INDEX)
            };
            boolean isSpcKeyIndex = Arrays.asList(special_key_index)
                    .contains(Integer.toString(i));
            if (isSpcKeyIndex) {
                width += width / 2;
                key.width = width;
            } else if (i == ENTER_KEY_INDEX) {
                width = width * 2;
                key.width = width;
            } else if (i == SPACE_KEY_INDEX) {
                int otherKeysWidth = (2 * width /* "." & ";" key*/ + (
                        width + width / 2) * 2 /*special key width*/
                        + width * 2 /*enter key*/);
                width = screenWidth - otherKeysWidth;
                key.width = width;
            } else if (key.label.charAt(0) == ' ') {
                if (rowIndex >= 3)
                    width = 0;
                key.width = 0;
                key.height = 0;
            } else {
                key.width = width;
            }
            totalWidth += width;
            key.x = key_x;
            key_x += width;
            if (totalWidth >= screenWidth) {
                key_y += defaultHeight;
                if (rowIndex == 1)
                    key_x = defaultWidth / 2;
                else
                    key_x = 0;
                totalWidth = 0;
                rowIndex++;
            }
            width = defaultWidth;
        }
        keys.get(SHIFT_KEY_INDEX).label = null;
        keys.get(SHIFT_KEY_INDEX).icon = context.getResources().getDrawable(R.drawable.icon_shift_key, null);
        keys.get(DELETE_KEY_INDEX).label = null;
        keys.get(DELETE_KEY_INDEX).icon = context.getResources().getDrawable(R.drawable.icon_delete_key, null);
        keys.get(SPACE_KEY_INDEX).label = "space";
        keys.get(SYMBOL_KEY_INDEX).label = "?123";
        keys.get(ENTER_KEY_INDEX).label = null;
        keys.get(ENTER_KEY_INDEX).icon = context.getResources().getDrawable(R.drawable.enter_key, null);
        keys.get(LANGUAGE_KEY_INDEX).label = null;
        keys.get(LANGUAGE_KEY_INDEX).icon = context.getResources().getDrawable(R.drawable.icon_language_key, null);
    }

    public void log(String message) {
        Log.d("KLSoft", "message:  " + message);
    }

    public void toast(String message) {
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
