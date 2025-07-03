package project.closet.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        Object nextCursor,
        int size,
        boolean hasNext,
        Long totalElements
) {

}
