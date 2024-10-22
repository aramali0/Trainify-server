package e_learning.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Date createdAt;
    private Date updatedAt;
    private boolean isApproved;
    @ManyToOne
    private UserApp createdBy;

    @ManyToOne
    private Cour cour;

    @OneToMany(mappedBy = "library" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceEntity> resources;
}