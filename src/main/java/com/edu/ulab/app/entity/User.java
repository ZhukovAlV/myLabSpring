package com.edu.ulab.app.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "person")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name = "sequence",
            sequenceName = "sequence",
            initialValue = 1, allocationSize = 20)
    private Long id;

    private String fullName;
    private String title;
    private int age;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="userId", cascade = CascadeType.REMOVE)
    private List<Book> bookList;
}
