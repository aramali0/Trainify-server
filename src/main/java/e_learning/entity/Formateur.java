package e_learning.entity;


import e_learning.enums.TypeFormateur;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("formateur")
public class Formateur extends UserApp {

    private TypeFormateur typeFormateur;
    private String CabinetName;
    private String CabinetNum;
    @ManyToOne
    private Entreprise entreprise;


    @ManyToMany(mappedBy = "formateurs", cascade = CascadeType.ALL)
    private List<Cour> cours;

}