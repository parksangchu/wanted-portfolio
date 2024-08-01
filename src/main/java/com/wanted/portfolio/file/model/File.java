package com.wanted.portfolio.file.model;

import com.wanted.portfolio.global.model.BaseModel;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@ToString(callSuper = true)
public class File extends BaseModel {

    private String originalName;

    private String storeName;

    private String url;
}


