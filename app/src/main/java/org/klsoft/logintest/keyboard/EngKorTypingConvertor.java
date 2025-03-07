package org.klsoft.logintest.keyboard;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author : tadadak (dev.tadadak@gmail.com)
 * Date : 2020-01-09
 * Desc : 영문->한글 / 한글->영문 타이핑 상호 변환모듈
 */
class EngKorTypingConvertor {

    /**
     * 유니코드표내 한글문자 전체 개수
     * 초성 19, 중성 21, 종성 28개의 전체 조합.
     * 11172 = 19 * 21 * 28
     */
    private static final int TOTAL_HANGUL_CHAR = 11172;

    /**
     * 표음문자
     **/
    private static final int PHONOGRAM = 28;

    /**
     * 모음수
     **/
    private static final int VOWEL = 21;

    /**
     * 한글문자 시작코드 [가], 코드번호 = 44032
     **/
    private static final int HANGUL_CHAR_START_CODE = 0xAC00;

    private static final Pattern ENG_PATTERN = Pattern.compile("[a-zA-Z]");

    /**
     * 음절(Syllable)타입
     **/
    enum HangulSyllableType {
        CHOSUNG, JUNGSUNG, JONGSUNG
    }

    /**
     * 한글변환시 리턴문자열 타입
     **/
    enum KorConvRtnType {
        ALPHABET, JAUM_MOUM
    }

    /**
     * 초성(19)
     * ㄱ      ㄲ      ㄴ      ㄷ      ㄸ
     * ㄹ      ㅁ      ㅂ      ㅃ      ㅅ
     * ㅆ      ㅇ      ㅈ      ㅉ      ㅊ
     * ㅋ      ㅌ      ㅍ      ㅎ
     */
    private static final String[] CHOSUNG_ENG = {
            "r", "R", "s", "e", "E",
            "f", "a", "q", "Q", "t",
            "T", "d", "w", "W", "c",
            "z", "x", "v", "g"
    };

    /**
     * 중성(21)
     * ㅏ      ㅐ      ㅑ      ㅒ      ㅓ
     * ㅔ      ㅕ      ㅖ      ㅗ      ㅘ
     * ㅙ      ㅚ      ㅛ      ㅜ      ㅝ
     * ㅞ      ㅟ      ㅠ      ㅡ      ㅢ
     * ㅣ
     */
    private static final String[] JUNGSONG_ENG = {
            "k", "o", "i", "O", "j",
            "p", "u", "P", "h", "hk",
            "ho", "hl", "y", "n", "nj",
            "np", "nl", "b", "m", "ml",
            "l"
    };

    /**
     * 종성(1+27)
     * 없음    ㄱ      ㄲ      ㄳ      ㄴ
     * ㄵ      ㄶ      ㄷ      ㄹ      ㄺ
     * ㄻ      ㄼ      ㄽ      ㄾ      ㄿ
     * ㅀ      ㅁ      ㅂ      ㅄ      ㅅ
     * ㅆ      ㅇ      ㅈ      ㅊ      ㅋ
     * ㅌ      ㅍ      ㅎ
     */
    private static final String[] JONGSUNG_ENG = {
            "", "r", "R", "rt", "s",
            "sw", "sg", "e", "f", "fr",
            "fa", "fq", "ft", "fx", "fv",
            "fg", "a", "q", "qt", "t",
            "T", "d", "w", "c", "z",
            "x", "v", "g"
    };

    /**
     * 단일 자음(30) - ㄸ,ㅃ,ㅉ 포함
     * ㄱ      ㄲ      ㄳ      ㄴ      ㄵ
     * ㄶ      ㄷ      ㄸ      ㄹ      ㄺ
     * ㄻ      ㄼ      ㄽ      ㄾ      ㄿ
     * ㅀ      ㅁ      ㅂ      ㅃ      ㅄ
     * ㅅ      ㅆ      ㅇ      ㅈ      ㅉ
     * ㅊ      ㅋ      ㅌ      ㅍ      ㅎ
     */
    private static final String[] SINGLE_JAUM_ENG = {
            "r", "R", "rt", "s", "sw",
            "sg", "e", "E", "f", "fr",
            "fa", "fq", "ft", "fx", "fv",
            "fg", "a", "q", "Q", "qt",
            "t", "T", "d", "w", "W",
            "c", "z", "x", "v", "g"
    };


    /**
     * 초성(19)
     */
    private static final char[] SPLIT_CHOSUNG_CHAR = {
            0x3131, 0x3132, 0x3134, 0x3137, 0x3138,
            0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
            0x3146, 0x3147, 0x3148, 0x3149, 0x314a,
            0x314b, 0x314c, 0x314d, 0x314e
    };
    /**
     * 중성(21)
     */
    private static final char[] SPLIT_JUNGSUNG_CHAR = {
            0x314f, 0x3150, 0x3151, 0x3152, 0x3153,
            0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
            0x3159, 0x315a, 0x315b, 0x315c, 0x315d,
            0x315e, 0x315f, 0x3160, 0x3161, 0x3162,
            0x3163
    };
    /**
     * 종성(1+27)
     */
    private static final char[] SPLIT_JONGSUNG_CHAR = {
            0x0000, 0x3131, 0x3132, 0x3133, 0x3134,
            0x3135, 0x3136, 0x3137, 0x3139, 0x313a,
            0x313b, 0x313c, 0x313d, 0x313e, 0x313f,
            0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
            0x3146, 0x3147, 0x3148, 0x314a, 0x314b,
            0x314c, 0x314d, 0x314e
    };

    private static Map<String, Integer> chosungEngIdx;
    private static Map<String, Integer> jungsungEngIdx;
    private static Map<String, Integer> jongsungEngIdx;

    /**
     * 단일 자음 한글코드표 범위
     **/
    private static final int[] JAUM_CODE_AREA = {34097, 34126};

    /**
     * 단일 모음 한글코드표 범위
     **/
    private static final int[] MOUM_CODE_AREA = {34127, 34147};

    static {
        chosungEngIdx = new HashMap<>();
        jungsungEngIdx = new HashMap<>();
        jongsungEngIdx = new HashMap<>();

        for (int i = 0; i < CHOSUNG_ENG.length; i++) {
            chosungEngIdx.put(CHOSUNG_ENG[i], i);
        }
        for (int i = 0; i < JUNGSONG_ENG.length; i++) {
            jungsungEngIdx.put(JUNGSONG_ENG[i], i);
        }
        for (int i = 0; i < JONGSUNG_ENG.length; i++) {
            jongsungEngIdx.put(JONGSUNG_ENG[i], i);
        }
    }

    /**
     * 단일영문자에 대응하는 음절코드표 index값 추출.
     *
     * @param type    CHOSUNG: 초성 | JUNGSUNG: 중성 | JONGSUNG: 종성
     * @param engChar 단일영문자
     */
    private static int getSyllableCode(HangulSyllableType type, String engChar) {
        Integer idx;
        switch (type) {
            case CHOSUNG:
                idx = chosungEngIdx.get(engChar);
                if (idx != null) {
                    return idx * VOWEL * PHONOGRAM;
                }
                break;
            case JUNGSUNG:
                idx = jungsungEngIdx.get(engChar);
                if (idx != null) {
                    return idx * PHONOGRAM;
                }
                break;
            case JONGSUNG:
                // 종성의 첫번째값은 쓰지 않음.
                idx = jongsungEngIdx.get(engChar);
                if (idx != null) {
                    return idx;
                }
                break;
        }
        return -1;
    }

    /**
     * 영문자열내 중성,종성 음절코드표 index값 추출
     *
     * @param type     CHOSUNG: 초성 | JUNGSUNG: 중성 | JONGSUNG: 종성
     * @param idx      문자열 index
     * @param engStr   영문자열
     * @param isDouble 한글자 or 두글자로 이루어졌는지 여부
     * @return int
     */
    private static int getSyllableCode(HangulSyllableType type, int idx, String engStr, boolean isDouble) {
        int offset = 1;
        // 두개의 자음or모음 조합일 경우, 탐색offset값 조정
        if (isDouble) offset = 2;
        if ((idx + offset) <= engStr.length()) {
            return getSyllableCode(type, engStr.substring(idx, idx + offset));
        }
        return -1;
    }

    /**
     * 한글로 조합되는 영문문자열을, 한글문자열로 변환
     * ex) gksrmffh -> 한글로
     *
     * @param engStr English Charactor
     * @param allowDoubleJaum - 복합자음 변환을 허용할지 여부.
     *                        - (default)   false: 허용안함 ㄱㅅ  ㅂㅅ
     *                        true : 허용함   ㄳ ㅄ
     * @return String
     */
    private static String convertEng2Kor(String engStr, boolean allowDoubleJaum) {
        StringBuilder sb = new StringBuilder();
        String currentChar;
        Matcher mc;

        int chosungCode;
        int jungsungCode;
        int jongsungCode;
        int tempJungsungCode;
        int tempJongsungCode;

        for (int i = 0; i < engStr.length(); i++) {
            currentChar = engStr.substring(i, i + 1);
            mc = ENG_PATTERN.matcher(currentChar);
            // 영문자(a-zA-Z) 아니면, 그대로 넘긴다.
            if (!mc.find()) {
                sb.append(currentChar);
                continue;
            }

            // 초성코드 추출
            chosungCode = getSyllableCode(HangulSyllableType.CHOSUNG, currentChar);

            // 1. 초성코드인 경우
            if (chosungCode != -1) {
                i++; // 다음문자로

                // 1.1. 2자 조합의 중성코드 추출
                tempJungsungCode = getSyllableCode(HangulSyllableType.JUNGSUNG, i, engStr, true);

                if (tempJungsungCode != -1) {
                    // 1.1.1. 찾으면, 다다음으로 탐색 index 설정
                    jungsungCode = tempJungsungCode;
                    i += 2;
                } else {
                    // 1.1.2. 없다면, 단일조합 중성코드 추출
                    jungsungCode = getSyllableCode(HangulSyllableType.JUNGSUNG, i, engStr, false);
                    // 단일조합 중성코드도 없는 경우,
                    if (jungsungCode == -1) {
                        char chars = (char) (SPLIT_CHOSUNG_CHAR[chosungEngIdx.get(currentChar)] - HANGUL_CHAR_START_CODE);

                        if (chars >= JAUM_CODE_AREA[0] && chars <= JAUM_CODE_AREA[1]) {
                            // allowDoubleJaum = true인 경우, 복합 초성코드 찾아봄 ex) ㄳ ㅄ
                            if (allowDoubleJaum && (i + 1) <= engStr.length()) {
                                String chkCombChar = engStr.substring(i, i + 1);
                                if (jongsungEngIdx.containsKey(currentChar + chkCombChar)) {
                                    sb.append(SPLIT_JONGSUNG_CHAR[jongsungEngIdx.get(currentChar + chkCombChar)]);
                                    continue;
                                }
                            }//end if

                            sb.append(SPLIT_CHOSUNG_CHAR[chosungEngIdx.get(currentChar)]);
                            i--;
                            continue;
                        }
                    } else {
                        i++;
                    }
                }

                // 1.2. 2자 조합의 종성코드 추출
                tempJongsungCode = getSyllableCode(HangulSyllableType.JONGSUNG, i, engStr, true);
                if (tempJongsungCode != -1) {
                    // 1.2.1. 종성코드 찾았으면, 바로 다음 중성문자에 대한 코드를 추출한다.
                    jongsungCode = tempJongsungCode;
                    tempJungsungCode = getSyllableCode(HangulSyllableType.JUNGSUNG, i + 2, engStr, false);
                    if (tempJungsungCode != -1) { // 코드 값이 있을 경우
                        // 중성코드 찾았으면, 단일조합 종성코드값 저장
                        jongsungCode = getSyllableCode(HangulSyllableType.JONGSUNG, i, engStr, false);
                    } else {
                        i++;
                    }
                } else {
                    // 1.2.2. 종성코드 못찾았을 경우, 그 다음의 중성 문자에 대한 코드 추출.
                    tempJungsungCode = getSyllableCode(HangulSyllableType.JUNGSUNG, i + 1, engStr, false);
                    if (tempJungsungCode != -1) {
                        // 중성 문자가 존재하면, 종성 문자는 없는 한글문자다.
                        jongsungCode = 0;
                        i--;
                    } else {
                        // 단일조합 종성코드 추출
                        jongsungCode = getSyllableCode(HangulSyllableType.JONGSUNG, i, engStr, false);
                        if (jongsungCode == -1) {
                            // 못찾았다면, 초성,중성으로 이루어진 한글이거나.. 그 외의 문자다.
                            jongsungCode = 0;
                            i--;
                        }
                    }
                }
                // 추출한 (초성문자 코드, 중성문자 코드, 종성문자 코드) 합한 후 변환하여 넘김.
                sb.append((char) (HANGUL_CHAR_START_CODE + chosungCode + jungsungCode + jongsungCode));

            } else {
                // 2. 초성코드가 아닌 경우
                // 2.1. 중성인지 검사(종성없이)
                Integer chkIdx = jungsungEngIdx.get(currentChar);
                if (chkIdx != null) {
                    // 2.1.1. 복합중성이 존재할경우
                    if ((i + 2) <= engStr.length()) {
                        String chkCombChar = engStr.substring(i + 1, i + 2);
                        if (jungsungEngIdx.containsKey(currentChar + chkCombChar)) {
                            sb.append(SPLIT_JUNGSUNG_CHAR[jungsungEngIdx.get(currentChar + chkCombChar)]);
                            i++;
                            continue;
                        }
                    }//end if

                    // 2.1.2. 단일중성인 경우
                    char chars = (char) (SPLIT_JUNGSUNG_CHAR[chkIdx] - HANGUL_CHAR_START_CODE);
                    if (chars >= MOUM_CODE_AREA[0] && chars <= MOUM_CODE_AREA[1]) {
                        sb.append(SPLIT_JUNGSUNG_CHAR[jungsungEngIdx.get(currentChar)]);
                    }
                }
            }//end if

        }
        return sb.toString();
    }

    static String convertEng2Kor(String eng) {
        return convertEng2Kor(eng, false);
    }


    /**
     * 한글문자열을, 한글조합되는 영문문자열로 변환
     * ex) 영어로 --> duddjfh
     *
     * @param korStr k c
     * @return String
     */
    static String convertKor2Eng(String korStr) {
        return convertHangul(korStr, KorConvRtnType.ALPHABET);
    }


    /**
     * 한글문자열 -> 영문문자열 or 자음,모음 문자열로 변환
     *
     * @param korStr k c
     * @param korConvRtnType  KorConvRtnType
     * @return String
     */
    private static String convertHangul(String korStr, KorConvRtnType korConvRtnType) {
        StringBuilder split = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < korStr.length(); i++) {
            // 문자표번호 = 유니코드번호 - 0xAC00
            char chars = (char) (korStr.charAt(i) - HANGUL_CHAR_START_CODE);

            if (chars <= TOTAL_HANGUL_CHAR) {

                // Case1. 자음+모음 조합글자인 경우

                // 1.1. 초/중/종성 분리
                int chosungIdx = chars / (VOWEL * PHONOGRAM);
                int jungsungIdx = chars % (VOWEL * PHONOGRAM) / PHONOGRAM;
                int jongsungIdx = chars % (VOWEL * PHONOGRAM) % PHONOGRAM;

                // 1.2. 분리결과 담기
                split.append(SPLIT_CHOSUNG_CHAR[chosungIdx])
                        .append(SPLIT_JUNGSUNG_CHAR[jungsungIdx]);

                // 1.3. 자음분리
                if (jongsungIdx != 0) {
                    // 종성이 존재할경우, 분리결과 담는다
                    split.append(SPLIT_JONGSUNG_CHAR[jongsungIdx]);
                }

                // 1.4. 영문변환결과 담기
                sb.append(CHOSUNG_ENG[chosungIdx])
                        .append(JUNGSONG_ENG[jungsungIdx]);

                if (jongsungIdx != 0) {
                    // 종성이 존재할경우, 영문변환결과에 담는다
                    sb.append(JONGSUNG_ENG[jongsungIdx]);
                }

            } else {
                // Case2. 한글 이외의 문자 or 자음만 있는 경우
                // 2.1. 자음 분리 & 분리결과에 담기
                split.append(((char) (chars + HANGUL_CHAR_START_CODE)));

                // 2.2. 영문변환
                if (chars >= JAUM_CODE_AREA[0] && chars <= JAUM_CODE_AREA[1]) {
                    // 2.2.1 단일자음 범위인 경우
                    int jaum = (chars - JAUM_CODE_AREA[0]);
                    sb.append(SINGLE_JAUM_ENG[jaum]);
                } else if (chars >= MOUM_CODE_AREA[0] && chars <= MOUM_CODE_AREA[1]) {
                    // 2.2.2 단일모음 범위인 경우
                    int moum = (chars - MOUM_CODE_AREA[0]);
                    sb.append(JUNGSONG_ENG[moum]);
                } else {
                    // 2.2.3 그 외 문자
                    sb.append(((char) (chars + HANGUL_CHAR_START_CODE)));
                }
            }//if
        }//for

        switch (korConvRtnType) {
            case ALPHABET:
                return sb.toString();
            case JAUM_MOUM:
                return split.toString();
            default:
                return null;
        }

    }

}