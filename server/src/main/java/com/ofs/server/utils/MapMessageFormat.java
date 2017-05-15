package com.ofs.server.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MapMessageFormat extends Format {
    private Locale locale = Locale.getDefault();
    private String pattern = "";
    private static final int MAX_ARGUMENTS = 100;
    private Format[] formats = new Format[100];
    private int[] offsets = new int[100];
    private String[] argumentKeys = new String[100];
    private int maxOffset = -1;
    private static final String[] typeList = new String[]{"", "", "number", "", "date", "", "time", "", "message"};
    private static final String[] modifierList = new String[]{"", "", "currency", "", "percent", "", "integer"};
    private static final String[] dateModifierList = new String[]{"", "", "short", "", "medium", "", "long", "", "full"};

    public MapMessageFormat(String pattern) {
        this.applyPattern(pattern);
    }

    public MapMessageFormat(String pattern, Locale locale) {
        this.locale = locale;
        this.applyPattern(pattern);
    }

    public void applyPattern(String newPattern) {
        StringBuffer[] segments = new StringBuffer[4];

        int part;
        for(part = 0; part < segments.length; ++part) {
            segments[part] = new StringBuffer();
        }

        part = 0;
        int formatNumber = 0;
        boolean inQuote = false;
        int braceStack = 0;
        this.maxOffset = -1;

        for(int i = 0; i < newPattern.length(); ++i) {
            char ch = newPattern.charAt(i);
            if(part == 0) {
                if(ch == 39) {
                    if(i + 1 < newPattern.length() && newPattern.charAt(i + 1) == 39) {
                        segments[part].append(ch);
                        ++i;
                    } else {
                        inQuote = !inQuote;
                    }
                } else if(ch == 123 && !inQuote) {
                    part = 1;
                } else {
                    segments[part].append(ch);
                }
            } else if(inQuote) {
                segments[part].append(ch);
                if(ch == 39) {
                    inQuote = false;
                }
            } else {
                switch(ch) {
                    case '\'':
                        inQuote = true;
                    default:
                        segments[part].append(ch);
                        break;
                    case ',':
                        if(part < 3) {
                            ++part;
                        } else {
                            segments[part].append(ch);
                        }
                        break;
                    case '{':
                        ++braceStack;
                        segments[part].append(ch);
                        break;
                    case '}':
                        if(braceStack == 0) {
                            part = 0;
                            this.makeFormat(i, formatNumber, segments);
                            ++formatNumber;
                        } else {
                            --braceStack;
                            segments[part].append(ch);
                        }
                }
            }
        }

        if(braceStack == 0 && part != 0) {
            this.maxOffset = -1;
            throw new IllegalArgumentException("Unmatched braces in the pattern.");
        } else {
            this.pattern = segments[0].toString();
        }
    }

    public String toPattern() {
        int lastOffset = 0;
        StringBuffer result = new StringBuffer();

        for(int i = 0; i <= this.maxOffset; ++i) {
            copyAndFixQuotes(this.pattern, lastOffset, this.offsets[i], result);
            lastOffset = this.offsets[i];
            result.append('{');
            result.append(this.argumentKeys[i]);
            if(this.formats[i] != null) {
                if(this.formats[i] instanceof DecimalFormat) {
                    if(this.formats[i].equals(NumberFormat.getInstance(this.locale))) {
                        result.append(",number");
                    } else if(this.formats[i].equals(NumberFormat.getCurrencyInstance(this.locale))) {
                        result.append(",number,currency");
                    } else if(this.formats[i].equals(NumberFormat.getPercentInstance(this.locale))) {
                        result.append(",number,percent");
                    } else if(this.formats[i].equals(this.getIntegerFormat(this.locale))) {
                        result.append(",number,integer");
                    } else {
                        result.append(",number," + ((DecimalFormat)this.formats[i]).toPattern());
                    }
                } else if(this.formats[i] instanceof SimpleDateFormat) {
                    if(this.formats[i].equals(DateFormat.getDateInstance(2, this.locale))) {
                        result.append(",date");
                    } else if(this.formats[i].equals(DateFormat.getDateInstance(3, this.locale))) {
                        result.append(",date,short");
                    } else if(this.formats[i].equals(DateFormat.getDateInstance(2, this.locale))) {
                        result.append(",date,medium");
                    } else if(this.formats[i].equals(DateFormat.getDateInstance(1, this.locale))) {
                        result.append(",date,long");
                    } else if(this.formats[i].equals(DateFormat.getDateInstance(0, this.locale))) {
                        result.append(",date,full");
                    } else if(this.formats[i].equals(DateFormat.getTimeInstance(2, this.locale))) {
                        result.append(",time");
                    } else if(this.formats[i].equals(DateFormat.getTimeInstance(3, this.locale))) {
                        result.append(",time,short");
                    } else if(this.formats[i].equals(DateFormat.getTimeInstance(2, this.locale))) {
                        result.append(",time,medium");
                    } else if(this.formats[i].equals(DateFormat.getTimeInstance(1, this.locale))) {
                        result.append(",time,long");
                    } else if(this.formats[i].equals(DateFormat.getTimeInstance(0, this.locale))) {
                        result.append(",time,full");
                    } else {
                        result.append(",date," + ((SimpleDateFormat)this.formats[i]).toPattern());
                    }
                } else if(this.formats[i] instanceof MapMessageFormat) {
                    result.append(",message," + ((MapMessageFormat)this.formats[i]).toPattern());
                }
            }

            result.append('}');
        }

        copyAndFixQuotes(this.pattern, lastOffset, this.pattern.length(), result);
        return result.toString();
    }

    public final StringBuffer format(Map source, StringBuffer result, FieldPosition ignore) {
        return this.format2(source, result, ignore);
    }

    public static String format(String pattern, Map arguments) {
        MapMessageFormat temp = new MapMessageFormat(pattern);
        return temp.format(arguments);
    }

    public static String format(String pattern, Map arguments, Locale locale) {
        MapMessageFormat temp = new MapMessageFormat(pattern, locale);
        return temp.format(arguments);
    }

    public final StringBuffer format(Object source, StringBuffer result, FieldPosition ignore) {
        return this.format2((Map)source, result, ignore);
    }

    public Map parse(String source, ParsePosition status) {
        HashMap result = new HashMap();
        if(source == null) {
            return result;
        } else {
            Object[] resultArray = new Object[10];
            int patternOffset = 0;
            int sourceOffset = status.getIndex();
            ParsePosition tempStatus = new ParsePosition(0);

            int len;
            for(len = 0; len <= this.maxOffset; ++len) {
                int len1 = this.offsets[len] - patternOffset;
                if(len1 != 0 && !this.pattern.regionMatches(patternOffset, source, sourceOffset, len1)) {
                    status.setErrorIndex(sourceOffset);
                    return null;
                }

                sourceOffset += len1;
                patternOffset += len1;
                if(this.formats[len] == null) {
                    int tempLength = len != this.maxOffset?this.offsets[len + 1]:this.pattern.length();
                    int next;
                    if(patternOffset >= tempLength) {
                        next = source.length();
                    } else {
                        next = source.indexOf(this.pattern.substring(patternOffset, tempLength), sourceOffset);
                    }

                    if(next < 0) {
                        status.setErrorIndex(sourceOffset);
                        return null;
                    }

                    String strValue = source.substring(sourceOffset, next);
                    if(!strValue.equals("{" + this.argumentKeys[len] + "}")) {
                        result.put(this.argumentKeys[len], source.substring(sourceOffset, next));
                    }

                    sourceOffset = next;
                } else {
                    tempStatus.setIndex(sourceOffset);
                    result.put(this.argumentKeys[len], this.formats[len].parseObject(source, tempStatus));
                    if(tempStatus.getIndex() == sourceOffset) {
                        status.setErrorIndex(sourceOffset);
                        return null;
                    }

                    sourceOffset = tempStatus.getIndex();
                }
            }

            len = this.pattern.length() - patternOffset;
            if(len != 0 && !this.pattern.regionMatches(patternOffset, source, sourceOffset, len)) {
                status.setErrorIndex(sourceOffset);
                return null;
            } else {
                status.setIndex(sourceOffset + len);
                return result;
            }
        }
    }

    public Map parse(String source) throws ParseException {
        ParsePosition status = new ParsePosition(0);
        Map result = this.parse(source, status);
        if(status.getIndex() == 0) {
            throw new ParseException("MapMessageFormat parse error!", status.getErrorIndex());
        } else {
            return result;
        }
    }

    public Object parseObject(String text, ParsePosition status) {
        return this.parse(text, status);
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj != null && this.getClass() == obj.getClass()) {
            MapMessageFormat other = (MapMessageFormat)obj;
            return this.pattern.equals(other.pattern) && this.locale.equals(other.locale);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.pattern.hashCode() ^ this.locale.hashCode();
    }

    private StringBuffer format2(Map arguments, StringBuffer result, FieldPosition status) {
        int lastOffset = 0;

        for(int i = 0; i <= this.maxOffset; ++i) {
            result.append(this.pattern.substring(lastOffset, this.offsets[i]));
            lastOffset = this.offsets[i];
            String argumentKey = this.argumentKeys[i];
            if(arguments != null && arguments.containsKey(argumentKey)) {
                Object obj = arguments.get(argumentKey);
                boolean tryRecursion = false;
                String arg;
                if(obj == null) {
                    arg = "null";
                } else if(this.formats[i] != null) {
                    arg = this.formats[i].format(obj);
                } else if(obj instanceof Number) {
                    arg = NumberFormat.getInstance(this.locale).format(obj);
                } else if(obj instanceof Date) {
                    arg = DateFormat.getDateTimeInstance(3, 3, this.locale).format(obj);
                } else if(obj instanceof String) {
                    arg = (String)obj;
                } else {
                    arg = obj.toString();
                    if(arg == null) {
                        arg = "null";
                    }
                }

                result.append(arg);
            } else {
                result.append("{" + argumentKey + "}");
            }
        }

        result.append(this.pattern.substring(lastOffset, this.pattern.length()));
        return result;
    }

    private void makeFormat(int position, int offsetNumber, StringBuffer[] segments) {
        Object newFormat;
        int oldMaxOffset = this.maxOffset;
        this.argumentKeys[offsetNumber] = segments[1].toString();
        this.maxOffset = offsetNumber;
        this.offsets[offsetNumber] = segments[0].length();
        newFormat = null;
        label55:
        switch(findKeyword(segments[2].toString(), typeList)) {
            case 0:
                break;
            case 1:
            case 2:
                switch(findKeyword(segments[3].toString(), modifierList)) {
                    case 0:
                        newFormat = NumberFormat.getInstance(this.locale);
                        break label55;
                    case 1:
                    case 2:
                        newFormat = NumberFormat.getCurrencyInstance(this.locale);
                        break label55;
                    case 3:
                    case 4:
                        newFormat = NumberFormat.getPercentInstance(this.locale);
                        break label55;
                    case 5:
                    case 6:
                        newFormat = this.getIntegerFormat(this.locale);
                        break label55;
                    default:
                        newFormat = NumberFormat.getInstance(this.locale);

                        try {
                            ((DecimalFormat)newFormat).applyPattern(segments[3].toString());
                            break label55;
                        } catch (Exception var11) {
                            this.maxOffset = oldMaxOffset;
                            throw new IllegalArgumentException("Pattern incorrect or locale does not support formats, error at ");
                        }
                }
            case 3:
            case 4:
                switch(findKeyword(segments[3].toString(), dateModifierList)) {
                    case 0:
                        newFormat = DateFormat.getDateInstance(2, this.locale);
                        break label55;
                    case 1:
                    case 2:
                        newFormat = DateFormat.getDateInstance(3, this.locale);
                        break label55;
                    case 3:
                    case 4:
                        newFormat = DateFormat.getDateInstance(2, this.locale);
                        break label55;
                    case 5:
                    case 6:
                        newFormat = DateFormat.getDateInstance(1, this.locale);
                        break label55;
                    case 7:
                    case 8:
                        newFormat = DateFormat.getDateInstance(0, this.locale);
                        break label55;
                    default:
                        newFormat = DateFormat.getDateInstance(2, this.locale);

                        try {
                            ((SimpleDateFormat)newFormat).applyPattern(segments[3].toString());
                            break label55;
                        } catch (Exception var10) {
                            this.maxOffset = oldMaxOffset;
                            throw new IllegalArgumentException("Pattern incorrect or locale does not support formats, error at ");
                        }
                }
            case 5:
            case 6:
                switch(findKeyword(segments[3].toString(), dateModifierList)) {
                    case 0:
                        newFormat = DateFormat.getTimeInstance(2, this.locale);
                        break label55;
                    case 1:
                    case 2:
                        newFormat = DateFormat.getTimeInstance(3, this.locale);
                        break label55;
                    case 3:
                    case 4:
                        newFormat = DateFormat.getTimeInstance(2, this.locale);
                        break label55;
                    case 5:
                    case 6:
                        newFormat = DateFormat.getTimeInstance(1, this.locale);
                        break label55;
                    case 7:
                    case 8:
                        newFormat = DateFormat.getTimeInstance(0, this.locale);
                        break label55;
                    default:
                        newFormat = DateFormat.getTimeInstance(2, this.locale);

                        try {
                            ((SimpleDateFormat)newFormat).applyPattern(segments[3].toString());
                            break label55;
                        } catch (Exception var9) {
                            this.maxOffset = oldMaxOffset;
                            throw new IllegalArgumentException("Pattern incorrect or locale does not support formats, error at ");
                        }
                }
            case 7:
            case 8:
                try {
                    newFormat = new MapMessageFormat(segments[3].toString(), this.locale);
                    break;
                } catch (Exception var8) {
                    this.maxOffset = oldMaxOffset;
                    throw new IllegalArgumentException("MapMessageFormat Pattern incorrect, error at ");
                }
            default:
                this.maxOffset = oldMaxOffset;
                throw new IllegalArgumentException("unknown format type at ");
        }

        this.formats[offsetNumber] = (Format)newFormat;
        segments[1].setLength(0);
        segments[2].setLength(0);
        segments[3].setLength(0);
    }

    private static final int findKeyword(String s, String[] list) {
        s = s.trim().toLowerCase();

        for(int i = 0; i < list.length; ++i) {
            if(s.equals(list[i])) {
                return i;
            }
        }

        return -1;
    }

    NumberFormat getIntegerFormat(Locale locale) {
        NumberFormat temp = NumberFormat.getInstance(locale);
        if(temp instanceof DecimalFormat) {
            DecimalFormat temp2 = (DecimalFormat)temp;
            temp2.setMaximumFractionDigits(0);
            temp2.setDecimalSeparatorAlwaysShown(false);
            temp2.setParseIntegerOnly(true);
        }

        return temp;
    }

    private static final void copyAndFixQuotes(String source, int start, int end, StringBuffer target) {
        for(int i = start; i < end; ++i) {
            char ch = source.charAt(i);
            if(ch == 123) {
                target.append("\'{\'");
            } else if(ch == 125) {
                target.append("\'}\'");
            } else if(ch == 39) {
                target.append("\'\'");
            } else {
                target.append(ch);
            }
        }

    }
}
