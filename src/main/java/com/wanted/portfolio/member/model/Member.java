package com.wanted.portfolio.member.model;

import com.wanted.portfolio.global.model.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true)
public class Member extends BaseModel {

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    @Column(unique = true)
    private String name;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    public boolean hasSameName(String name) {
        return this.name.equals(name);
    }
}
