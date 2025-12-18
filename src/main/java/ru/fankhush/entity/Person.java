package ru.fankhush.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Person {
    private Integer  id;
    private String name;
    private Gender gender;
    private LocalDate birthDate;
    private String photo;
    private Integer fatherId;
    private Integer motherId;
    private Integer spouseId;
}
