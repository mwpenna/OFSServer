package com.ofs.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class IOUtils {
    private IOUtils() {
    }

    public static byte[] getBytes(InputStream in) throws IOException {
        return getBytes(in, false);
    }

    public static byte[] getBytes(InputStream in, boolean close) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];

        int len;
        while((len = in.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }

        if(close) {
            close(in);
        }

        return baos.toByteArray();
    }

    public static String toString(InputStream in, Charset charset) throws IOException {
        return toString(in, charset, false);
    }

    public static String toString(InputStream in, Charset charset, boolean close) throws IOException {
        return toString(new InputStreamReader(in, charset), close);
    }

    public static String toString(Reader in) throws IOException {
        return toString(in, false);
    }

    public static String toString(Reader in, boolean close) throws IOException {
        StringWriter sw = new StringWriter();
        char[] buf = new char[2048];

        int len;
        while((len = in.read(buf)) != -1) {
            sw.write(buf, 0, len);
        }

        if(close) {
            close(in);
        }

        return sw.toString();
    }

    public static long copyTo(InputStream in, OutputStream out) throws IOException {
        return copyTo(in, out, false);
    }

    public static long copyTo(InputStream in, OutputStream out, boolean close) throws IOException {
        long total = 0L;

        int len;
        try {
            for(byte[] buf = new byte[2048]; (len = in.read(buf)) != -1; total += (long)len) {
                out.write(buf, 0, len);
            }
        } finally {
            if(close) {
                close(in, out);
            }

        }

        return total;
    }

    public static long copyTo(Reader in, Writer out) throws IOException {
        return copyTo(in, out, false);
    }

    public static long copyTo(Reader in, Writer out, boolean close) throws IOException {
        long total = 0L;

        int len;
        try {
            for(char[] buf = new char[2048]; (len = in.read(buf)) != -1; total += (long)len) {
                out.write(buf, 0, len);
            }
        } finally {
            if(close) {
                close(in, out);
            }

        }

        return total;
    }

    public static boolean sameStream(InputStream is1, InputStream is2) throws IOException {
        int b1;
        int b2;
        do {
            b1 = is1.read();
            b2 = is2.read();
            if(b1 == -1) {
                if(b2 != -1) {
                    return false;
                }

                return true;
            }
        } while(b1 == b2);

        return false;
    }

    public static boolean sameStream(Reader is1, Reader is2) throws IOException {
        int b1;
        int b2;
        do {
            b1 = is1.read();
            b2 = is2.read();
            if(b1 == -1) {
                if(b2 != -1) {
                    return false;
                }

                return true;
            }
        } while(b1 == b2);

        return false;
    }

    public static boolean close(OutputStream output) {
        if(null == output) {
            return false;
        } else {
            try {
                output.close();
                return true;
            } catch (IOException var2) {
                return false;
            }
        }
    }

    public static boolean close(InputStream input) {
        if(null == input) {
            return false;
        } else {
            try {
                input.close();
                return true;
            } catch (IOException var2) {
                return false;
            }
        }
    }

    public static void close(InputStream in, OutputStream out) {
        try {
            if(in != null) {
                in.close();
            }
        } catch (Exception var4) {
            ;
        }

        try {
            if(out != null) {
                out.close();
            }
        } catch (Exception var3) {
            ;
        }

    }

    public static boolean close(Writer output) {
        if(null == output) {
            return false;
        } else {
            try {
                output.close();
                return true;
            } catch (IOException var2) {
                return false;
            }
        }
    }

    public static boolean close(Reader input) {
        if(null == input) {
            return false;
        } else {
            try {
                input.close();
                return true;
            } catch (IOException var2) {
                return false;
            }
        }
    }

    public static void close(Reader in, Writer out) {
        try {
            if(in != null) {
                in.close();
            }
        } catch (Exception var4) {
            ;
        }

        try {
            if(out != null) {
                out.close();
            }
        } catch (Exception var3) {
            ;
        }

    }
}
