package sample.util;

import com.ibm.icu.text.Transliterator;

import java.util.Locale;

public class CyrillicToLatin {
    private static String rules="::[А-ЪЬЮ-ъьюяѢѣѪѫ];" +
            "а > a;"+
            "б > b;"+
            "в > v;"+
            "г > g;"+
            "д > d;"+
            "е > e;"+
            "ё > yo;"+
            "ж > j;"+
            "з > z;"+
            "и > i;"+
            "й > y;"+
            "к > k;"+
            "л > l;"+
            "м > m;"+
            "н > n;"+
            "о > o;"+
            "п > p;"+
            "р > r;"+
            "с > s;"+
            "т > t;"+
            "у > u;"+
            "ф > f;"+
            "х > h;"+
            "ц > w;"+
            "ч > ch;"+
            "ш > sh;"+
            "щ > sch;"+
            "ъ > ;"+
            "ы > i;"+
            "ь > ;"+
            "э > e;"+
            "ю > yu;"+
            "x > h;"+
            "я > ya;";

    public static String transliterate(String message){
        Transliterator transliterator=Transliterator.createFromRules("temp", rules,Transliterator.FORWARD);
        return transliterator.transliterate(message.toLowerCase(Locale.ROOT));
    }
}
