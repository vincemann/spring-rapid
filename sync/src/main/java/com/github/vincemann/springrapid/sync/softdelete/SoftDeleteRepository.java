package com.github.vincemann.springrapid.sync.softdelete;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Kristijan Georgiev
 * @modifiedBy vincemann
 *
 * @param <T>
 *            the class of the entity
 * @param <ID>
 *            the ID class of the entity
 *
 *            NoRepositoryBean interface for the soft delete functionality
 */

@Transactional
@NoRepositoryBean
public interface SoftDeleteRepository<T, ID extends Serializable> {


    List<T> findAllActive();


    Iterable<T> findAllActive(Sort sort);

//    Page<T> findAllActive(Pageable pageable);

    List<T> findAllActive(Specification<T> specification);

    List<T> findAllActive(Specification<T> specification, Sort sort);

    List<T> findAllActive(Collection<ID> ids);

    Optional<T> findOneActive(ID id);

    @Modifying
    void softDelete(ID id);

    @Modifying
    void softDelete(T entity);

    @Modifying
    void softDelete(Iterable<? extends T> entities);

    @Modifying
    void softDeleteAll();

    @Modifying
    void scheduleSoftDelete(ID id, Date date);

    @Modifying
    void scheduleSoftDelete(T entity, Date date);

    long countActive();

    boolean existsActive(ID id);

}
