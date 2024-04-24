package ru.aston.hw3.owner.model;

import lombok.Builder;
import lombok.Data;
import ru.aston.hw3.solution.model.Solution;

import java.util.List;

@Data
@Builder
public class Owner {
    private Long id;
    private String lastName;
    private String firstName;
    private String email;
    private List<Solution> solutions;
}
