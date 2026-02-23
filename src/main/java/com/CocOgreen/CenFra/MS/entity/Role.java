package com.CocOgreen.CenFra.MS.entity;

<<<<<<< feature/Login-Authorize
import com.CocOgreen.CenFra.MS.enums.RoleName;
=======
>>>>>>> main
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name ="role")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer roleId;
    @Column
    private RoleName roleName;

    @OneToMany(mappedBy = "role")
    private List<User> users;

}
