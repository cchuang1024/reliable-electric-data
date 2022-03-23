package edu.nccu.cs.recorder.domain;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

import edu.nccu.cs.exception.ApplicationException;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.RocksIteratorInterface;

public class RocksIteratorWrapper implements RocksIteratorInterface, Closeable {

    private RocksIterator iterator;

    public RocksIteratorWrapper(RocksIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public void close() throws IOException {
        this.iterator.close();
    }

    @Override
    public boolean isValid() {
        return this.iterator.isValid();
    }

    @Override
    public void seekToFirst() {
        this.iterator.seekToFirst();
        status();
    }

    @Override
    public void seekToLast() {
        this.iterator.seekToLast();
        status();
    }

    @Override
    public void seek(byte[] target) {
        this.iterator.seek(target);
        status();
    }

    @Override
    public void seekForPrev(byte[] target) {
        this.iterator.seekForPrev(target);
        status();
    }

    @Override
    public void seek(ByteBuffer target) {
        this.iterator.seek(target);
        status();
    }

    @Override
    public void seekForPrev(ByteBuffer target) {
        this.iterator.seekForPrev(target);
        status();
    }

    @Override
    public void next() {
        this.iterator.next();
        status();
    }

    @Override
    public void prev() {
        this.iterator.prev();
        status();
    }

    @Override
    public void status() {
        try {
            iterator.status();
        } catch (RocksDBException ex) {
            throw new ApplicationException(ex);
        }
    }

    @Override
    public void refresh() throws RocksDBException {
        this.iterator.refresh();
        status();
    }

    public byte[] key() {
        return this.iterator.key();
    }

    public byte[] value() {
        return this.iterator.value();
    }
}
