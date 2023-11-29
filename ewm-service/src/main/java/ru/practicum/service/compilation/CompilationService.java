package ru.practicum.service.compilation;

import ru.practicum.model.compilation.dto.CompilationCreationDto;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.compilation.dto.CompilationForUpdateDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(CompilationCreationDto compilationDto);

    void deleteCompilation(long compId);

    CompilationDto updateCompilation(long compId, CompilationForUpdateDto compilationDto);

    CompilationDto getCompilationById(long compId);

    List<CompilationDto> getCompilations(boolean pinned, int from, int size);
}
