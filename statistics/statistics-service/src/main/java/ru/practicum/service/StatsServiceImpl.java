package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatDto;
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
        if (params.getUris().length == 0) {
            if (params.isUnique()) return repository.findAllUniqueStatistic(params.getStart(), params.getEnd());
            else return repository.findAllStatistic(params.getStart(), params.getEnd());
        } else {
            List<String> uris = Arrays.stream(params.getUris()).collect(Collectors.toList());
            if (params.isUnique()) return repository
                    .findUniqueStatisticByUris(uris, params.getStart(), params.getEnd());
            else return repository.findStatisticByUris(uris, params.getStart(), params.getEnd());
        }
    }
}
