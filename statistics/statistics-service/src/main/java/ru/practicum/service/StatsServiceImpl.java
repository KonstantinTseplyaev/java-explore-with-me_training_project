package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stat.dto.StatDto;
import ru.practicum.model.Stat;
import ru.practicum.repository.StatsRepository;
import stat.dto.StatCreationDto;
import stat.dto.StatParams;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public void saveStatistic(StatCreationDto statCreationDto) {
        Stat stat = Stat.builder()
                .app(statCreationDto.getApp())
                .uri(statCreationDto.getUri())
                .ip(statCreationDto.getIp())
                .visitTime(statCreationDto.getTimestamp()).build();
        repository.save(stat);
    }

    @Override
    public List<StatDto> getStatistic(StatParams params) {
        List<String> uris;
        if (params.getUris().length == 0) uris = null;
        else uris = Arrays.stream(params.getUris()).collect(Collectors.toList());
        if (params.isUnique()) return repository.findUniqueStatistic(uris, params.getStart(), params.getEnd());
        else return repository.findStatistic(uris, params.getStart(), params.getEnd());
    }
}
