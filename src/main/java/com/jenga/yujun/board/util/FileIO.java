package com.jenga.yujun.board.util;

import java.io.File;

public interface FileIO {

    public abstract File getFile(final String path, String fileName);
    public abstract File getUploadFile(final String path, final String name);


}
