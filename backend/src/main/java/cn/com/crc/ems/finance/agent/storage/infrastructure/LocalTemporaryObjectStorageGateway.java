package cn.com.crc.ems.finance.agent.storage.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageGateway;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageIoException;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageLimitExceededException;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStorageNotFoundException;
import cn.com.crc.ems.finance.agent.storage.application.ObjectStoragePathViolationException;
import cn.com.crc.ems.finance.agent.storage.domain.ObjectReference;

/**
 *
 * @Description: 当前类型承载本模块的职责、边界与协作契约。
 *
 * @Version: 0.1.0
 * @Author: yigeyige99
 * @Date: 2026-09-26 19:35:44
 */
public final class LocalTemporaryObjectStorageGateway implements ObjectStorageGateway {

    private final Path root;
    private final long maxBytes;

    public LocalTemporaryObjectStorageGateway(Path root, long maxBytes) {
        if (root == null || maxBytes <= 0) {
            throw new IllegalArgumentException("root and positive maxBytes are required");
        }
        this.root = root.toAbsolutePath().normalize();
        this.maxBytes = maxBytes;
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: objectKey 输入参数。
     * @param: content 输入参数。
     * @param: mediaType 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    @Override
    public ObjectReference put(String objectKey, InputStream content, String mediaType) {
        String safeKey = safeKey(objectKey);
        if (content == null) {
            throw new IllegalArgumentException("content is required");
        }
        Path temporary = null;
        try {
            Files.createDirectories(root);
            temporary = Files.createTempFile(root, ".upload-", ".tmp");
            MessageDigest digest = sha256();
            long size = 0;
            byte[] buffer = new byte[8192];
            try (OutputStream output = Files.newOutputStream(temporary)) {
                int read;
                while ((read = content.read(buffer)) != -1) {
                    size += read;
                    if (size > maxBytes) {
                        throw new ObjectStorageLimitExceededException();
                    }
                    digest.update(buffer, 0, read);
                    output.write(buffer, 0, read);
                }
            }
            Path target = resolve(safeKey);
            Files.createDirectories(target.getParent());
            moveIntoPlace(temporary, target);
            temporary = null;
            return new ObjectReference(safeKey, hex(digest.digest()), size, mediaType);
        } catch (ObjectStorageLimitExceededException exception) {
            deleteQuietly(temporary);
            throw exception;
        } catch (IOException exception) {
            deleteQuietly(temporary);
            throw new ObjectStorageIoException(exception);
        }
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: reference 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    @Override
    public InputStream open(ObjectReference reference) {
        Path target = resolve(reference == null ? null : reference.objectKey());
        try {
            if (!Files.isRegularFile(target)) {
                throw new ObjectStorageNotFoundException();
            }
            return Files.newInputStream(target);
        } catch (ObjectStorageNotFoundException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new ObjectStorageIoException(exception);
        }
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: reference 输入参数。
     * @author: yigeyige99
     */
    @Override
    public void delete(ObjectReference reference) {
        Path target = resolve(reference == null ? null : reference.objectKey());
        try {
            if (!Files.deleteIfExists(target)) {
                throw new ObjectStorageNotFoundException();
            }
        } catch (ObjectStorageNotFoundException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new ObjectStorageIoException(exception);
        }
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: objectKey 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    private Path resolve(String objectKey) {
        String safeKey = safeKey(objectKey);
        Path target = root.resolve(safeKey.replace('/', root.getFileSystem().getSeparator().charAt(0))).normalize();
        if (!target.startsWith(root)) {
            throw new ObjectStoragePathViolationException();
        }
        return target;
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: objectKey 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    private static String safeKey(String objectKey) {
        try {
            return ObjectReference.requireLogicalKey(objectKey);
        } catch (IllegalArgumentException exception) {
            throw new ObjectStoragePathViolationException();
        }
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: bytes 输入参数。
     * @return: 当前处理结果。
     * @author: yigeyige99
     */
    private static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(String.format("%02x", value));
        }
        return result.toString();
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: source 输入参数。
     * @param: target 输入参数。
     * @throws: IOException 执行失败时抛出。
     * @author: yigeyige99
     */
    private static void moveIntoPlace(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     *
     * @description: 执行当前职责所需的处理。
     *
     * @param: path 输入参数。
     * @author: yigeyige99
     */
    private static void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // Best effort cleanup; the original typed failure remains authoritative.
        }
    }
}
