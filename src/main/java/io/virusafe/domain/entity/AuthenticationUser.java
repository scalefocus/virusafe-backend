package io.virusafe.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "authentication_users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuthenticationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @EqualsAndHashCode.Include
    private String username;

    private String password;

    private Boolean active;

    /**
     * All-args constructor for AuthenticationUser.
     * Can be used as a Lombok builder.
     *
     * @param id       the entity's DB ID
     * @param username the user's username
     * @param password the user's hashed password
     * @param active   whether the user is active
     */
    @Builder
    public AuthenticationUser(final Long id,
                              final String username,
                              final String password,
                              final Boolean active) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.active = active;
    }
}
