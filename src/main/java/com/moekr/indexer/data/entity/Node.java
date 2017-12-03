package com.moekr.indexer.data.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "node", indexes = @Index(columnList = "path"))
public class Node {
    @Id
    @Column(name = "id")
    private String id;

    @Basic
    @Column(name = "directory")
    private boolean directory;

    @Basic
    @Column(name = "path")
    private String path;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "size")
    private long size;

    @Basic
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
