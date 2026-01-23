package ru.fankhush.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonRequestDto {
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String photo;
    private Integer fatherId;
    private Integer motherId;
    private Integer spouseId;
    private List<Integer> pids;
}
