package org.intraportal.webtool.security.filters;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    private static final Pattern[] PATTERNS = new Pattern[]{
            // Script fragments
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<iframe>(.*?)</iframe>", Pattern.CASE_INSENSITIVE),
            // src='...'
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // lonely script tags
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("<iframe(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("</iframe>", Pattern.CASE_INSENSITIVE),
            // eval(...)
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // expression(...)
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // javascript:...
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // vbscript:...
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            // onload(...)=...
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // input fields
            Pattern.compile("<input(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("<input(.*?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    public XssRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);

        if (values == null) {
            return null;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXSS(values[i]);
        }

        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);

        return stripXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return stripXSS(value);
    }

    private String stripXSS(String value) {
        if (value != null) {

            value = value.replaceAll("\0", "");
            value = value.trim().replaceAll(" +", " ");
            value = value.replaceAll("\\<.*?\\>", "");
            value = value.trim();

            for (Pattern scriptPattern : PATTERNS) {
                value = scriptPattern.matcher(value).replaceAll("");
            }
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), byteArrayOutputStream);
        String decoded = byteArrayOutputStream.toString("ISO-8859-1");
        decoded = stripXSSForMultipart(decoded);
        byte[] encoded = decoded.getBytes("ISO-8859-1");
        return new CachedServletInputStream(encoded);
    }

    private String stripXSSForMultipart(String valueToStrip) {
        int index = valueToStrip.indexOf("\n");
        String delimeter = index > 0 ? valueToStrip.substring(0, index) : null;
        if (delimeter == null || !delimeter.contains("--") || !valueToStrip.contains(delimeter)) {
            return stripXSS(valueToStrip);
        } else {
            List<String> inputValues = Arrays.asList(valueToStrip.split(delimeter));
            List<String> valueStripped = new ArrayList<>();
            for (String value : inputValues) {
                if (value.contains("Content-Type:")) {
                    valueStripped.add(delimeter + value);
                } else if (!value.isEmpty()) {
                    valueStripped.add(stripXSS(delimeter + value));
                }
            }
            return String.join("", valueStripped);
        }
    }

    public class CachedServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream input;

        public CachedServletInputStream(byte[] stripedXSS) {
            input = new ByteArrayInputStream(stripedXSS);
        }

        @Override
        public int read() {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
