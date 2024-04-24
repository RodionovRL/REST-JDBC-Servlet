package ru.aston.hw3.solution.model;

import lombok.Builder;
import lombok.Data;
import ru.aston.hw3.developer.model.Developer;
import ru.aston.hw3.owner.model.Owner;

import java.util.List;

@Data
@Builder
public class Solution {
    private Long id;
    private String name;
    private String version;
    private Owner owner;
    private List<Developer> developers;
}

