package com.nhom17.web;

import java.math.BigDecimal;
import java.util.*;

/**
 * Lightweight, zero-dependency JSON builder and parser for Java 17 HttpServer.
 */
public final class JsonUtil {

    private JsonUtil() {}

    public static String quote(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String s) return quote(s);
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof Enum<?> e) return quote(e.name());
        if (obj instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append(quote(String.valueOf(entry.getKey()))).append(":").append(toJson(entry.getValue()));
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Iterable<?> col) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : col) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJson(item));
            }
            sb.append("]");
            return sb.toString();
        }
        return quote(obj.toString());
    }

    /**
     * Simple flat JSON object parser for request bodies, plus nested items array extraction.
     */
    public static Map<String, String> parseFlatObject(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        if (json == null || json.isBlank()) return map;
        String trimmed = json.trim();
        if (trimmed.startsWith("{")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("}")) trimmed = trimmed.substring(0, trimmed.length() - 1);

        int i = 0;
        int n = trimmed.length();
        while (i < n) {
            while (i < n && (Character.isWhitespace(trimmed.charAt(i)) || trimmed.charAt(i) == ',')) i++;
            if (i >= n) break;
            if (trimmed.charAt(i) != '"') break;
            int keyStart = ++i;
            StringBuilder keySb = new StringBuilder();
            while (i < n && trimmed.charAt(i) != '"') {
                if (trimmed.charAt(i) == '\\' && i + 1 < n) {
                    i++;
                }
                keySb.append(trimmed.charAt(i++));
            }
            i++; // skip closing quote
            while (i < n && (Character.isWhitespace(trimmed.charAt(i)) || trimmed.charAt(i) == ':')) i++;
            if (i >= n) break;

            String key = keySb.toString();
            char c = trimmed.charAt(i);
            if (c == '"') {
                i++;
                StringBuilder valSb = new StringBuilder();
                while (i < n) {
                    char vc = trimmed.charAt(i++);
                    if (vc == '\\' && i < n) {
                        char next = trimmed.charAt(i++);
                        switch (next) {
                            case 'n' -> valSb.append('\n');
                            case 'r' -> valSb.append('\r');
                            case 't' -> valSb.append('\t');
                            case '"' -> valSb.append('"');
                            case '\\' -> valSb.append('\\');
                            default -> valSb.append(next);
                        }
                    } else if (vc == '"') {
                        break;
                    } else {
                        valSb.append(vc);
                    }
                }
                map.put(key, valSb.toString());
            } else if (c == '[') {
                int depth = 0;
                int arrStart = i;
                while (i < n) {
                    char ac = trimmed.charAt(i++);
                    if (ac == '"') {
                        while (i < n) {
                            char sc = trimmed.charAt(i++);
                            if (sc == '\\') i++;
                            else if (sc == '"') break;
                        }
                    } else if (ac == '[') depth++;
                    else if (ac == ']') {
                        depth--;
                        if (depth == 0) break;
                    }
                }
                map.put(key, trimmed.substring(arrStart, i));
            } else if (c == '{') {
                int depth = 0;
                int objStart = i;
                while (i < n) {
                    char oc = trimmed.charAt(i++);
                    if (oc == '"') {
                        while (i < n) {
                            char sc = trimmed.charAt(i++);
                            if (sc == '\\') i++;
                            else if (sc == '"') break;
                        }
                    } else if (oc == '{') depth++;
                    else if (oc == '}') {
                        depth--;
                        if (depth == 0) break;
                    }
                }
                map.put(key, trimmed.substring(objStart, i));
            } else {
                int valStart = i;
                while (i < n && trimmed.charAt(i) != ',' && trimmed.charAt(i) != '}') i++;
                String rawVal = trimmed.substring(valStart, i).trim();
                map.put(key, "null".equals(rawVal) ? null : rawVal);
            }
        }
        return map;
    }

    public static List<Map<String, String>> parseObjectArray(String jsonArray) {
        List<Map<String, String>> list = new ArrayList<>();
        if (jsonArray == null || jsonArray.isBlank()) return list;
        String s = jsonArray.trim();
        if (s.startsWith("[")) s = s.substring(1);
        if (s.endsWith("]")) s = s.substring(0, s.length() - 1);

        int i = 0;
        int n = s.length();
        while (i < n) {
            while (i < n && (Character.isWhitespace(s.charAt(i)) || s.charAt(i) == ',')) i++;
            if (i >= n) break;
            if (s.charAt(i) == '{') {
                int depth = 0;
                int start = i;
                while (i < n) {
                    char c = s.charAt(i++);
                    if (c == '"') {
                        while (i < n) {
                            char sc = s.charAt(i++);
                            if (sc == '\\') i++;
                            else if (sc == '"') break;
                        }
                    } else if (c == '{') depth++;
                    else if (c == '}') {
                        depth--;
                        if (depth == 0) break;
                    }
                }
                list.add(parseFlatObject(s.substring(start, i)));
            } else {
                i++;
            }
        }
        return list;
    }

    public static BigDecimal toBigDecimal(String val, BigDecimal defaultVal) {
        if (val == null || val.isBlank()) return defaultVal;
        try {
            return new BigDecimal(val.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static int toInt(String val, int defaultVal) {
        if (val == null || val.isBlank()) return defaultVal;
        try {
            return (int) Double.parseDouble(val.trim());
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
