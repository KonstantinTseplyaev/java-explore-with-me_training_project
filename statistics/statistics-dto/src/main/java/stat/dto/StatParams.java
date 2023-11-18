package stat.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatParams {
    @JsonDeserialize(using = CustomDeserializer.class)
    @JsonSerialize(using = CustomSerializer.class)
    private LocalDateTime start;
    @JsonDeserialize(using = CustomDeserializer.class)
    @JsonSerialize(using = CustomSerializer.class)
    private LocalDateTime end;
    @Builder.Default
    private String[] uris = {};
    private boolean unique;
}
