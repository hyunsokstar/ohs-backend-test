package org.zerock.apiserver.dto.category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDtoMini {
    private Long categoryId;
    private String name;
}
