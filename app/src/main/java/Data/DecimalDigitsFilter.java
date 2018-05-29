package Data;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DecimalDigitsFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsFilter() {
        mPattern = Pattern.compile("^\\d{0,2}(\\.\\d?)?$");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        Matcher matcher=mPattern.matcher(dest);
        if(!matcher.matches())
            return "";
        return null;
    }

}
