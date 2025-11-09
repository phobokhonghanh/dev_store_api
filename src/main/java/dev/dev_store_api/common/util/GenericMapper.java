package dev.dev_store_api.common.util;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenericMapper {
    private final ModelMapper modelMapper;

    public GenericMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <E, D> D toDTO(E entity, Class<D> dtoClass) {
        try {
            return modelMapper.map(entity, dtoClass);
        } catch (MappingException | IllegalArgumentException e) {
            throw new RuntimeException("Lỗi khi ánh xạ từ Entity sang DTO: " + e.getMessage());
        }
    }

    public <D, E> E toEntity(D dto, Class<E> entityClass) {
        try {
            return modelMapper.map(dto, entityClass);
        } catch (MappingException | IllegalArgumentException e) {
            throw new RuntimeException("Lỗi khi ánh xạ từ DTO sang Entity: " + e.getMessage());
        }
    }
    public <E, D> List<D> toDTOList(List<E> entities, Class<D> dtoClass) {
        if (entities == null || entities.isEmpty()) return List.of();
        try {
            return entities.stream()
                    .map(entity -> toDTO(entity, dtoClass))
                    .collect(Collectors.toList());
        } catch (MappingException | IllegalArgumentException e) {
            throw new RuntimeException("Lỗi khi ánh xạ danh sách từ Entity sang DTO: " + e.getMessage());
        }
    }

    public <D, E> List<E> toEntityList(List<D> dtos, Class<E> entityClass) {
        if (dtos == null || dtos.isEmpty()) return List.of();
        try {
            return dtos.stream()
                    .map(dto -> toEntity(dto, entityClass))
                    .collect(Collectors.toList());
        } catch (MappingException | IllegalArgumentException e) {
            throw new RuntimeException("Lỗi khi ánh xạ danh sách từ DTO sang Entity: " + e.getMessage());
        }
    }
}