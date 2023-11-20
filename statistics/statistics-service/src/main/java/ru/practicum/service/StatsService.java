package ru.practicum.service;


import ru.practicum.dto.StatDto;
import stat.dto.StatCreationDto;
import stat.dto.StatParams;

import java.util.List;

public interface StatsService {
    List<StatDto> getStatistic(StatParams params);

    void saveStatistic(StatCreationDto statCreationDto);
}
