package ru.yandex.yandexlavka.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SuppressWarnings("NullableProblems")
public class OffsetLimitPageable implements Pageable {
    private final int offset;
    private final int limit;

    public static Pageable of(int offset, int limit) {
        return new OffsetLimitPageable(offset, limit);
    }

    private OffsetLimitPageable(int offset, int limit) {
        if (offset < 0)
            throw new IllegalArgumentException("Offset must not be less than zero!");

        if (limit < 0)
            throw new IllegalArgumentException("Limit must not be less than zero!");

        this.offset = offset;
        this.limit = limit;
    }

    @Override
    public int getPageNumber() { return 0; }

    @Override
    public int getPageSize() { return limit; }

    @Override
    public long getOffset() { return offset; }

    @Override
    public Sort getSort() { return null; }

    @Override
    public Pageable next() { return null; }

    @Override
    public Pageable previousOrFirst() { return this; }

    @Override
    public Pageable first() { return this; }

    @Override
    public Pageable withPage(int pageNumber) { return null; }

    @Override
    public boolean hasPrevious() { return false; }
}
