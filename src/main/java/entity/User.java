package entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table (name = "users", schema = "bitmexbot")
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private String id;
    @Column (name = "username")
    private String username;
    @Column (name = "password")
    private String password;
    @Column (name = "secret_key")
    private String secretKey;
    @Column (name = "api_key")
    private String apiKey;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    private Date createDate;
}
