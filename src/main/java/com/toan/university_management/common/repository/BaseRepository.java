package com.toan.university_management.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    Optional<T> findByIdAndDeletedFalse(ID id);

    List<T> findAllByDeletedFalse();

    Page<T> findAllByDeletedFalse(Pageable pageable);

    List<T> findAllByIdInAndDeletedFalse(Collection<ID> ids);

    boolean existsByIdAndDeletedFalse(ID id);
}
