package dev.dev_store_api.libs.utils;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

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
}