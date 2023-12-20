package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.ModelNotFoundException;
import ru.practicum.util.MapperUtil;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationCreationDto;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.compilation.dto.CompilationForUpdateDto;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.dto.RequestDto;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public CompilationDto createCompilation(CompilationCreationDto compilationDto) {
        List<Event> events = checkEvents(compilationDto.getEvents());
        Compilation compilation = compilationRepository.save(MapperUtil.convertToCompilation(events, compilationDto));
        return MapperUtil.convertToCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(long compId, CompilationForUpdateDto compilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ModelNotFoundException("Compilation with id =" + compId + " was not found"));
        compilation.setPinned(compilation.isPinned());
        if (compilationDto.getTitle() != null) compilation.setTitle(compilationDto.getTitle());
        if (compilationDto.getEvents() != null) compilation.setEvents(checkEvents(compilationDto.getEvents()));
        return MapperUtil.convertToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ModelNotFoundException("Compilation with id =" + compId + " was not found"));
        return MapperUtil.convertToCompilationDto(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, pageable);
        List<RequestDto> confRequestsDto = requestRepository.findAllConfirmRequest();
        Map<Long, List<RequestDto>> confirmedRequestsMap = confRequestsDto.stream()
                .collect(Collectors.groupingBy(RequestDto::getEvent));
        return compilations.stream().map(compilation -> {
                    CompilationDto compilationDto = MapperUtil.convertToCompilationDto(compilation);
                    compilationDto.getEvents().forEach(event -> {
                        int confirmedRequests = confirmedRequestsMap.getOrDefault(event.getId(), List.of()).size();
                        event.setConfirmedRequests(confirmedRequests);
                    });
                    return compilationDto;
                })
                .collect(Collectors.toList());
    }

    private List<Event> checkEvents(List<Long> ids) {
        if (!ids.isEmpty()) return eventRepository.findByIdIn(ids);
        else return new ArrayList<>();
    }
}
