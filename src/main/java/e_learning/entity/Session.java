package e_learning.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Date sessionDate;
    private int duree;
    private Date startDate;
    private Date endDate;
    private boolean isApproved;
    private boolean isSent;

    @ManyToOne
    @JsonIgnore // Prevent recursion
    @JsonManagedReference // Parent-side of the relationship
    private Cour cour;

    @ManyToOne
    private UserApp createdBy;

    @OneToMany(mappedBy = "session", cascade = {CascadeType.PERSIST, CascadeType.MERGE,})
    private List<Section> sections;


    @OneToMany(mappedBy = "session",cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<VideoConference> videoConferences;

}

