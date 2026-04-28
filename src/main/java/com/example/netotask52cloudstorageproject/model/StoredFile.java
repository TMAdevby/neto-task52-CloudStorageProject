
package com.example.netotask52cloudstorageproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import com.example.netotask52cloudstorageproject.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "stored_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(nullable = false, unique = true, length = 255)
    private String storageFilename;

    @Column(nullable = true, length = 255)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = true, length = 64)
    private String fileHash;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onUpload() {
        uploadedAt = LocalDateTime.now();
    }
}
