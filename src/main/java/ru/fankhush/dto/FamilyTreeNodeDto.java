package ru.fankhush.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyTreeNodeDto {
    private Integer id;
    private List<Integer> pids;
    private Integer mid;
    private Integer fid;
    private String name;
    private String photo;
    private String gender;
    private String born;
}
