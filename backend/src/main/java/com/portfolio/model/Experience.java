package com.portfolio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "experience")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String role;
    private String location;
    private String startDate;
    private String endDate;
    private Boolean isCurrent;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "experience_highlights", joinColumns = @JoinColumn(name = "experience_id"))
    @Column(name = "highlight", columnDefinition = "TEXT")
    private List<String> highlights = new ArrayList<>();

    private Integer displayOrder;
}
