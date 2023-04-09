package de.achimonline.changelistorganizer;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import static org.mockito.Mockito.mock;

@Data
public class VirtualFileDummy implements Serializable {
    private final String name;
    private final String path;

    public VirtualFile getVirtualFile() {
        return new VirtualFile() {
            @Override
            public @NotNull String getName() {
                return name;
            }

            @Override
            public @NotNull VirtualFileSystem getFileSystem() {
                return mock(VirtualFileSystem.class);
            }

            @Override
            public @NotNull String getPath() {
                return path + (!path.endsWith(File.separator) ? File.separator : "") + name;
            }

            @Override
            public boolean isWritable() {
                return false;
            }

            @Override
            public boolean isDirectory() {
                return false;
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public VirtualFile getParent() {
                return null;
            }

            @Override
            public VirtualFile[] getChildren() {
                return new VirtualFile[0];
            }

            @Override
            public @NotNull OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) {
                return mock(OutputStream.class);
            }

            @Override
            public byte @NotNull [] contentsToByteArray() {
                return new byte[0];
            }

            @Override
            public long getTimeStamp() {
                return 0;
            }

            @Override
            public long getLength() {
                return 0;
            }

            @Override
            public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {
            }

            @Override
            public @NotNull InputStream getInputStream() {
                return mock(InputStream.class);
            }

            @Override
            public String toString() {
                return path;
            }
        };
    }
}
