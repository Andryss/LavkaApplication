package ru.yandex.yandexlavka.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SuppressWarnings("NullableProblems")
public class OffsetLimitPageable implements Pageable {
    private final int offset;
    private final int limit;
    private final Sort sort;

    public static Pageable of(int offset, int limit) {
        return new OffsetLimitPageable(offset, limit, Sort.unsorted());
    }

    private OffsetLimitPageable(int offset, int limit, Sort sort) {
        if (offset < 0)
            throw new IllegalArgumentException("Offset must not be less than zero!");

        if (limit < 1)
            throw new IllegalArgumentException("Limit must be positive!");

        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetLimitPageable(
                (int)(getOffset() + getPageSize()), getPageSize(), getSort()
        );
    }

    public OffsetLimitPageable previousOrThis() {
        return hasPrevious() ? new OffsetLimitPageable(
                (int)(getOffset() - getPageSize()), getPageSize(), getSort()
        ) : this;
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previousOrThis() : first();
    }

    @Override
    public Pageable first() {
        return new OffsetLimitPageable(
                0, getPageSize(), getSort()
        );
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetLimitPageable(
                pageNumber * getPageSize(), getPageSize(), getSort()
        );
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}
