package ru.practicum.model.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.request.RequestState;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedEventRequestStatusesDto {
    private List<Long> requestIds;
    private RequestState status;
}
