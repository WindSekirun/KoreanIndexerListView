package com.github.windsekirun.koreanindexer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class KoreanIndexerListView extends ListView {
    private Context context;
    private Handler listHandler = new Handler();

    private boolean showLetter = true;
    private float leftPosition;
    private int indexSize;
    private float radius;
    private int indWidth;
    private int delayMillis;
    private boolean useSection;
    private String section;
    private static String[] sections = new String[]{};
    private static LinkedHashMap<String, Integer> mapIndex = new LinkedHashMap<>();
    private RectF positionRect;
    private RectF sectionPositionRect;
    private Paint backgroundPaint;
    private Paint sectionBackgroundPaint;
    private Paint textPaint;
    private Paint sectionTextPaint;

    public KoreanIndexerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init(attrs);
    }

    public KoreanIndexerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        // initialize variables
        positionRect = new RectF();
        sectionPositionRect = new RectF();
        textPaint = new Paint();
        sectionTextPaint = new Paint();
        backgroundPaint = new Paint();
        sectionBackgroundPaint = new Paint();

        backgroundPaint.setAntiAlias(true);
        sectionBackgroundPaint.setAntiAlias(true);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KoreanIndexerListView, 0, 0);

        int indexerBackground = array.getColor(R.styleable.KoreanIndexerListView_indexerBackground, 0xffffffff);
        int sectionBackground = array.getColor(R.styleable.KoreanIndexerListView_sectionBackground, 0xffffffff);
        int indexerTextColor = array.getColor(R.styleable.KoreanIndexerListView_indexerTextColor, 0xff000000);
        int sectionTextColor = array.getColor(R.styleable.KoreanIndexerListView_sectionTextColor, 0xff000000);
        float indexerRadius = array.getFloat(R.styleable.KoreanIndexerListView_indexerRadius, 60f);
        int indexerWidth = array.getInt(R.styleable.KoreanIndexerListView_indexerWidth, 20);
        int sectionDelay = array.getInt(R.styleable.KoreanIndexerListView_sectionDelay, 3 * 1000);
        boolean useSection = array.getBoolean(R.styleable.KoreanIndexerListView_useSection, true);

        setIndexerBackgroundColor(indexerBackground);
        setSectionBackgroundColor(sectionBackground);
        setIndexerTextColor(indexerTextColor);
        setSectionTextColor(sectionTextColor);
        setIndexerRadius(indexerRadius);
        setIndexerWidth(indexerWidth);
        setSectionDelayMillis(sectionDelay);
        setUseSection(useSection);

        array.recycle();
    }

    /**
     * 인덱서 백그라운드 색상 설정
     * [XML Field] indexerBackground
     *
     * @param colorInt 설정할 색상
     */
    public void setIndexerBackgroundColor(@ColorInt int colorInt) {
        backgroundPaint.setColor(colorInt);
    }

    /**
     * 패스트 스크롤 텍스트의 배경 색상
     * [XML Field] sectionBackground
     *
     * @param colorInt 설정할 색상
     */
    public void setSectionBackgroundColor(@ColorInt int colorInt) {
        sectionBackgroundPaint.setColor(colorInt);
    }

    /**
     * 인덱서 텍스트 색상 설정
     * [XML Field] indexerTextColor
     *
     * @param colorInt 설정할 색상
     */
    public void setIndexerTextColor(@ColorInt int colorInt) {
        textPaint.setColor(colorInt);
    }

    /**
     * 패스트 스크롤 텍스트의 색상 설정
     * [XML Field] sectionTextColor
     *
     * @param colorInt 설정할 색상
     */
    public void setSectionTextColor(@ColorInt int colorInt) {
        textPaint.setColor(colorInt);
    }

    /**
     * 인덱서 배경의 곡선도
     * [XML Field] indexerRadius
     *
     * @param radius 설정할 곡선도
     */
    public void setIndexerRadius(float radius) {
        this.radius = radius;
    }

    /**
     * 인덱서의 전체 너비
     * [XML Field] indexerWidth
     *
     * @param width 설정할 너비
     */
    public void setIndexerWidth(int width) {
        this.indWidth = width;
    }

    /**
     * 패스트 스크롤 텍스트의 표시 시간 설정
     * [XML Field] sectionDelay
     *
     * @param delayMillis 설정할 시간 (단위: ms)
     */
    public void setSectionDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    /**
     * 패스트 스크롤 텍스트의 표시 여부
     * [XML Field] useSection
     *
     * @param useSection 표시 여부
     */
    public void setUseSection(boolean useSection) {
        this.useSection = useSection;
    }

    /**
     * 키워드 리스트 설정
     * <p>
     * 이 메소드에 넘겨지는 리스트 파라미터는 정렬 여부와 상관이 없습니다.
     * setAdapter() 전에 호출해주세요.
     *
     * @param keywordList 키워드 리스트
     */
    public void setKeywordList(ArrayList<String> keywordList) {
        Collections.sort(keywordList, OrderingByKorean.getComparator());

        for (int i = 0; i < keywordList.size(); i++) {
            String item = keywordList.get(i);
            String index = item.substring(0, 1);

            char c = index.charAt(0);
            if (OrderingByKorean.isKorean(c)) {
                index = String.valueOf(KoreanChar.getCompatChoseong(c));
            }

            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }

        ArrayList<String> indexList = new ArrayList<>(mapIndex.keySet());
        sections = new String[indexList.size()];
        indexList.toArray(sections);

        indexList.clear();
        indexList.trimToSize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scaledWidth = indWidth * getDensity();
        leftPosition = this.getWidth() - this.getPaddingRight() - scaledWidth;

        positionRect.left = leftPosition;
        positionRect.right = leftPosition + scaledWidth;
        positionRect.top = this.getPaddingTop();
        positionRect.bottom = this.getHeight() - this.getPaddingBottom();

        canvas.drawRoundRect(positionRect, radius, radius, backgroundPaint);
        indexSize = (this.getHeight() - this.getPaddingTop() - getPaddingBottom()) / sections.length;

        textPaint.setTextSize(scaledWidth / 2);
        for (int i = 0; i < sections.length; i++) {
            canvas.drawText(sections[i].toUpperCase(), leftPosition + textPaint.getTextSize() / 2,
                    getPaddingTop() + indexSize * (i + 1), textPaint);
        }

        sectionTextPaint.setTextSize(50 * getScaledDensity());
        if (useSection && showLetter & !TextUtils.isEmpty(section)) {
            float mPreviewPadding = 5 * getDensity();
            float previewTextWidth = sectionTextPaint.measureText(section.toUpperCase());
            float previewSize = 2 * mPreviewPadding + sectionTextPaint.descent() - sectionTextPaint.ascent();

            sectionPositionRect.left = (getWidth() - previewSize) / 2;
            sectionPositionRect.right = (getWidth() - previewSize) / 2 + previewSize;
            sectionPositionRect.top = (getHeight() - previewSize) / 2;
            sectionPositionRect.bottom = (getHeight() - previewSize) / 2 + previewSize;

            canvas.drawRoundRect(sectionPositionRect, mPreviewPadding, mPreviewPadding, sectionBackgroundPaint);
            canvas.drawText(section.toUpperCase(),
                    sectionPositionRect.left + (previewSize - previewTextWidth) / 2 - 1,
                    sectionPositionRect.top + mPreviewPadding - sectionTextPaint.ascent() + 1, sectionTextPaint);
        }
    }

    private float getDensity() {
        return context.getResources().getDisplayMetrics().density;
    }

    private float getScaledDensity() {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (x < leftPosition) {
                    return super.onTouchEvent(event);
                } else {
                    try {
                        float y = event.getY() - this.getPaddingTop() - getPaddingBottom();
                        int currentPosition = (int) Math.floor(y / indexSize);
                        section = sections[currentPosition];
                        showLetter = true;
                        this.setSelection(((SectionIndexer) getAdapter()).getPositionForSection(currentPosition));
                    } catch (Exception e) {
                        Log.v(KoreanIndexerListView.class.getSimpleName(),
                                "Something error happened. but who ever care this exception? " + e.getMessage());
                    }
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (x < leftPosition) {
                    return super.onTouchEvent(event);
                } else {
                    try {
                        float y = event.getY();
                        int currentPosition = (int) Math.floor(y / indexSize);
                        section = sections[currentPosition];
                        showLetter = true;
                        this.setSelection(((SectionIndexer) getAdapter()).getPositionForSection(currentPosition));
                    } catch (Exception e) {
                        Log.v(KoreanIndexerListView.class.getSimpleName(),
                                "Something error happened. but who ever care this exception? " + e.getMessage());
                    }
                }
                break;

            }

            case MotionEvent.ACTION_UP: {
                listHandler.postDelayed(showLetterRunnable, delayMillis);
                break;
            }
        }
        return true;
    }

    private Runnable showLetterRunnable = new Runnable() {
        @Override
        public void run() {
            showLetter = false;
            KoreanIndexerListView.this.invalidate();
        }
    };

    @SuppressWarnings("WeakerAccess")
    public abstract static class KoreanIndexerAdapter<T> extends ArrayAdapter<T> implements SectionIndexer {

        public KoreanIndexerAdapter(@NonNull Context context, ArrayList<T> list) {
            super(context, 0, list);
        }

        @Override
        public Object[] getSections() {
            return sections;
        }

        @Override
        public int getPositionForSection(int section) {
            String letter = sections[section];
            return mapIndex.get(letter);
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }
    }

    // this minimum class forked from https://github.com/bangjunyoung/KoreanTextMatcher
    private static class KoreanChar {

        private static final int CHOSEONG_COUNT = 19;
        private static final int JUNGSEONG_COUNT = 21;
        private static final int JONGSEONG_COUNT = 28;
        private static final int HANGUL_SYLLABLE_COUNT = CHOSEONG_COUNT * JUNGSEONG_COUNT * JONGSEONG_COUNT;
        private static final int HANGUL_SYLLABLES_BASE = 0xAC00;
        private static final int HANGUL_SYLLABLES_END = HANGUL_SYLLABLES_BASE + HANGUL_SYLLABLE_COUNT;

        private static final int[] COMPAT_CHOSEONG_MAP = new int[]{
                0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
                0x3146, 0x3147, 0x3148, 0x3149, 0x314A, 0x314B, 0x314C, 0x314D, 0x314E
        };

        private KoreanChar() {
            // Can never be instantiated.
        }


        private static boolean isSyllable(char c) {
            return HANGUL_SYLLABLES_BASE <= c && c < HANGUL_SYLLABLES_END;
        }

        private static char getCompatChoseong(char value) {
            if (!isSyllable(value))
                return '\0';

            final int choseongIndex = getChoseongIndex(value);
            return (char) COMPAT_CHOSEONG_MAP[choseongIndex];
        }

        private static int getChoseongIndex(char syllable) {
            final int syllableIndex = syllable - HANGUL_SYLLABLES_BASE;
            return syllableIndex / (JUNGSEONG_COUNT * JONGSEONG_COUNT);
        }
    }

    // this class come from http://reimaginer.tistory.com/entry/한글영어특수문자-순-정렬하는-java-compare-메서드-만들기
    private static class OrderingByKorean {
        private static final int REVERSE = -1;
        private static final int LEFT_FIRST = -1;
        private static final int RIGHT_FIRST = 1;

        private static Comparator<String> getComparator() {
            return new Comparator<String>() {
                public int compare(String left, String right) {
                    return OrderingByKorean.compare(left, right);
                }
            };
        }

        private static int compare(String left, String right) {

            left = StringUtils.upperCase(left).replaceAll(" ", "");
            right = StringUtils.upperCase(right).replaceAll(" ", "");

            int leftLen = left.length();
            int rightLen = right.length();
            int minLen = Math.min(leftLen, rightLen);

            for (int i = 0; i < minLen; ++i) {
                char leftChar = left.charAt(i);
                char rightChar = right.charAt(i);

                if (leftChar != rightChar) {
                    if (isKoreanAndEnglish(leftChar, rightChar) || isKoreanAndNumber(leftChar, rightChar)
                            || isEnglishAndNumber(leftChar, rightChar) || isKoreanAndSpecial(leftChar, rightChar)) {
                        return (leftChar - rightChar) * REVERSE;
                    } else if (isEnglishAndSpecial(leftChar, rightChar) || isNumberAndSpecial(leftChar, rightChar)) {
                        if (isEnglish(leftChar) || isNumber(leftChar)) {
                            return LEFT_FIRST;
                        } else {
                            return RIGHT_FIRST;
                        }
                    } else {
                        return leftChar - rightChar;
                    }
                }
            }

            return leftLen - rightLen;
        }

        private static boolean isKoreanAndEnglish(char ch1, char ch2) {
            return (isEnglish(ch1) && isKorean(ch2)) || (isKorean(ch1) && isEnglish(ch2));
        }

        private static boolean isKoreanAndNumber(char ch1, char ch2) {
            return (isNumber(ch1) && isKorean(ch2)) || (isKorean(ch1) && isNumber(ch2));
        }

        private static boolean isEnglishAndNumber(char ch1, char ch2) {
            return (isNumber(ch1) && isEnglish(ch2)) || (isEnglish(ch1) && isNumber(ch2));
        }

        private static boolean isKoreanAndSpecial(char ch1, char ch2) {
            return (isKorean(ch1) && isSpecial(ch2)) || (isSpecial(ch1) && isKorean(ch2));
        }

        private static boolean isEnglishAndSpecial(char ch1, char ch2) {
            return (isEnglish(ch1) && isSpecial(ch2)) || (isSpecial(ch1) && isEnglish(ch2));
        }

        private static boolean isNumberAndSpecial(char ch1, char ch2) {
            return (isNumber(ch1) && isSpecial(ch2)) || (isSpecial(ch1) && isNumber(ch2));
        }

        private static boolean isEnglish(char ch) {
            return (ch >= (int) 'A' && ch <= (int) 'Z') || (ch >= (int) 'a' && ch <= (int) 'z');
        }

        private static boolean isKorean(char ch) {
            return ch >= Integer.parseInt("AC00", 16) && ch <= Integer.parseInt("D7A3", 16);
        }

        private static boolean isNumber(char ch) {
            return ch >= (int) '0' && ch <= (int) '9';
        }

        private static boolean isSpecial(char ch) {
            return (ch >= (int) '!' && ch <= (int) '/') // !"#$%&'()*+,-./
                    || (ch >= (int) ':' && ch <= (int) '@') //:;<=>?@
                    || (ch >= (int) '[' && ch <= (int) '`') //[\]^_`
                    || (ch >= (int) '{' && ch <= (int) '~'); //{|}~
        }
    }

}