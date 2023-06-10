package dev.danilbel.demo.todo.list.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AskDto {

    Boolean answer;

    public static AskDto makeDefault(Boolean answer) {
        return builder()
                .answer(answer)
                .build();
    }
}
