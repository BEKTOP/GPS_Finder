package com.github.a5809909.gps_finder.ImageLoader.streams;


import java.io.IOException;
import java.io.InputStream;

public interface StreamProvider<T> {
    InputStream get(T path) throws IOException;
}
