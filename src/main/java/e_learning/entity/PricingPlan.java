package e_learning.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PricingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String planAR;
    private String planFR;
    private String planEN;
    private String price;
    private String per;
    private String link;
    private boolean popular;

    @ElementCollection
    private List<String> featuresAR;

    @ElementCollection
    private List<String> featuresFR;

    @ElementCollection
    private List<String> featuresEN;

}
