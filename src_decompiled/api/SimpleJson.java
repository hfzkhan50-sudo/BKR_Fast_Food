/*
 * Decompiled with CFR 0.152.
 */
package api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SimpleJson {
    public static String escape(String string) {
        if (string == null) {
            return "";
        }
        return string.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public static Object parse(String string) {
        if (string == null) {
            return null;
        }
        if ((string = string.trim()).isEmpty()) {
            return null;
        }
        return new Parser(string).parseValue();
    }

    public static Map<String, Object> parseObject(String string) {
        Object object = SimpleJson.parse(string);
        if (object instanceof Map) {
            return (Map)object;
        }
        return Collections.emptyMap();
    }

    public static List<Map<String, Object>> parseArray(String string) {
        Object object = SimpleJson.parse(string);
        if (object instanceof List) {
            List list = (List)object;
            ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
            for (Object e : list) {
                if (!(e instanceof Map)) continue;
                arrayList.add((Map)e);
            }
            return arrayList;
        }
        return Collections.emptyList();
    }

    public static String getString(Map<String, Object> map, String string, String string2) {
        Object object = map.get(string);
        return object != null ? object.toString() : string2;
    }

    public static int getInt(Map<String, Object> map, String string, int n) {
        Object object = map.get(string);
        if (object instanceof Number) {
            return ((Number)object).intValue();
        }
        if (object != null) {
            try {
                return Integer.parseInt(object.toString());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return n;
    }

    public static BigDecimal getBigDecimal(Map<String, Object> map, String string, BigDecimal bigDecimal) {
        Object object = map.get(string);
        if (object != null) {
            try {
                return new BigDecimal(object.toString());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return bigDecimal;
    }

    public static boolean getBoolean(Map<String, Object> map, String string, boolean bl) {
        Object object = map.get(string);
        if (object instanceof Boolean) {
            return (Boolean)object;
        }
        if (object != null) {
            return Boolean.parseBoolean(object.toString());
        }
        return bl;
    }

    public static LocalDate getLocalDate(Map<String, Object> map, String string) {
        Object object = map.get(string);
        if (object != null) {
            try {
                return LocalDate.parse(object.toString());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return LocalDate.now();
    }

    public static LocalTime getLocalTime(Map<String, Object> map, String string) {
        Object object = map.get(string);
        if (object != null) {
            try {
                return LocalTime.parse(object.toString());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return LocalTime.now();
    }

    private static class Parser {
        private final String src;
        private int idx = 0;

        Parser(String string) {
            this.src = string;
        }

        Object parseValue() {
            this.skipWhitespace();
            if (this.idx >= this.src.length()) {
                return null;
            }
            char c = this.src.charAt(this.idx);
            if (c == '{') {
                return this.parseObject();
            }
            if (c == '[') {
                return this.parseArray();
            }
            if (c == '\"') {
                return this.parseString();
            }
            if (c == 't' || c == 'f') {
                return this.parseBoolean();
            }
            if (c == 'n') {
                return this.parseNull();
            }
            return this.parseNumber();
        }

        private Map<String, Object> parseObject() {
            LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
            ++this.idx;
            this.skipWhitespace();
            if (this.idx < this.src.length() && this.src.charAt(this.idx) == '}') {
                ++this.idx;
                return linkedHashMap;
            }
            while (this.idx < this.src.length()) {
                this.skipWhitespace();
                String string = this.parseString();
                this.skipWhitespace();
                if (this.idx < this.src.length() && this.src.charAt(this.idx) == ':') {
                    ++this.idx;
                }
                this.skipWhitespace();
                Object object = this.parseValue();
                linkedHashMap.put(string, object);
                this.skipWhitespace();
                if (this.idx < this.src.length() && this.src.charAt(this.idx) == ',') {
                    ++this.idx;
                    continue;
                }
                if (this.idx >= this.src.length() || this.src.charAt(this.idx) != '}') continue;
                ++this.idx;
                break;
            }
            return linkedHashMap;
        }

        private List<Object> parseArray() {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            ++this.idx;
            this.skipWhitespace();
            if (this.idx < this.src.length() && this.src.charAt(this.idx) == ']') {
                ++this.idx;
                return arrayList;
            }
            while (this.idx < this.src.length()) {
                Object object = this.parseValue();
                arrayList.add(object);
                this.skipWhitespace();
                if (this.idx < this.src.length() && this.src.charAt(this.idx) == ',') {
                    ++this.idx;
                    continue;
                }
                if (this.idx >= this.src.length() || this.src.charAt(this.idx) != ']') continue;
                ++this.idx;
                break;
            }
            return arrayList;
        }

        private String parseString() {
            char c;
            if (this.src.charAt(this.idx) == '\"') {
                ++this.idx;
            }
            StringBuilder stringBuilder = new StringBuilder();
            block11: while (this.idx < this.src.length() && (c = this.src.charAt(this.idx++)) != '\"') {
                if (c == '\\' && this.idx < this.src.length()) {
                    char c2 = this.src.charAt(this.idx++);
                    switch (c2) {
                        case '\"': {
                            stringBuilder.append('\"');
                            break;
                        }
                        case '\\': {
                            stringBuilder.append('\\');
                            break;
                        }
                        case '/': {
                            stringBuilder.append('/');
                            break;
                        }
                        case 'b': {
                            stringBuilder.append('\b');
                            break;
                        }
                        case 'f': {
                            stringBuilder.append('\f');
                            break;
                        }
                        case 'n': {
                            stringBuilder.append('\n');
                            break;
                        }
                        case 'r': {
                            stringBuilder.append('\r');
                            break;
                        }
                        case 't': {
                            stringBuilder.append('\t');
                            break;
                        }
                        case 'u': {
                            if (this.idx + 4 > this.src.length()) continue block11;
                            String string = this.src.substring(this.idx, this.idx + 4);
                            stringBuilder.append((char)Integer.parseInt(string, 16));
                            this.idx += 4;
                            break;
                        }
                        default: {
                            stringBuilder.append(c2);
                            break;
                        }
                    }
                    continue;
                }
                stringBuilder.append(c);
            }
            return stringBuilder.toString();
        }

        private Object parseNumber() {
            char c;
            int n = this.idx;
            while (this.idx < this.src.length() && (Character.isDigit(c = this.src.charAt(this.idx)) || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E')) {
                ++this.idx;
            }
            String string = this.src.substring(n, this.idx);
            if (string.contains(".")) {
                try {
                    return Double.parseDouble(string);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            try {
                return Long.parseLong(string);
            }
            catch (Exception exception) {
                return string;
            }
        }

        private Boolean parseBoolean() {
            if (this.src.startsWith("true", this.idx)) {
                this.idx += 4;
                return true;
            }
            if (this.src.startsWith("false", this.idx)) {
                this.idx += 5;
                return false;
            }
            return false;
        }

        private Object parseNull() {
            if (this.src.startsWith("null", this.idx)) {
                this.idx += 4;
            }
            return null;
        }

        private void skipWhitespace() {
            while (this.idx < this.src.length() && Character.isWhitespace(this.src.charAt(this.idx))) {
                ++this.idx;
            }
        }
    }
}

