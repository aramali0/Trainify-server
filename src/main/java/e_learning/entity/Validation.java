package e_learning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Validation {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Instant createdAt;
    private Instant activation;
    private Instant expireAt;
    private String code;
    @ManyToOne
    private UserApp userApp;

}