package uiharu.domain;

import artoria.util.StringUtils;

import static artoria.common.Constants.NEWLINE;

/**
 * Request bean with body.
 * @author Kahle
 */
public class RequestBean extends artoria.servlet.RequestBean {
    private String body;

    public String getBody() {

        return body;
    }

    public void setBody(String body) {

        this.body = body;
    }

    @Override
    public String toString() {
        String toString = super.toString();
        if (StringUtils.isBlank(toString)
                || StringUtils.isBlank(body)) {
            return toString;
        }
        int lastLineLength = 35;
        int length = toString.length();
        int beginIndex = length - lastLineLength;
        String lastLine = toString.substring(beginIndex, length);
        String primary = toString.substring(0, beginIndex);
        primary += "---- Body ----";
        primary += NEWLINE;
        primary += body;
        primary += lastLine;
        return primary;
    }

}
