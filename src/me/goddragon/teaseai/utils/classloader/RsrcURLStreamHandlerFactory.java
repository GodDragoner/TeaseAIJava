package me.goddragon.teaseai.utils.classloader;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class RsrcURLStreamHandlerFactory implements URLStreamHandlerFactory {
    private ClassLoader classLoader;
    private URLStreamHandlerFactory chainFac;

    public RsrcURLStreamHandlerFactory(ClassLoader cl) {
        this.classLoader = cl;
    }

    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("rsrc".equals(protocol)) {
            return new RsrcURLStreamHandler(this.classLoader);
        } else {
            return this.chainFac != null ? this.chainFac.createURLStreamHandler(protocol) : null;
        }
    }

    public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
        this.chainFac = fac;
    }
}
