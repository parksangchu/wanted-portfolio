package com.wanted.portfolio.member.model;

import com.wanted.portfolio.global.model.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Member extends BaseModel {

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    private String name;

    private String password;
}
