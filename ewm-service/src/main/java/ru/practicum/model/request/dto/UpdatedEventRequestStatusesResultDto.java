package ru.practicum.model.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedEventRequestStatusesResultDto {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
