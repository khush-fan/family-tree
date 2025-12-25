package ru.fankhush.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTreeNodeDto {
    private Integer id;
    private Integer pid;
    private Integer mid;
    private Integer fid;
    private String name;
    private String image;
    private String gender;
    private String born;
}
