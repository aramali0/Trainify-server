package e_learning.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import e_learning.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Builder
@Entity(name="users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="user_app",
        discriminatorType = DiscriminatorType.STRING)
public class UserApp implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
//    @Column(unique = true, nullable = false)
    private String matriculeId;
    @Column(unique = true, nullable = false)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String firstName;
    private String lastName;
    private String num;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String CIN;
    private boolean isVerified;
    private int age;
    private boolean isEnabled;
    private String passwordResetCode;
    private LocalDateTime codeExpirationTime;
    private LocalDateTime lastActiveTime;
    private String profileImagePath;
    private Date createdAt;
    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleApp> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (RoleApp role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole().toString()));
        }
        return Collections.unmodifiableList(authorities);
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return isEnabled && isVerified;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled && isVerified;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled && isVerified;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled && isVerified;
    }


}
