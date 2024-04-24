package ru.aston.hw3.developer.model;

import lombok.Builder;
import lombok.Data;
import ru.aston.hw3.solution.model.Solution;

import java.util.List;

@Data
@Builder
public class Developer {
    private long id;
    private String lastName;
    private String firstName;
    private String email;
    private List<Solution> solutions;
}
