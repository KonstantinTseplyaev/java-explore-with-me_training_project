package ru.practicum.model.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationCreationDto {
    @Length(min = 1, max = 50)
    @NotBlank
    private String title;
    @Builder.Default
    private List<Long> events = new ArrayList<>();
    @Builder.Default
    private boolean pinned = false;
}
