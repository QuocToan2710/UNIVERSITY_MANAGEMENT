package com.toan.university_management.common.mapper;

import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Generic BaseMapper interface for MapStruct mappers.
 *
 * @param <E>  Entity type
 * @param <RQ> Request DTO type
 * @param <RS> Response DTO type
 */
public interface BaseMapper<E, RQ, RS> {

    E toEntity(RQ request);

    RS toResponse(E entity);

    List<RS> toResponseList(List<E> entities);

    void updateEntity(@MappingTarget E entity, RQ request);
}
