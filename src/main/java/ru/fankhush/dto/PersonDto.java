package ru.fankhush.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {
    private Integer id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String photo;
    private Integer fatherId;
    private Integer motherId;
    private Integer spouseId;
}
