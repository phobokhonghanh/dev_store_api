package dev.dev_store_api.common.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class GenericUpdater {
    /**
     * Trả về entity mới được patch từ DTO
     *
     * @param source         DTO input
     * @param target         entity hiện tại
     * @param excludedFields các field không muốn update (có thể null hoặc bỏ)
     * @param <T>            kiểu entity
     * @return entity mới với dữ liệu được update
     */
    @SuppressWarnings("unchecked")
    public <T> T patch(Object source, T target, String... excludedFields) {
        if (source == null || target == null) return target;

        Set<String> exclude = excludedFields != null
                ? new HashSet<>(Arrays.asList(excludedFields))
                : Set.of();

        BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        BeanWrapper targetWrapper = new BeanWrapperImpl(target);

        try {
            T copy = (T) target.getClass().getDeclaredConstructor().newInstance();
            BeanWrapper copyWrapper = new BeanWrapperImpl(copy);

            Arrays.stream(targetWrapper.getPropertyDescriptors())
                    .filter(pd -> !"class".equals(pd.getName()))
                    .forEach(pd -> copyWrapper.setPropertyValue(pd.getName(), targetWrapper.getPropertyValue(pd.getName())));

            Arrays.stream(srcWrapper.getPropertyDescriptors())
                    .filter(pd -> !"class".equals(pd.getName()))
                    .forEach(pd -> {
                        String name = pd.getName();
                        if (!exclude.contains(name)) {
                            Object value = srcWrapper.getPropertyValue(name);
                            if (value != null) {
                                copyWrapper.setPropertyValue(name, value);
                            }
                        }
                    });

            return copy;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo bản copy entity: " + e.getMessage(), e);
        }
    }
}
