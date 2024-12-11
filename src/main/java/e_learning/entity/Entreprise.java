package e_learning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Entreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nomCommercial;
    private String numeroRC;
    private String numeroCNSS;
    private String numeroIF;
    private String numeroTP;
    private int nombreSalaries;
    private String logo;
    private Integer maxSize;
    private boolean showQuizResult;
    private boolean showQuizCorrection;
    private boolean showExcelMethode;
    private boolean downloadVideo;

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL)
    private List<HierarchicalUnit> hierarchicalUnits; //  List of all hierarchical units


    // Other relationships like ResponsableFormation, Cours, Participants, etc.
    @OneToMany(mappedBy = "entreprise",cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<ResponsableFormation> responsableFormations;

    @OneToMany(mappedBy = "entreprise",cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Cour> cours;

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Participant> appendingParticipants;

    @OneToMany(mappedBy = "entreprise",cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Participant> participants;

    @OneToMany(mappedBy = "entreprise",cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Formateur> formateurs;

    @OneToMany(mappedBy = "entreprise",cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<ChargeFormation> chargeFormations;
}
