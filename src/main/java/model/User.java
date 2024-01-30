package model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table (name = "user")
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private Integer id;
    @Column (name = "username")
    private String username;
    @Column (name = "password")
    private String password;
    @Column (name = "secret_key")
    private String secretKey;
    @Column (name = "api_key")
    private String apiKey;
}
